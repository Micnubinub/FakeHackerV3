package tbs.fakehackerv3.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.media.AudioManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import tbs.fakehackerv3.Message;
import tbs.fakehackerv3.P2PManager;
import tbs.fakehackerv3.R;
import tbs.fakehackerv3.StaticValues;

/**
 * Created by Michael on 6/10/2015.
 */
public class RemoteMaterial extends Fragment {

    private static Switch flashLight, wifi, bluetooth;
    private TextView takePictureFront, takePictureBack, previousTrack, nextTrack, pausePlay;
    private SeekBar alarm, notification, all, ringer, media, brightness;
    private static Activity context;

    @Nullable
    @Override
    public View getView() {
        //Todo
        final View v = View.inflate(getActivity(), R.layout.remote, null);

        context = getActivity();

        //Switch
        flashLight = (Switch) v.findViewById(R.id.flash);
        wifi = (Switch) v.findViewById(R.id.wifi);
        bluetooth = (Switch) v.findViewById(R.id.bluetooth);

        flashLight.setOnCheckedChangeListener(switchListener);
        wifi.setOnCheckedChangeListener(switchListener);
        bluetooth.setOnCheckedChangeListener(switchListener);

        //TextView
        takePictureFront = (TextView) v.findViewById(R.id.take_pic_front);
        takePictureBack = (TextView) v.findViewById(R.id.take_pic_back);
        previousTrack = (TextView) v.findViewById(R.id.previous);
        nextTrack = (TextView) v.findViewById(R.id.next);
        pausePlay = (TextView) v.findViewById(R.id.play_pause);

        takePictureFront.setOnClickListener(clickListener);
        takePictureBack.setOnClickListener(clickListener);
        previousTrack.setOnClickListener(clickListener);
        nextTrack.setOnClickListener(clickListener);
        pausePlay.setOnClickListener(clickListener);

        //SeekBar
        alarm = (SeekBar) v.findViewById(R.id.volume_alarm);
        notification = (SeekBar) v.findViewById(R.id.volume_notification);
        all = (SeekBar) v.findViewById(R.id.volume_all);
        ringer = (SeekBar) v.findViewById(R.id.volume_ringer);
        media = (SeekBar) v.findViewById(R.id.volume_media);
        brightness = (SeekBar) v.findViewById(R.id.brightness);

        alarm.setOnSeekBarChangeListener(seekBarListener);
        notification.setOnSeekBarChangeListener(seekBarListener);
        all.setOnSeekBarChangeListener(seekBarListener);
        ringer.setOnSeekBarChangeListener(seekBarListener);
        media.setOnSeekBarChangeListener(seekBarListener);
        brightness.setOnSeekBarChangeListener(seekBarListener);

        return v;
    }

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

