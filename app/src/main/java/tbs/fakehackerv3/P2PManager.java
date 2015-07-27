package tbs.fakehackerv3;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;

import tbs.fakehackerv3.fragments.FileManagerFragment;
import tbs.fakehackerv3.fragments.LogFragment;

/**
 * Created by Michael on 5/16/2015.
 */
public class P2PManager extends Service {
    public static final ListenerRunnable listenerRunnable = new ListenerRunnable();
    public static final MainThreadRunnable mainThreadRunnable = new MainThreadRunnable();
    public static final WifiP2pConfig config = new WifiP2pConfig();
    public static final IntentFilter intentFilter = new IntentFilter();
    public static final ArrayList<byte[]> messageChunks = new ArrayList<byte[]>();
    private static final ArrayList<Message> messages = new ArrayList<Message>();
    private static final WifiP2pManager.ActionListener actionListener = new WifiP2pManager.ActionListener() {
        @Override
        public void onSuccess() {
            log("Action listener success");
        }


        @Override
        public void onFailure(int reason) {
            log("Action listener failure");
        }
    };
    private static final ArrayList<LocalFileMoved> LocalFileMovedListeners = new ArrayList<LocalFileMoved>();
    public static Thread mainThread, getClientSocketThread, getServerSocketThread, listenerThread;
    public static ServerSocket serverSocket;
    public static WifiP2pManager manager;
    public static boolean isReceivingFile, isSendingFile;
    public static WifiManager wifiManager;
    public static Collection<WifiP2pDevice> peers;
    public static WifiP2pManager.Channel channel;
    public static P2PBroadcastReceiver receiver;
    public static P2PListener p2PListener;
    public static boolean stop, isWaitingForConfirmation, hasntSentConfirmation;
    public static String CONFIRMATION = "MES_REC_CON";
    public static boolean dialogShown, tryingToConnect;
    public static boolean isGroupOwner;
    public static P2PManager p2PManager;
    static int reconnectRetries;
    private static Context context;
    private static InputStream inputStream;
    private static OutputStream outputStream;
    private static Socket socketFromServer, socketFromClient;
    private static String host;
    private static Activity activity;
    private static Dialog dialog;
    private static WifiP2pDevice clickedDevice;
    private static WifiP2pInfo wifiP2pInfo;
    public static WifiP2pManager.ConnectionInfoListener connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
            P2PManager.wifiP2pInfo = wifiP2pInfo;
            if (wifiP2pInfo == null || ((wifiP2pInfo.groupOwnerAddress == null) && !wifiP2pInfo.isGroupOwner)) {
                log("connectionInfoFailed > isOwner?" + wifiP2pInfo.isGroupOwner + ", isGroupFormed? " + wifiP2pInfo.groupFormed);
                requestConnectionInfo("wifip2pinfo listener : wifiInfo" + ((wifiP2pInfo == null) ? "null " : (wifiP2pInfo.groupOwnerAddress == null ? " addr is null " : wifiP2pInfo.groupOwnerAddress)) + String.valueOf(reconnectRetries));
//                if (reconnectRetries > 8) {
//                    manager.cancelConnect(channel, new WifiP2pManager.ActionListener() {
//                        @Override
//                        public void onSuccess() {
//                            log("connection cancelled");
//                            requestConnectionInfo("cancel connection");
//                        }
//
//                        @Override
//                        public void onFailure(int reason) {
//
//                        }
//                    });
//                }
                reconnectRetries++;
                return;
            }
            handleWifiP2PInfo(wifiP2pInfo);
            log("receivedInfo : " + wifiP2pInfo.groupOwnerAddress.toString() + "\nisOwner? : " + wifiP2pInfo.isGroupOwner);
        }
    };
    public static final WifiP2pManager.ActionListener wifiP2PActionListener = new WifiP2pManager.ActionListener() {
        @Override
        public void onSuccess() {
            log("wifiP2PActionListener success");
        }

        @Override
        public void onFailure(int i) {
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
                            connectToDeviceForConnectToDevice();
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
    public static final P2PBroadcastReceiver.P2PBroadcastReceiverListener p2pBClistener = new P2PBroadcastReceiver.P2PBroadcastReceiverListener() {
        @Override
        public void onDeviceDisconnected() {
            p2PListener.onDevicesDisconnected("not sure");
            log("device disconnected, releasing sockets");
            nullifySockets();
        }

        @Override
        public void onDeviceConnected(WifiP2pInfo info) {
            if (info == null) {
                requestConnectionInfo("onDevConnected");
                return;
            }

            handleWifiP2PInfo(info);
        }

        @Override
        public void onPeersChanged() {
            if (manager != null && !isActive() && !tryingToConnect) {
                if (!isActive())
                    manager.requestPeers(channel, wifiP2PPeerListener);
            }
        }
    };
    private static P2PAdapter adapter;
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
                toast("No peers found, try again in a minute");
                log("No peers found, try again");
                //Todo maybe add start scan here and make sure that oyu do a retry count so it doesn't overflow toasts
            }
        }
    };
    private static boolean requestingPeers;
    private static boolean isSendingMessage, shouldSendFileStraightAfter;

    public P2PManager() {
        super();
    }

    private P2PManager(Activity activity) {
        P2PManager.activity = activity;
        if (isServiceRunning() || isActive() || tryingToConnect)
            return;
        activity.startService(new Intent(activity, P2PManager.class));
    }

    public static Context getContext() {
        return activity;
    }

    public static P2PManager getP2PManager(Activity activity, P2PListener p2PListener) {
        P2PManager.p2PListener = p2PListener;
        if (activity != null) {
            p2PManager = new P2PManager(activity);
        }
        return p2PManager;
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
        dismissDialog();
        stopScan();
        reconnectRetries = 0;
        config.deviceAddress = device.deviceAddress;
        config.groupOwnerIntent = 15;

        manager.connect(channel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                log("connected, requesting connection info");
                dismissDialog();
                requestConnectionInfo("connect to device");
                /*manager.requestConnectionInfo(channel, new WifiP2pManager.ConnectionInfoListener() {
                    @Override
                    public void onConnectionInfoAvailable(WifiP2pInfo info) {
                        if (wifiP2pInfo == null || ((wifiP2pInfo.groupOwnerAddress == null) && !wifiP2pInfo.isGroupOwner)) {
                            if (wifiP2pInfo == null)
                                log("connectToDeviceFailed : wifiInfoNull");
                            else if (((wifiP2pInfo.groupOwnerAddress == null) && !wifiP2pInfo.isGroupOwner))
                                log("connectToDeviceFailed : groupOwnerAddressNull, not own");

                            manager.cancelConnect(channel, new WifiP2pManager.ActionListener() {
                                @Override
                                public void onSuccess() {
                                    connectToDeviceForConnectToDevice();
                                }

                                @Override
                                public void onFailure(int reason) {
                                    connectToDeviceForConnectToDevice();
                                }
                            });

                        }
                    }
                });*/
            }

            @Override
            public void onFailure(int reason) {
                String out = "";
                switch (reason) {
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
                dismissDialog();
                log("failed to connect to " + device.deviceName + " (" + device.deviceAddress + ")" + "because of : " + out);
                disconnectToCurrentDevice();
                connectToDeviceForConnectToDevice();
            }
        });
    }

    private static void connectToDeviceForConnectToDevice() {
        manager.createGroup(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                log("successfully created group, now connecting");
                connectToDevice(clickedDevice);
            }

            @Override
            public void onFailure(int reason) {
                log("failed to create group");
            }
        });
    }

    public static void toast(final String msg) {
        if (activity != null)
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Toast.makeText(activity, msg, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
    }

    public static void requestConnectionInfo(String from) {
        log("requestConnectionInfo : " + from);
        if (!isActive()) {
            manager.requestConnectionInfo(channel, connectionInfoListener);
        } else {
            log("reqInfo : active " + from);
        }
    }

    public static void getClientSocketThreadVoid(final String hostIP) {
        if (isActive()) {
            log("getClientSocketThreadVoid>active");
            return;
        }
        if (getClientSocketThread != null)
            try {
                getClientSocketThread.interrupt();
                getClientSocketThread.join();

            } catch (Exception e) {
                log("failed to interrupt getClientSocket");
                e.printStackTrace();
            }

        tryingToConnect = true;
        getClientSocketThread = new Thread(new Runnable() {
            @Override
            public void run() {
                log("getClientSocket : host > " + hostIP);
                try {
                    if (hostIP.length() < 7 || !hostIP.contains(".")) {
                        log("invalidIp getting connection info > " + hostIP);
                        requestConnectionInfo("getClientThreadVoid : invalid ip > " + hostIP);
                        tryingToConnect = false;
                        return;
                    } else {
                        host = hostIP.replace("/", "").trim();
                        boolean run = true;
                        do {
                            log(String.format("getCliSoc2 socket %s, %s", (socketFromClient == null ? "null" :
                                    (socketFromClient.isConnected() ? "connected" : "not connected")), (run ? "run" : "not run")));
                            try {
                                Thread.sleep(50);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            try {
                                socketFromClient = new Socket(host, 8899);
                            } catch (Exception ee) {
                                log("ras failed to get socket client because : " + ee.toString());
                            }

                            run = ((socketFromClient == null) || (!socketFromClient.isConnected()));

                            if (socketFromClient == null) {
                                log("socket from client == nul");
                            } else if (!socketFromClient.isConnected()) {
                                log("socketFromClient not connected");
                            }

                        }
                        while ((socketFromClient == null || !socketFromClient.isConnected()) && run);
                        startMainThread();
                    }
                } catch (Exception e) {
                    log("crashed (getClientSocketThread)> " + e.toString());
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

    public static void setReadyToReceiveFile() {
        isReceivingFile = true;
    }

    public static void sendFile() {
        log("should send file >> " + FileManagerFragment.tmpFileBeingUploadedPath);
        if (isSendingMessage) {
            shouldSendFileStraightAfter = true;
        } else {
            shouldSendFileStraightAfter = false;
            isWaitingForConfirmation = true;
            if (FileManagerFragment.tmpFileBeingUploadedPath != null)
                uploadFile(FileManagerFragment.tmpFileBeingUploadedPath);
        }
    }

    public static void getServerSocketThreadVoid() {
        if (isActive()) {
            log("getServerSocket, already active> exiting...");
            return;
        }
        tryingToConnect = true;
        if (!(getServerSocketThread == null))
            try {
                getServerSocketThread.interrupt();
                getServerSocketThread.join();
            } catch (Exception e) {
                log("crashed (getServerSocketThreadVoid)> " + e.getMessage());
                e.printStackTrace();
            }

        getServerSocketThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    serverSocket = new ServerSocket();
                    serverSocket.setReuseAddress(true);
                    serverSocket.bind(new InetSocketAddress(8899));

                    do {
                        log("accepting");
                        socketFromServer = serverSocket.accept();
                    } while (socketFromServer == null || !socketFromServer.isConnected());
                    log("stepped past accepting connected >> " + socketFromServer.isConnected());

                    if (!isActive()) {
                        log("not active, request info");
                        Thread.sleep(50);
                        requestConnectionInfo("getServerSocketThreadVoid");
                    } else {
                        log("getServerSocket3");
                        startMainThread();
                    }
                } catch (IOException e) {
                    disconnectToCurrentDevice();
                    requestConnectionInfo("getServerSocket IOException");
                    try {
                        getServerSocketThread.stop();
                        getServerSocketThread.interrupt();

                        return;
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

    public static void checkIfShouldRequestInfoOrConnect() {
        manager.requestPeers(channel, new WifiP2pManager.PeerListListener() {
            @Override
            public void onPeersAvailable(WifiP2pDeviceList peers) {
                for (WifiP2pDevice device : peers.getDeviceList()) {
                    if (device.status == WifiP2pDevice.CONNECTED) {

                    }
                }
            }
        });
    }

    public static void startMainThread() {
        log("should start mainthread");
        tryingToConnect = false;

        if (mainThread == null || (!mainThread.isAlive())) {
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
        //Todo see if you can clean this up
        isSendingMessage = true;
        final boolean isMessageConfirmation = (message.messageType == Message.MessageType.CONFIRMATION);
        if (hasntSentConfirmation && !isMessageConfirmation) {
            sendConfirmation();
            return;
        }

        log("sending Message : current list > " + messages.toString());
        sendSimpleText(message.getSendableMessage());

        isSendingMessage = false;
        if (shouldSendFileStraightAfter)
            sendFile();
        messages.remove(message);
    }

    public static void setConfirmationReceived() {
        isWaitingForConfirmation = false;
    }

    public static void sendConfirmation() {
        log("should send confirmation");
        messages.add(0, new Message(CONFIRMATION, Message.MessageType.CONFIRMATION));
        hasntSentConfirmation = true;
    }

    public static void setIsWaitingForConfirmation() {
        log("isWaitingForConf");
        isWaitingForConfirmation = true;
    }

    private synchronized static boolean sendSimpleText(String text) {
        log("sending Message : " + text);
        try {
            if (text.contains(FileManagerFragment.READY_TO_RECEIVE_FILE)) {
                setReadyToReceiveFile();
            }

            if (outputStream == null) {
                getInputAndOutputStream(getSocket());
            }

            outputStream.write(text.getBytes());
            outputStream.flush();

            if (text.equals(CONFIRMATION)) {
                hasntSentConfirmation = false;
            } else {
                setIsWaitingForConfirmation();
            }

        } catch (IOException e) {
            return false;
        } catch (NullPointerException e) {

        }
        return true;
    }

    private static void getInputAndOutputStream(final Socket socket) {
        try {
            outputStream = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            inputStream = socket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (inputStream == null || outputStream == null) {
            log("tried getting I/O streams, but failed >> I/O null");
        } else {
            if (socket == null) {
                log("tried getting I/O streams, but failed >> socket null");
            } else if (!socket.isConnected()) {
                log("tried getting I/O streams, but failed >> not connected");
            } else if (socket.isClosed()) {
                log("tried getting I/O streams, but failed >> closed");
            }
        }
    }


    public synchronized static boolean uploadFile(String path) {
        isSendingFile = true;
        try {
            final File file = new File(path);
            if (!file.exists()) {
                log("file doesn't exist > " + file.getAbsolutePath());
                return false;
            } else {
                log("uploading > " + file.getAbsolutePath());
            }
            final FileInputStream inputStream = new FileInputStream(file);
            //Todo apply file name
            long uploaded = 0;
            final byte buf[] = new byte[1024];
            int len;
            try {
                if (outputStream == null) {
                    getInputAndOutputStream(getSocket());
                }
                while ((len = inputStream.read(buf)) != -1) {
                    uploaded += 1024;
                    outputStream.write(buf, 0, len);
                    FileManagerFragment.setProgress(100 * ((float) (uploaded / (double) (FileManagerFragment.fileLength))));
                }
                outputStream.flush();
                MainActivity.toast("Finished uploading > " + file.getName());
            } catch (IOException e) {
                log("p2pfailed to copyFile >> " + e.toString());
                return false;
            }
            FileManagerFragment.tmpFileBeingUploadedPath = null;
            isSendingFile = false;
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        isSendingFile = false;
        return false;
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
        LogFragment.log(msg);
    }

    public static Socket getSocket() {
        return (socketFromServer == null) ? socketFromClient : socketFromServer;
    }

    public static boolean isActive() {
        try {
            if (getSocket() != null)
                return getSocket().isConnected() && !(getSocket().isClosed()) && (getSocket().getInputStream().available() >= 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isServiceRunning() {
        final ActivityManager manager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);//use context received in broadcastreceiver
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (P2PManager.class.getName().equals(service.service.getClassName())) return true;
        }
        return false;
    }

    public static void sendSimpleMessage(String message) {
        enqueueMessage(new Message(message, Message.MessageType.MESSAGE));
    }

    public static void registerReceivers() {
        try {
            unRegisterReceivers();
            if (activity != null)
                activity.registerReceiver(receiver, intentFilter);
        } catch (IllegalArgumentException e) {
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
                log("Connecting to " + clickedDevice.deviceName + " (" + clickedDevice.deviceAddress + ")");
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
        try {
//            manager.cancelConnect(channel, actionListener);
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

    public static void handleWifiP2PInfo(WifiP2pInfo wifiP2pInfo) {
        isGroupOwner = wifiP2pInfo.isGroupOwner;
        if (!isActive()) {
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
    }

    public static void stopScan() {
  /* Todo not recommended     try {
            if (MainActivity.connected) {
                MainViewManager.fab.setState(FAB.State.HIDING);
            } else {
                MainViewManager.fab.setState(FAB.State.IDLE);
            }
            manager.stopPeerDiscovery(channel, null);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    public static void startScan() {
        if (isActive())
            return;

        log("starting scan");
        unRegisterReceivers();
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
                    if (p2PListener != null) {
                        p2PListener.onDevicesDisconnected("channel");
                        nullifySockets();
                    }
                }
            });
        if (receiver == null) {
            receiver = new P2PBroadcastReceiver(manager, p2PManager, p2pBClistener);
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

    private static boolean isIO() {
        final Socket socket = getSocket();
        if (socket == null) {
            log("not is IO, socket is null");
            return false;
        }

        if (socket.isClosed()) {
            log("not is IO, socket is closed");
            return false;
        }

        if (!socket.isConnected()) {
            log("not is IO, socket noy connected");
            return false;
        }

        getInputAndOutputStream(socket);

        if (inputStream == null) {
            log("not is IO, inputStream is null");
            return false;
        }

//        try {
//            if (inputStream.read() < 0) {
//                log("not is IO, inputStream < 0");
//                return false;
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
        if (outputStream == null) {
            log("not is IO, inputStream is null");
            return false;
        }


        return true;
    }

    public static void addLocalFileMovedListener(LocalFileMoved LocalFileMovedListener) {
        //Todo loop through these listeners when a upload is done
        try {
            LocalFileMovedListeners.add(LocalFileMovedListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void removeLocalFileMovedListener(LocalFileMoved LocalFileMovedListener) {
        try {
            if (LocalFileMovedListeners.contains(LocalFileMovedListener))
                LocalFileMovedListeners.remove(LocalFileMovedListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void connectedDeviceNullFix() {

        if (!requestingPeers) {
            requestingPeers = true;
            manager.requestPeers(channel, new WifiP2pManager.PeerListListener() {
                @Override
                public void onPeersAvailable(WifiP2pDeviceList peers) {
                    requestingPeers = false;
                    for (WifiP2pDevice device : peers.getDeviceList()) {
                        if (device.status == WifiP2pDevice.CONNECTED) {
                            ((MainActivity) activity).connectedDevice = device;
                            ((MainActivity) activity).setConnected(true);
                        }
                    }
                }
            });
        }
    }

    public static void downloadFile() {
        if (FileManagerFragment.tmpFileBeingDownloadedPath == null) {
            return;
        }
        isReceivingFile = true;

        log("downloadingFile >> " + FileManagerFragment.tmpFileBeingDownloadedPath);

        try {
            final File file = new File(FileManagerFragment.tmpFileBeingDownloadedPath);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    log("failed to create file > " + e.getMessage());
                    e.printStackTrace();
                }
            }
            long downloaded = 0;
            final FileOutputStream out = new FileOutputStream(file);
            final byte buf[] = new byte[1024];
            int len;
            try {
                while ((len = inputStream.read(buf)) != -1) {
                    downloaded += 1024;
                    out.write(buf, 0, len);
                    FileManagerFragment.setProgress(100 * ((float) (downloaded / (double) (FileManagerFragment.fileLength))));
                }

                out.close();
                MainActivity.toast("Finished Downloading > " + file.getName());
            } catch (IOException e) {
                log("failed to send file > " + e.getMessage());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            log("failed to get output file > " + e.getMessage());
        }
        sendConfirmation();
        isReceivingFile = false;

        log("done downloading file");

    }

    public void disconnect() {
        // potentially dangerous
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

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        context = getBaseContext();
        return Service.START_STICKY;//Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        destroy();
        super.onDestroy();
    }

    public interface P2PListener {
        void onScanStarted();

        void onMessageReceived(String msg);

        void onDevicesDisconnected(String reason);

        void onSocketsConfigured();
    }

    public static class MainThreadRunnable implements Runnable {

        @Override
        public void run() {
            log("attempting to start main thread");
            if (!isIO()) {
                requestConnectionInfo("not isIO");
                return;
            }

            dismissDialog();
            if (p2PListener != null)
                p2PListener.onSocketsConfigured();

            listenerThread = new Thread(listenerRunnable);
            listenerThread.start();

            int count = 0;
            while (getSocket() == null) {
                final Socket socket = getSocket();
                try {
                    if (socket != null) {
                        getInputAndOutputStream(socket);
                    }
                    Thread.sleep(50);
                } catch (Exception e) {
                    log("p2pserver " + e.getMessage());
                    return;
                }

                if (count > 12) {
                    if (wifiP2pInfo.groupFormed) {
                        if (wifiP2pInfo.isGroupOwner) {
                            getServerSocketThreadVoid();
                            return;
                        } else {
                            getClientSocketThreadVoid(wifiP2pInfo.groupOwnerAddress.toString());
                            return;
                        }
                    } else {
                        connectToDevice(clickedDevice);
                        return;
                    }
                }
                count++;
            }

            stop = false;
            while (!stop) {
                try {
                    if (inputStream.available() < 0) {
                        if (p2PListener != null) {
                            p2PListener.onDevicesDisconnected("server input less than 0");
                            nullifySockets();
                        }

                        if (wifiP2pInfo.isGroupOwner)
                            getServerSocketThreadVoid();
                        else requestConnectionInfo("main thread");
                        stop = true;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (!isWaitingForConfirmation) {
                    sendMessage();
                }
                try {
                    if (!isReceivingFile && !isSendingMessage && !isSendingFile)
                        Thread.sleep(5);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static class ListenerRunnable implements Runnable {

        public ListenerRunnable() {

        }

        @Override
        public void run() {
            dismissDialog();
            stop = false;

            try {
                getInputAndOutputStream(getSocket());
            } catch (Exception e) {
                log("crashed (P2PClientRunnable InputStream)> " + e.getMessage());
                e.printStackTrace();
            }

            while (!stop) {
                try {
                    int available = inputStream.available();
                    final boolean hadInfoAtTheBeginning = (available > 0);

                    while (messageChunks.size() > 0) {
                        messageChunks.clear();
                    }

                    if (isReceivingFile) {
                        downloadFile();
                    } else {
                        while (available > 0) {
                            log("available " + String.valueOf(available));
                     /* Todo      userInput =stdIn.readLine();
                            if(userInput.Equals("START_MSG");
                            boolean reading=true;
                            while(reading)
                            {
                                userInput=stdIn.readLine();
                                if(userInput.Equals("END_MSG")
                                {
                                    //END LOOP!
                                    reading = false;
                                }
                                else
                                {
                                    //You have received a msg - do what you want here
                                }
                            }*/
                            final byte[] msg = new byte[available];
                            final int input = inputStream.read(msg, 0, available);
                            if (input < 0) {
                                if (p2PListener != null) {
                                    p2PListener.onDevicesDisconnected("server input less than 0");
                                    nullifySockets();
                                }

                                if (wifiP2pInfo.isGroupOwner)
                                    getServerSocketThreadVoid();
                                else requestConnectionInfo("server thread");
                                stop = true;
                                break;
                            } else {
                                messageChunks.add(msg);
                            }
                            available = inputStream.available();
                        }

                        if (hadInfoAtTheBeginning) {
                            final StringBuilder builder = new StringBuilder();

                            for (byte[] messageChunk : messageChunks) {
                                builder.append(new String(messageChunk));
                            }

                            final String message = builder.toString();
                            log("inputMsg = " + message);
                            if (message.equals(CONFIRMATION)) {
                                setConfirmationReceived();
                            } else if (message.contains(CONFIRMATION)) {
                                setConfirmationReceived();
                                sendConfirmation();
                                final String[] split = message.split(CONFIRMATION);
                                for (String s : split) {
                                    if (p2PListener != null) {
                                        p2PListener.onMessageReceived(s);
                                    } else {
                                        log("len : " + messageChunks.size() + " , listener : " + (p2PListener == null));
                                    }
                                }
                            } else {
                                sendConfirmation();
                                if (p2PListener != null) {
                                    p2PListener.onMessageReceived(message);
                                } else {
                                    log("len : " + messageChunks.size() + " , listener : " + (p2PListener == null));
                                }
                            }
                        }

                        try {
                            Thread.sleep(5);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
//                    log("crashed (P2PClientRunnable errthang)> " + e.toString());
//                    e.printStackTrace();
                }
            }

            log("p2PListener stopping");
        }
    }

    public static class P2PAdapter extends BaseAdapter {
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
            if (view == null)
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
