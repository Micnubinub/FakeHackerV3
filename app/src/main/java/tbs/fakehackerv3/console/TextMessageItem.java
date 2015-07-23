package tbs.fakehackerv3.console;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Michael on 7/22/2015.
 */
public class TextMessageItem {
    private static final Date dateObject = new Date();
    public final String number, body;
    private long date;

    public TextMessageItem(String number, String body, long date) {
        this.date = date;
        this.number = (number == null) ? "" : number;
        this.body = (body == null) ? "" : body;
    }

    public TextMessageItem(String textMEssageItem) {
        final String[] items = textMEssageItem.split("//");
        this.number = items[0];
        this.body = items[1];
        try {
            this.date = Long.parseLong(items[2]);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<TextMessageItem> getTextMessageItems(String textMessageItems) {
        final String[] items = textMessageItems.split(":/:/");
        final ArrayList<TextMessageItem> textMessageItems1 = new ArrayList<TextMessageItem>(items.length);

        for (String item : items) {
            try {
                textMessageItems1.add(new TextMessageItem(item));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return textMessageItems1;
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
