package tbs.fakehackerv3.local_system_data;

import android.os.CountDownTimer;

import java.text.DecimalFormat;
import java.util.ArrayList;

import tbs.fakehackerv3.fragments.ConsoleFragment;


public class PlayerSystem {
    // public static String userName;
    //  private static double Lvl;
    public static double storage;
    public static String storageStr;
    public static int batLifeInt;
    public static LocalFolder location;
    public static LocalFolder deviceStats;
    private static String name;
    private static double storageMax;
    private static ArrayList<LocalFolder> allFolders;
    private static ArrayList<LocalFile> allFiles;
    private static ArrayList<ZipFolder> allZips;
    private static LocalFolder cDriveF;
    // private static localFolder lastLocation;
    private static ArrayList<String> locationStr;

    private static ZipFolder targetZip;

    //  private static Handler handler;

    public PlayerSystem() {
        locationStr = new ArrayList();
        allFiles = new ArrayList();
        allFolders = new ArrayList();
        allZips = new ArrayList();

        targetZip = new ZipFolder("", null, false);
        targetZip.location = targetZip;

        //   handler = new Handler();

        LocalFolder statsFolder = new LocalFolder("Stats", null, false);
        statsFolder.location = statsFolder;
        LocalFolder BTDevices = new LocalFolder("Devices", statsFolder, false);
        statsFolder.folders.add(BTDevices);
        statsFolder.update();
        deviceStats = statsFolder;

        // GAME SETUP
        allFolders = new ArrayList();
        LocalFolder cFolder = new LocalFolder("C:", null, false);
        cFolder.location = cFolder;
        cFolder.update();
        allFolders.add(cFolder);

        //lastLocation = cFolder;
        location = cFolder;
        cDriveF = cFolder;
        locationStr.add(cFolder.name + "/");
        updateFileListings();
        drawDeviceList(); // ALSO CALLED WHEN SOMEHTING IN THE INV IS CHAnGED

        // STARTING INVENTORY
        showDevicesMenu();

    }

    public static void Setup(String sm) {
        name = "Sidney";
        //Lvl = 0.00;
        storage = 0;

        String tmp = sm;
        tmp = tmp.replace(",", "");
        tmp = tmp.substring(0, tmp.length() - 2);
        storageMax = Integer.valueOf(tmp);
        storageStr = sm;
        // Debug
        System.out.println(storageMax + " VS " + sm);

        // Extra
        batLifeInt = 0;
    }

    private static void updateFileListings() {
        allFiles.clear();
        for (int i = 0; i < allFolders.size(); ++i) {
            for (int x = 0; x < allFolders.get(i).files.size(); ++x) {
                allFiles.add(allFolders.get(i).files.get(x));
            }
        }
    }

    public static void updateFolderListings() {
        location.update();
        for (int i = 0; i < location.folders.size(); ++i) {
            location.folders.get(i).update();
        }
    }

    public static void update() {
        storage = 0;
        double tempStorage = 0;
        for (int i = 0; i < ConsoleFragment.player.deviceStats.folders.size(); ++i) {
            for (int x = 0; x < ConsoleFragment.player.deviceStats.folders.get(i).files
                    .size(); ++x) {
                tempStorage += ConsoleFragment.player.deviceStats.folders.get(i).files
                        .get(x).size;
            }
        }
        storage = tempStorage;
        if (storage >= storageMax) {
            ConsoleFragment.consoleEntries.add("Out of Memmory!");
        }
    }

    private static void drawDeviceList() {
        final DecimalFormat df = new DecimalFormat("0");
        ConsoleFragment.inventoryItems.clear();
        for (int i = 0; i < ConsoleFragment.player.deviceStats.folders.size(); ++i) {
            ConsoleFragment.inventoryItems.add(" > "
                    + ConsoleFragment.player.deviceStats.folders.get(i).name);
            System.out.println("TEST 1");
            if (ConsoleFragment.player.deviceStats.folders.get(i).folders.size() > 0) {
                System.out.println("TEST 2");
                for (int x = 0; x < ConsoleFragment.player.deviceStats.folders
                        .get(i).folders.size(); ++x) {
                    System.out.println("TEST 3");
                    ConsoleFragment.inventoryItems
                            .add("     > "
                                            + ConsoleFragment.player.deviceStats.folders
                                            .get(i).folders.get(x).name
                                            + " - "
                                            + df.format(deviceStats.folders
                                            .get(i).folders.get(x).size)
                                            + "kb"
                            );
                }
            } else {
                ConsoleFragment.inventoryItems
                        .add("     > Empty");
            }
        }
    }

