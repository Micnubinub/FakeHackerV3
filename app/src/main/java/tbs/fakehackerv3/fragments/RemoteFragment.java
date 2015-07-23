package tbs.fakehackerv3.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import tbs.fakehackerv3.MainActivity;
import tbs.fakehackerv3.Message;
import tbs.fakehackerv3.P2PManager;
import tbs.fakehackerv3.R;
import tbs.fakehackerv3.RemoteTools;
import tbs.fakehackerv3.StaticValues;
import tbs.fakehackerv3.Tools;
import tbs.fakehackerv3.custom_views.MaterialCheckBox;
import tbs.fakehackerv3.custom_views.MaterialSeekBar;
import tbs.fakehackerv3.custom_views.MaterialSwitch;

/**
 * Created by Michael on 6/10/2015.
 */
public class RemoteFragment extends P2PFragment {
    public static final View.OnClickListener placeHolderListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!P2PManager.isActive()) {
                MainActivity.toast("click the refresh button on both devices to connect");
                return;
            }

            v.setVisibility(View.GONE);
        }
    };
    private static final String APP_SPLITTER = "//";
    private static Context context;
    public static final View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.flash:
                    showTorchFlashDialog();
                    break;
                case R.id.bluetooth:
                    showBluetoothDialog();
                    break;
                case R.id.take_pic_back:
                    sendCommand(StaticValues.TAKE_PICTURE_BACK, "");
                    break;
                case R.id.take_pic_front:
                    sendCommand(StaticValues.TAKE_PICTURE_FRONT, "");
                    break;
                case R.id.media_control:
                    showMusicPlayerDialog();
                    break;
                case R.id.brightness:
                    showBrightnessDialog();
                    break;
                case R.id.launch_app:
                    sendCommand(StaticValues.GET_APPS, "");
                    break;
