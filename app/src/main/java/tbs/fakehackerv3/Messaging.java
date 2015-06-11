package tbs.fakehackerv3;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Michael on 5/22/2015.
 */
public class Messaging extends Activity {
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

            messages.add(new ReceivedMessage(msg, "Sent : " + String.valueOf(System.currentTimeMillis()), "me"));

            Log.e("p2p", "message : " + msg);
            notifyDataSetChanged();
        }
    };
    private static final MessageAdapter messageAdapter = new MessageAdapter();

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

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.messaging_fragment);
        sendMessage = (ImageView) findViewById(R.id.send);
        messageEditText = (EditText) findViewById(R.id.message);
        messageList = (ListView) findViewById(R.id.list);

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
    }

    private static Activity context;
    private static P2PManager p2PManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        P2PManager.getP2PManager(this);

        setContentView(R.layout.messaging_fragment);
        sendMessage = (ImageView) findViewById(R.id.send);
        messageEditText = (EditText) findViewById(R.id.message);
        messageList = (ListView) findViewById(R.id.list);
        messageList.setAdapter(messageAdapter);

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
        p2PManager = P2PManager.getP2PManager(this, p2pListener, true);
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

    private static final P2PManager.P2PListener p2pListener = new P2PManager.P2PListener() {
        @Override
        public void onScanStarted() {
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "Scanning", Toast.LENGTH_LONG).show();
                }
            });
        }

        @Override
        public void onMessageReceived(String msg) {
            final String[] received = msg.split(Message.MESSAGE_SEPARATOR, 3);
            messages.add(new ReceivedMessage(received[1], "RECEIVED : " + received[2], "random"));
            notifyDataSetChanged();
            Log.e("notified", "msg");
        }

        @Override
        public void onDevicesConnected() {

        }

        @Override
        public void onDevicesDisconnected() {

        }

        @Override
        public void onSocketsConfigured() {
            messageEditText.setEnabled(true);
        }
    };


}
