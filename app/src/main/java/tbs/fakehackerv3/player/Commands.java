package tbs.fakehackerv3.player;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.BatteryManager;
import android.os.Environment;

import java.util.ArrayList;
import java.util.Date;

import tbs.fakehackerv3.MainActivity;
import tbs.fakehackerv3.P2PManager;
import tbs.fakehackerv3.RemoteTools;
import tbs.fakehackerv3.Tools;
import tbs.fakehackerv3.console.CommandItem;
import tbs.fakehackerv3.fragments.CallLogFragment;
import tbs.fakehackerv3.fragments.ConsoleFragment;
import tbs.fakehackerv3.fragments.FileManagerFragment;
import tbs.fakehackerv3.fragments.MessageReaderFragment;
import tbs.fakehackerv3.fragments.MessagingFragent;
import tbs.fakehackerv3.fragments.RemoteFragment;


public class Commands {
    private static final Date date = new Date();
    private final ArrayList<CommandItem> commands;
    private final boolean exists;
    // Handler handler = new Handler();
    private String lastCom;
    // BOOLEANS
    private boolean isMute;

    public Commands() {
        this.commands = new ArrayList();
        this.exists = false;

        lastCom = "";
        isMute = false;
    }

    private static void print(String string) {
        ConsoleFragment.addConsoleItem(string);
    }

    public static void seperator() {
        ConsoleFragment.addConsoleItem("--------------------------------------------------------------------------------------------------");
    }

