package tbs.fakehackerv3.fragments;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import tbs.fakehackerv3.console.TextMessageItem;

/**
 * Created by Michael on 7/10/2015.
 */
public class MessageReaderFragment extends P2PFragment {
    public static final View.OnClickListener placeHolderListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!P2PManager.isActive()) {
                MainActivity.toast("click the refresh button on both devices to connect");
                return;
            }
            requestTexts();
            v.setVisibility(View.GONE);
        }
    };
    private static final String[] columns = {"ADDRESS", "BODY", "DATE"};
    private static Activity context;
    private static ListView listView;
    private static View placeHolder;

    public static void requestTexts() {
        P2PManager.enqueueMessage(new Message(String.valueOf(Message.MessageType.COMMAND) + Message.MESSAGE_SEPARATOR + StaticValues.GET_TEXTS, Message.MessageType.COMMAND));
    }

    public static void parseReceivedData(final String data) {

        try {
            listView.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        listView.setAdapter(new MessageReaderAdapter(TextMessageItem.getTextMessageItems(data)));
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
        final Cursor cursor = context.getContentResolver().query(Uri.parse("content://sms/inbox"), columns, null, null, null);
        if (cursor.moveToFirst()) { // must check the result to prevent exception
            do {
                final String body = cursor.getString(cursor.getColumnIndex("BODY"));
                final String address = cursor.getString(cursor.getColumnIndex("ADDRESS"));
                final String date = cursor.getString(cursor.getColumnIndex("DATE"));

                builder.append(address);
                builder.append("//");
                builder.append(body);
                builder.append("//");
                builder.append(date);

                if (!cursor.isLast()) {
                    builder.append(":/:/");
                }
            } while (cursor.moveToNext());
        } else {
            // empty box, no SMS
        }
        return builder.toString();
    }

    public static final ArrayList<TextMessageItem> getSMS() {
        final ArrayList<TextMessageItem> messageItems = new ArrayList<TextMessageItem>();
        if (context == null)
            return null;
        final Cursor cursor = context.getContentResolver().query(Uri.parse("content://sms/inbox"), columns, null, null, null);
        messageItems.ensureCapacity(cursor.getCount());
        if (cursor.moveToFirst()) { // must check the result to prevent exception
            do {
                String msgData = "";
                for (int idx = 0; idx < cursor.getColumnCount(); idx++) {
                    msgData += " " + cursor.getColumnName(idx) + ":" + cursor.getString(idx);
                }
                // use msgData
            } while (cursor.moveToNext());
        } else {
            // empty box, no SMS
        }

        return messageItems;
    }

    public static void handleConsoleCommand(String command) {
        if (!P2PManager.isActive()) {
            MainActivity.toast("click the refresh button on both devices to connect");
            return;
        }
        requestTexts();
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

    private static class MessageReaderAdapter extends BaseAdapter {
        final ArrayList<TextMessageItem> textMessageItems;

        public MessageReaderAdapter(ArrayList<TextMessageItem> textMessageItems) {

            this.textMessageItems = textMessageItems;
        }

        @Override
        public int getCount() {
            return textMessageItems.size();
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
                convertView = View.inflate(context, R.layout.text_message_item, null);
                final TextView sent_by = (TextView) convertView.findViewById(R.id.sent_by);
                final TextView body = (TextView) convertView.findViewById(R.id.body);
                final TextView date = (TextView) convertView.findViewById(R.id.date);

                holder = new ViewHolder(sent_by, body, date);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final TextMessageItem item = textMessageItems.get(position);
            holder.sent_by.setText(item.number);
            holder.date.setText(item.getDate());
            holder.body.setText(item.body);

            return convertView;
        }

        private static class ViewHolder {
            final TextView sent_by, body, date;

            public ViewHolder(TextView sent_by, TextView body, TextView date) {
                this.sent_by = sent_by;
                this.body = body;
                this.date = date;
            }
        }
    }
}
