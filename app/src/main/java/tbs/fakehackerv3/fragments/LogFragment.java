package tbs.fakehackerv3.fragments;

import android.support.v4.app.Fragment;

/**
 * Created by Michael on 6/30/2015.
 */
public class LogFragment extends Fragment {
//    private static final ArrayList<String> logs = new ArrayList<String>();
//    private static ListView listView;
//    private static FragmentActivity context;
//    private static LogAdapter adapter;
//
//    public static void log(final String string) {
//        try {
//            if (context != null)
//                context.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        logs.add(string);
//                        try {
//                            adapter.notifyDataSetChanged();
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        context = getActivity();
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        listView = (ListView) inflater.inflate(R.layout.log_fragment, null);
//        adapter = new LogAdapter();
//        listView.setAdapter(adapter);
//        return listView;
//    }
//
//    public static class LogAdapter extends BaseAdapter {
//        @Override
//        public int getCount() {
//            return logs.size();
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return null;
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return 0;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            ViewHolder holder;
//            if (convertView == null) {
//                convertView = View.inflate(context, R.layout.log_item, null);
//                holder = new ViewHolder((TextView) convertView.findViewById(R.id.text));
//                convertView.setTag(holder);
//            } else {
//                holder = (ViewHolder) convertView.getTag();
//            }
//
//            holder.textView.setText(logs.get(position));
//            return convertView;
//        }
//    }
//
//    public static class ViewHolder {
//        final TextView textView;
//
//        private ViewHolder(TextView textView) {
//            this.textView = textView;
//        }
//    }


}
