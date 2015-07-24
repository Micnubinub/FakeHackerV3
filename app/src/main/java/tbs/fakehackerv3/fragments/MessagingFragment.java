package tbs.fakehackerv3.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
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
import java.util.Date;

import tbs.fakehackerv3.MainActivity;
import tbs.fakehackerv3.Message;
import tbs.fakehackerv3.P2PManager;
import tbs.fakehackerv3.R;
import tbs.fakehackerv3.ReceivedMessage;
import tbs.fakehackerv3.custom_views.DisconnectedButton;

/**
 * Created by Michael on 5/22/2015.
 */
public class MessagingFragment extends P2PFragment {
    public static final View.OnClickListener placeHolderListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!P2PManager.isActive()) {
                DisconnectedButton.show();
                return;
            }
            v.setVisibility(View.GONE);
        }
    };
    private static final MessageAdapter messageAdapter = new MessageAdapter();
    private static final Runnable update = new Runnable() {
        @Override
        public void run() {
            messageAdapter.notifyDataSetChanged();
        }
    };
    private static final ArrayList<ReceivedMessage> messages = new ArrayList<ReceivedMessage>();
    private static final Date date = new Date();
    private static ListView messageList;
    private static EditText messageEditText;
    private static ImageView sendMessage;
    private static FragmentActivity context;
    private static final View.OnClickListener sendMessageClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (messageEditText == null)
                return;
            sendMessage(messageEditText.getText().toString());
        }
    };
    private static View v;

    private static void notifyDataSetChanged() {
        try {
            context.runOnUiThread(update);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void handleReceivedMessage(String msg) {
        log("handleRecMsg > " + msg);
        String[] split = msg.split(Message.MESSAGE_SEPARATOR);
        if (MainActivity.connectedDevice == null) {
            P2PManager.connectedDeviceNullFix();
            addReceivedMessage(new ReceivedMessage(split[0], "Received : " + split[1], "..."));
        } else {
            addReceivedMessage(new ReceivedMessage(split[0], "Received : " + split[1], MainActivity.connectedDevice.deviceName));
        }
    }

    public static void log(String msg) {
        Log.e("Messaging Fragment", msg);
        LogFragment.log(msg);
    }

    public static void addReceivedMessage(final ReceivedMessage message) {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (message != null && !messages.contains(message)) {
                    messages.add(message);
                    notifyDataSetChanged();
                    addMessageToDataBase(message);
                }
            }
        });
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

    private static void sendMessage(String msg) {
        if (msg.length() > 0)
            P2PManager.sendSimpleMessage(msg);
        messageEditText.setText("");
        date.setTime(System.currentTimeMillis());
        addReceivedMessage(new ReceivedMessage(msg, "Sent : " + date.toString(), "me"));
        notifyDataSetChanged();
    }

    public static void handleConsoleCommand(String command) {
        sendMessage(command.replace("chat ", ""));
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Todo

        v = View.inflate(getActivity(), R.layout.messaging_fragment, null);
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
        placeholder = v.findViewById(R.id.placeholder);
        placeholder.setOnClickListener(placeHolderListener);
        return v;
    }

    public void init() {

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
        init();
        placeholder.post(new Runnable() {
            @Override
            public void run() {
                placeholder.setVisibility(View.GONE);
            }
        });
    }

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
}
