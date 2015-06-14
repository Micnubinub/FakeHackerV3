package tbs.fakehackerv3.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import tbs.fakehackerv3.Message;
import tbs.fakehackerv3.P2PManager;
import tbs.fakehackerv3.R;
import tbs.fakehackerv3.StaticValues;
import tbs.fakehackerv3.custom_views.MaterialCheckBox;
import tbs.fakehackerv3.custom_views.MaterialRadioButton;
import tbs.fakehackerv3.custom_views.MaterialRadioGroup;
import tbs.fakehackerv3.custom_views.MaterialSeekBar;
import tbs.fakehackerv3.custom_views.MaterialSwitch;

/**
 * Created by Michael on 6/10/2015.
 */
public class RemoteMaterial extends Fragment {

    private static ListView list;
    private static Activity context;
    private static final String[] commands = {"Wifi", "Bluetooth", "Data", "Brightness", "Silent mode",
            "Notification volume", "Alarm volume", "Media",
            "Media Volume", "Ringer volume", "Auto rotation", "Sleep time out", "Sync", "Take picture front", "Take picture back", "Toggle flash"};

    @Nullable
    @Override
    public View getView() {
        //Todo
        context = getActivity();
        list = (ListView) View.inflate(getActivity(), R.layout.remote_fragment, null);
        list.setAdapter(new CommandAdapter());
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openDialog(position);
            }
        });
        return list;
    }

    private void openDialog(int i) {
        switch (i) {
            case 0:
                showWifiDialog();
                break;
            case 1:
                showBluetoothDialog();
                break;
            case 2:
                showDataDialog();
                break;
            case 3:
                showBrightnessDialog();
                break;
            case 4:
                showSilentModeDialog();
                break;
            case 5:
                showNotificationVolumeDialog();
                break;
            case 6:
                showAlarmVolumeDialog();
                break;
            case 7:
                showMusicPlayerDialog();
                break;
            case 8:
                showMediaVolumeDialog();
                break;
            case 9:
                showRingtoneVolumeDialog();
                break;
            case 10:
                showAutoRotationDialog();
                break;
            case 11:
                showSleepTimeoutDialog();
                break;
            case 12:
                showSyncDialog();
                break;
            case 13:
                sendCommand(StaticValues.TAKE_PICTURE_FRONT, "");
                break;
            case 14:
                sendCommand(StaticValues.TAKE_PICTURE_BACK, "");
                break;
        }
    }

    private static final View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.take_pic_back:

                    break;
                case R.id.take_pic_front:
                    sendCommand(StaticValues.TAKE_PICTURE_FRONT, "");
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

    private Dialog getDialog() {
        return new Dialog(context, R.style.CustomDialog);
    }

    private void showAlarmVolumeDialog() {
        final Dialog dialog = getDialog();
        dialog.setContentView(R.layout.seekbar);
        ((TextView) dialog.findViewById(R.id.title)).setText("Alarm volume");

        final MaterialSeekBar materialSeekBar = (MaterialSeekBar) dialog.findViewById(R.id.material_seekbar);
        materialSeekBar.setOnProgressChangedListener(new MaterialSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(int max, int progress) {
                ((TextView) dialog.findViewById(R.id.text)).setText("Alarm volume will be set to: " + (Math.round((progress / (float) max) * 100)) + "%");
            }
        });


        dialog.findViewById(R.id.save_cancel).findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.save_cancel).findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCommand(StaticValues.SET_ALARM_VOLUME, String.valueOf(materialSeekBar.getProgress()));
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
        materialSeekBar.setOnProgressChangedListener(new MaterialSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(int max, int progress) {
                ((TextView) dialog.findViewById(R.id.text)).setText(String.format("Media volume will be set to: %d", progress));
            }
        });

        dialog.findViewById(R.id.save_cancel).findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.save_cancel).findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCommand(StaticValues.SET_MEDIA_VOLUME, String.valueOf(materialSeekBar.getProgress()));
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
        materialSeekBar.setOnProgressChangedListener(new MaterialSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(int max, int progress) {
                ((TextView) dialog.findViewById(R.id.text)).setText(String.format("Notification volume will be set to: %d", progress));
            }
        });


        dialog.findViewById(R.id.save_cancel).findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.save_cancel).findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCommand(StaticValues.SET_NOTIFICATION_VOLUME, String.valueOf(materialSeekBar.getProgress()));
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
        materialSeekBar.setOnProgressChangedListener(new MaterialSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(int max, int progress) {
                ((TextView) dialog.findViewById(R.id.text)).setText(String.format("Ringtone volume will be set to: %d", progress));
            }
        });

        dialog.findViewById(R.id.save_cancel).findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.save_cancel).findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCommand(StaticValues.SET_RINGER_VOLUME, String.valueOf(materialSeekBar.getProgress()));
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


        dialog.findViewById(R.id.save_cancel).findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.save_cancel).findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (materialCheckBox.isChecked()) {
                    sendCommand(StaticValues.SET_BRIGHTNESS_MODE, "1");
                } else {
                    sendCommand(StaticValues.SET_BRIGHTNESS, String.valueOf(materialSeekBar.getProgress()));
                }
            }
        });

        dialog.show();
    }

    private void showDataDialog() {
        final Dialog dialog = getDialog();
        dialog.setContentView(R.layout.radio_group);
        ((TextView) dialog.findViewById(R.id.title)).setText("Data");
        final MaterialRadioGroup materialRadioGroup = (MaterialRadioGroup) dialog.findViewById(R.id.material_radio_group);
        final ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, dpToPixels(40));

        final MaterialRadioButton button1 = new MaterialRadioButton(context);
        button1.setLayoutParams(params);
        button1.setText("Off");
        final MaterialRadioButton button2 = new MaterialRadioButton(context);
        button2.setLayoutParams(params);
        button2.setText("2G");
        final MaterialRadioButton button3 = new MaterialRadioButton(context);
        button3.setLayoutParams(params);
        button3.setText("3G");
        final MaterialRadioButton button4 = new MaterialRadioButton(context);
        button4.setLayoutParams(params);
        button4.setText("4G");

        materialRadioGroup.addView(button1);
        materialRadioGroup.addView(button2);
        materialRadioGroup.addView(button3);
        materialRadioGroup.addView(button4);


        dialog.findViewById(R.id.save_cancel).findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Todo
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

    private int dpToPixels(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    private void showSleepTimeoutDialog() {
        final Dialog dialog = getDialog();
        dialog.setContentView(R.layout.radio_group);

        ((TextView) dialog.findViewById(R.id.title)).setText("Data");
        final MaterialRadioGroup materialRadioGroup = (MaterialRadioGroup) dialog.findViewById(R.id.material_radio_group);
        final ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, dpToPixels(40));

        //Todo get the values from this and add them to the profile service >> make a method getScreenTimeOutTextByInt(that returns the secs/mins then use this in data and the music player
        final MaterialRadioButton button1 = new MaterialRadioButton(context);
        button1.setLayoutParams(params);
        button1.setText("10 secs");
        final MaterialRadioButton button2 = new MaterialRadioButton(context);
        button2.setLayoutParams(params);
        button2.setText("30 secs");
        final MaterialRadioButton button3 = new MaterialRadioButton(context);
        button3.setLayoutParams(params);
        button3.setText("1 min");
        final MaterialRadioButton button4 = new MaterialRadioButton(context);
        button4.setLayoutParams(params);
        button4.setText("2 min");
        final MaterialRadioButton button5 = new MaterialRadioButton(context);
        button5.setLayoutParams(params);
        button5.setText("5 min");
        final MaterialRadioButton button6 = new MaterialRadioButton(context);
        button6.setLayoutParams(params);
        button6.setText("10 min");
        final MaterialRadioButton button7 = new MaterialRadioButton(context);
        button7.setLayoutParams(params);
        button7.setText("15 min");
        final MaterialRadioButton button8 = new MaterialRadioButton(context);
        button8.setLayoutParams(params);
        button8.setText("30 min");

        materialRadioGroup.addView(button1);
        materialRadioGroup.addView(button2);
        materialRadioGroup.addView(button3);
        materialRadioGroup.addView(button5);
        materialRadioGroup.addView(button6);
        materialRadioGroup.addView(button7);
        materialRadioGroup.addView(button8);


        dialog.findViewById(R.id.save_cancel).findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Todo
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
        ((TextView) dialog.findViewById(R.id.title)).setText("Media Control");
        final MaterialRadioGroup materialRadioGroup = (MaterialRadioGroup) dialog.findViewById(R.id.material_radio_group);
        final ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, dpToPixels(40));

        //Todo get the values from this and add them to the profile service

        final MaterialRadioButton button1 = new MaterialRadioButton(context);
        button1.setText("Play");
        button1.setLayoutParams(params);
        final MaterialRadioButton button2 = new MaterialRadioButton(context);
        button2.setLayoutParams(params);
        button2.setText("Pause");
        final MaterialRadioButton button3 = new MaterialRadioButton(context);
        button3.setLayoutParams(params);
        button3.setText("Previous");
        final MaterialRadioButton button4 = new MaterialRadioButton(context);
        button4.setLayoutParams(params);
        button4.setText("Skip");

        materialRadioGroup.addView(button1);
        materialRadioGroup.addView(button2);
        materialRadioGroup.addView(button3);
        materialRadioGroup.addView(button4);

        dialog.findViewById(R.id.save_cancel).findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
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


        dialog.findViewById(R.id.save_cancel).findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Todo sendCommand(StaticValues.MEDIA_CONTROL_PREVIOUS, materialSwitch.isChecked()?"1":"0");

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
        materialSwitch.setText("Sync");


        dialog.findViewById(R.id.save_cancel).findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Todo sendCommand(StaticValues.MEDIA_CONTROL_PREVIOUS, materialSwitch.isChecked()?"1":"0");

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


        dialog.findViewById(R.id.save_cancel).findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendCommand(StaticValues.SET_BLUETOOTH, materialSwitch.isChecked() ? "1" : "0");
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

        dialog.findViewById(R.id.save_cancel).findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                setCommandValue(Utility.WIFI_SETTING, materialSwitch.isChecked() ? "1" : "0");
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


        dialog.findViewById(R.id.save_cancel).findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //                setCommandValue(Utility.WIFI_SETTING, materialSwitch.isChecked() ? "1" : "0");
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

    private static class CommandAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return commands.length;
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
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.command_item, null);
            }

            ((TextView) convertView).setText(commands[position]);
            return convertView;
        }
    }

    public static void toast(final String msg) {
        if (context != null) {
            try {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void log(String msg) {
        Log.e("Remote", msg);
    }
}