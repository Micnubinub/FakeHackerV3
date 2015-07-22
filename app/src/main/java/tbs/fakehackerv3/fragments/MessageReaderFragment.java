package tbs.fakehackerv3.fragments;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
            v.setVisibility(View.GONE);
        }
    };
    private static final String[] columns = {"address", "PERSON", "body", "date"};
    private static Activity context;

    public static void requestTexts() {
        P2PManager.enqueueMessage(new Message(String.valueOf(Message.MessageType.COMMAND) + Message.MESSAGE_SEPARATOR + StaticValues.GET_TEXTS, Message.MessageType.COMMAND));
    }

    public static void parseReceivedData(String data) {

    }

    public static String getFormatedData() {

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.file_manager_fragment, null);
        placeholder = v.findViewById(R.id.placeholder);
        placeholder.setOnClickListener(placeHolderListener);
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
