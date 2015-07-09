package tbs.fakehackerv3.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import tbs.fakehackerv3.R;
import tbs.fakehackerv3.console.ConsoleListAdapter;
import tbs.fakehackerv3.local_system_data.PlayerSystem;
import tbs.fakehackerv3.player.Commands;

/**
 * Created by Michael on 7/5/2015.
 */
public class ConsoleFragment extends Fragment {
    public static final ArrayList<String> consoleEntries = new ArrayList<String>();
    public static final ArrayList<String> inventoryItems = new ArrayList<String>();
    private static final ArrayList<String> infoEntries = new ArrayList<String>();
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
    public static ConsoleListAdapter c3_adapter;
    public static PlayerSystem player;
    public static FragmentActivity context;
    private static View mainView;
    // Todo
    private static ConsoleListAdapter c2_adapter;
    private static Commands commands;
    private static ListView infoList, deviceList, mainListView;
    private static EditText userCommand;
    private static Handler handler;
    // BATTERY
    private static BroadcastReceiver mBatInfoReceiver;
    private static TextView deviceName, dataStorage, batLife;
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

    private static String getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
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
        c2_adapter.notifyDataSetChanged();
        c3_adapter.notifyDataSetChanged();
    }

    private static ListView getListView() {
        return mainListView;
    }

    private static void initEverythingElse() {
        userCommand = (EditText) mainView.findViewById(R.id.userCommand);
        Button submitCMD = (Button) mainView.findViewById(R.id.subCommand);
        final DecimalFormat df = new DecimalFormat("0");
        //  final DecimalFormat mb = new DecimalFormat("0.00");

        // Styling TEXT
        //cmdEntry = (TextView) findViewById(R.id.commandEntry);

        final Date d = new Date();
        CharSequence timeString = DateFormat.format("hh: mm: ss -- d/MM/yyyy ",
                d.getTime());

        infoEntries.add(new String("  " + timeString));
        infoEntries.add(new String("> Mucrusoft Wendows [Version 6.1.7601]"));
        infoEntries
                .add(new String(
                        "> Copyright (c) 2009 Mucrusoft Corporation. All rights resrved."));
        // infoEntries.add(new String(
        // "> Copyright (c) 2078 Vlaas Corporation. All rights reserved"));
        // infoEntries.add(new
        // String("> Diamex Software Operating Systems"));

        c2_adapter.notifyDataSetChanged();

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


        mBatInfoReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context ctxt, Intent intent) {
                player.batLifeInt = intent.getIntExtra(
                        BatteryManager.EXTRA_LEVEL, 0);
            } // Update Player Bat Life
        };
        context.registerReceiver(mBatInfoReceiver, new IntentFilter(
                Intent.ACTION_BATTERY_CHANGED));

        deviceName = (TextView) mainView.findViewById(R.id.playerName);
        dataStorage = (TextView) mainView.findViewById(R.id.localStorage);
        batLife = (TextView) mainView.findViewById(R.id.batLife);

        deviceName.setText(android.os.Build.MODEL);

        String memoryUsage = String.valueOf(df.format(player.storage));
        dataStorage.setText(memoryUsage + "/" + player.storageStr);

        final Runnable runnableMain = new Runnable() {
            @Override
            public void run() {
                {
                    player.update();
                    String memoryUsage = String.valueOf(df
                            .format(player.storage));
                    dataStorage.setText(memoryUsage + "/" + player.storageStr);

                    // BatteryLife
                    batLife.setText(String.valueOf(player.batLifeInt) + "%");
                    handler.postDelayed(this, 500); // EDIT FOR PERFORMANCE
                }
            }
        };
        handler.post(runnableMain);

        final Runnable runnableTime = new Runnable() {
            @Override
            public void run() {
                {
                    Date d = new Date();
                    CharSequence timeString = DateFormat.format(
                            "hh: mm: ss -- d/MM/yyyy ", d.getTime());
                    try {
                        infoEntries.remove(0);
                        infoEntries.add(0, "  " + timeString);
                        c2_adapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    // UPDATE FILE SPACE:

                    handler.postDelayed(this, 1000);
                }
            }
        };
        handler.post(runnableTime);

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
                            c2_adapter.notifyDataSetChanged();
                            c3_adapter.notifyDataSetChanged();
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
        infoList = (ListView) mainView.findViewById(R.id.infoView);

        // infoList.setTypeface(null, Typeface.BOLD);

        deviceList = (ListView) mainView.findViewById(R.id.inventory);

        cl_adapter = new ConsoleListAdapter(context, R.layout.console_entry,
                consoleEntries);
        mainListView.setAdapter(cl_adapter);

        c2_adapter = new ConsoleListAdapter(context, R.layout.console_entry,
                infoEntries);
        infoList.setAdapter(c2_adapter);

        c3_adapter = new ConsoleListAdapter(context, R.layout.console_entry,
                inventoryItems);
        deviceList.setAdapter(c3_adapter);


        return mainView;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        commands = new Commands();
        player.Setup(getTotalInternalMemorySize());
        handler = new Handler();
        player = new PlayerSystem();
        initEverythingElse();
    }
}
