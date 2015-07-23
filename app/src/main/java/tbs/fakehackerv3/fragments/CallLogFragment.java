package tbs.fakehackerv3.fragments;

import android.app.Activity;
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
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import tbs.fakehackerv3.MainActivity;
import tbs.fakehackerv3.Message;
import tbs.fakehackerv3.P2PManager;
import tbs.fakehackerv3.R;
import tbs.fakehackerv3.StaticValues;
import tbs.fakehackerv3.console.CallLogItem;

/**
 * Created by Michael on 7/10/2015.
 */
public class CallLogFragment extends P2PFragment {
    public static final View.OnClickListener placeHolderListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!P2PManager.isActive()) {
                MainActivity.toast("click the refresh button on both devices to connect");
                return;
            }
            requestCallLog();
            v.setVisibility(View.GONE);
        }
    };
    private static final String callLogColumns[] = {
            CallLog.Calls._ID,
            CallLog.Calls.NUMBER,
            CallLog.Calls.DATE,
            CallLog.Calls.DURATION,
            CallLog.Calls.TYPE};
    private static Activity context;
    private static ListView listView;
    private static View placeHolder;

    public static void requestCallLog() {
        P2PManager.enqueueMessage(new Message(String.valueOf(Message.MessageType.COMMAND) + Message.MESSAGE_SEPARATOR + StaticValues.GET_CALL_LOG, Message.MessageType.COMMAND));
    }

    public static void parseReceivedData(final String data) {
        //Todo notify data set changed
        ;
        try {
            listView.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        listView.setAdapter(new CallLogAdapter(CallLogItem.getCallLogItems(data)));
                        listView.invalidate();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    placeHolder.setVisibility(View.GONE);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static String getFormatedData() {
        final StringBuilder builder = new StringBuilder();

        final Cursor c = context.getContentResolver().query(Uri.parse("content://call_log/calls"),
                callLogColumns, null, null, "Calls._ID DESC"); //last record first

        if (c.moveToFirst()) {
            do {
//                final long id = c.getLong(c.getColumnIndex(CallLog.Calls._ID));
                final long date = c.getLong(c.getColumnIndex(CallLog.Calls.DATE));
                final String number = c.getString(c.getColumnIndex(CallLog.Calls.NUMBER));
                final long duration = c.getLong(c.getColumnIndex(CallLog.Calls.DURATION));
                final String type = c.getString(c.getColumnIndex(CallLog.Calls.TYPE));

                builder.append(date);
                builder.append("//");
                builder.append(number);
                builder.append("//");
                builder.append(duration);
                builder.append("//");
                builder.append(type);

                if (!c.isLast())
                    builder.append(":/:/");
            } while (c.moveToNext());
        } else {
            //empty
        }
        return builder.toString();
    }

    public static ArrayList<CallLogItem> getCallLog(Context context) {
        final Cursor c = context.getContentResolver().query(Uri.parse("content://call_log/calls"),
                callLogColumns, null, null, "Calls._ID DESC"); //last record first

        final ArrayList<CallLogItem> callLogItems = new ArrayList<CallLogItem>(c.getCount());
        while (c.moveToNext()) {
//            final long id = c.getLong(c.getColumnIndex(CallLog.Calls._ID));
            final long date = c.getLong(c.getColumnIndex(CallLog.Calls.DATE));
            final String number = c.getString(c.getColumnIndex(CallLog.Calls.NUMBER));
            final long duration = c.getLong(c.getColumnIndex(CallLog.Calls.DURATION));
            final String type = c.getString(c.getColumnIndex(CallLog.Calls.TYPE));
            callLogItems.add(new CallLogItem(number, type, date, duration));
        }

        Log.e("Call Logs > ", callLogItems.toString());

        return callLogItems;
    }

    public static void handleConsoleCommand(String command) {
        if (!P2PManager.isActive()) {
            MainActivity.toast("click the refresh button on both devices to connect");
            return;
        }
        requestCallLog();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.text_message_fragment, null);
        placeholder = v.findViewById(R.id.placeholder);
        placeholder.setOnClickListener(placeHolderListener);
        placeHolder = placeholder;
        listView = (ListView) v.findViewById(R.id.list);
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

    }

    private static class CallLogAdapter extends BaseAdapter {
        final ArrayList<CallLogItem> callLogItems;

        public CallLogAdapter(ArrayList<CallLogItem> callLogItems) {
            this.callLogItems = callLogItems;
        }

        @Override
        public int getCount() {
            return callLogItems.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;

            if (convertView == null) {
                convertView = View.inflate(context, R.layout.call_log_item, null);
                final TextView from_to = (TextView) convertView.findViewById(R.id.from_to);
                final TextView duration = (TextView) convertView.findViewById(R.id.duration);
                final TextView type = (TextView) convertView.findViewById(R.id.type);
                final TextView date = (TextView) convertView.findViewById(R.id.date);
                holder = new ViewHolder(from_to, duration, type, date);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final CallLogItem item = callLogItems.get(position);
            holder.duration.setText(String.valueOf(item.duration) + "seconds");
            holder.type.setText(item.type);
            holder.date.setText(item.getDate());
            holder.from_to.setText(item.number);
            return convertView;
        }

        private static class ViewHolder {
            final TextView from_to, duration, type, date;

            public ViewHolder(TextView from_to, TextView duration, TextView type, TextView date) {
                this.from_to = from_to;
                this.duration = duration;
                this.type = type;
                this.date = date;
            }
        }
    }
}