    private static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return false;
        }
        // only got here if we didn't return false
        return true;
    }

    private static int getBatteryPercent() {
        final IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        final Intent batteryStatus = MainActivity.context.registerReceiver(null, filter);
        return batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
    }

    private static String getRamUsage() {
        final StringBuilder builder = new StringBuilder("");
        try {
            ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
            ActivityManager activityManager = (ActivityManager) MainActivity.context.getSystemService(Activity.ACTIVITY_SERVICE);
            activityManager.getMemoryInfo(mi);

            builder.append(Tools.getFileSize(mi.availMem));
            builder.append(" available, out of ");
            builder.append(Tools.getFileSize(mi.totalMem));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    public void checkCommand(String userCmd) {
        //TODO ASAP check commands and hijack them for the respective fragments where applicable
        userCmd = userCmd.toLowerCase();

        if (!userCmd.equals("repeat")) {
            lastCom = userCmd;
        }

        if (userCmd.startsWith("calllog")) {
            CallLogFragment.handleConsoleCommand(userCmd);
            return;
        } else if (userCmd.startsWith("filemanager")) {
            FileManagerFragment.handleConsoleCommand(userCmd);
            return;
        } else if (userCmd.startsWith("chat")) {
            MessagingFragent.handleConsoleCommand(userCmd);
            return;
        } else if (userCmd.startsWith("remote")) {
            RemoteFragment.handleConsoleCommand(userCmd);
            return;
        } else if (userCmd.startsWith("textreader")) {
            MessageReaderFragment.handleConsoleCommand(userCmd);
            return;
        }

        if (userCmd.equals("scan")) {
            P2PManager.startScan();
            return;
        }

        if (userCmd.equals("toggleflash")) {
            RemoteTools.toggleTorch();
            return;
        }

        if (userCmd.equals("devicebat")) {
            print("Device has : " + getBatteryPercent() + "% battery left"); // TO DO
            print("");
            return;
        }
        if (userCmd.equals("devicetime")) {
            date.setTime(System.currentTimeMillis());
            print(date.toString()); // TO DO
            print("");
            return;
        }
        if (userCmd.equals("devicememory")) {
            print(getRamUsage()); // TO DO.toggleTorch();
            print("");
            return;
        }

        if (userCmd.equals("totalspace")) {
            print("Total space : " + Tools.getFileSize(FileManagerFragment.getTotalSpace(Environment.getExternalStorageDirectory().getPath())));
            print("");
            return;
        }

        if (userCmd.equals("freespace")) {
            print("Free space :" + Tools.getFileSize(FileManagerFragment.getFreeSpace(Environment.getExternalStorageDirectory().getPath())));
            print("");
            return;
        }

        final String[] commands = userCmd.split("\\s+");

        if (userCmd.equals("launch ie")) {
            Tools.launchIE(ConsoleFragment.context);
            return;
        }

        if (commands[0].equals("setbackground")) {
            try {
                Tools.setBackgroundColor(MainActivity.context, Color.parseColor("#" + commands[1]));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        if (commands[0].equals("settextcolor")) {
            try {
                Tools.setTextColor(MainActivity.context, Color.parseColor("#" + commands[1]));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        if (userCmd.equals("getpackages")) {
            Tools.getPackages(ConsoleFragment.context);
            return;
        }

        if (userCmd.equals("tree")) {
            //Todo FileManagerFragment.showTree(Environment.getExternalStorageDirectory());
            // seperator();
            print("  type the respective number for each directory to open it");
            print("--------------------------------------------------------------------------------------------------");
            return;
        }

        if (userCmd.equals("restart")) {
            //Todo MikeMode.restart(ConsoleFragment.context);
            return;
        }

        //if (userCmd.equals("set theme")) {
        // Todo
        // return;
        // }

        if (commands[0].equals("launch")
                && commands.length == 2) {
            try {
                Tools.launchInt(ConsoleFragment.context,
                        Integer.parseInt(commands[1]));
            } catch (NumberFormatException e) {
                Tools.launchPackage(ConsoleFragment.context,
                        commands[1]);
            } catch (Exception e) {

            }

            return;
        }


        final ArrayList<String> cmdArray = new ArrayList(commands.length);
        for (int i = 0; i < commands.length; i++) {
            cmdArray.add(commands[i]);
        }

        if (cmdArray.get(0).equals("for")) {
            if (isInteger(cmdArray.get(1))) {

                int itr = Integer.valueOf(cmdArray.get(1));
                String command = "";
                if (cmdArray.size() > 2) {
                    // Make COmmand
                    for (int x = 2; x < cmdArray.size(); ++x) {
                        command += (cmdArray.get(x) + " ");
                    }

                    System.out.println("CMD: " + command);

                    if (command.contains("repeat")) {
                        print("  cannot specify repeat in a for loop.");
                        seperator();
                    } else {
                        for (int i = 0; i < itr; ++i) {
                            ConsoleFragment.handleCommand(command);
                        }
                    }
                    return;
                } else {
                    print("  must specify a command.");
                    seperator();
                }

            } else {
                print("  must specify a number value.");
                seperator();
            }
        }

        System.out.println(cmdArray);

        if (cmdArray.size() == 1) {
            checkSingle(cmdArray);
        } else if (cmdArray.size() == 2) {
            checkTwo(cmdArray);
        } else if (cmdArray.size() == 3) {
            checkThree(cmdArray);
        } else if (cmdArray.size() == 4) {
            checkFour(cmdArray);
        } else if (cmdArray.size() == 5) {
            checkFive(cmdArray);
        } else if (cmdArray.size() == 6) {
            checkSix(cmdArray);
        } else if (cmdArray.size() == 0) {
            ConsoleFragment.consoleEntries.add("Blank Command!");
            ConsoleFragment.consoleEntries
                    .add(
                            "--------------------------------------------------------------------------------------------------");
        } else {
            ConsoleFragment.consoleEntries.add(
                    "Command out of Range");
            ConsoleFragment.consoleEntries
                    .add(
                            "--------------------------------------------------------------------------------------------------");
        }

    }

    void checkSingle(ArrayList cmd) {
        if (cmd.get(0).equals("help")) {
            ConsoleFragment.consoleEntries.add(" COMMANDS: ");
            ConsoleFragment.consoleEntries
                    .add(
                            "--------------------------------------------------------------------------------------------------");
            // for (int i = 0; i < commands.size(); ++i) {
            // ConsoleFragment.consoleEntries.add("   "
            // + commands.get(i).cmd + " - " + commands.get(i).desc));
            // }
            printHelp();
            ConsoleFragment.consoleEntries
                    .add(
                            "--------------------------------------------------------------------------------------------------");
        } else if (cmd.get(0).equals("repeat")) {
            ConsoleFragment.handleCommand(lastCom);
        } else if (cmd.get(0).equals("landscape")) {
            MainActivity.context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else if (cmd.get(0).equals("portrait")) {
            MainActivity.context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else if (cmd.get(0).equals("insertbreak")) {
            seperator();
        } else if (cmd.get(0).equals("sweep")) {
            ConsoleFragment.consoleEntries.clear();
            ConsoleFragment.notifyDataSetChanged();
            seperator();
            print(" Console cleared.");
            seperator();
        } else if (cmd.get(0).equals("exit")) {
            // To Do
            System.exit(0);
        } else if (cmd.get(0).equals("togglemute")) { // MUTE UNMUTE
            if (!isMute) {
                AudioManager audioManager = (AudioManager) ConsoleFragment.context
                        .getSystemService(Context.AUDIO_SERVICE);
                audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                isMute = false;

                print("  device sound output toggled: OFF");
            } else {
                AudioManager audioManager = (AudioManager) ConsoleFragment.context
                        .getSystemService(Context.AUDIO_SERVICE);
                int maxVolume = audioManager
                        .getStreamMaxVolume(AudioManager.STREAM_RING);

                audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                audioManager.setStreamVolume(AudioManager.STREAM_RING,
                        maxVolume, AudioManager.FLAG_SHOW_UI
                                + AudioManager.FLAG_PLAY_SOUND
                );
                isMute = true;

                print("  device sound output toggled: ON");
            }
            seperator();
        } else if (cmd.get(0).equals("guide")) {
            print(" GUIDE: ");
            seperator();
            print("    Welcome to Phone Hacker. This Application not only lets you ");
            print("    control your device. But it will also let you control other ");
            print("    devices. If you want to control other devices, you should message ");
            print("    us on our Facebook Page. (Link on App Page!). We have to do it this ");
            print("    way, because it is illegal. ");
            print("");
            print("    Thank You for Downloading this App. TBS");
            seperator();
        } else if (cmd.get(0).equals("lol")) {
            print("  what are you laughing at?");
            seperator();
        } else if (cmd.get(0).equals("UUDDLRLRBA")) {
            print("  you found a secret Code! Keep it up!");
            seperator();
        } else if (cmd.get(0).equals("ASDERHNIP")) {
            print("  Hl3.confirmed = true;");
            seperator();
        } else {
            invalidCommand(cmd);
        }
    }

    void checkTwo(ArrayList cmd) {
        if (cmd.get(0).equals("print")) {
            ConsoleFragment.consoleEntries.add(cmd.get(1) + "");
            ConsoleFragment.consoleEntries
                    .add("--------------------------------------------------------------------------------------------------");
        } else if (cmd.get(0).equals("setbt")) {
            if (isInteger(cmd.get(1).toString())) {
                int num = Integer.valueOf(cmd.get(1).toString());
                if (num >= 0 && num <= 255) {
                    RemoteTools.setBrightness(num);
                    print("  set brightness to " + num + ".");
                } else {
                    print("  must enter value between 0 and 255.");
                }

            } else {
                print("  must enter an integer value.");
            }
            seperator();
        } else if (cmd.get(0).equals("record")) {
            if (isInteger(cmd.get(1).toString())) {
                int num = Integer.valueOf(cmd.get(1).toString());
                RemoteTools.record(num);
                print("  recording for " + num + "seconds.");
            } else {
                print("  must enter an integer value.");
            }
            seperator();
        } else {
            invalidCommand(cmd);
        }
    }

    void checkThree(ArrayList cmd) {
        invalidCommand(cmd);
    }

    void checkFour(ArrayList cmd) {
        invalidCommand(cmd);
    }

    void checkFive(ArrayList cmd) {
        invalidCommand(cmd);
    }

    void checkSix(ArrayList cmd) {
        invalidCommand(cmd);
    }

    void invalidCommand(ArrayList userCmd) {
        ConsoleFragment.consoleEntries.add("command " + userCmd
                + " does not exist.");
        ConsoleFragment.consoleEntries.add(
                "type [ help ] for a list of commands.");
        ConsoleFragment.consoleEntries
                .add("--------------------------------------------------------------------------------------------------");
    }

    void printHelp() {
        print(" Basic");
        seperator(); // Commands
        print(" help - displays list of commands");
        print(" guide - displays a short guide");
        print(" sweep - clears the console");
        print(" scan - scans for other devices (other device needs to be scanning too)");
        print(" repeat - executes last command");
        print(" restart - restarts the application");
        print(" exit - exits the application");

        seperator();
        print(" Device Control");
        seperator(); // Device Control
        print(" getpackages - displays list of all application packages on device");
        print(" launch number - launch an application based on its number in the getpackages list");
        print(" launch nm - launch an application using its package name");
        print(" launch ie - starts internet explorer");
        print(" togglemute - toggles sound output on/off for device");
        print(" toggleflash - toggles the flashlight on/off");
        print(" setbt 0-255 - set screen brightness");
        print(" landscape - set screen orientation landscape");
        print(" portrait - set screen orientation portrait");
        print(" record number - records audio for the specified number of seconds");

        seperator();
        print(" Editor");
        seperator(); // Device Control
        print(" insertbreak - inserts a breakline into the console");
        print(" print x - prints a value to the console");
        print(" for x command - repeats a command for a specified amount of times");

        seperator();
        print(" Device Information");
        seperator(); // Device Information
        print(" devicebat - show battery status and information");
        print(" devicetime - show time and date information");
        print(" devicememory - show device RAM usage");
        print(" totalspace - show device total storage capacity");
        print(" freespace - show device available storage capacity");


        seperator();
        print(" Chat");
        print("Type in the word chat, followed by the message, and the message will be sent to the other device");

        FileManagerFragment.printHelp();

        RemoteFragment.printHelp();

        seperator();
        print(" Special");
        seperator(); // Bluetooth
        print(" there are many special commands that you will have to discover yourself :)");

    }


}
