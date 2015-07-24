package tbs.fakehackerv3.console;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import tbs.fakehackerv3.R;

public class ConsoleListAdapter extends ArrayAdapter<String> {

    private final ArrayList<String> consoleItems;

    public ConsoleListAdapter(Context context, int textViewResourceId,
                              ArrayList<String> objects) {
        super(context, textViewResourceId, objects);
        this.consoleItems = objects;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(getContext(), R.layout.console_entry, null);
            final ViewHolder holder = new ViewHolder((TextView) convertView.findViewById(R.id.commandEntry));
            convertView.setTag(holder);
        }
        ((ViewHolder) convertView.getTag()).textView.setText(
                consoleItems.get(position));

        if (consoleItems.get(position).contains("----"))
            ((ViewHolder) convertView.getTag()).textView.setSingleLine(true);
        else
            ((ViewHolder) convertView.getTag()).textView.setSingleLine(false);
        return convertView;
    }

    private static class ViewHolder {
        public final TextView textView;

        public ViewHolder(TextView textView) {
            this.textView = textView;
        }
    }
}