    public static void showCurrent() {
        final DecimalFormat df = new DecimalFormat("0");
        location.update();
        ConsoleFragment.consoleEntries.add(" Location: "
                + locationStr + ">");
        ConsoleFragment.consoleEntries
                .add("--------------------------------------------------------------------------------------------------");
        // COMPRESSED
        ConsoleFragment.consoleEntries.add("   > Folders:");
        if (location.folders.size() < 1) {
            ConsoleFragment.consoleEntries.add("      > None");
        } else {
            for (int i = 0; i < location.folders.size(); ++i) {
                ConsoleFragment.consoleEntries.add(
                        "        > "
                                + location.folders.get(i).name
                                + " - "
                                + (location.folders.get(i).files.size()
                                + location.folders.get(i).folders
                                .size() + location.folders
                                .get(i).zipFiles.size())
                                + " files - "
                                + df.format(location.folders.get(i).size)
                                + "kb"
                );
            }
        }
        // FILES
        ConsoleFragment.consoleEntries.add("   > Files:");
        if (location.files.size() < 1) {
            ConsoleFragment.consoleEntries.add("      > None");
        } else {
            for (int i = 0; i < location.files.size(); ++i) {
                ConsoleFragment.consoleEntries.add("      > "
                        + location.files.get(i).name + "."
                        + location.files.get(i).type + " - "
                        + df.format(location.files.get(i).size) + "kb");
            }
        }

        // ZIP
        ConsoleFragment.consoleEntries.add("   > Compressed:");
        if (location.zipFiles.size() < 1) {
            ConsoleFragment.consoleEntries.add("      > None");
        } else {
            for (int i = 0; i < location.zipFiles.size(); ++i) {
                ConsoleFragment.consoleEntries
                        .add(
                                "        > "
                                        + location.zipFiles.get(i).name
                                        + "."
                                        + location.zipFiles.get(i).type
                                        + " - "
                                        + (location.zipFiles.get(i).files
                                        .size() + location.zipFiles
                                        .get(i).folders.size())
                                        + " files - "
                                        + df.format(location.zipFiles.get(i).size)
                                        + "kb"
                        );
            }
        }
        // DEVICES
        ConsoleFragment.consoleEntries.add("   > Devices:");
        if (deviceStats.folders.get(0).folders.size() < 1) {
            ConsoleFragment.consoleEntries.add("      > None");
        } else {
            for (int i = 0; i < deviceStats.folders.get(0).folders.size(); ++i) {
                ConsoleFragment.consoleEntries
                        .add(
                                "        > "
                                        + deviceStats.folders.get(0).folders
                                        .get(i).name
                                        + " - "
                                        + (deviceStats.folders.get(0).folders
                                        .get(i).files.size() + deviceStats.folders
                                        .get(0).folders.get(i).folders
                                        .size())
                                        + " files - "
                                        + df.format(deviceStats.folders.get(0).folders
                                        .get(i).size) + "kb"
                        );
            }
        }
        ConsoleFragment.consoleEntries
                .add("--------------------------------------------------------------------------------------------------");
    }

    private static void showDevicesMenu() {

    }

    public static void incLocation(String folderName, LocalFolder targetFolder) {
        if (targetFolder.locked) {
            ConsoleFragment.consoleEntries.add("Access Denied");
            ConsoleFragment.consoleEntries.add(folderName
                    + " is a secure location");
            ConsoleFragment.consoleEntries
                    .add("--------------------------------------------------------------------------------------------------");
        } else if (!targetFolder.locked) {
            // lastLocation = location;
            location = targetFolder;
            locationStr.add("/" + targetFolder.name);
            ConsoleFragment.consoleEntries.add("Opened "
                    + folderName + "");
            ConsoleFragment.consoleEntries
                    .add("--------------------------------------------------------------------------------------------------");
        }
    }

    public static void decLocation() {
        locationStr.remove(locationStr.size() - 1); // remove last
        location = location.location;
    }

    public static void unzip(final ZipFolder zip) {
        ConsoleFragment.consoleEntries.add(
                "Attempting to extract " + zip.name + ".zip");
        ConsoleFragment.consoleEntries
                .add("--------------------------------------------------------------------------------------------------");

        targetZip = zip;

        new CountDownTimer(2000, 1000) { // Delayed Extract
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                // Extract Delayed
                ConsoleFragment.consoleEntries.add(
                        "Successfully extracted:");
                targetZip.unzip();
                targetZip.location.zipFiles.remove(targetZip);
                ConsoleFragment.cl_adapter.notifyDataSetChanged();
                ConsoleFragment.c3_adapter.notifyDataSetChanged();
            }
        }.start();
    }
}
