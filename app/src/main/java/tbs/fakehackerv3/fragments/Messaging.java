package tbs.fakehackerv3.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import tbs.fakehackerv3.P2PManager;
import tbs.fakehackerv3.R;
import tbs.fakehackerv3.ReceivedMessage;

/**
 * Created by Michael on 5/22/2015.
 */
public class Messaging extends Fragment {
    private static ListView messageList;
    private static EditText messageEditText;
    private static ImageView sendMessage;
    private static final View.OnClickListener sendMessageClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (messageEditText == null)
                return;
            final String msg = messageEditText.getText().toString();

            if (msg.length() > 0)
                P2PManager.setMessage(msg);
            messageEditText.setText("");
            addReceivedMessage(new ReceivedMessage(msg, "Sent : " + String.valueOf(System.currentTimeMillis()), "me"));
            Log.e("p2p", "message : " + msg);
            notifyDataSetChanged();
        }
    };
    private static final MessageAdapter messageAdapter = new MessageAdapter();
    private static Activity context;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Todo
        context = getActivity();
        final View v = View.inflate(getActivity(), R.layout.messaging_fragment, null);
        sendMessage = (ImageView) v.findViewById(R.id.send);
        messageEditText = (EditText) v.findViewById(R.id.message);
        messageList = (ListView) v.findViewById(R.id.list);

        messageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sendMessage.setEnabled(messageEditText.getText().toString().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        sendMessage.setOnClickListener(sendMessageClickListener);
        messageList.setAdapter(messageAdapter);
        return v;
    }

    private static void notifyDataSetChanged() {
        try {
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    messageAdapter.notifyDataSetChanged();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final ArrayList<ReceivedMessage> messages = new ArrayList<ReceivedMessage>();

    private static class MessageAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return messages == null ? 0 : messages.size();
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
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.message_item, null);
            }
            final TextView body = (TextView) convertView.findViewById(R.id.body);
            final TextView when = (TextView) convertView.findViewById(R.id.received_when);
            final TextView sentBy = (TextView) convertView.findViewById(R.id.sent_by);

            final ReceivedMessage message = messages.get(position);

            body.setText(message.message);
            when.setText(message.when);
            sentBy.setText(message.from);
            return convertView;
        }
    }

    public static void addReceivedMessage(ReceivedMessage message) {
        if (message != null && !messages.contains(message)) {
            messages.add(message);
            addMessageToDataBase(message);
        }
    }

    public static void addMessageToDataBase(ReceivedMessage message) {
        //TODO
    }
    public static void toast(final String msg) {
        if (context != null) {
            try {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
