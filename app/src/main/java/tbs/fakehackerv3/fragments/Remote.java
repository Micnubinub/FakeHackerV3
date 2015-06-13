package tbs.fakehackerv3.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import tbs.fakehackerv3.MainActivity;
import tbs.fakehackerv3.Message;
import tbs.fakehackerv3.P2PManager;
import tbs.fakehackerv3.R;
import tbs.fakehackerv3.RemoteTools;
import tbs.fakehackerv3.StaticValues;

/**
 * Created by Michael on 6/10/2015.
 */
public class Remote extends Fragment {

    @Nullable
    @Override
    public View getView() {
        //Todo
        final View view = View.inflate(getActivity(), R.layout.message_item, null);
        return view;
    }

    private static Switch flashLight, wifi, bluetooth;
    private TextView takePictureFront, takePictureBack, previousTrack, nextTrack, pausePlay;
    private SeekBar alarm, notification, all, ringer, media, brightness;
    private static P2PManager p2PManager;
    private static Activity context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.remote);
        context = this;

        //Switch
        flashLight = (Switch) findViewById(R.id.flash);
        wifi = (Switch) findViewById(R.id.wifi);
        bluetooth = (Switch) findViewById(R.id.bluetooth);

        flashLight.setOnCheckedChangeListener(switchListener);
        wifi.setOnCheckedChangeListener(switchListener);
        bluetooth.setOnCheckedChangeListener(switchListener);

        //TextView
        takePictureFront = (TextView) findViewById(R.id.take_pic_front);
        takePictureBack = (TextView) findViewById(R.id.take_pic_back);
        previousTrack = (TextView) findViewById(R.id.previous);
        nextTrack = (TextView) findViewById(R.id.next);
        pausePlay = (TextView) findViewById(R.id.play_pause);

        takePictureFront.setOnClickListener(clickListener);
        takePictureBack.setOnClickListener(clickListener);
        previousTrack.setOnClickListener(clickListener);
        nextTrack.setOnClickListener(clickListener);
        pausePlay.setOnClickListener(clickListener);

        //SeekBar
        alarm = (SeekBar) findViewById(R.id.volume_alarm);
        notification = (SeekBar) findViewById(R.id.volume_notification);
        all = (SeekBar) findViewById(R.id.volume_all);
        ringer = (SeekBar) findViewById(R.id.volume_ringer);
        media = (SeekBar) findViewById(R.id.volume_media);
        brightness = (SeekBar) findViewById(R.id.brightness);

        alarm.setOnSeekBarChangeListener(seekBarListener);
        notification.setOnSeekBarChangeListener(seekBarListener);
        all.setOnSeekBarChangeListener(seekBarListener);
        ringer.setOnSeekBarChangeListener(seekBarListener);
        media.setOnSeekBarChangeListener(seekBarListener);
        brightness.setOnSeekBarChangeListener(seekBarListener);

        p2PManager = P2PManager.getP2PManager(this, p2pListener, true);
    }

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
            //Todo
            handleReceivedCommand(msg);
        }

        @Override
        public void onDevicesConnected() {

        }

        @Override
        public void onDevicesDisconnected() {

        }

        @Override
        public void onSocketsConfigured() {

        }
    };

    private static final View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.take_pic_back:
                    sendCommand(StaticValues.TAKE_PICTURE_BACK, "");
                    break;
                case R.id.take_pic_front:
                    sendCommand(StaticValues.TAKE_PICTURE_FRONT, "");
                    break;
                case R.id.next:
                    sendCommand(StaticValues.MEDIA_CONTROL_SKIP, "");
                    break;
                case R.id.play_pause:
                    sendCommand(StaticValues.MEDIA_CONTROL_PLAY_PAUSE, "");
                    break;
                case R.id.previous:
                    sendCommand(StaticValues.MEDIA_CONTROL_PREVIOUS, "");
                    break;
            }
        }
    };

    private static Switch.OnCheckedChangeListener switchListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            switch (buttonView.getId()) {
                case R.id.flash:
                    sendCommand(StaticValues.TOGGLE_TORCH, "");
                    break;
                case R.id.wifi:
                    //TODO maybe consider adding this
                    sendCommand(StaticValues.SET_BLUETOOTH, isChecked ? "1" : "0");
                    break;
                case R.id.bluetooth:
                    sendCommand(StaticValues.SET_BLUETOOTH, isChecked ? "1" : "0");
                    break;
            }
        }
    };

    private static final SeekBar.OnSeekBarChangeListener seekBarListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            final int progress = seekBar.getProgress();
            switch (seekBar.getId()) {
                case R.id.volume_alarm:
                    sendCommand(StaticValues.SET_ALARM_VOLUME, String.valueOf(progress));
                    break;
                case R.id.brightness:
                    sendCommand(StaticValues.SET_BRIGHTNESS, String.valueOf(progress));
                    break;
                case R.id.volume_all:
                    sendCommand(StaticValues.SET_BRIGHTNESS, String.valueOf(progress));
                    break;
                case R.id.volume_media:
                    sendCommand(StaticValues.SET_MEDIA_VOLUME, String.valueOf(progress));
                    break;
                case R.id.volume_notification:
                    sendCommand(StaticValues.SET_NOTIFICATION_VOLUME, String.valueOf(progress));
                    break;
                case R.id.volume_ringer:
                    sendCommand(StaticValues.SET_RINGER_VOLUME, String.valueOf(progress));
                    break;
            }
        }
    };



    public static void sendCommand(String commandType, String cvs) {
        //TODO

        final StringBuilder builder = new StringBuilder(String.valueOf(Message.MessageType.SEND_COMMAND));
        builder.append(Message.MESSAGE_SEPARATOR);
        builder.append(commandType);
        builder.append(Message.MESSAGE_SEPARATOR);

        //TODO check all these and make sure they match up with the receive command counterpart
        if (commandType.contains(StaticValues.SCHEDULED_RECORDING)) {
            builder.append(cvs);
        } else if (commandType.contains(StaticValues.SCHEDULED_COMMAND)) {
            builder.append(cvs);
        } else if (commandType.contains(StaticValues.WAKE_UP)) {
            builder.append(cvs);
        } else if (commandType.contains(StaticValues.TOGGLE_TORCH)) {
            builder.append(cvs);
        } else if (commandType.contains(StaticValues.PRESS_BACK)) {
            builder.append(cvs);
        } else if (commandType.contains(StaticValues.PRESS_MENU)) {
            builder.append(cvs);
        } else if (commandType.contains(StaticValues.PRESS_VOLUME_UP)) {
            builder.append(cvs);
        } else if (commandType.contains(StaticValues.PRESS_VOLUME_DOWN)) {
            builder.append(cvs);
        } else if (commandType.contains(StaticValues.GET_FOLDER_TREE)) {
            builder.append(cvs);
        } else if (commandType.contains(StaticValues.OPEN_FILE)) {
            builder.append(cvs);
        } else if (commandType.contains(StaticValues.RECORD_AUDIO)) {
            builder.append(cvs);
        } else if (commandType.contains(StaticValues.SET_ALARM_VOLUME)) {
            builder.append(cvs);
        } else if (commandType.contains(StaticValues.TAKE_PICTURE_BACK)) {
            builder.append(cvs);
        } else if (commandType.contains(StaticValues.TAKE_PICTURE_FRONT)) {
            builder.append(cvs);
        } else if (commandType.contains(StaticValues.SET_TORCH)) {
            builder.append(cvs);
        } else if (commandType.contains(StaticValues.GET_FILE_DETAILS)) {
            builder.append(cvs);
        } else if (commandType.contains(StaticValues.DELETE_FILE)) {
            builder.append(cvs);
        } else if (commandType.contains(StaticValues.DOWNLOAD_FILE)) {
            builder.append(cvs);
        } else if (commandType.contains(StaticValues.MOVE_FILE)) {
            builder.append(cvs);
        } else if (commandType.contains(StaticValues.STREAM_FILE)) {
            builder.append(cvs);
        } else if (commandType.contains(StaticValues.CREATE_DIRECTORY)) {
            builder.append(cvs);
        } else if (commandType.contains(StaticValues.TAKE_SCREENSHOT)) {
            builder.append(cvs);
        } else if (commandType.contains(StaticValues.SET_MEDIA_VOLUME)) {
            builder.append(cvs);
        } else if (commandType.contains(StaticValues.SET_NOTIFICATION_VOLUME)) {
            builder.append(cvs);
        } else if (commandType.contains(StaticValues.SET_RINGER_VOLUME)) {
            builder.append(cvs);
        } else if (commandType.contains(StaticValues.SET_BRIGHTNESS)) {
            builder.append(cvs);
        } else if (commandType.contains(StaticValues.SET_BRIGHTNESS_MODE)) {
            builder.append(cvs);
        } else if (commandType.contains(StaticValues.SET_BLUETOOTH)) {
            builder.append(cvs);
        } else if (commandType.contains(StaticValues.CHAT_MESSAGE)) {
            builder.append(cvs);
        } else if (commandType.contains(StaticValues.SPOOF_TOUCH)) {
            builder.append(cvs);
        } else if (commandType.contains(StaticValues.SPOOF_TOUCH_FINGER_2)) {
            builder.append(cvs);
        } else if (commandType.contains(StaticValues.RECORD_VIDEO_FRONT)) {
            builder.append(cvs);
        } else if (commandType.contains(StaticValues.RECORD_VIDEO_BACK)) {
            builder.append(cvs);
        } else if (commandType.contains(StaticValues.MEDIA_CONTROL_SKIP)) {
            builder.append(cvs);
        } else if (commandType.contains(StaticValues.MEDIA_CONTROL_PREVIOUS)) {
            builder.append(cvs);
        } else if (commandType.contains(StaticValues.MEDIA_CONTROL_PLAY_PAUSE)) {
            builder.append(cvs);
        }

        if (cvs != null && cvs.length() > 0 && p2PManager != null) {
            P2PManager.enqueueMessage(new Message(builder.toString(), Message.MessageType.SEND_COMMAND));
        } else {
            log("please enter a message string or please init p2pManager");
        }

    }

    private static void log(String msg) {
        Log.e("Remote", msg);
    }
}