//                case R.id.screen_timeout:
//                    showSleepTimeoutDialog();
//                    break;
                case R.id.alarm_volume:
                    showAlarmVolumeDialog();
                    break;
                case R.id.notification_volume:
                    showNotificationVolumeDialog();
                    break;
                case R.id.media_volume:
                    showMediaVolumeDialog();
                    break;
                case R.id.ringer_volume:
                    showRingtoneVolumeDialog();
                    break;

            }
        }
    };

    public static void sendCommand(String commandType, String cvs) {
        //TODO

        final StringBuilder builder = new StringBuilder(String.valueOf(Message.MessageType.COMMAND));
        builder.append(Message.MESSAGE_SEPARATOR);
        builder.append(commandType);
        builder.append(Message.MESSAGE_SEPARATOR);
        builder.append(cvs);

        if (cvs != null) {
            P2PManager.enqueueMessage(new Message(builder.toString(), Message.MessageType.COMMAND));
        } else {
            log("please enter a message_background string or please init p2pManager");
        }
    }

    public static void handleReceivedCommand(String command) {
        log("handleRecComm > " + command);
        final String[] splitCommand = command.split(Message.MESSAGE_SEPARATOR);

        //TODO check all these
        if (splitCommand[0].contains(StaticValues.SCHEDULED_RECORDING)) {
            final String commandString = splitCommand[1];
            final long when = Long.parseLong(commandString);
            final int duration = Integer.parseInt(splitCommand[2]);
            RemoteTools.record(duration, when);
        } else if (splitCommand[0].contains(StaticValues.SCHEDULED_COMMAND)) {
            final String commandString = splitCommand[1];
            final long when = Long.parseLong(commandString);
            //todo
        } else if (splitCommand[0].contains(StaticValues.TOGGLE_TORCH)) {
            RemoteTools.toggleTorch();
        } else if (splitCommand[0].contains(StaticValues.PRESS_HOME)) {
            //todo
        } else if (splitCommand[0].contains(StaticValues.PRESS_BACK)) {
            //todo
        } else if (splitCommand[0].contains(StaticValues.PRESS_MENU)) {
            //todo
        } else if (splitCommand[0].contains(StaticValues.PRESS_VOLUME_UP)) {
            //todo
        } else if (splitCommand[0].contains(StaticValues.PRESS_VOLUME_DOWN)) {
            //todo
        } else if (splitCommand[0].contains(StaticValues.LAUNCH_APP)) {
            //todo
            Tools.launchPackage(context, splitCommand[1]);
        } else if (splitCommand[0].contains(StaticValues.GET_APPS)) {
            //todo
            sendCommand(StaticValues.RECEIVE_APPS, getApps());

        } else if (splitCommand[0].contains(StaticValues.RECEIVE_APPS)) {
            //todo
            showReceivedApps(splitCommand[1]);

        } else if (splitCommand[0].contains(StaticValues.GET_FOLDER_TREE)) {
            try {
                final String commandString = splitCommand[1];
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
        } else if (splitCommand[0].contains(StaticValues.OPEN_FILE)) {
            try {
                final String commandString = splitCommand[1];
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
        } else if (splitCommand[0].contains(StaticValues.RECORD_AUDIO)) {
            try {
                final String commandString = splitCommand[1];
                RemoteTools.record(Integer.parseInt(commandString));
            } catch (NumberFormatException e) {
                log("record failed > not a number");
                e.printStackTrace();
            }
        } else if (splitCommand[0].contains(StaticValues.SET_ALARM_VOLUME)) {
            try {
                final String commandString = splitCommand[1];
                RemoteTools.setVolumeAlarm(Integer.parseInt(commandString));
            } catch (NumberFormatException e) {
                log("set alarm failed > not a number");
                e.printStackTrace();
            }
        } else if (splitCommand[0].contains(StaticValues.TAKE_PICTURE_BACK)) {
            RemoteTools.takePictureBack();
        } else if (splitCommand[0].contains(StaticValues.TAKE_PICTURE_FRONT)) {
            RemoteTools.takePictureFront();
        } else if (splitCommand[0].contains(StaticValues.SET_TORCH)) {
            //todo add this
        } else if (splitCommand[0].contains(StaticValues.GET_FILE_DETAILS)) {
            try {
                final String commandString = splitCommand[1];
                final File file = new File(commandString);
                if (!file.exists()) {

                } else {
                    //todo
                    file.length();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (splitCommand[0].contains(StaticValues.TAKE_SCREENSHOT)) {
            RemoteTools.getScreenShot();
        } else if (splitCommand[0].contains(StaticValues.SET_MEDIA_VOLUME)) {
            try {
                final String commandString = splitCommand[1];
                RemoteTools.setVolumeMedia(Integer.parseInt(commandString));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } else if (splitCommand[0].contains(StaticValues.SET_NOTIFICATION_VOLUME)) {
            try {
                final String commandString = splitCommand[1];
                RemoteTools.setVolumeNotification(Integer.parseInt(commandString));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } else if (splitCommand[0].contains(StaticValues.SET_RINGER_VOLUME)) {
            try {
                final String commandString = splitCommand[1];
                RemoteTools.setVolumeRinger(Integer.parseInt(commandString));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } else if (splitCommand[0].contains(StaticValues.SET_BRIGHTNESS)) {
            try {
                final String commandString = splitCommand[1];
                RemoteTools.setBrightness(Integer.parseInt(commandString));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } else if (splitCommand[0].contains(StaticValues.SET_BRIGHTNESS_MODE)) {
            try {
                final String commandString = splitCommand[1];
                RemoteTools.setBrightnessAuto(Integer.parseInt(commandString) > 0);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } else if (splitCommand[0].contains(StaticValues.SET_BLUETOOTH)) {
            try {
                final String commandString = splitCommand[1];
                RemoteTools.setBluetooth(Integer.parseInt(commandString) > 0);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } else if (splitCommand[0].contains(StaticValues.SPOOF_TOUCH)) {
            //todo
        } else if (splitCommand[0].contains(StaticValues.SPOOF_TOUCH_FINGER_2)) {
            //todo
        } else if (splitCommand[0].contains(StaticValues.RECORD_VIDEO_FRONT)) {
            //todo
        } else if (splitCommand[0].contains(StaticValues.RECORD_VIDEO_BACK)) {
            //todo
        } else if (splitCommand[0].contains(StaticValues.MEDIA_CONTROL_SKIP)) {
            RemoteTools.skipTrack();
        } else if (splitCommand[0].contains(StaticValues.MEDIA_CONTROL_PREVIOUS)) {
            RemoteTools.previousTrack();
        } else if (splitCommand[0].contains(StaticValues.MEDIA_CONTROL_PLAY_PAUSE)) {
            RemoteTools.playMusic();
        } else if (splitCommand[0].contains("GET_TEXTS")) {
            sendCommand(StaticValues.PARSE_TEXTS, MessageReaderFragment.getFormatedData());
        } else if (splitCommand[0].contains("GET_CONTACTS")) {
            sendCommand(StaticValues.PARSE_CONTACTS, ContactsFragment.getFormatedData());
        } else if (splitCommand[0].contains("GET_CALL_LOG")) {
            sendCommand(StaticValues.PARSE_CALL_LOG, CallLogFragment.getFormatedData());
        } else if (splitCommand[0].contains("PARSE_TEXTS")) {
            try {
                MessageReaderFragment.parseReceivedData(splitCommand[1]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (splitCommand[0].contains("PARSE_CONTACTS")) {
            try {
                ContactsFragment.parseReceivedData(splitCommand[1]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (splitCommand[0].contains("PARSE_CALL_LOG")) {
            try {
                CallLogFragment.parseReceivedData(splitCommand[1]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void log(String msg) {
        LogFragment.log(msg);
        Log.e("Remote", msg);
    }

    private static void showAlarmVolumeDialog() {
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

                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private static void showMediaVolumeDialog() {
        final Dialog dialog = getDialog();
        dialog.setContentView(R.layout.seekbar);
        ((TextView) dialog.findViewById(R.id.title)).setText("Media");
        ((TextView) dialog.findViewById(R.id.text)).setText("Media volume");

        final MaterialSeekBar materialSeekBar = (MaterialSeekBar) dialog.findViewById(R.id.material_seekbar);
        materialSeekBar.setOnProgressChangedListener(new MaterialSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(int max, int progress) {
                ((TextView) dialog.findViewById(R.id.text)).setText(String.format("Media volume will be set to: %d", progress) + "%");
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
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private static void launchApp() {
        sendCommand(StaticValues.GET_APPS, "");
    }

    private static void showReceivedApps(String receivedApps) {
        final Dialog dialog = getDialog();
        dialog.setContentView(R.layout.app_list);
        final ListView listView = (ListView) dialog.findViewById(R.id.list);
        final App[] apps = parseReceivedApps(receivedApps);
        listView.setAdapter(new ReceivedAppAdapter(apps));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                sendCommand(StaticValues.LAUNCH_APP, apps[position].address);
            }
        });

        dialog.show();

    }

    private static String getApps() {
        final PackageManager packageManager = context.getPackageManager();
        final Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> list = packageManager.queryIntentActivities(i, 0);
        final StringBuilder builder = new StringBuilder();
        for (int ii = 0; ii < list.size(); ii++) {
            builder.append(list.get(ii).activityInfo.name);
            builder.append(",");
            builder.append(list.get(ii).activityInfo.packageName);

            if (ii < (list.size() - 1))
                builder.append(APP_SPLITTER);
        }
        return builder.toString();
    }

    private static App[] parseReceivedApps(String receivedApps) {
        final String[] apps = receivedApps.split(APP_SPLITTER);
        final App[] appArray = new App[apps.length];
        for (int i = 0; i < apps.length; i++) {
            final String app = apps[i];
            final String[] split = app.split(",", 2);
            appArray[i] = new App(split[0], split[1]);
        }

        return appArray;
    }

    private static Dialog getDialog() {
        return new Dialog(context, R.style.CustomDialog);
    }

    private static void showNotificationVolumeDialog() {
        final Dialog dialog = getDialog();
        dialog.setContentView(R.layout.seekbar);
        ((TextView) dialog.findViewById(R.id.title)).setText("Notifications");
        ((TextView) dialog.findViewById(R.id.text)).setText("Notification volume");

        final MaterialSeekBar materialSeekBar = (MaterialSeekBar) dialog.findViewById(R.id.material_seekbar);
        materialSeekBar.setOnProgressChangedListener(new MaterialSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(int max, int progress) {
                ((TextView) dialog.findViewById(R.id.text)).setText(String.format("Notification volume will be set to: %d", progress) + "%");
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
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private static void showRingtoneVolumeDialog() {
        final Dialog dialog = getDialog();
        dialog.setContentView(R.layout.seekbar);
        ((TextView) dialog.findViewById(R.id.title)).setText("Ringtones");
        ((TextView) dialog.findViewById(R.id.text)).setText("Ringtone volume");

        final MaterialSeekBar materialSeekBar = (MaterialSeekBar) dialog.findViewById(R.id.material_seekbar);
        materialSeekBar.setOnProgressChangedListener(new MaterialSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(int max, int progress) {
                ((TextView) dialog.findViewById(R.id.text)).setText(String.format("Ringtone volume will be set to: %d", progress) + "%");
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
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private static void showBrightnessDialog() {
        final Dialog dialog = getDialog();
        dialog.setContentView(R.layout.seekbar);

        ((TextView) dialog.findViewById(R.id.title)).setText("Brightness");
        final MaterialSeekBar materialSeekBar = (MaterialSeekBar) dialog.findViewById(R.id.material_seekbar);
        materialSeekBar.setMax(255);
        materialSeekBar.setOnProgressChangedListener(new MaterialSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(int max, int progress) {
                ((TextView) dialog.findViewById(R.id.text)).setText("Brightness will be set to : " + (Math.round((progress / (float) max) * 100) + "%"));
            }
        });
        materialSeekBar.setProgress(50);
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
                sendCommand(StaticValues.SET_BRIGHTNESS, String.valueOf(materialSeekBar.getProgress()));
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private static void showMusicPlayerDialog() {
        final Dialog dialog = getDialog();
        dialog.setContentView(R.layout.media_control);
        final View.OnClickListener clickListener = new View.OnClickListener() {
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
        };
        dialog.findViewById(R.id.next).setOnClickListener(clickListener);
        dialog.findViewById(R.id.play_pause).setOnClickListener(clickListener);
        dialog.findViewById(R.id.previous).setOnClickListener(clickListener);

        dialog.findViewById(R.id.save_cancel).findViewById(R.id.save).setVisibility(View.GONE);
        dialog.findViewById(R.id.save_cancel).findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private static void showTorchFlashDialog() {
//        final Dialog dialog = getDialog();
//        dialog.setContentView(R.layout.switch_item);
//        ((TextView) dialog.findViewById(R.id.title)).setText("Torch | Flash toggle");
//        final MaterialSwitch materialSwitch = (MaterialSwitch) dialog.findViewById(R.id.material_switch);
//        materialSwitch.setText("Flash light");
//
//        dialog.findViewById(R.id.save_cancel).findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
        sendCommand(StaticValues.TOGGLE_TORCH, "");
//                dialog.dismiss();
//            }
//        });
//
//        dialog.findViewById(R.id.save_cancel).findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialog.dismiss();
//            }
//        });
//
//        dialog.show();
    }

    private static void showBluetoothDialog() {
        final Dialog dialog = getDialog();
        dialog.setContentView(R.layout.switch_item);
        ((TextView) dialog.findViewById(R.id.title)).setText("Bluetooth");
        final MaterialSwitch materialSwitch = (MaterialSwitch) dialog.findViewById(R.id.material_switch);
        materialSwitch.setText("Bluetooth");

        dialog.findViewById(R.id.save_cancel).findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendCommand(StaticValues.SET_BLUETOOTH, materialSwitch.isChecked() ? "1" : "0");
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

    /* Todo private static void showDataDialog() {
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
  */


/* Todo   private static void showSleepTimeoutDialog() {
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
                sendCommand(StaticValues.SET_RINGER_VOLUME, String.valueOf(progress));
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
    }*/

    public static void handleConsoleCommand(String command) {
        command = command.replace("remote ", "").trim();

        if (command.contains("")) {

        } else if (command.contains("toggletorch")) {
            showTorchFlashDialog();
        }
    }

    private static void print(String string) {
        ConsoleFragment.addConsoleItem(string);
    }


/* Todo   private static void showAutoRotationDialog() {
        final Dialog dialog = getDialog();
        dialog.setContentView(R.layout.switch_item);
        ((TextView) dialog.findViewById(R.id.title)).setText("Auto-rotation");
        final MaterialSwitch materialSwitch = (MaterialSwitch) dialog.findViewById(R.id.material_switch);
        materialSwitch.setText("Auto-rotation");

        dialog.findViewById(R.id.save_cancel).findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendCommand(StaticValues.SET_BLUETOOTH, isChecked ? "1" : "0");
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
    }*/

/* Todo   private static void showSyncDialog() {
        final Dialog dialog = getDialog();
        dialog.setContentView(R.layout.switch_item);
        ((TextView) dialog.findViewById(R.id.title)).setText("Sync");
        final MaterialSwitch materialSwitch = (MaterialSwitch) dialog.findViewById(R.id.material_switch);
        dialog.findViewById(R.id.coming_soon).setVisibility(View.VISIBLE);
        materialSwitch.setText("Sync");


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
    }*/

    public static void printHelp() {
        seperator();
        print(" Files and Directories");
        seperator(); // Files
        print(" NOTE: when specifying a name, you may also specify a path along with it");
        print("");
        print(" tree - displays list of files/folders in current directory");
        print(" num - enter a num to open a file or directory that corresponds with it in the tree");
        print(" if the number corresponds to a file, a file can be shared.");
        print(" freespace - shows space available in the current partition");
        print(" totalspace - shows the total space of the current partition");
        print(" showdetails file_path/num - shows information on specified file");
        print(" del num - deletes the file that corresponds with num in the tree");
        print(" mkdir nm - creates a folder with the name nm in the root directory of the ExternalStorage");
        print(" mkfile nm.extension - creates a file with the name nm in the root directory of the ExternalStorage");
        print(" you can also specify a filepath when creating a file or directory.");
        print(" back - opens current directories parent");
        print(" open name/directory - opens folder at specified directory");
        print(" showdetails name/num - shows details about specified file");
    }

    public static void seperator() {
        ConsoleFragment.addConsoleItem("--------------------------------------------------------------------------------------------------");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        context = getActivity();
    }
 /* Todo  private static void showSilentModeDialog() {
        final Dialog dialog = getDialog();
        dialog.setContentView(R.layout.switch_item);
        ((TextView) dialog.findViewById(R.id.title)).setText("Silent Mode");
        final MaterialSwitch materialSwitch = (MaterialSwitch) dialog.findViewById(R.id.material_switch);
        materialSwitch.setText("Silent Mode");

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
*/

    public void init() {
        //TODO
        getView().setVisibility(View.VISIBLE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Todo
        final View v = View.inflate(getActivity(), R.layout.remote, null);

        v.findViewById(R.id.flash).setOnClickListener(listener);
        v.findViewById(R.id.bluetooth).setOnClickListener(listener);
        v.findViewById(R.id.take_pic_back).setOnClickListener(listener);
        v.findViewById(R.id.take_pic_front).setOnClickListener(listener);
        v.findViewById(R.id.media_control).setOnClickListener(listener);
        v.findViewById(R.id.brightness).setOnClickListener(listener);
//        v.findViewById(R.id.screen_timeout).setOnClickListener(listener);
        v.findViewById(R.id.alarm_volume).setOnClickListener(listener);
        v.findViewById(R.id.media_volume).setOnClickListener(listener);
        v.findViewById(R.id.notification_volume).setOnClickListener(listener);
        v.findViewById(R.id.ringer_volume).setOnClickListener(listener);

        placeholder = v.findViewById(R.id.placeholder);
        placeholder.setOnClickListener(placeHolderListener);

        return v;
    }

//    private static Dialog getScheduledAds() {
//        final Dialog dialog = new Dialog(MainActivity.context, R.style.CustomDialog);
//        dialog.setContentView(R.layout.scheduled_dialog);
//        prefix = prefs.getBoolean(Utility.LOOP_SCHEDULE, true) ? "A full screen Ad will be shown every : " : "A full screen Ad will be shown in : ";
//        final TextView frequency = (TextView) dialog.findViewById(R.id.frequency);
//        final AbstractWheel hours = (AbstractWheel) dialog.findViewById(R.id.hours);
//        final AbstractWheel minutes = (AbstractWheel) dialog.findViewById(R.id.minutes);
//        final MaterialCheckBox loop = (MaterialCheckBox) dialog.findViewById(R.id.loop_checkbox);
//
//        dialog.findViewById(R.id.save_cancel).findViewById(R.id.save).setOnClickListener(listener);
//        dialog.findViewById(R.id.save_cancel).findViewById(R.id.cancel).setOnClickListener(listener);
//
//        hours.setViewAdapter(new NumericWheelAdapter(MainActivity.context, 0, 23));
//        hours.setCyclic(true);
//
//        minutes.setViewAdapter(new NumericWheelAdapter(MainActivity.context, 0, 59));
//        minutes.setCyclic(true);
//
//        // set current time
//        frequencyMinutes = prefs.getInt(Utility.FULLSCREEN_AD_FREQUENCY_MINUTES, 20);
//
//        hours.setCurrentItem(frequencyMinutes / 60);
//        minutes.setCurrentItem(frequencyMinutes % 60);
//
//        frequency.setText(prefix + (frequencyMinutes == 1 ? " minute" : frequencyMinutes + " minutes"));
//
//        OnWheelChangedListener wheelListener = new OnWheelChangedListener() {
//            public void onChanged(AbstractWheel wheel, int oldValue, int newValue) {
//                final StringBuilder text = new StringBuilder();
//
//                int mins = minutes.getCurrentItem();
//                int hr = hours.getCurrentItem();
//                frequencyMinutes = (hr * 60) + mins;
//                text.append(prefix);
//                if (!(hr == 0)) {
//                    text.append(hr);
//                    text.append(hr == 1 ? " hour" : " hours");
//                    text.append(" and ");
//                }
//
//                if (!(mins == 0)) {
//                    text.append(mins);
//                    text.append(mins == 1 ? " minute" : " minutes");
//                }
//
//                final String out = text.toString();
//
//                if (out.equals(prefix))
//                    frequency.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            frequency.setText("No full screen ads will be scheduled (will remove current schedule)");
//                        }
//                    });
//                else
//                    frequency.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            frequency.setText(out);
//                        }
//                    });
//            }
//        };
//
//        loop.setChecked(prefs.getBoolean(Utility.LOOP_SCHEDULE, true));
//        loop.setText("Repeat?");
//        loop.setOnCheckedChangeListener(new MaterialCheckBox.OnCheckedChangedListener() {
//            @Override
//            public void onCheckedChange(MaterialCheckBox materialCheckBox, boolean isChecked) {
//                loopBool = isChecked;
//                prefix = isChecked ? "A full screen Ad will be shown every : " : "A full screen Ad will be shown in : ";
//                final StringBuilder text = new StringBuilder();
//
//                int mins = minutes.getCurrentItem();
//                int hr = hours.getCurrentItem();
//                frequencyMinutes = (hr * 60) + mins;
//                text.append(prefix);
//                if (!(hr == 0)) {
//                    text.append(hr);
//                    text.append(hr == 1 ? " hour" : " hours");
//                    text.append(" and ");
//                }
//
//                if (!(mins == 0)) {
//                    text.append(mins);
//                    text.append(mins == 1 ? " minute" : " minutes");
//                }
//
//                final String out = text.toString();
//
//                if (out.equals(prefix))
//                    frequency.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            frequency.setText("No full screen ads will be scheduled (will remove current schedule)");
//                        }
//                    });
//                else
//                    frequency.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            frequency.setText(out);
//                        }
//                    });
//
//            }
//        });
//
//        hours.addChangingListener(wheelListener);
//        minutes.addChangingListener(wheelListener);
//        return dialog;
//    }

    @Override
    public void onP2PDisconnected() {
        placeholder.post(new Runnable() {
            @Override
            public void run() {
                placeholder.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onP2PConnected() {
        init();
        placeholder.post(new Runnable() {
            @Override
            public void run() {
                placeholder.setVisibility(View.GONE);
            }
        });
    }

    private static class App {
        public final String name, address;

        public App(String name, String address) {
            this.name = name;
            this.address = address;
        }
    }

    private static class ReceivedAppAdapter extends BaseAdapter {
        private final App[] apps;

        public ReceivedAppAdapter(App[] apps) {
            this.apps = apps;
        }

        @Override
        public int getCount() {
            return apps == null ? 0 : apps.length;
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
            if (convertView == null)
                convertView = View.inflate(context, R.layout.app_item, null);

            final App app = apps[position];
            ((TextView) convertView).setText(app.name + " (" + app.address + ")");
            return convertView;
        }
    }

}