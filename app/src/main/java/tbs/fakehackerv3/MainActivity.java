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
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.Toast;

import tbs.fakehackerv3.custom_views.PagerSlidingTabStrip;
import tbs.fakehackerv3.fragments.CustomAndDownloadedCommands;
import tbs.fakehackerv3.fragments.FileManagerFragment;
import tbs.fakehackerv3.fragments.LogFragment;
import tbs.fakehackerv3.fragments.MessagingFragent;
import tbs.fakehackerv3.fragments.OnlineRepo;
import tbs.fakehackerv3.fragments.RemoteFragment;
import tbs.fakehackerv3.fragments.Settings;


public class MainActivity extends FragmentActivity {

    private static final Fragment[] fragments = new Fragment[4];
    public static WifiP2pDevice connectedDevice;
    public static P2PManager p2PManager;
    public static Activity context;
    public static MainViewManager mainViewManager;
    //Fragments
    public static CustomAndDownloadedCommands customAndDownloadedCommands;
    public static OnlineRepo onlineRepo;
    public static Settings settings;
    public static boolean connected;
    public static WifiP2pGroup currentGroup;
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
            final String[] received = msg.split(Message.MESSAGE_SEPARATOR, 2);
            if (received[0].equals(String.valueOf(Message.MessageType.COMMAND))) {
                RemoteFragment.handleReceivedCommand(received[1]);
            } else if (received[0].equals(String.valueOf(Message.MessageType.MESSAGE))) {
                MessagingFragent.handleReceivedMessage(received[1]);
            } else if (received[0].equals(String.valueOf(Message.MessageType.FILE))) {
                FileManagerFragment.handleMessage(received[1]);
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
            setConnected(false);
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
    private static PagerSlidingTabStrip tabs;
    private static ViewPager pager;
    private static MyPagerAdapter pagerAdapter;

    public static void setConnected(final boolean connected) {
        if (MainActivity.connected != connected) {
            if (connected) {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (connectedDevice != null) {
                            MainViewManager.setStaticText("Connected to");
                            MainViewManager.setConnectedToDevice(connectedDevice.deviceName + " (" + connectedDevice.deviceAddress + ")");
                            for (Fragment fragment : fragments) {
                                if (fragment instanceof MessagingFragent) {
                                    ((MessagingFragent) fragment).init();
                                } else if (fragment instanceof FileManagerFragment) {
                                    ((FileManagerFragment) fragment).init();
                                } else if (fragment instanceof RemoteFragment) {
                                    ((RemoteFragment) fragment).init();
                                }
                            }
                        } else {
                            P2PManager.connectedDeviceNullFix();
                        }

                    }
                });
            } else {
                MainViewManager.setStaticText("Not connected");
                MainViewManager.setConnectedToDevice("");
                toast("disConnected");
            }
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

    private static void log(String msg) {
        Log.e("main", msg);
        LogFragment.log(msg);
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        p2PManager = P2PManager.getP2PManager(this, p2pListener);
        setContentView(R.layout.main_view);
        mainViewManager = new MainViewManager(findViewById(R.id.main_view));

        setUpFragments();
//        showDialog();
    }

    @Override
    protected void onDestroy() {
        P2PManager.destroy();
        super.onDestroy();
    }

    private void setUpFragments() {
        fragments[0] = new MessagingFragent();
        fragments[1] = new RemoteFragment();
        fragments[2] = new FileManagerFragment();
        fragments[3] = new LogFragment();

        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        pager = (ViewPager) findViewById(R.id.view_pager);
        pager.setOffscreenPageLimit(4);

        pagerAdapter = new MyPagerAdapter(getSupportFragmentManager());

        pager.setAdapter(pagerAdapter);
        tabs.setViewPager(pager);

    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        private final String[] TITLES = {"Messaging", "Remote", "File Manager", "Log"};

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }
    }

}
