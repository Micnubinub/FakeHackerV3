package tbs.fakehackerv3.fragments;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import tbs.fakehackerv3.R;

/**
 * Created by Michael on 7/10/2015.
 */
public class ContactsFragment extends Fragment {
    @Nullable
    @Override
    public View getView() {
        //Todo
        final View v = View.inflate(getActivity(), R.layout.file_manager_fragment, null);
        return v;
    }
}
