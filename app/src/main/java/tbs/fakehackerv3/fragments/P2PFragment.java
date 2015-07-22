package tbs.fakehackerv3.fragments;

import android.support.v4.app.Fragment;
import android.view.View;

/**
 * Created by Michael on 7/18/2015.
 */
public abstract class P2PFragment extends Fragment {
    public View placeholder;
    protected boolean isInit;

    public abstract void onP2PDisconnected();

    public abstract void onP2PConnected();
}
