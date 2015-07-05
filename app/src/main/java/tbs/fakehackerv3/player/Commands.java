package tbs.fakehackerv3.player;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.os.Environment;
import android.provider.Settings;

import java.io.File;
import java.util.ArrayList;

import coGame.consoletest.CommandItem;
import coGame.consoletest.ConsoleItem;
import coGame.consoletest.MainActivity;
import mike.Chat;
import mike.FileManager;
import mike.MikeMode;
import mike.Tools;

public class Commands {
    private static Chat chat;
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
        MainActivity.consoleEntries.add(new ConsoleItem(string));
    }

    public static void seperator() {
        MainActivity.consoleEntries
                .add(new ConsoleItem(
                        "--------------------------------------------------------------------------------------------------"));
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

    public void checkCommand(String userCmd) {

        if (!userCmd.equals("repeat")) {
            lastCom = userCmd;
        }

        userCmd = userCmd.toLowerCase();
        // Todo
        if (userCmd.equals("toggleflash")) {
            Tools.toggleTorch(MainActivity.context);
            return;
        }

        // if (userCmd.equals("filemanager")) {
        // FileManager.startFilemanager();
        // return;
        // }

        String[] split_command = userCmd.split("\\s+");
        try {

            if (chat.chatInitialising) {
                try {
                    chat.initiateChat(Integer.parseInt(userCmd));
                    return;
                } catch (Exception e) {

                }
            }
        } catch (Exception e) {
        }


        if (chat.isChatScan() && !chat.isDeviceChosen()) {
            try {
                chat.startChat(Integer.parseInt(userCmd));
                return;
            } catch (Exception e) {

            }
        }

        if (userCmd.equals("printthread") && chat != null) {
            chat.printThread();
            return;
        }

        if (chat.restoreThread) {
            try {
                chat.restoreThread(Integer.parseInt(userCmd));
            } catch (Exception e) {
                return;
            }
        }

        if (userCmd.equals("restorethread") && chat != null) {
            chat.restoreThread();
            return;
        }

        if (userCmd.equals("showthread") && chat != null) {
            chat.printThread();
            return;
        }

        if (userCmd.equals("savethread") && chat != null) {
            chat.saveThread();
            return;
        }

        if (split_command[0].equals("exit") && split_command[1].equals("chat")) {
            chat.disconnectChat();
            return;
        }


        if (split_command[0].equals("chat")) {
            if (chat == null)
                chat = new Chat();


            if (chat.isIsInChat()) {
                chat.sendChatMessage(userCmd.split(" ", 2)[1]);
                return;
            } else {
                if (!chat.chatModeChosen) {
                    chat.setUpChat();
                } else {
                    try {
                        chat.initiateChat(Integer.parseInt(userCmd));

                    } catch (Exception e) {
                        chat.chatModeChosen = false;
                    }
                }
                return;
            }
        }

        if (FileManager.isIsInFileManagerMode()) {

            if (userCmd.equals("totalspace")) {
                print("Total space : " + Tools.fileSize(FileManager.getTotalSpace(Environment.getExternalStorageDirectory().getPath())));
                print("");
                return;
            }

            if (userCmd.equals("freespace")) {
                print("Free space :" + Tools.fileSize(FileManager.getFreeSpace(Environment.getExternalStorageDirectory().getPath())));
                print("");
                return;
            }

            try {
                FileManager.open(Integer.parseInt(userCmd));
                // Commands.seperator();
                print(" type the respective number of each file/folder to open it");
                print(" or type back to return to its parent directory.");
                seperator();
                return;
            } catch (Exception e) {

            }

            if (userCmd.split("\\s+")[0].equals("del")
                    && userCmd.split("\\s+").length == 2) {
                try {
                    FileManager
                            .delete(Integer.parseInt(userCmd.split("\\s+")[1]));
                } catch (NumberFormatException e) {
                    FileManager.delete(userCmd.split("\\s+")[1]);
                } catch (Exception e) {
                }

                return;
            }

            if (userCmd.equals("back")) {
                FileManager.openFolder(new File(FileManager
                        .getCurrentDirectory()).getParent());
                print(" type the respective number of each file/folder to open it");
                print(" or type back to return to its parent directory.");
                seperator();
                return;
            }
        }

        // if (userCmd.equals("exit")) {
        // if (FileManager.isIsInFileManagerMode())
        // FileManager.exitFilemanager();
        // return;
        // }

        if (userCmd.equals("launch ie")) {
            Tools.launchIE(MainActivity.context);
            return;
        }

        String[] commands = userCmd.split("\\s+");
        if (commands[0].equals("open")) {
            try {

                print("opening :" + commands[1]);
                FileManager.open(Environment.getExternalStorageDirectory()
                        .toString() + commands[1]);

            } catch (Exception e) {
            }
            return;
        }

        if (commands[0].equals("showdetails") && commands.length == 2) {
            try {
                FileManager.showFileDetails(Integer.parseInt(commands[1]));
            } catch (NumberFormatException e) {
                FileManager.showFileDetails(commands[1]);
            } catch (Exception e) {
                print("Failed to show details");
            }

            return;
        }

        if (commands[0].equals("mkdir") && commands.length == 2) {
            try {
                if (commands[1].endsWith("/")) {
                    FileManager.createFolder(Environment
                            .getExternalStorageDirectory().getPath()
                            + "/"
                            + commands[1]);
                } else {
                    FileManager.createFolder(new File(FileManager
                            .getCurrentDirectory(), commands[1] + "/"));
                }

                return;
            } catch (Exception e) {
                print("Failed to create directory");
            }

        }

        if (commands[0].equals("mkfile") && commands.length == 2) {
            try {
                if (commands[1].contains("/")) {
                    FileManager.createFile(Environment
                            .getExternalStorageDirectory() + commands[1]);
                } else {
                    FileManager.createFile(new File(FileManager
                            .getCurrentDirectory(), commands[1]));
                }
                return;
            } catch (Exception e) {
                print("Failed to create file");
            }
        }

        if (userCmd.equals("getpackages")) {
            Tools.getPackages(MainActivity.context);
            return;
        }

        if (userCmd.equals("totalspace")) {
            print("Total space : "
                    + Tools.fileSize(FileManager.getTotalSpace(Environment
                    .getExternalStorageDirectory().getPath())));
            print("");
            return;
        }

        if (userCmd.equals("freespace")) {
            print("Free space :"
                    + Tools.fileSize(FileManager.getFreeSpace(Environment
                    .getExternalStorageDirectory().getPath())));
            print("");
            return;
        }

        if (userCmd.equals("tree")) {
            FileManager.showTree(Environment.getExternalStorageDirectory());
            // seperator();
            print("  type the respective number for each directory to open it");
            print("--------------------------------------------------------------------------------------------------");
            return;
        }

        if (userCmd.equals("restart")) {
            MikeMode.restart(MainActivity.context);
            return;
        }

        //if (userCmd.equals("set theme")) {
        // Todo
        // return;
        // }

        if (userCmd.split("\\s+")[0].equals("launch")
                && userCmd.split("\\s+").length == 2) {
            try {
                Tools.launchInt(MainActivity.context,
                        Integer.parseInt(userCmd.split("\\s+")[1]));
            } catch (NumberFormatException e) {
                Tools.launchPackage(MainActivity.context,
                        userCmd.split("\\s+")[1]);
            } catch (Exception e) {

            }

            return;
        }

        String[] splitCmd = userCmd.split("\\s+");
        ArrayList<String> cmdArray = new ArrayList();
        for (int i = 0; i < splitCmd.length; i++) {
            cmdArray.add(splitCmd[i]);
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
                            MainActivity.handleCommand(command);
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
            MainActivity.consoleEntries.add(new ConsoleItem("Blank Command!"));
            MainActivity.consoleEntries
                    .add(new ConsoleItem(
                            "--------------------------------------------------------------------------------------------------"));
        } else {
            MainActivity.consoleEntries.add(new ConsoleItem(
                    "Command out of Range"));
            MainActivity.consoleEntries
                    .add(new ConsoleItem(
                            "--------------------------------------------------------------------------------------------------"));
        }

    }

    void checkSingle(ArrayList cmd) {
        if (cmd.get(0).equals("btsn")) {
            // Find Devices
            MainActivity.player.location.update();

            MainActivity.player.deviceStats.folders.get(0).folders.clear(); // Reset
            // Device
            // List
            MainActivity.mBluetoothAdapter.startDiscovery();
            MainActivity.consoleEntries.add(new ConsoleItem(
                    "bluetooth adapter initialized"));
            MainActivity.consoleEntries.add(new ConsoleItem(
                    "seeking devices in range"));
            MainActivity.consoleEntries
                    .add(new ConsoleItem(
                            "--------------------------------------------------------------------------------------------------"));
        } else if (cmd.get(0).equals("help")) {
            MainActivity.consoleEntries.add(new ConsoleItem(" COMMANDS: "));
            MainActivity.consoleEntries
                    .add(new ConsoleItem(
                            "--------------------------------------------------------------------------------------------------"));
            // for (int i = 0; i < commands.size(); ++i) {
            // MainActivity.consoleEntries.add(new ConsoleItem("   "
            // + commands.get(i).cmd + " - " + commands.get(i).desc));
            // }
            printHelp();
            MainActivity.consoleEntries
                    .add(new ConsoleItem(
                            "--------------------------------------------------------------------------------------------------"));
        } else if (cmd.get(0).equals("repeat")) {
            MainActivity.handleCommand(lastCom);
        } else if (cmd.get(0).equals("landscape")) {
            // MainActivity.consoleEntries.clear();
            // MainActivity.infoEntries.clear();
            ((Activity) MainActivity.context)
                    .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        } else if (cmd.get(0).equals("portrait")) {
            // MainActivity.consoleEntries.clear();
            // MainActivity.infoEntries.clear();
            ((Activity) MainActivity.context)
                    .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        } else if (cmd.get(0).equals("insertbreak")) {
            seperator();
        } else if (cmd.get(0).equals("sweep")) {
            MainActivity.consoleEntries.clear();
            seperator();
            print(" Console cleared.");
            seperator();
        } else if (cmd.get(0).equals("exit")) {
            // To Do
            System.exit(0);
        } else if (cmd.get(0).equals("togglemute")) { // MUTE UNMUTE
            if (!isMute) {
                AudioManager audioManager = (AudioManager) MainActivity.context
                        .getSystemService(Context.AUDIO_SERVICE);
                audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                isMute = false;

                print("  device sound output toggled: OFF");
            } else {
                AudioManager audioManager = (AudioManager) MainActivity.context
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
            print("    controll your device. But it will also let you control other ");
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
        } else if (cmd.get(0).equals("enter dungeon")) {
            print("  Dungeon will be unlcoked soon!");
            seperator();
        } else {
            invalidCommand(cmd);
        }
    }

    void checkTwo(ArrayList cmd) {
        if (cmd.get(0).equals("print")) {
            MainActivity.consoleEntries.add(new ConsoleItem(cmd.get(1) + ""));
            MainActivity.consoleEntries
                    .add(new ConsoleItem(
                            "--------------------------------------------------------------------------------------------------"));
        } else if (cmd.get(0).equals("setbt")) {
            if (isInteger(cmd.get(1).toString())) {
                int num = Integer.valueOf(cmd.get(1).toString());
                if (num >= 0 && num <= 255) {
                    Settings.System.putInt(
                            MainActivity.context.getContentResolver(),
                            android.provider.Settings.System.SCREEN_BRIGHTNESS,
                            num);
                    print("  set brightness to " + num + ".");
                } else {
                    print("  must enter value between 0 and 255.");
                }

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
        MainActivity.consoleEntries.add(new ConsoleItem("command " + userCmd
                + " does not exist."));
        MainActivity.consoleEntries.add(new ConsoleItem(
                "type [ help ] for a list of commands."));
        MainActivity.consoleEntries
                .add(new ConsoleItem(
                        "--------------------------------------------------------------------------------------------------"));
    }

    void printHelp() {
        print(" Basic");
        seperator(); // Commands
        print(" help - displays list of commands");
        print(" guide - displays a short guide");
        print(" sweep - clears the console");
        print(" repeat - executes last command");
        print(" restart - restarts the application");
        print(" exit - exits the application");

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

        seperator();
        print(" Device Control");
        seperator(); // Device Control
        print(" getpackages - displays list of all application packages on device");
        print(" launch number - launch an application based on its number in the getpackages list");
        print(" launch nm - launch an application using its package name");
        print(" launch ie - starts internet explorer");
        print(" togglemute - toggles sound output on/off for device");
        print(" toggleflash - toggles the flashlight on/off"); // TO DO
        print(" setbt 0-255 - set screen brightness"); // TO DO
        print(" settheme theme - change theme of application"); // TO DO
        print(" landscape - set screen orientation landscape");
        print(" portrait - set screen orientation portrait");

        seperator();
        print(" Editor");
        seperator(); // Device Control
        print(" insertbreak - inserts a breakline into the console");
        print(" print x - prints a vlue to the console");
        print(" for x command - repeats a command for a specified amount of times");

        seperator();
        print(" Device Information");
        seperator(); // Device Information
        print(" devicebat - show battery status and information"); // TO DO
        print(" devicetime - show time and date information"); // TO DO
        print(" devicememory - show device RAM usage"); // TO DO
        print(" totalspace - show device total storage capacity");
        print(" freespace - show device available storage capacity");

        seperator();
        print(" Bluetooth");
        seperator(); // Bluetooth
        print(" btsn - scan for bluetooth devices");

        seperator();
        print(" Chat");


        seperator();
        print(" Special");
        seperator(); // Bluetooth
        print(" there are many special commands that you will have to discover yourself :)");
    }
}