        if (cvs != null && cvs.length() > 0) {
            P2PManager.enqueueMessage(new Message(builder.toString(), Message.MessageType.SEND_COMMAND));
        } else {
            log("please enter a message string or please init p2pManager");
        }

    }

    private void showAlarmVolumeDialog() {
        final Dialog dialog = getDialog();
        dialog.setContentView(R.layout.seekbar);
        ((TextView) dialog.findViewById(R.id.title)).setText("Alarm volume");

        final MaterialSeekBar materialSeekBar = (MaterialSeekBar) dialog.findViewById(R.id.material_seekbar);
        materialSeekBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM));
        materialSeekBar.setOnProgressChangedListener(new MaterialSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(int max, int progress) {
                ((TextView) dialog.findViewById(R.id.text)).setText("Alarm volume will be set to: " + (Math.round((progress / (float) max) * 100)) + "%");
            }
        });

        try {
            final TriggerOrCommand command = getTriggerOrCommandFromArray(Type.COMMAND, Utility.ALARM_VOLUME_SETTING);
            if (command != null)
                materialSeekBar.setProgress(Integer.parseInt(command.getValue()));
            else
                materialSeekBar.setProgress(5);
        } catch (Exception e) {
            e.printStackTrace();
        }
        dialog.findViewById(R.id.save_cancel).findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.save_cancel).findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCommandValue(Utility.ALARM_VOLUME_SETTING, String.valueOf(materialSeekBar.getProgress()));
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void showMediaVolumeDialog() {
        final Dialog dialog = getDialog();
        dialog.setContentView(R.layout.seekbar);
        ((TextView) dialog.findViewById(R.id.title)).setText("Media");
        ((TextView) dialog.findViewById(R.id.text)).setText("Media volume");

        final MaterialSeekBar materialSeekBar = (MaterialSeekBar) dialog.findViewById(R.id.material_seekbar);
        materialSeekBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        materialSeekBar.setOnProgressChangedListener(new MaterialSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(int max, int progress) {
                ((TextView) dialog.findViewById(R.id.text)).setText(String.format("Media volume will be set to: %d", progress));
            }
        });
        try {
            final TriggerOrCommand command = getTriggerOrCommandFromArray(Type.COMMAND, Utility.MEDIA_VOLUME_SETTING);
            if (command != null)
                materialSeekBar.setProgress(Integer.parseInt(command.getValue()));
            else
                materialSeekBar.setProgress(5);
        } catch (Exception e) {
            e.printStackTrace();
        }
        dialog.findViewById(R.id.save_cancel).findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.save_cancel).findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCommandValue(Utility.MEDIA_VOLUME_SETTING, String.valueOf(materialSeekBar.getProgress()));
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void showNotificationVolumeDialog() {
        final Dialog dialog = getDialog();
        dialog.setContentView(R.layout.seekbar);
        ((TextView) dialog.findViewById(R.id.title)).setText("Notifications");
        ((TextView) dialog.findViewById(R.id.text)).setText("Notification volume");

        final MaterialSeekBar materialSeekBar = (MaterialSeekBar) dialog.findViewById(R.id.material_seekbar);
        materialSeekBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION));
        materialSeekBar.setOnProgressChangedListener(new MaterialSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(int max, int progress) {
                ((TextView) dialog.findViewById(R.id.text)).setText(String.format("Notification volume will be set to: %d", progress));
            }
        });

        try {
            final TriggerOrCommand command = getTriggerOrCommandFromArray(Type.COMMAND, Utility.NOTIFICATION_VOLUME_SETTING);
            if (command != null)
                materialSeekBar.setProgress(Integer.parseInt(command.getValue()));
            else
                materialSeekBar.setProgress(5);
        } catch (Exception e) {
            e.printStackTrace();
        }
        dialog.findViewById(R.id.save_cancel).findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.save_cancel).findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCommandValue(Utility.NOTIFICATION_VOLUME_SETTING, String.valueOf(materialSeekBar.getProgress()));
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void showRingtoneVolumeDialog() {
        final Dialog dialog = getDialog();
        dialog.setContentView(R.layout.seekbar);
        ((TextView) dialog.findViewById(R.id.title)).setText("Ringtones");
        ((TextView) dialog.findViewById(R.id.text)).setText("Ringtone volume");

        final MaterialSeekBar materialSeekBar = (MaterialSeekBar) dialog.findViewById(R.id.material_seekbar);
        materialSeekBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_RING));
        materialSeekBar.setOnProgressChangedListener(new MaterialSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(int max, int progress) {
                ((TextView) dialog.findViewById(R.id.text)).setText(String.format("Ringtone volume will be set to: %d", progress));
            }
        });

        try {
            final TriggerOrCommand command = getTriggerOrCommandFromArray(Type.COMMAND, Utility.RINGER_VOLUME_SETTING);
            if (command != null)
                materialSeekBar.setProgress(Integer.parseInt(command.getValue()));
            else
                materialSeekBar.setProgress(5);
        } catch (Exception e) {
            e.printStackTrace();
        }

        dialog.findViewById(R.id.save_cancel).findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.save_cancel).findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCommandValue(Utility.RINGER_VOLUME_SETTING, String.valueOf(materialSeekBar.getProgress()));
                dialog.dismiss();
            }
        });

        dialog.show();
    }
    private void showBrightnessDialog() {
        final Dialog dialog = getDialog();
        dialog.setContentView(R.layout.seekbar);

        ((TextView) dialog.findViewById(R.id.title)).setText("Brightness");
        final MaterialSeekBar materialSeekBar = (MaterialSeekBar) dialog.findViewById(R.id.material_seekbar);
        materialSeekBar.setMax(255);
        materialSeekBar.setProgress(50);
        materialSeekBar.setOnProgressChangedListener(new MaterialSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(int max, int progress) {
                ((TextView) dialog.findViewById(R.id.text)).setText("Brightness will be set to : " + (Math.round((progress / (float) max) * 100) + "%"));
            }
        });
        final MaterialCheckBox materialCheckBox = (MaterialCheckBox) dialog.findViewById(R.id.material_checkbox);
        materialCheckBox.setVisibility(View.VISIBLE);
        materialCheckBox.setText("Auto-Brightness");
        materialCheckBox.setOnCheckedChangeListener(new MaterialCheckBox.OnCheckedChangedListener() {
            @Override
            public void onCheckedChange(MaterialCheckBox materialCheckBox, boolean isChecked) {
                materialSeekBar.setVisibility(isChecked ? View.GONE : View.VISIBLE);
                dialog.findViewById(R.id.text).setVisibility(isChecked ? View.GONE : View.VISIBLE);
            }
        });
        try {
            final TriggerOrCommand command = getTriggerOrCommandFromArray(Type.COMMAND, Utility.BRIGHTNESS_SETTING);
            if (command != null) {
                if (command.getValue().equals("-1"))
                    materialCheckBox.setChecked(true);
                else
                    materialSeekBar.setProgress(Integer.parseInt(command.getValue()));
            } else
                materialSeekBar.setProgress(35);
        } catch (Exception e) {
            e.printStackTrace();
        }
        dialog.findViewById(R.id.save_cancel).findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.save_cancel).findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCommandValue(Utility.BRIGHTNESS_SETTING, materialCheckBox.isChecked() ? "-1" : String.valueOf(materialSeekBar.getProgress()));
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void showDataDialog() {
        final Dialog dialog = getDialog();
        dialog.setContentView(R.layout.radio_group);
        dialog.findViewById(R.id.coming_soon).setVisibility(View.VISIBLE);
        ((TextView) dialog.findViewById(R.id.title)).setText("Data");
        final MaterialRadioGroup materialRadioGroup = (MaterialRadioGroup) dialog.findViewById(R.id.material_radio_group);
        final ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, dpToPixels(40));

        final MaterialRadioButton button1 = new MaterialRadioButton(this);
        button1.setLayoutParams(params);
        button1.setText("Off");
        final MaterialRadioButton button2 = new MaterialRadioButton(this);
        button2.setLayoutParams(params);
        button2.setText("2G");
        final MaterialRadioButton button3 = new MaterialRadioButton(this);
        button3.setLayoutParams(params);
        button3.setText("3G");
        final MaterialRadioButton button4 = new MaterialRadioButton(this);
        button4.setLayoutParams(params);
        button4.setText("4G");

        materialRadioGroup.addView(button1);
        materialRadioGroup.addView(button2);
        materialRadioGroup.addView(button3);
        materialRadioGroup.addView(button4);

        try {
            final TriggerOrCommand command = getTriggerOrCommandFromArray(Type.COMMAND, Utility.DATA_SETTING);
            if (command != null)
                materialRadioGroup.setSelected(Integer.parseInt(command.getValue()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        dialog.findViewById(R.id.save_cancel).findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCommandValue(Utility.DATA_SETTING, String.valueOf(materialRadioGroup.getSelection()));
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.save_cancel).findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
    private void showSleepTimeoutDialog() {
        final Dialog dialog = getDialog();
        dialog.setContentView(R.layout.radio_group);

        ((TextView) dialog.findViewById(R.id.title)).setText("Data");
        final MaterialRadioGroup materialRadioGroup = (MaterialRadioGroup) dialog.findViewById(R.id.material_radio_group);
        final ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, dpToPixels(40));

        //Todo get the values from this and add them to the profile service >> make a method getScreenTimeOutTextByInt(that returns the secs/mins then use this in data and the music player
        final MaterialRadioButton button1 = new MaterialRadioButton(this);
        button1.setLayoutParams(params);
        button1.setText("10 secs");
        final MaterialRadioButton button2 = new MaterialRadioButton(this);
        button2.setLayoutParams(params);
        button2.setText("30 secs");
        final MaterialRadioButton button3 = new MaterialRadioButton(this);
        button3.setLayoutParams(params);
        button3.setText("1 min");
        final MaterialRadioButton button4 = new MaterialRadioButton(this);
        button4.setLayoutParams(params);
        button4.setText("2 min");
        final MaterialRadioButton button5 = new MaterialRadioButton(this);
        button5.setLayoutParams(params);
        button5.setText("5 min");
        final MaterialRadioButton button6 = new MaterialRadioButton(this);
        button6.setLayoutParams(params);
        button6.setText("10 min");
        final MaterialRadioButton button7 = new MaterialRadioButton(this);
        button7.setLayoutParams(params);
        button7.setText("15 min");
        final MaterialRadioButton button8 = new MaterialRadioButton(this);
        button8.setLayoutParams(params);
        button8.setText("30 min");

        materialRadioGroup.addView(button1);
        materialRadioGroup.addView(button2);
        materialRadioGroup.addView(button3);
        materialRadioGroup.addView(button5);
        materialRadioGroup.addView(button6);
        materialRadioGroup.addView(button7);
        materialRadioGroup.addView(button8);

        try {
            final TriggerOrCommand command = getTriggerOrCommandFromArray(Type.COMMAND, Utility.SLEEP_TIMEOUT_SETTING);
            if (command != null)
                materialRadioGroup.setSelected(Integer.parseInt(command.getValue()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        dialog.findViewById(R.id.save_cancel).findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCommandValue(Utility.SLEEP_TIMEOUT_SETTING, String.valueOf(materialRadioGroup.getSelection()));
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.save_cancel).findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void showMusicPlayerDialog() {
        final Dialog dialog = getDialog();
        dialog.setContentView(R.layout.radio_group);
        dialog.findViewById(R.id.coming_soon).setVisibility(View.VISIBLE);
        ((TextView) dialog.findViewById(R.id.title)).setText("Media Control");
        final MaterialRadioGroup materialRadioGroup = (MaterialRadioGroup) dialog.findViewById(R.id.material_radio_group);
        final ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, dpToPixels(40));

        //Todo get the values from this and add them to the profile service

        final MaterialRadioButton button1 = new MaterialRadioButton(this);
        button1.setText("Play");
        button1.setLayoutParams(params);
        final MaterialRadioButton button2 = new MaterialRadioButton(this);
        button2.setLayoutParams(params);
        button2.setText("Pause");
        final MaterialRadioButton button3 = new MaterialRadioButton(this);
        button3.setLayoutParams(params);
        button3.setText("Previous");
        final MaterialRadioButton button4 = new MaterialRadioButton(this);
        button4.setLayoutParams(params);
        button4.setText("Skip");

        materialRadioGroup.addView(button1);
        materialRadioGroup.addView(button2);
        materialRadioGroup.addView(button3);
        materialRadioGroup.addView(button4);

        dialog.findViewById(R.id.coming_soon).setVisibility(View.VISIBLE);

        try {
            final TriggerOrCommand command = getTriggerOrCommandFromArray(Type.COMMAND, Utility.MEDIA_CONTROL_SETTING);
            if (command != null)
                materialRadioGroup.setSelected(Integer.parseInt(command.getValue()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        dialog.findViewById(R.id.save_cancel).findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCommandValue(Utility.MEDIA_CONTROL_SETTING, String.valueOf(materialRadioGroup.getSelection()));
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.save_cancel).findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void showAutoRotationDialog() {
        final Dialog dialog = getDialog();
        dialog.setContentView(R.layout.switch_item);
        ((TextView) dialog.findViewById(R.id.title)).setText("Auto-rotation");
        final MaterialSwitch materialSwitch = (MaterialSwitch) dialog.findViewById(R.id.material_switch);
        materialSwitch.setText("Auto-rotation");
        try {
            final TriggerOrCommand command = getTriggerOrCommandFromArray(Type.COMMAND, Utility.AUTO_ROTATION_SETTING);
            if (command != null)
                materialSwitch.setChecked(command.getValue().equals("1"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        dialog.findViewById(R.id.save_cancel).findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCommandValue(Utility.AUTO_ROTATION_SETTING, materialSwitch.isChecked() ? "1" : "0");
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.save_cancel).findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void showSyncDialog() {
        final Dialog dialog = getDialog();
        dialog.setContentView(R.layout.switch_item);
        ((TextView) dialog.findViewById(R.id.title)).setText("Sync");
        final MaterialSwitch materialSwitch = (MaterialSwitch) dialog.findViewById(R.id.material_switch);
        dialog.findViewById(R.id.coming_soon).setVisibility(View.VISIBLE);
        materialSwitch.setText("Sync");
        try {
            final TriggerOrCommand command = getTriggerOrCommandFromArray(Type.COMMAND, Utility.ACCOUNT_SYNC_SETTING);
            if (command != null)
                materialSwitch.setChecked(command.getValue().equals("1"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        dialog.findViewById(R.id.save_cancel).findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCommandValue(Utility.ACCOUNT_SYNC_SETTING, materialSwitch.isChecked() ? "1" : "0");
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.save_cancel).findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void showBluetoothDialog() {
        final Dialog dialog = getDialog();
        dialog.setContentView(R.layout.switch_item);
        ((TextView) dialog.findViewById(R.id.title)).setText("Bluetooth");
        final MaterialSwitch materialSwitch = (MaterialSwitch) dialog.findViewById(R.id.material_switch);
        materialSwitch.setText("Bluetooth");

        try {
            final TriggerOrCommand command = getTriggerOrCommandFromArray(Type.COMMAND, Utility.BLUETOOTH_SETTING);
            if (command != null)
                materialSwitch.setChecked(command.getValue().equals("1"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        dialog.findViewById(R.id.save_cancel).findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCommandValue(Utility.BLUETOOTH_SETTING, materialSwitch.isChecked() ? "1" : "0");
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.save_cancel).findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void showWifiDialog() {
        final Dialog dialog = getDialog();
        dialog.setContentView(R.layout.switch_item);
        ((TextView) dialog.findViewById(R.id.title)).setText("Wifi");
        final MaterialSwitch materialSwitch = (MaterialSwitch) dialog.findViewById(R.id.material_switch);
        materialSwitch.setText("Wifi");

        try {
            final TriggerOrCommand command = getTriggerOrCommandFromArray(Type.COMMAND, Utility.WIFI_SETTING);
            if (command != null)
                materialSwitch.setChecked(command.getValue().equals("1"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        dialog.findViewById(R.id.save_cancel).findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCommandValue(Utility.WIFI_SETTING, materialSwitch.isChecked() ? "1" : "0");
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.save_cancel).findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void showSilentModeDialog() {
        final Dialog dialog = getDialog();
        dialog.setContentView(R.layout.switch_item);
        ((TextView) dialog.findViewById(R.id.title)).setText("Silent Mode");
        final MaterialSwitch materialSwitch = (MaterialSwitch) dialog.findViewById(R.id.material_switch);
        materialSwitch.setText("Silent Mode");

        try {
            final TriggerOrCommand command = getTriggerOrCommandFromArray(Type.COMMAND, Utility.SILENT_MODE_SETTING);
            if (command != null)
                materialSwitch.setChecked(command.getValue().equals("1"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        dialog.findViewById(R.id.save_cancel).findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCommandValue(Utility.SILENT_MODE_SETTING, materialSwitch.isChecked() ? "1" : "0");
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.save_cancel).findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    private static void log(String msg) {
        Log.e("Remote", msg);
    }
}