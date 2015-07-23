package tbs.fakehackerv3.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import tbs.fakehackerv3.R;

/**
 * Created by Michael on 6/30/2015.
 */
public class LogFragment extends Fragment {
    private static final ArrayList<String> logs = new ArrayList<String>();
    private static ListView listView;
    private static FragmentActivity context;
    private static LogAdapter adapter;

    public static void log(final String string) {
        if (context != null)
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    logs.add(string);
                    try {
                        adapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        listView = (ListView) inflater.inflate(R.layout.log_fragment, null);
        adapter = new LogAdapter();
        listView.setAdapter(adapter);
        return listView;
    }

    public static class LogAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return logs.size();
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
            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.log_item, null);
                holder = new ViewHolder((TextView) convertView.findViewById(R.id.text));
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.textView.setText(logs.get(position));
            return convertView;
        }
    }

    public static class ViewHolder {
        final TextView textView;

        public ViewHolder(TextView textView) {
            this.textView = textView;
        }
    }


}
