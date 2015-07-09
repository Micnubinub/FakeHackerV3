package tbs.fakehackerv3.console;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import tbs.fakehackerv3.R;

public class ConsoleListAdapter extends ArrayAdapter<ConsoleItem> {

    private final ArrayList<ConsoleItem> consoleItems;

    public ConsoleListAdapter(Context context, int textViewResourceId,
                              ArrayList<ConsoleItem> objects) {
        super(context, textViewResourceId, objects);
        this.consoleItems = objects;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.console_entry, null);
        }

        ConsoleItem i = consoleItems.get(position);

        if (i != null) {
            TextView btd = (TextView) v.findViewById(R.id.commandEntry);
            if (btd != null) {
                btd.setText(i.getDetails());
            }
        }
        return v;
    }
}
