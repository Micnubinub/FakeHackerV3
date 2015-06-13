package tbs.fakehackerv3.fragments;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import tbs.fakehackerv3.R;

/**
 * Created by Michael on 6/14/2015.
 */
public class Fragments extends Fragment {

    @Nullable
    @Override
    public View getView() {
        //Todo
        final View view = View.inflate(getActivity(), R.layout.message_item, null);
        return view;
    }
}
