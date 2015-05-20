package tbs.fakehackerv3;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;


public class MainActivity extends ActionBarActivity {
    public static P2PManager p2PManager;
    public static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        p2PManager = new P2PManager(this, new P2PManager.P2PListener() {
            @Override
            public void onScanStarted() {

            }

            @Override
            public void onMessageReceived(String msg) {
                Tools.handleReceiveFile(msg);
            }

            @Override
            public void onDevicesConnected() {

            }

            @Override
            public void onDevicesDisconnected() {

            }

            @Override
            public void onSocketsConfigured() {

            }
        }, true);
    }

}
