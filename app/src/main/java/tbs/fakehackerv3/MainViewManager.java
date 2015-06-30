package tbs.fakehackerv3;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Michael on 6/13/2015.
 */
public class MainViewManager {
    //TODO dialog.findViewById(R.id.text).setSelected(true);
    private static View mainView;
    public static FAB fab;
    public static LinearLayout sidePane;
    private static ImageView hamburger;
    private static Context context;
    private static TextView connectedToStaticText, connectedToDevice;
    private static boolean isSidePaneOpen;


    public MainViewManager(View view) {
        mainView = view;
        MainViewManager.context = mainView.getContext();
        init();
    }

    private void init() {
        sidePane = (LinearLayout) mainView.findViewById(R.id.side_pane);
        fab = (FAB) mainView.findViewById(R.id.fab);
        hamburger = (ImageView) mainView.findViewById(R.id.hamburger);
        connectedToDevice = (TextView) mainView.findViewById(R.id.device_text);
        connectedToStaticText = (TextView) mainView.findViewById(R.id.static_text);
    }

    private static View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.hamburger:

                    break;
                case R.id.fab:

                    break;
            }
        }
    };

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

    private int dpToPixels(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }


}
