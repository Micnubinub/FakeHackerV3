package tbs.fakehackerv3;

import android.app.Activity;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import tbs.fakehackerv3.fragments.CustomAndDownloadedCommands;
import tbs.fakehackerv3.fragments.Messaging;
import tbs.fakehackerv3.fragments.OnlineRepo;
import tbs.fakehackerv3.fragments.Remote;
import tbs.fakehackerv3.fragments.Settings;


public class MainActivity extends FragmentActivity {

    public static WifiP2pDevice connectedDevice;

    private static final P2PManager.P2PListener p2pListener = new P2PManager.P2PListener() {
        @Override
        public void onScanStarted() {
            log("scanning");
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "Scanning", Toast.LENGTH_LONG).show();
                }
            });
        }

        @Override
        public void onMessageReceived(String msg) {
            log("message received : " + msg);
            toast("message received : " + msg);
            setConnected(true);
            final String[] received = msg.split(Message.MESSAGE_SEPARATOR, 3);
            if (received[0].equals("")) {

            }
            //TODO split them up into the different categories then
          /*Todo  messages.add(new ReceivedMessage(received[1], "RECEIVED : " + received[2], "random"));
            notifyDataSetChanged();
            Log.e("notified", "msg");*/
        }

        @Override
        public void onDevicesDisconnected(String reason) {
            log("disconnected because of : " + reason);
            toast("disconnected because of : " + reason);
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    MainViewManager.setStaticText("Not connected");
                    MainViewManager.setConnectedToDevice("");
                }
            });
            setConnected(true);
            nullifyGroupAndDevice();

        }

        @Override
        public void onSocketsConfigured() {
            log("socket configured");
            setConnected(true);
            P2PManager.manager.requestConnectionInfo(P2PManager.channel, new WifiP2pManager.ConnectionInfoListener() {
                @Override
                public void onConnectionInfoAvailable(final WifiP2pInfo info) {
                    if (info.isGroupOwner) {
                        P2PManager.manager.requestPeers(P2PManager.channel, new WifiP2pManager.PeerListListener() {
                            @Override
                            public void onPeersAvailable(WifiP2pDeviceList peers) {
                                String out = "connected devices : ";
                                for (WifiP2pDevice device : peers.getDeviceList()) {
                                    if (device.status == WifiP2pDevice.CONNECTED) {
                                        toast("Connected : " + device.deviceName);
                                        P2PManager.manager.requestConnectionInfo(P2PManager.channel, new WifiP2pManager.ConnectionInfoListener() {
                                            @Override
                                            public void onConnectionInfoAvailable(final WifiP2pInfo info) {

                                                if (info.isGroupOwner) {
                                                    P2PManager.manager.requestPeers(P2PManager.channel, new WifiP2pManager.PeerListListener() {
                                                        @Override
                                                        public void onPeersAvailable(WifiP2pDeviceList peers) {
                                                            String out = "connected devices : ";
                                                            for (WifiP2pDevice wifiP2pDevice : peers.getDeviceList()) {
                                                                if (wifiP2pDevice.status == WifiP2pDevice.CONNECTED) {
                                                                    out += wifiP2pDevice.deviceName + " (" + wifiP2pDevice.deviceAddress + "),";
                                                                }
                                                            }
                                                            log("connection info from onDeviceConnected : ");
                                                            log("ownerAdd : " + info.groupOwnerAddress + ", isOwner : " + info.isGroupOwner + ", isGroupFormed : " + info.groupFormed);
                                                            log(out);
                                                        }
                                                    });
                                                }
                                            }
                                        });
                                        connectedDevice = device;
                                    }

                                    out += device.deviceName + " (" + device.deviceAddress + "),";

                                }
                                log("connection info from onDeviceConnected : ");
                                log("ownerAdd : " + info.groupOwnerAddress + ", isOwner : " + info.isGroupOwner + ", isGroupFormed : " + info.groupFormed);
                                log(out);
                            }
                        });
                    }
                }
            });
            /*Todo messageEditText.setEnabled(true);*/
        }
    };
    public static P2PManager p2PManager;
    public static Activity context;
    public static FragmentManager fragmentManager;
    public static FragmentTransaction fragmentTransaction;
    public static MainViewManager mainViewManager;
    //Fragments
    public static CustomAndDownloadedCommands customAndDownloadedCommands;
    public static OnlineRepo onlineRepo;
    public static Remote remote;
    public static Messaging messaging;
    public static Settings settings;
    public static boolean connected;
    public static WifiP2pGroup currentGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        p2PManager = P2PManager.getP2PManager(this, p2pListener);
        setContentView(R.layout.main_view);
        mainViewManager = new MainViewManager(findViewById(R.id.main_view));

        // get fragment manager
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

