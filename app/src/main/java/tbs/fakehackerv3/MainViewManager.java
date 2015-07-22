package tbs.fakehackerv3;

import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;

import tbs.fakehackerv3.custom_views.HackerTextView;

/**
 * Created by Michael on 6/13/2015.
 */
public class MainViewManager {
    public static FAB fab;
    public static LinearLayout sidePane;
    private static View mainView;
    private static Context context;
    private static HackerTextView connectedToStaticText, connectedToDevice;
    private static boolean isSidePaneOpen;
    private static View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.fab:

                    break;
            }
        }
    };

    public MainViewManager(View view) {
        mainView = view;
        MainViewManager.context = mainView.getContext();
        init();
    }

    public static void setConnectedToDevice(String connectedToDeviceText) {
        if (connectedToDeviceText != null && connectedToDevice != null)
            connectedToDevice.setText(connectedToDeviceText);
    }

    public static void setStaticText(String staticText) {
        if (connectedToStaticText != null && staticText != null)
            connectedToStaticText.setText(staticText);
    }

    private static void toggleSidePane() {
        isSidePaneOpen = !isSidePaneOpen;
    }

    private static void setViewDimen(View v, int w, int h) {
        // v.setLayoutParams(new ViewGroup.LayoutParams(w, h));
    }

    private static void setViewPadding(View v, int padding) {
        v.setPadding(padding, padding, padding, padding);
    }

    private void init() {
        sidePane = (LinearLayout) mainView.findViewById(R.id.side_pane);
        fab = (FAB) mainView.findViewById(R.id.fab);

        connectedToDevice = (HackerTextView) mainView.findViewById(R.id.device_text);
        connectedToStaticText = (HackerTextView) mainView.findViewById(R.id.static_text);

        connectedToDevice.setTypeFaceStyle(Typeface.BOLD);
        connectedToStaticText.setTypeFaceStyle(Typeface.BOLD);
    }

    private int dpToPixels(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }


}
