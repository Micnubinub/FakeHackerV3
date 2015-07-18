package tbs.fakehackerv3.fragments;

import android.support.v4.app.Fragment;
import android.view.View;

import tbs.fakehackerv3.MainActivity;
import tbs.fakehackerv3.P2PManager;

/**
 * Created by Michael on 7/18/2015.
 */
public abstract class P2PFragment extends Fragment {
    public static final View.OnClickListener placeHolderListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!P2PManager.isActive()) {
                MainActivity.toast("click the refresh button on both devices to connect");
                return;
            }

            v.setVisibility(View.GONE);
        }
    };
    public View placeholder;
    protected boolean isInit;

    public abstract void onP2PDisconnected();

    public abstract void onP2PConnected();
}
