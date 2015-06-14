package tbs.fakehackerv3.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import tbs.fakehackerv3.MainActivity;
import tbs.fakehackerv3.R;

/**
 * Created by Michael on 6/14/2015.
 */
public class CustomAndDownloadedCommands extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Todo
        final View view = View.inflate(getActivity(), R.layout.message_item, null);
        return view;
    }

    public static void toast(final String msg) {
        if (MainActivity.context != null) {
            try {
                MainActivity.context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.context, msg, Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}