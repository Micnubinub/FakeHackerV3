package tbs.fakehackerv3.local_system_data;

import java.util.ArrayList;

import tbs.fakehackerv3.fragments.ConsoleFragment;


public class ZipFolder extends LocalFolder {
    public final SidFileType type;
    private final LocalFile info;
    private final LocalFolder content;
    private final ArrayList<LocalFile> attacks;
    private double maxSize;

    public ZipFolder(String name, LocalFolder newLocation, boolean isLcoked) {
        super(name, newLocation, isLcoked);
        this.attacks = new ArrayList();
        this.type = SidFileType.zip;
        this.info = new LocalFile("info", SidFileType.txt, 20, 0);
        this.content = new LocalFolder("content", this, true);
        this.maxSize = 0;
    }

    public void setup(int diff) {
        this.folders.add(content);
        this.files.add(info);
        // this.name = name;
        this.maxSize = diff;

        double tempSize = 0.03;
        for (int i = 0; i < files.size(); ++i) {
            tempSize += files.get(i).size;
        }
        for (int i = 0; i < folders.size(); ++i) {
            for (int x = 0; x < folders.get(i).files.size(); ++x) {
                tempSize += folders.get(i).files.get(x).size;
            }
        }
        for (int i = 0; i < zipFiles.size(); ++i) {
            tempSize += zipFiles.get(i).size;
        }
        this.size = tempSize;

    }

    public void update() {
        double tempSize = 0.03;
        for (int i = 0; i < files.size(); ++i) {
            tempSize += files.get(i).size;
        }
        for (int i = 0; i < folders.size(); ++i) {
            for (int x = 0; x < folders.get(i).files.size(); ++x) {
                tempSize += folders.get(i).files.get(x).size;
            }
        }
        for (int i = 0; i < zipFiles.size(); ++i) {
            tempSize += zipFiles.get(i).size;
        }
        this.size = tempSize;
    }

    public void unzip() { // UNZIP
        ConsoleFragment.consoleEntries.add(
                "Successfully Extracted:");
        for (int i = 0; i < folders.get(0).files.size(); ++i) {
            System.out.println("For Loop for Adding");

            ConsoleFragment.player.location.files.add(folders.get(0).files.get(i));
            ConsoleFragment.consoleEntries.add(" > "
                    + folders.get(0).files.get(i).name);
        }
        ConsoleFragment.consoleEntries
                .add("--------------------------------------------------------------------------------------------------");
    }
}