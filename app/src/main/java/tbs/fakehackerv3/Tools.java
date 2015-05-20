package tbs.fakehackerv3;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.hardware.Camera;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * Created by root on 29/07/14.
 */
public class Tools {

    public static final String SCHEDULED_RECORDING = "SCHEDULED_RECORDING";
    public static final String SCHEDULED_COMMAND = "SCHEDULED_COMMAND";
    public static final String WAKE_UP = "WAKE_UP";
    public static final String WIFI_OFF = "WIFI_OFF";
    //public static final String
    public static Camera camera;

    public static void getPackages(Context context) {

        PackageManager packageManager = context.getPackageManager();
        final Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> list = packageManager.queryIntentActivities(i, 0);
        // ArrayList<String> pacs = new ArrayList<String>(list.size());

        for (int ii = 0; ii < list.size(); ii++) {
            //  pacs.add(ii, list.get(ii).activityInfo.packageName);
            //TODO MainActivity.consoleEntries.add(new ConsoleItem(String.valueOf(ii + 1) + ". " + list.get(ii).loadLabel(packageManager).toString() + " (" + list.get(ii).activityInfo.packageName + ")"));
        }

    }

    public static void launchInt(Context context, int ii) {
        ii = ii - 1 > 0 ? ii - 1 : 0;

        PackageManager packageManager = context.getPackageManager();
        final Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> list = packageManager.queryIntentActivities(i, 0);
        Intent LaunchApp = packageManager.getLaunchIntentForPackage(list.get(ii).activityInfo.packageName);
        context.startActivity(LaunchApp);

    }

    public static void launchPackage(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        Intent LaunchApp = packageManager.getLaunchIntentForPackage(packageName);
        context.startActivity(LaunchApp);
    }

    public static ArrayList<String> getBroadcastRecievers(Context context) {
        //Todo

        PackageManager packageManager = context.getPackageManager();
        final Intent i = new Intent(Intent.ACTION_MAIN, null);
        List<ResolveInfo> list = packageManager.queryBroadcastReceivers(i, 0);
        ArrayList<String> pacs = new ArrayList<String>(list.size());

        for (int ii = 0; ii < list.size(); ii++) {

            pacs.add(ii, list.get(ii).loadLabel(packageManager).toString() + " (" + list.get(ii).activityInfo.packageName + ")");
        }

        return pacs;
    }


    public static void toggleTorch(Context context) {
        String string = "Camera not supported";
        try {

            if (camera == null)
                camera = Camera.open();
            else
                camera.reconnect();

            final PackageManager packageManager = context.getPackageManager();

            if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {

                if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
                    Camera.Parameters p = camera.getParameters();
                    if (p.getFlashMode().equals(Camera.Parameters.FLASH_MODE_TORCH)) {
                        p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                        camera.setParameters(p);
                        camera.stopPreview();
                        camera.release();
                        camera = null;
                        string = "Turning the flash off...";
                    } else {
                        p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                        camera.setParameters(p);
                        camera.startPreview();
                        string = "Turning the flash on...";
                    }
                } else {
                    string = "Flash not supported";
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            string = "Toggling failed";
        }
        //TODO MainActivity.consoleEntries.add(new ConsoleItem(string));
    }

    public static void launchIE(final Context context) {
        final Thread thread = new Thread();

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2500);
                    Thread.sleep(2500);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {

                    final Dialog dialog = new Dialog(context, R.style.CustomDialog);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setCancelable(false);

                    TextView textView = new TextView(context);
                    textView.setTextColor(0xffffffff);
                    textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    textView.setPadding(15, 15, 15, 15);
                    textView.setBackgroundColor(0xff2067b2);
                    textView.setText("A problem has been detected and Windows has been shut down to prevent damage\n" +
                            " to your computer.\n" +
                            "\nThe problem seems to be caused by the following file: SPCMDCON.SYS\n" +
                            "\nPAGE_FAULT_IN_NONPAGED_AREA\n\nIf this is the first time you've seen this stop error screen,\n" +
                            " restart your computer. If this screen appears again, follow these steps:\n" +
                            "\nCheck to make sure any new hardware or software is properly installed.\n" +
                            "\n\nIf this is a new installation, ask your hardware or software manufacturer\n" +
                            " for any Windows updates you might need.\n" +
                            "\nIf problems continue, disable or remove any newly installed hardware or software. Disable BIOS memory options such as caching or shadowing.\n" +
                            "\nIf you need to use Safe Mode to remove or disable components, restart\n" +
                            " your computer, press F8 to select Advanced Startup Options, and then\n" +
                            " select Safe Mode.\n" +
                            "\nTechnical information:\n" +
                            "\n*** STOP: 0x00000050 (0xFD3094C2,0x00000001,0xFBFE7617,0x0000 0000)\n" +
                            "\n*** SPCMDCON.SYS - Address FBFE7617 base at FBFE5000, DateStamp 3d6dd67c ");

                    textView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent event) {

                            switch (event.getAction()) {
                                case MotionEvent.ACTION_UP:
                                    try {
                                        thread.sleep(2500);
                                        thread.sleep(2500);


                                        dialog.dismiss();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    break;

                            }
                            return true;
                        }
                    });
                    dialog.setContentView(textView);
                    dialog.show();

                }
                //TODO MainActivity.consoleEntries.add(new ConsoleItem("Exiting Intanet Eksplora ..."));
            }
        };
        runnable.run();
    }


    public static String fileSize(long size) {
        String s = String.valueOf(size) + "Bytes";

        if (size < 1024)
            return s;

        if (size > 1024 && size < Math.pow(2, 20)) {
            s = String.format("%.3f", size / 1024) + "KB";
            return s;
        }

        if (size > Math.pow(2, 20) && size < Math.pow(2, 30)) {
            s = String.format("%.3f", size / Math.pow(2, 20)) + "MB";
            return s;
        }

        if (size > Math.pow(2, 30) && size < Math.pow(2, 40)) {
            s = String.format("%.3f", size / Math.pow(2, 30)) + "GB";
            return s;
        }

        if (size > Math.pow(2, 40)) {
            s = String.format("%.3f", size / Math.pow(2, 40)) + "TB";
            return s;
        }

        return s;
    }

    public static String getDate(long date) {
        // Create a DateFormatter object for displaying date in specified format.
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        return formatter.format(calendar.getTime());
    }

}
