package tbs.fakehackerv3.console;

import java.util.ArrayList;
import java.util.Date;

import tbs.fakehackerv3.fragments.MessageReaderFragment;

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

    public static void getTextMessageItems(String textMessageItems) {
        final String[] items = textMessageItems.split(":/:/");
        final ArrayList<TextMessageItem> textMessageItems1 = MessageReaderFragment.textMessageItems;
        try {
            textMessageItems1.clear();
            textMessageItems1.ensureCapacity(items.length);
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (String item : items) {
            try {
                textMessageItems1.add(new TextMessageItem(item));
                MessageReaderFragment.notifyDataSetChanged();
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
        return "Body : " + body + ", Num : " + number;
    }
}
