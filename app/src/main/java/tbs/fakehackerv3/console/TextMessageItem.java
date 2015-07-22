package tbs.fakehackerv3.console;

import java.util.Date;

/**
 * Created by Michael on 7/22/2015.
 */
public class TextMessageItem {
    private static final Date dateObject = new Date();
    public final String number, body;
    private final long date;

    public TextMessageItem(String number, String body, long date) {
        this.date = date;
        this.number = number;
        this.body = body;
    }

    public String getDate() {
        dateObject.setTime(date);
        return dateObject.toString();
    }

    @Override
    public String toString() {
        return "Body : " + body + ", Num : " + number;
    }
}
