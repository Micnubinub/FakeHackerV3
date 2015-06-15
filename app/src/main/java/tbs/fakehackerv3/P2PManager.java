package tbs.fakehackerv3;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Michael on 5/16/2015.
 */
public class P2PManager extends Service {
    public static final ListenerRunnable listenerRunnable = new ListenerRunnable();
    public static final MainThreadRunnable mainThreadRunnable = new MainThreadRunnable();
    public static final WifiP2pConfig config = new WifiP2pConfig();
    public static final IntentFilter intentFilter = new IntentFilter();
    public static final WifiP2pManager.ActionListener wifiP2PActionListener = new WifiP2pManager.ActionListener() {
        @Override
        public void onSuccess() {
            //TODO devices found >> do something about it
            log("actionListener success");
        }

        @Override
        public void onFailure(int i) {
            //TODO devices not found >> do something about it (rescan?)
            String out = "";
            switch (i) {
                case WifiP2pManager.P2P_UNSUPPORTED:
                    out = "UnSupported";
                    break;
                case WifiP2pManager.ERROR:
                    out = "Error";
                    break;
                case WifiP2pManager.BUSY:
                    out = "Busy";
                    break;
            }

            log("failed to discover devices : " + out);

            manager.requestGroupInfo(channel, new WifiP2pManager.GroupInfoListener() {
                @Override
                public void onGroupInfoAvailable(WifiP2pGroup group) {
                    manager.removeGroup(channel, new WifiP2pManager.ActionListener() {
                        @Override
                        public void onSuccess() {
                            log("successfully removed group");
                        }

                        @Override
                        public void onFailure(int reason) {
                            log("failed to remove group");
                        }
                    });
                }
            });
        }
    };
    private static final ArrayList<Message> messages = new ArrayList<Message>();
    public static Thread mainThread, getClientSocketThread, getServerSocketThread, listenerThread;
    public static ServerSocket serverSocket;
    public static ContentResolver cr;
    public static WifiP2pManager manager;
    public static WifiManager wifiManager;
    public static Collection<WifiP2pDevice> peers;
    public static WifiP2pManager.Channel channel;
    public static P2PBroadcastReceiver receiver;
    public static P2PListener p2PListener;
    private static Context context;
    private static InputStream inputStream;
    private static OutputStream outputStream;
    //Todo stop this somewhere
    public static boolean stop = false, currentlySendingSomething = false;
    private static Socket socketFromServer, socketFromClient;
    private static String host;
    private static Activity activity;
    private static Dialog dialog;
    private static WifiP2pDevice clickedDevice;
    private static WifiP2pInfo wifiP2pInfo;
    public static WifiP2pManager.ConnectionInfoListener connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
            if (wifiP2pInfo == null || wifiP2pInfo.groupOwnerAddress == null) {
                requestConnectionInfo("wifip2pinfo listener");
                return;
            }

            P2PManager.wifiP2pInfo = wifiP2pInfo;