//        showDialog();
    }

    public static void addFragment(Fragment fragment) {
        if (fragment.isInLayout())
            return;
        fragmentTransaction.add(R.id.container, fragment);
        try {
            fragmentTransaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void replaceFragment(Fragment fragment) {
        fragmentTransaction.replace(R.id.container, fragment);
        try {
            fragmentTransaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setConnected(final boolean connected) {
        if (MainActivity.connected != connected) {
            if (connected) {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (connectedDevice != null) {
                            MainViewManager.setStaticText("Connected to");
                            MainViewManager.setConnectedToDevice(connectedDevice.deviceName + " (" + connectedDevice.deviceAddress + ")");
                            addFragment(getMessaging());
                        } else {
                            P2PManager.connectedDeviceNullFix();
                            log("Connected device is null");
                            toast("Connected device is null");
                        }
                    }
                });
            } else {
                MainViewManager.setStaticText("Not connected");
                MainViewManager.setConnectedToDevice("");
                replaceFragment(new Fragment());
                toast("disConnected");
            }
        }
    }

    public static void handleReceivedCommand(String command) {
        final String[] splitCommand = command.split(Message.MESSAGE_SEPARATOR);
        final String commandString = splitCommand[2];
        //TODO check all these
        if (splitCommand[1].contains(StaticValues.SCHEDULED_RECORDING)) {
            final long when = Long.parseLong(commandString);
            final int duration = Integer.parseInt(splitCommand[3]);
            RemoteTools.record(duration, when);
        } else if (splitCommand[1].contains(StaticValues.SCHEDULED_COMMAND)) {
            final long when = Long.parseLong(commandString);
            //todo
        } else if (splitCommand[1].contains(StaticValues.TOGGLE_TORCH)) {
            RemoteTools.toggleTorch();
        } else if (splitCommand[1].contains(StaticValues.PRESS_HOME)) {
            //todo
        } else if (splitCommand[1].contains(StaticValues.PRESS_BACK)) {
            //todo
        } else if (splitCommand[1].contains(StaticValues.PRESS_MENU)) {
            //todo
        } else if (splitCommand[1].contains(StaticValues.PRESS_VOLUME_UP)) {
            //todo
        } else if (splitCommand[1].contains(StaticValues.PRESS_VOLUME_DOWN)) {
            //todo
        } else if (splitCommand[1].contains(StaticValues.GET_FOLDER_TREE)) {
            try {
                final File file = new File(commandString);
                if (!file.exists()) {
                } else if (!file.isDirectory()) {

                } else {
                    //todo
                    file.listFiles();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (splitCommand[1].contains(StaticValues.OPEN_FILE)) {
            try {
                final File file = new File(commandString);
                if (!file.exists()) {
                } else if (!file.isDirectory()) {
                } else {
                    //todo
                    file.listFiles();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (splitCommand[1].contains(StaticValues.RECORD_AUDIO)) {
            try {
                RemoteTools.record(Integer.parseInt(commandString));
            } catch (NumberFormatException e) {
                log("record failed > not a number");
                e.printStackTrace();
            }
        } else if (splitCommand[1].contains(StaticValues.SET_ALARM_VOLUME)) {
            try {
                RemoteTools.setVolumeAlarm(Integer.parseInt(commandString));
            } catch (NumberFormatException e) {
                log("set alarm failed > not a number");
                e.printStackTrace();
            }
        } else if (splitCommand[1].contains(StaticValues.TAKE_PICTURE_BACK)) {
            RemoteTools.takePictureBack();
        } else if (splitCommand[1].contains(StaticValues.TAKE_PICTURE_FRONT)) {
            RemoteTools.takePictureFront();
        } else if (splitCommand[1].contains(StaticValues.SET_TORCH)) {
            //todo add this
        } else if (splitCommand[1].contains(StaticValues.GET_FILE_DETAILS)) {
            try {
                final File file = new File(commandString);
                if (!file.exists()) {

                } else {
                    //todo
                    file.length();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (splitCommand[1].contains(StaticValues.DELETE_FILE)) {
            try {
                final File file = new File(commandString);
                if (!file.exists()) {
                } else if (!file.isDirectory()) {

                } else {
                    //todo
                    file.delete();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (splitCommand[1].contains(StaticValues.DOWNLOAD_FILE)) {
            final File file = new File(commandString);
            if (!file.exists()) {

            } else {
                if (MainActivity.p2PManager != null)
                    P2PManager.enqueueMessage(new Message(file.getAbsolutePath(), Message.MessageType.SEND_FILE));
            }
        } else if (splitCommand[1].contains(StaticValues.MOVE_FILE)) {
            final File file = new File(commandString);
            final File toLocation = new File(splitCommand[3]);
            final File out = new File(splitCommand[3] + file.getName());
            if (!file.exists() || !toLocation.exists()) {
            } else if (!toLocation.isDirectory()) {

            } else {
                //todo
                file.renameTo(out);
            }
        } else if (splitCommand[1].contains(StaticValues.STREAM_FILE)) {
//todo
        } else if (splitCommand[1].contains(StaticValues.CREATE_DIRECTORY)) {
            final File file = new File(commandString);
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    log("createfile failed > ioexeption");
                    e.printStackTrace();
                }
            }
        } else if (splitCommand[1].contains(StaticValues.TAKE_SCREENSHOT)) {
            RemoteTools.getScreenShot();
        } else if (splitCommand[1].contains(StaticValues.SET_MEDIA_VOLUME)) {
            try {
                RemoteTools.setVolumeMedia(Integer.parseInt(commandString));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } else if (splitCommand[1].contains(StaticValues.SET_NOTIFICATION_VOLUME)) {
            try {
                RemoteTools.setVolumeNotification(Integer.parseInt(commandString));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } else if (splitCommand[1].contains(StaticValues.SET_RINGER_VOLUME)) {
            try {
                RemoteTools.setVolumeRinger(Integer.parseInt(commandString));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } else if (splitCommand[1].contains(StaticValues.SET_BRIGHTNESS)) {
            try {
                RemoteTools.setBrightness(Integer.parseInt(commandString));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } else if (splitCommand[1].contains(StaticValues.SET_BRIGHTNESS_MODE)) {
            try {
                RemoteTools.setBrightnessAuto(Integer.parseInt(commandString) > 0);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } else if (splitCommand[1].contains(StaticValues.SET_BLUETOOTH)) {
            try {
                RemoteTools.setBluetooth(Integer.parseInt(commandString) > 0);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } else if (splitCommand[1].contains(StaticValues.SPOOF_TOUCH)) {
            //todo
        } else if (splitCommand[1].contains(StaticValues.SPOOF_TOUCH_FINGER_2)) {
            //todo
        } else if (splitCommand[1].contains(StaticValues.RECORD_VIDEO_FRONT)) {
            //todo
        } else if (splitCommand[1].contains(StaticValues.RECORD_VIDEO_BACK)) {
            //todo
        } else if (splitCommand[1].contains(StaticValues.MEDIA_CONTROL_SKIP)) {
            RemoteTools.skipTrack();
        } else if (splitCommand[1].contains(StaticValues.MEDIA_CONTROL_PREVIOUS)) {
            RemoteTools.previousTrack();
        } else if (splitCommand[1].contains(StaticValues.MEDIA_CONTROL_PLAY_PAUSE)) {
            RemoteTools.playMusic();
        }
    }

    public static void nullifyGroupAndDevice() {
        connectedDevice = null;
        currentGroup = null;
    }

    public static Settings getSettings() {
        if (settings == null) {
            settings = new Settings();
        }
        return settings;
    }

    public static OnlineRepo getOnlineRepo() {
        if (onlineRepo == null) {
            onlineRepo = new OnlineRepo();
        }
        return onlineRepo;
    }

    public static CustomAndDownloadedCommands getCustomAndDownloadedCommands() {
        if (customAndDownloadedCommands == null) {
            customAndDownloadedCommands = new CustomAndDownloadedCommands();
        }
        return customAndDownloadedCommands;
    }

    public static Messaging getMessaging() {
        if (messaging == null) {
            messaging = new Messaging();
        }
        return messaging;
    }

    public static Remote getRemote() {
        if (remote == null) {
            remote = new Remote();
        }
        return remote;
    }

    private static void log(String msg) {
        Log.e("main", msg);
    }

    public static void toast(final String msg) {
        if (context != null) {
            try {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        P2PManager.destroy();
        super.onDestroy();
    }

//    public static void showDialog() {
//        final Dialog dialog = new Dialog(context, R.style.CustomDialog);
//        dialog.setContentView(R.layout.store_gridview);
//        dialog.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//
//        final ArrayList<StoreItem> storeItems = new ArrayList<StoreItem>();
//        storeItems.add(new StoreItem("item1", R.drawable.cancel));
//        storeItems.add(new StoreItem("item2", R.drawable.hamburger));
//        storeItems.add(new StoreItem("item1", R.drawable.cancel));
//        storeItems.add(new StoreItem("item3", R.drawable.cancel));
//        storeItems.add(new StoreItem("item1", R.drawable.tick));
//        storeItems.add(new StoreItem("item21", R.drawable.cancel));
//        storeItems.add(new StoreItem("item13", R.drawable.send));
//        storeItems.add(new StoreItem("item11", R.drawable.cancel));
//        storeItems.add(new StoreItem("item12", R.drawable.bg_circle));
//        storeItems.add(new StoreItem("item", R.drawable.cancel));
//        storeItems.add(new StoreItem("item1232", R.drawable.refresh));
//        storeItems.add(new StoreItem("item12", R.drawable.tick_green));
//        storeItems.add(new StoreItem("item132", R.drawable.cancel));
//        storeItems.add(new StoreItem("item21", R.drawable.cancel));
//        storeItems.add(new StoreItem("item13", R.drawable.send));
//        storeItems.add(new StoreItem("item11", R.drawable.cancel));
//        storeItems.add(new StoreItem("item12", R.drawable.bg_circle));
//        storeItems.add(new StoreItem("item", R.drawable.cancel));
//        storeItems.add(new StoreItem("item1232", R.drawable.refresh));
//        storeItems.add(new StoreItem("item12", R.drawable.tick_green));
//        storeItems.add(new StoreItem("item132", R.drawable.cancel));
//        storeItems.add(new StoreItem("item91", R.drawable.scanning));
//
//        final GridView gridView = (GridView) dialog.findViewById(R.id.grid);
//        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                toast(storeItems.get(position).text);
//            }
//        });
//        gridView.setAdapter(new StoreAdapter(storeItems));
//        dialog.show();
//    }
//
//    private static class StoreItem {
//        public final int drawableId;
//        public final String text;
//
//        public StoreItem(String text, int drawableId) {
//            this.text = text;
//            this.drawableId = drawableId;
//        }
//    }
//
//    public static class StoreAdapter extends BaseAdapter {
//        private ArrayList<StoreItem> items;
//
//        public StoreAdapter(ArrayList<StoreItem> storeItems) {
//            items = storeItems;
//        }
//
//        @Override
//        public int getCount() {
//            return items == null ? 0 : items.size();
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return null;
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return 0;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            if (convertView == null) {
//                convertView = View.inflate(context, R.layout.store_item, null);
//            }
//
//            ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
//            TextView textView = (TextView) convertView.findViewById(R.id.text);
//            final StoreItem item = items.get(position);
//
//            icon.setImageResource(item.drawableId);
//            textView.setText(item.text);
//            return convertView;
//        }
//    }
}
