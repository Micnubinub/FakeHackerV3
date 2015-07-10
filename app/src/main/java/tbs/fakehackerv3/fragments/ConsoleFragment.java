package tbs.fakehackerv3.fragments;

import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import tbs.fakehackerv3.R;
import tbs.fakehackerv3.console.ConsoleListAdapter;
import tbs.fakehackerv3.player.Commands;

/**
 * Created by Michael on 7/5/2015.
 */
public class ConsoleFragment extends Fragment {
    public static final ArrayList<String> consoleEntries = new ArrayList<String>();
    private static final Random random = new Random();
    public static ConsoleListAdapter cl_adapter;
    private static final Runnable notifyConsoleList = new Runnable() {
        @Override
        public void run() {
            try {
                cl_adapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    public static FragmentActivity context;
    private static View mainView;
    // Todo
    private static Commands commands;
    private static ListView mainListView;
    private static EditText userCommand;
    // BATTERY
    private static boolean runPrintRandomShit;
    private static String[] randomCommandWords = {"acpi", "export", "init", "boot", "chainloader",
            "gettext", "gtpsync", "drivemap", "echo", "loop", "loopback", "xss", "linux", "ls", "partition", "crc", "cat", "gpuid", "cpu",
            "gpu", "x86", "read", "gparted", "set", "halt", "pxe_unload", "bash", "chmod", "command", "cp",
            "dir", "mkdir"};
    private static final Runnable printRandomShit = new Runnable() {
        @Override
        public void run() {
            while (runPrintRandomShit) {
                addConsoleItem(new String(getRandomHackerString()));
            }
        }
    };

    //Todo make commands and ad to help
    public static void printRandomShit() {
        runPrintRandomShit = true;
        new Thread(printRandomShit).start();
    }

    //Todo make command and add to help
    public static void printRandomShit(int num) {
        for (int i = 0; i < num; i++) {
            addConsoleItem(new String(getRandomHackerString()));
        }
    }

    private static String getTotalInternalMemorySize() {
        final File path = Environment.getDataDirectory();
        final StatFs stat = new StatFs(path.getPath());
        final long blockSize = stat.getBlockSize();
        final long totalBlocks = stat.getBlockCount();
        return formatSize(totalBlocks * blockSize);
    }

    private static String formatSize(long size) {
        String suffix = null;
        if (size >= 1024) {
            suffix = "KB";
            size /= 1024;
            if (size >= 1024) {
                suffix = "MB";
                size /= 1024;
            }
        }

        final StringBuilder resultBuffer = new StringBuilder(Long.toString(size));

        int commaOffset = resultBuffer.length() - 3;
        while (commaOffset > 0) {
            resultBuffer.insert(commaOffset, ',');
            commaOffset -= 3;
        }

        if (suffix != null)
            resultBuffer.append(suffix);
        return resultBuffer.toString();
    }

    public static int randInt(int min, int max) {
        return random.nextInt((max - min) + 1) + min;
    }

    public static void handleCommand(String str) {
        commands.checkCommand(str);
        cl_adapter.notifyDataSetChanged();
    }

    private static ListView getListView() {
        return mainListView;
    }

    private static void initEverythingElse() {
        consoleEntries
                .add(new String(
                        "--------------------------------------------------------------------------------------------------"));
        consoleEntries.add(new String(
                "Mucrusuft Wendows [Version 6.1.7601]"));

        consoleEntries
                .add(new String(
                        "--------------------------------------------------------------------------------------------------"));
        // CHANGE TEXT
        consoleEntries
                .add(new String(
                        "Type [help] to get a list of possible commands. Or type [guide] to view"));
        consoleEntries.add(new String(
                "the instructions and learn everything works."));
        consoleEntries
                .add(new String(
                        "--------------------------------------------------------------------------------------------------"));
        cl_adapter.notifyDataSetChanged();


        userCommand = (EditText) mainView.findViewById(R.id.userCommand);
        final Button submitCMD = (Button) mainView.findViewById(R.id.subCommand);

        userCommand.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            // consoleEntries.add(new String(
                            // localSystem.location.name
                            // + " > "
                            // + userCommand.getText()
                            // .toString()));
                            commands.checkCommand(userCommand.getText().toString());
                            userCommand.setText("");

                            cl_adapter.notifyDataSetChanged();
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });

        submitCMD.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String str = userCommand.getText().toString();
                userCommand.setText("");
                handleCommand(str);
            }
        });
    }

    public static void addConsoleItem(String consoleItem) {
        consoleEntries.add(consoleItem);
        if (context != null)
            context.runOnUiThread(notifyConsoleList);

    }

    public static String getRandomHackerString() {
        if (random.nextBoolean())
            return randomCommandWords[random.nextInt(randomCommandWords.length)] + " 0x" + Integer.toHexString(random.nextInt());
        else
            return randomCommandWords[random.nextInt(randomCommandWords.length)] + " " + randomCommandWords[random.nextInt(randomCommandWords.length)] + " 0x" + Integer.toHexString(random.nextInt());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.console_frament, null);
        mainListView = (ListView) mainView.findViewById(R.id.list);
        cl_adapter = new ConsoleListAdapter(context, R.layout.console_entry,
                consoleEntries);
        mainListView.setAdapter(cl_adapter);

        initEverythingElse();

        return mainView;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        context = getActivity();
        commands = new Commands();
    }
}
