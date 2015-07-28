package tbs.fakehackerv3;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

/**
 * Created by Michael on 5/12/2015.
 */
public class P2PBroadcastReceiver extends BroadcastReceiver {
    public static WifiP2pManager wifiP2pManager;
    private static P2PManager p2PManager;
    private static P2PBroadcastReceiverListener listener;

    public P2PBroadcastReceiver(WifiP2pManager manager, P2PManager p2PManager, P2PBroadcastReceiverListener listener) {
        this.wifiP2pManager = manager;
        P2PBroadcastReceiver.p2PManager = p2PManager;
        this.listener = listener;
    }

    public static void log(String msg) {
        // MainActivity.addLog(msg);
//        LogFragment.log(msg);
        Log.e("p2p", "broadcast : " + msg);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            final int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state != WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                if (p2PManager != null)
                    P2PManager.toast("Please enable Wifi Direct");
            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            if (listener != null) {
                listener.onPeersChanged();
            }
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            // Respond to new connection or disconnections
            WifiP2pInfo wifiInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_INFO);
            long start = System.currentTimeMillis();
            while (wifiInfo == null) {
                wifiInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_INFO);
                if (start < (System.currentTimeMillis() - 2000))
                    break;
            }

            final NetworkInfo networkState = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            log("networkState : " + networkState.toString());


            if (networkState.isConnected() && !P2PManager.isActive() && !P2PManager.tryingToConnect) {
                if (listener != null) {
                    listener.onDeviceConnected(wifiInfo);
                }
            } else {
                if (listener != null) {
                    listener.onDeviceDisconnected();
                }
            }
        }
    }

    public interface P2PBroadcastReceiverListener {
        void onDeviceDisconnected();

        void onDeviceConnected(WifiP2pInfo info);

        void onPeersChanged();
    }
}
