package tbs.fakehackerv3.console;

import java.util.Date;

/**
 * Created by Michael on 7/22/2015.
 */
public class CallLogItem {
    private static final Date dateObject = new Date();
    public final long duration;
    public final String number, type;
    private final long date;

    public CallLogItem(String number, String type, long date, long duration) {
        this.date = date;
        this.duration = duration;
        this.number = number;
        this.type = type;
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