            log("receivedInfo : " + wifiP2pInfo.groupOwnerAddress.toString() + "\nisOwner? : " + wifiP2pInfo.isGroupOwner);
            if (wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner) {
                log("infoReceived :" + wifiP2pInfo.groupFormed + "formed<>owner " + wifiP2pInfo.isGroupOwner);
                getServerSocketThreadVoid();
            } else if (wifiP2pInfo.groupFormed) {
                log("infoReceived, group formed");
                getClientSocketThreadVoid(wifiP2pInfo.groupOwnerAddress.toString());
            } else {
                log("info receive, connecting to " + clickedDevice.deviceName);
                connectToDevice(clickedDevice);
            }
        }
    };
    public static boolean dialogShown, tryingToConnect;
    public static final WifiP2pManager.PeerListListener wifiP2PPeerListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(final WifiP2pDeviceList wifiP2pDeviceList) {
            //TODO make refresh button visible
            peers = wifiP2pDeviceList.getDeviceList();
            try {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }

            if (peers.size() > 0)
                showDeviceDialog();
            else {
                toast("No peers found, try again");
                log("No peers found, try again");
            }
        }
    };

    public static P2PManager p2PManager;
    private static P2PAdapter adapter;

    public P2PManager() {
        super();
    }

    public static Context getContext() {
        return activity;
    }

    public static P2PManager getP2PManager(Activity activity, P2PListener p2PListener) {
        P2PManager.p2PListener = p2PListener;
        if (P2PManager.activity != activity) {
            p2PManager = new P2PManager(activity);
        }
        return p2PManager;
    }

    private P2PManager(Activity activity) {
        P2PManager.activity = activity;


        if (isServiceRunning() || isActive() || tryingToConnect)
            return;
        activity.startService(new Intent(activity, P2PManager.class));
        //Todo make a preference and put it in setttings

    }

    private static void nullifySockets() {
        if (socketFromClient != null) {
            try {
                socketFromClient.close();
            } catch (IOException e) {

                e.printStackTrace();
            }
            socketFromClient = null;
        }

        if (socketFromServer != null) {
            try {
                socketFromServer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            socketFromServer = null;
        }
    }

    public static void connectToDevice(final WifiP2pDevice device) {
        //TODO major need to sort this out >> look at host > getIP...
        //Todo make a listview...
        dismissDialog();
        nullifySockets();
        config.deviceAddress = device.deviceAddress;
        manager.connect(channel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                //TODO connected to device start service here and broadcast receiver
//                MainActivity.toast("connected");
                if (p2PListener != null)
                    p2PListener.onDevicesConnected(device);
                requestConnectionInfo("connect to device");
            }

            @Override
            public void onFailure(int reason) {
                //TODO failed to connect
                log("failed to connect to " + device.deviceName + " (" + device.deviceAddress + ")");
            }
        });
    }

    public static void toast(final String msg) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    public static void requestConnectionInfo(String from) {
        if (!isActive()) {
            try {
                Thread.sleep(350);
            } catch (InterruptedException e) {
//                e.printStackTrace();
            }
            // log("reqInfo : not active " + from);
            manager.requestConnectionInfo(channel, connectionInfoListener);
        } else {
            log("reqInfo : active " + from);
        }
    }

    public void disconnect() {
        //Todo potentially dangerous
        destroy();
        if (context != null) {
            try {
                context.stopService(new Intent(context, P2PManager.class));
            } catch (Exception e) {
                log("crashed (disconnect)> " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

//    public static WifiP2pDevice getConnectedPeer() {
//        if (peers == null)
//            return null;
//        WifiP2pDevice peer = null;
//        for (WifiP2pDevice d : peers)f {
//            if (d.status == WifiP2pDevice.CONNECTED) {
//                peer = d;
//            }
//        }
//        return peer;
//    }

    public static void getClientSocketThreadVoid(final String hostIP) {
        if (isActive()) {
            log("getClientSocketThreadVoid>active");
            return;
        }
        if (!(getClientSocketThread == null))
            try {
                getClientSocketThread.interrupt();
            } catch (Exception e) {
                log("crashed (getClientSocketThreadVoid)> " + e.getMessage());
                e.printStackTrace();
            }

        tryingToConnect = true;
        getClientSocketThread = new Thread(new Runnable() {
            @Override
            public void run() {
                log("getClientSocket");
                try {
                    host = hostIP;
                    if (host.length() < 7 || !host.contains(".")) {
                        log("invalidIp getting connection info");
                        requestConnectionInfo("getClientThreadVoid : invalid ip");
                        return;
                    } else {
                        if (host.startsWith("/"))
                            host = host.split("/")[1].trim();
                        boolean run = true;
                        while ((socketFromClient == null || !socketFromClient.isConnected()) && run) {
                            log("2");
                            try {
                                socketFromClient = new Socket(host, 8899);
                            } catch (Exception ee) {
                                log("host : " + host);
                                log("ras : " + ee.toString());
                            }
                            log("3");
                            log("failed, trying to connect in 450ms");
                            run = !testClientSocketRead();

                            if (socketFromClient == null) {
                                log("socket from client == nul");
                            } else {
                                if (!socketFromClient.isConnected()) {
                                    log("socketFromClient not connected");
                                }
                            }
                            if (run) {
                                log("isRun");
                            }

                            try {
                                Thread.sleep(450);
                            } catch (Exception e) {
//                                log("crashed (sleep)> " + e.getMessage());
                                e.printStackTrace();
                                long stop = System.currentTimeMillis() + 450;
                                while (System.currentTimeMillis() < stop) {
                                }
                            }
                        }
                        startMainThread();
                    }
                } catch (Exception e) {
                    log("crashed (getClientSocketThread)> " + e.toString());
//                  e.printStackTrace();
                    try {
                        getClientSocketThreadVoid(hostIP);
                    } catch (Exception w) {
                        w.printStackTrace();
                    }
                }
            }
        });
        getClientSocketThread.start();
    }

    private static boolean testClientSocketRead() {
        if (socketFromClient == null)
            return false;
        try {
            return (socketFromClient.getInputStream().read() >= 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void getServerSocketThreadVoid() {
        if (isActive()) {
            log("getServerSocket, already active> exiting...");
            return;
        }
//        log("getServerSocket0");
        nullifySockets();
        tryingToConnect = true;
        if (!(getServerSocketThread == null))
            try {
                getServerSocketThread.interrupt();
            } catch (Exception e) {
                log("crashed (getServerSocketThreadVoid)> " + e.getMessage());
                e.printStackTrace();
            }

        getServerSocketThread = new Thread(new Runnable() {
            @Override
            public void run() {
//                log("getServerSocket1");

                try {
                    serverSocket = new ServerSocket();
                    serverSocket.setReuseAddress(true);
                    serverSocket.bind(new InetSocketAddress(8899));

                    while (socketFromServer == null || !socketFromServer.isConnected()) {
                        log("accepting");
                        socketFromServer = serverSocket.accept();
                        log("stepped past accepting");
                    }

                    if (!isActive()) {
                        log("not active, request info");
                        Thread.sleep(150);
                        requestConnectionInfo("getServerSocketThreadVoid");
                    } else {
                        log("getServerSocket3");
                        startMainThread();
                    }
                } catch (IOException e) {
                    disconnectToCurrentDevice();
                    getServerSocketThreadVoid();
                    try {
                        getServerSocketThread.stop();
                        getServerSocketThread.interrupt();
                    } catch (Exception d) {
                        d.printStackTrace();

                    }
                    //log("disconnectingAndGettingServerSocket " + e.toString());
                } catch (Exception e) {
                    log("crashed (getServerSocketThread)> " + e.getMessage());
                }

            }
        });
        getServerSocketThread.start();
    }

    public static void startMainThread() {
        log("should start mainthread");
        tryingToConnect = false;

        if (mainThread == null || (!mainThread.isAlive() && !(mainThread.isInterrupted()))) {
            mainThread = new Thread(mainThreadRunnable);
            mainThread.start();
        }
    }

    public static synchronized void enqueueMessage(Message message) {
        if (messages != null && message != null) {
            if (!messages.contains(message)) {
                messages.add(message);
            }
        }
    }

    private static synchronized void sendMessage() {
        if (messages == null || messages.size() < 1)
            return;

        final Message message = messages.get(0);
        switch (message.messageType) {
            case SEND_COMMAND:
                log("send command");
                sendSimpleText(message.getSendableMessage());
                break;
            case SEND_FILE:
                log("send file");
                //Todo message structure >> fileName + sep + file
                if (cr == null && (activity != null))
                    cr = activity.getContentResolver();
                try {
                    final File file = new File(message.getMessage());
                    copyFile(file.getName(), cr.openInputStream(Uri.parse(Environment.getExternalStorageDirectory().getPath() + "a014.jpg")));
                } catch (Exception e) {
                    log("crashed (sendFile)> " + e.getMessage());
                    e.printStackTrace();
                }
                break;
            case SEND_MESSAGE:
                log("send message");
                //Todo message structure >> timeLong + sep + message
                sendSimpleText(message.getSendableMessage());
                break;
        }
        messages.remove(message);
    }

    private static boolean sendSimpleText(String text) {
        log("sending Message : " + text);
        try {
            currentlySendingSomething = true;
            if (outputStream == null) {
                outputStream = getSocket().getOutputStream();
            }
//            log("writing message : " + text);
            outputStream.write(text.getBytes());
            //Todo check this
            outputStream.flush();
            currentlySendingSomething = false;
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public synchronized static boolean copyFile(String fileName, InputStream inputStream) {
        //Todo apply file name
        byte buf[] = new byte[1024];
        int len;
        try {
            if (outputStream == null) {
                outputStream = getSocket().getOutputStream();
            }
            currentlySendingSomething = true;
            while ((len = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, len);
            }
            outputStream.flush();
            inputStream.close();
            currentlySendingSomething = false;
        } catch (IOException e) {
            log("p2pfailed to copyFile >> " + e.toString());
            return false;
        }
        return true;
    }

    public static void destroy() {
        log("destroy called");
        unRegisterReceivers();
        try {
            getSocket().close();
        } catch (Exception e) {
            log("crashed (destroy)> " + e.getMessage());
            e.printStackTrace();
        }
        try {
            outputStream.close();
        } catch (Exception e) {
            log("crashed (destroy)> " + e.getMessage());
            e.printStackTrace();
        }
        try {
            inputStream.close();
        } catch (Exception e) {
            log("crashed (destroy)> " + e.getMessage());
            e.printStackTrace();
        }
        disconnectToCurrentDevice();


    }

    public static void log(String msg) {
        Log.e("p2p", msg);
    }

    public static Socket getSocket() {
        return (socketFromServer == null) ? socketFromClient : socketFromServer;
    }

    public static boolean isActive() {
        boolean bool = false;
        try {
            if (getSocket() != null)
                bool = (getSocket().getInputStream().read() >= 0);
        } catch (Exception e) {
//            log("crashed (isActive)> " + e.getMessage());
            e.printStackTrace();
        }
        return bool;
    }

    public static boolean isServiceRunning() {
        final ActivityManager manager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);//use context received in broadcastreceiver
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (P2PManager.class.getName().equals(service.service.getClassName())) return true;

        }
        return false;
    }

    public static void setMessage(String message) {
        enqueueMessage(new Message(message, Message.MessageType.SEND_MESSAGE));
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        context = getBaseContext();
        return Service.START_STICKY;//Service.START_STICKY;
    }

    public static void registerReceivers() {
        try {
            unRegisterReceivers();
            activity.registerReceiver(receiver, intentFilter);
        } catch (Exception e) {
            log("crashed (registerReceivers)> " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void unRegisterReceivers() {
        try {
            activity.unregisterReceiver(receiver);
        } catch (Exception e) {
        }
    }

    private static void showDeviceDialog() {
        if (dialogShown || isActive())
            return;

        if (activity == null)
            return;

        dismissDialog();

        dialog = new Dialog(activity, R.style.CustomDialog);
        dialog.setContentView(R.layout.device_list_dialog);
        final ListView listView = (ListView) dialog.findViewById(R.id.list_view);
        adapter = new P2PAdapter();
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                clickedDevice = (WifiP2pDevice) peers.toArray()[i];
                toast("Connecting to " + clickedDevice.deviceName + " (" + clickedDevice.deviceAddress + ")");
                dismissDialog();
                connectToDevice(clickedDevice);
            }
        });

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.show();
            }
        });
    }

    public static void disconnectToCurrentDevice() {
        tryingToConnect = false;
        nullifySockets();
//        log("disconnecting from current group");
        final WifiP2pManager.ActionListener actionListener = new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                //todo
            }

            @Override
            public void onFailure(int reason) {
                //todo
            }
        };

        try {
            manager.cancelConnect(channel, actionListener);
            manager.removeGroup(channel, actionListener);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void dismissDialog() {
        try {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (dialog != null)
                        dialog.dismiss();
                }
            });
        } catch (Exception e) {
            log("crashed (dismissDialog)> " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void stopScan() {
        try {
            if (MainActivity.connected) {
                MainViewManager.fab.setState(FAB.State.HIDING);
            } else {
                MainViewManager.fab.setState(FAB.State.IDLE);
            }
            manager.stopPeerDiscovery(channel, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void startScan() {
        if (isActive())
            return;

        MainActivity.mainViewManager.fab.setState(FAB.State.SCANNING);

        log("starting scan");
        dialogShown = false;
        if (wifiManager == null) {
            wifiManager = (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);
            if (!wifiManager.isWifiEnabled()) {
                log("Wifi not enabled, enabling");
                wifiManager.setWifiEnabled(true);
            }
        }

        if (manager == null)
            manager = (WifiP2pManager) activity.getSystemService(Context.WIFI_P2P_SERVICE);

        if (channel == null)
            channel = manager.initialize(activity, activity.getMainLooper(), new WifiP2pManager.ChannelListener() {
                @Override
                public void onChannelDisconnected() {
                    //TODO disconnected
                    if (p2PListener != null) {
                        p2PListener.onDevicesDisconnected("channel");
                    }
                }
            });

        if (receiver == null) {
            receiver = new P2PBroadcastReceiver(manager, channel, p2PManager);
            intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
            intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
            intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
            intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        }

        registerReceivers();

        if (p2PListener != null)
            p2PListener.onScanStarted();

        manager.discoverPeers(channel, wifiP2PActionListener);
    }

    @Override
    public void onDestroy() {
        destroy();
        super.onDestroy();
    }

    public interface P2PListener {
        //Todo handle messages here
        void onScanStarted();

        //Todo handle messages here
        void onMessageReceived(String msg);

        //Todo this is when the devices are connected, but not yet communicating
        void onDevicesConnected(WifiP2pDevice device);

        //Todo this is when the devices are disconnected, but not yet communicating
        void onDevicesDisconnected(String reason);

        //Todo this is when the devices can send messages
        void onSocketsConfigured();

    }

    public static class MainThreadRunnable implements Runnable {

        @Override
        public void run() {
            dismissDialog();

            listenerThread = new Thread(listenerRunnable);
            listenerThread.start();
            //TODO remove for final release

            enqueueMessage(new Message("mikeCheck 1,2,1,2", Message.MessageType.SEND_MESSAGE));
            log("mainThread step 1");
            while (getSocket() == null) {
                try {
                    if (getSocket() != null) {
                        log("mainThread step 2 start");
                        inputStream = getSocket().getInputStream();
                        outputStream = getSocket().getOutputStream();
                        log("mainThread step 2 end");
                    } else {
                        log("mainThread step 2 p2psocket is null mainThread");
                    }
                    Thread.sleep(400);
                } catch (Exception e) {
                    log("p2pserver " + e.getMessage());
                    return;
                }
            }
            if (p2PListener != null)
                p2PListener.onSocketsConfigured();
            log("mainThread step 3");
            stop = false;
            while (!stop) {
                try {
                    if (inputStream.read() < 0) {
                        log("mT2");
                        if (p2PListener != null)
                            p2PListener.onDevicesDisconnected("server input less than 0");
                        log("mT3");
                        stop = true;
                        destroy();
                        if (wifiP2pInfo.isGroupOwner)
                            getServerSocketThreadVoid();
                        else requestConnectionInfo("server thread");
                        log("mT4");
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    log("crashed in listenerThread : " + e.getMessage());
                }
                if (!currentlySendingSomething && messages != null && messages.size() > 0) {
                    sendMessage();
                }
                try {
                    Thread.sleep(25);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            log("mainThread stopping");
        }
    }

    public static class ListenerRunnable implements Runnable {

        public ListenerRunnable() {

        }

        @Override
        public void run() {
            log("listener1");
            // Todo receive file             byte buf[] = new byte[1024];
//         try {
//                /**
//                 * Create a socketFromServer socketFromServer with the host,
//                 * port, and timeout information.
//                 */
//                /**
//                 * Create a byte stream from a JPEG file and pipe it to the output stream
//                 * of the socketFromServer. This data will be retrieved by the server device.
//                 */
//                final OutputStream outputStream = getSocket().getOutputStream();
//                final InputStream inputStream = cr.openInputStream(Uri.parse(Environment.getExternalStorageDirectory().getPath() + "a014.jpg"));
//                while ((len = inputStream.read(buf)) != -1) {
//                    outputStream.write(buf, 0, len);
//                }
//                outputStream.close();
//                inputStream.close();
//            } catch (FileNotFoundException e) {
//                //catch logic
//            } catch (IOException e) {
//                //catch logic
//            }

            if (p2PListener != null)
                p2PListener.onSocketsConfigured();

            stop = false;

            log("listener0");
            try {
                inputStream = getSocket().getInputStream();
            } catch (Exception e) {
                log("crashed (P2PClientRunnable InputStream)> " + e.getMessage());
                e.printStackTrace();
            }

            log("listener1");

            while (!stop) {
                try {
                    //Todo this is to save battery a bit (checks if there's a message to be downloaded 50 times a second)
                    //Todo if you want things to be instantaneous just delete the whole try catch statement or reduce the sleep

                log("listener2");
                    if (inputStream.read() < 0) {
                        if (p2PListener != null)
                            p2PListener.onDevicesDisconnected("client input less than 0");
                        stop = true;
                        log("listener3");
                        destroy();
                        if (wifiP2pInfo.isGroupOwner)
                            getServerSocketThreadVoid();
                        else requestConnectionInfo("client Thread");
                        log("listener4");
                        break;

                    }
                    final byte[] msg = new byte[inputStream.available()];
//                    log("3");
                    if (inputStream.available() > 0) {
                        log("available " + String.valueOf(inputStream.available()));
                        inputStream.read(msg, 0, inputStream.available());
                        log("inputMsg = " + new String(msg));
                        if (p2PListener != null) {
                            p2PListener.onMessageReceived(new String(msg));
                        } else {
                            log("len : " + msg.length + " , listener : " + (p2PListener == null));
                        }
                    }
                    Thread.sleep(20);
                } catch (Exception e) {
                    log("crashed (P2PClientRunnable errthang)> " + e.toString());
                    e.printStackTrace();
                }
            }
            log("p2PListener stopping");
        }
    }

    public static class P2PAdapter extends BaseAdapter {
        public P2PAdapter() {

        }

        @Override
        public int getCount() {
            return peers == null ? 0 : peers.size();
        }

        @Override
        public Object getItem(int i) {
            return peers == null ? null : peers.toArray()[i];
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = View.inflate(activity, R.layout.device_list_item, null);
            final WifiP2pDevice device = ((WifiP2pDevice) peers.toArray()[i]);
            ((TextView) view.findViewById(R.id.device_name)).setText(device.deviceName + " (" + device.deviceAddress + ")");
            final TextView deviceStatus = ((TextView) view.findViewById(R.id.device_status));
            switch (device.status) {
                case WifiP2pDevice.CONNECTED:
                    deviceStatus.setText("Connected");
                    deviceStatus.setTextColor(0xff5677fc);
                    break;
                case WifiP2pDevice.UNAVAILABLE:
                    deviceStatus.setText("Available");
                    deviceStatus.setTextColor(0xff999999);
                    break;
                case WifiP2pDevice.INVITED:
                    deviceStatus.setText("Invited");
                    deviceStatus.setTextColor(0xfffb8c00);
                    break;
                case WifiP2pDevice.FAILED:
                    deviceStatus.setText("Failed");
                    deviceStatus.setTextColor(0xffe51c23);
                    break;
                default:
                    deviceStatus.setText("Available");
                    deviceStatus.setTextColor(0xff259b24);
                    break;
            }

            return view;
        }
    }
}
