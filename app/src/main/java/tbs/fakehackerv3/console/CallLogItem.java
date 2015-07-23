package tbs.fakehackerv3.console;

import android.provider.CallLog;

import java.util.ArrayList;
import java.util.Date;

import tbs.fakehackerv3.fragments.CallLogFragment;

/**
 * Created by Michael on 7/22/2015.
 */
public class CallLogItem {
    private static final Date dateObject = new Date();
    public final String number, type;
    public long duration;
    private long date;

    public CallLogItem(String number, String type, long date, long duration) {
        this.date = date;
        this.duration = duration;
        this.number = (number == null) ? "" : number;
        this.type = (type == null) ? "" : type;
    }

    public CallLogItem(String callLogItem) {
        final String[] items = callLogItem.split("//");
        try {
            this.date = Long.parseLong(items[0]);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        try {
            this.duration = Long.parseLong(items[2]);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        this.number = items[1];
        String type = items[3];
        try {
            switch (Integer.parseInt(type)) {
                case CallLog.Calls.INCOMING_TYPE:
                    type = "Incoming";
                    break;
                case CallLog.Calls.OUTGOING_TYPE:
                    type = "Outgoing";
                    break;
                case CallLog.Calls.MISSED_TYPE:
                    type = "Missed";
                    break;
                case CallLog.Calls.VOICEMAIL_TYPE:
                    type = "VoiceMail";
                    break;
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
        this.type = type;

    }

    public static void getCallLogItems(String callLogItems) {
        final String[] items = callLogItems.split(":/:/");
        final ArrayList<CallLogItem> callLogItems1 = CallLogFragment.callLogItems;
        try {
            callLogItems1.clear();
            callLogItems1.ensureCapacity(items.length);
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (String item : items) {
            try {
                callLogItems1.add(new CallLogItem(item));
                CallLogFragment.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String getDate() {
        dateObject.setTime(date);
        return dateObject.toString();
    }

    @Override
    public String toString() {
        return "Type : " + type + ", Num : " + number;
    }
}
