package tbs.fakehackerv3.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

import tbs.fakehackerv3.R;

/**
 * Created by Michael on 6/14/2015.
 */
public class Fragments extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Todo
        final View view = inflater.inflate(R.layout.message_item, null);

        return view;
    }

}
