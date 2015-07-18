package tbs.fakehackerv3;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;


/**
 * Created by root on 29/07/14.
 */
public class Tools {

    public static final Random random = new Random();

    public static void getPackages(Context context) {
        final PackageManager packageManager = context.getPackageManager();
        final Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> list = packageManager.queryIntentActivities(i, 0);
//        ArrayList<String> pacs = new ArrayList<String>(list.size());
//
//        for (int ii = 0; ii < list.size(); ii++) {
//            pacs.add(ii, list.get(ii).activityInfo.packageName);
//            MainActivity.consoleEntries.add(new ConsoleItem(String.valueOf(ii + 1) + ". " + list.get(ii).loadLabel(packageManager).toString() + " (" + list.get(ii).activityInfo.packageName + ")"));
//        }

    }

    public static void launchInt(Context context, int ii) {
        ii = ii - 1 > 0 ? ii - 1 : 0;

        final PackageManager packageManager = context.getPackageManager();
        final Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> list = packageManager.queryIntentActivities(i, 0);
        Intent LaunchApp = packageManager.getLaunchIntentForPackage(list.get(ii).activityInfo.packageName);
        context.startActivity(LaunchApp);

    }

    public static void launchPackage(Context context, String packageName) {
        final PackageManager packageManager = context.getPackageManager();
        final Intent LaunchApp = packageManager.getLaunchIntentForPackage(packageName);
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

    public static Bitmap getBitmapFromDrawable(Drawable drawable) {
        //TODO test
        Bitmap bitmap = null;
        if (drawable instanceof BitmapDrawable) ;
        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }
        final Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
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
            }
        };
        runnable.run();
    }


    public static String getFileSize(long size) {
        String s = String.valueOf(size) + "Bytes";

        if (size < 1024)
            return s;

        if (size > 1024 && size < Math.pow(2, 20)) {
            s = String.format("%.3f", size / 1024f) + "KB";
            return s;
        }

        if (size > Math.pow(2, 20) && size < Math.pow(2, 30)) {
            s = String.format("%.3f", size / 1048576f) + "MB";
            return s;
        }

        if (size > Math.pow(2, 30) && size < Math.pow(2, 40)) {
            s = String.format("%.3f", size / 1073741824f) + "GB";
            return s;
        }

        if (size > Math.pow(2, 40)) {
            s = String.format("%.3f", size / 1099511627776f) + "TB";
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


    public static void handleMessage(Message message) {
        switch (message.messageType) {
            case MESSAGE:
                handleReceivedMessage(message.getMessage());
                break;
            case FILE:
                handleReceiveFile(message.getMessage());
                break;
            case COMMAND:
                handleReceivedCommand(message.getMessage());
                break;
        }
    }

    public static void handleReceivedMessage(String message) {
        //TODO
    }

    public static void handleReceiveFile(String file) {
        //TODO
    }

    public static void handleReceivedCommand(String command) {
        final String[] splitCommand = command.split(Message.MESSAGE_SEPARATOR);
        final String commandString = splitCommand[2];
        //TODO check all these
        if (splitCommand[1].contains(StaticValues.SCHEDULED_RECORDING)) {
            final long when = Long.parseLong(commandString);
            final int duration = Integer.parseInt(splitCommand[3]);
            RemoteTools.record(duration, when);
        } else if (splitCommand[1].contains(StaticValues.SCHEDULED_COMMAND)) {
            final long when = Long.parseLong(commandString);
            //todo
        } else if (splitCommand[1].contains(StaticValues.TOGGLE_TORCH)) {
            RemoteTools.toggleTorch();
        } else if (splitCommand[1].contains(StaticValues.PRESS_HOME)) {
            //todo
        } else if (splitCommand[1].contains(StaticValues.PRESS_BACK)) {
            //todo
        } else if (splitCommand[1].contains(StaticValues.PRESS_MENU)) {
            //todo
        } else if (splitCommand[1].contains(StaticValues.PRESS_VOLUME_UP)) {
            //todo
        } else if (splitCommand[1].contains(StaticValues.PRESS_VOLUME_DOWN)) {
            //todo
        } else if (splitCommand[1].contains(StaticValues.GET_FOLDER_TREE)) {
            try {
                final File file = new File(commandString);
                if (!file.exists()) {
                } else if (!file.isDirectory()) {

                } else {
                    //todo
                    file.listFiles();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (splitCommand[1].contains(StaticValues.OPEN_FILE)) {
            try {
                final File file = new File(commandString);
                if (!file.exists()) {
                } else if (!file.isDirectory()) {

                } else {
                    //todo
                    file.listFiles();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (splitCommand[1].contains(StaticValues.RECORD_AUDIO)) {
            try {
                RemoteTools.record(Integer.parseInt(commandString));
            } catch (NumberFormatException e) {
                log("record failed > not a number");
                e.printStackTrace();
            }
        } else if (splitCommand[1].contains(StaticValues.SET_ALARM_VOLUME)) {
            try {
                RemoteTools.setVolumeAlarm(Integer.parseInt(commandString));
            } catch (NumberFormatException e) {
                log("set alarm failed > not a number");
                e.printStackTrace();
            }
        } else if (splitCommand[1].contains(StaticValues.TAKE_PICTURE_BACK)) {
            RemoteTools.takePictureBack();
        } else if (splitCommand[1].contains(StaticValues.TAKE_PICTURE_FRONT)) {
            RemoteTools.takePictureFront();
        } else if (splitCommand[1].contains(StaticValues.SET_TORCH)) {
            //todo add this
        } else if (splitCommand[1].contains(StaticValues.GET_FILE_DETAILS)) {
            try {
                final File file = new File(commandString);
                if (!file.exists()) {

                } else {
                    //todo
                    file.length();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (splitCommand[1].contains(StaticValues.CREATE_DIRECTORY)) {
            final File file = new File(commandString);
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    log("createfile failed > ioexeption");
                    e.printStackTrace();
                }
            }
        } else if (splitCommand[1].contains(StaticValues.TAKE_SCREENSHOT)) {
            try {
                RemoteTools.getScreenShot();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (splitCommand[1].contains(StaticValues.SET_MEDIA_VOLUME)) {
            try {
                RemoteTools.setVolumeMedia(Integer.parseInt(commandString));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } else if (splitCommand[1].contains(StaticValues.SET_NOTIFICATION_VOLUME)) {
            try {
                RemoteTools.setVolumeNotification(Integer.parseInt(commandString));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } else if (splitCommand[1].contains(StaticValues.SET_RINGER_VOLUME)) {
            try {
                RemoteTools.setVolumeRinger(Integer.parseInt(commandString));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } else if (splitCommand[1].contains(StaticValues.SET_BRIGHTNESS)) {
            try {
                RemoteTools.setBrightness(Integer.parseInt(commandString));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } else if (splitCommand[1].contains(StaticValues.SET_BRIGHTNESS_MODE)) {
            try {
                RemoteTools.setBrightnessAuto(Integer.parseInt(commandString) > 0);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } else if (splitCommand[1].contains(StaticValues.SET_BLUETOOTH)) {
            try {
                RemoteTools.setBluetooth(Integer.parseInt(commandString) > 0);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } else if (splitCommand[1].contains(StaticValues.SPOOF_TOUCH)) {
            //todo
        } else if (splitCommand[1].contains(StaticValues.SPOOF_TOUCH_FINGER_2)) {
            //todo
        } else if (splitCommand[1].contains(StaticValues.RECORD_VIDEO_FRONT)) {
            //todo
        } else if (splitCommand[1].contains(StaticValues.RECORD_VIDEO_BACK)) {
            //todo
        } else if (splitCommand[1].contains(StaticValues.MEDIA_CONTROL_SKIP)) {
            RemoteTools.skipTrack();
        } else if (splitCommand[1].contains(StaticValues.MEDIA_CONTROL_PREVIOUS)) {
            RemoteTools.previousTrack();
        } else if (splitCommand[1].contains(StaticValues.MEDIA_CONTROL_PLAY_PAUSE)) {
            RemoteTools.playMusic();
        }
    }

    public static void sendMessage(String message) {
        //TODO
        if (MainActivity.p2PManager != null)
            P2PManager.enqueueMessage(new Message(message, Message.MessageType.MESSAGE));
    }

    public static void sendFile(File file) {
        //TODO
        if (MainActivity.p2PManager != null) {
            //TODO maybe add a prefix >> receiveFile then listen for it when getting a message_background and makes sure the device
            //todo is ready to receive a file
            P2PManager.enqueueMessage(new Message(file.getName(), Message.MessageType.FILE));
        }
    }

    public static void showStopServiceDialog(final Context context) {
        final Dialog dialog = new Dialog(context, R.style.CustomDialog);
        //TODO add don't ask again checkbox
        dialog.setContentView(R.layout.stop_service_dialog);
        dialog.findViewById(R.id.stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    context.stopService(new Intent(context, P2PManager.class));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.continue_service).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private static void log(String msg) {
        Log.e("FH3Tools", msg);
    }


}
