package tbs.fakehackerv3;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

/**
 * Created by Michael on 5/22/2015.
 */
public class Messaging extends Activity {
    private static ListView messageList;
    private static EditText message;
    private static ImageView sendMessage;
    private static final View.OnClickListener sendMessageClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (message == null)
                return;

            final String msg = message.getText().toString();

            if (msg.length() > 0)
                P2PManager.sendSimpleText(msg);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.messaging_fragment);
        sendMessage = (ImageView) findViewById(R.id.send);
        message = (EditText) findViewById(R.id.message);
        messageList = (ListView) findViewById(R.id.list);

        message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sendMessage.setEnabled(message.getText().toString().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        sendMessage.setOnClickListener(sendMessageClickListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messaging_fragment);
        sendMessage = (ImageView) findViewById(R.id.send);
        message = (EditText) findViewById(R.id.message);
        messageList = (ListView) findViewById(R.id.list);

        message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sendMessage.setEnabled(message.getText().toString().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        sendMessage.setOnClickListener(sendMessageClickListener);
    }


}
