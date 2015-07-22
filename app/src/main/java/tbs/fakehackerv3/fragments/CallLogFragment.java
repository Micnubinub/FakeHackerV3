package tbs.fakehackerv3.fragments;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import tbs.fakehackerv3.R;
import tbs.fakehackerv3.console.CallLogItem;

/**
 * Created by Michael on 7/10/2015.
 */
public class CallLogFragment extends P2PFragment {
    private static final String callLogColumns[] = {
            CallLog.Calls._ID,
            CallLog.Calls.NUMBER,
            CallLog.Calls.DATE,
            CallLog.Calls.DURATION,
            CallLog.Calls.TYPE};

    public static ArrayList<CallLogItem> getCallLog(Context context) {
        final Cursor c = context.getContentResolver().query(Uri.parse("content://call_log/calls"),
                callLogColumns, null, null, "Calls._ID DESC"); //last record first

        final ArrayList<CallLogItem> callLogItems = new ArrayList<CallLogItem>(c.getCount());
        while (c.moveToNext()) {
            final long id = c.getLong(c.getColumnIndex(CallLog.Calls._ID));
            final long date = c.getLong(c.getColumnIndex(CallLog.Calls.DATE));
            final String number = c.getString(c.getColumnIndex(CallLog.Calls.NUMBER));
            final long duration = c.getLong(c.getColumnIndex(CallLog.Calls.DURATION));
            final String type = c.getString(c.getColumnIndex(CallLog.Calls.TYPE));
            callLogItems.add(new CallLogItem(number, type, date, duration));
        }

        Log.e("Call Logs > ", callLogItems.toString());

        return callLogItems;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.file_manager_fragment, null);
        placeholder = v.findViewById(R.id.placeholder);
        placeholder.setOnClickListener(placeHolderListener);
        getCallLog(getActivity());
        return v;
    }

    @Override
    public void onP2PDisconnected() {
        placeholder.post(new Runnable() {
            @Override
            public void run() {
                placeholder.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onP2PConnected() {
        placeholder.post(new Runnable() {
            @Override
            public void run() {
                placeholder.setVisibility(View.GONE);
            }
        });
    }
}
