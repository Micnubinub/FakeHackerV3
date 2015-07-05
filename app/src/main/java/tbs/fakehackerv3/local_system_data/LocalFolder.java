package tbs.fakehackerv3.local_system_data;

import java.util.ArrayList;

public class LocalFolder {
    public final String name;
    public final ArrayList<LocalFile> files;
    public final ArrayList<LocalFolder> folders;
    public final ArrayList<ZipFolder> zipFiles;
    public final boolean locked;
    public double size; //show size of all files in folder. SHows hacker if there is valuable content
    public LocalFolder location;

    public LocalFolder(String name, LocalFolder newLocation, boolean isLocked) {
        this.name = name;
        this.size = 0.03;
        this.files = new ArrayList();
        this.zipFiles = new ArrayList();
        this.folders = new ArrayList();
        this.location = newLocation;
        this.locked = isLocked;
    }

    public void update() {
        double tempSize = 0.03;
        for (int i = 0; i < files.size(); ++i) {
            tempSize += files.get(i).size;
        }
        for (int i = 0; i < folders.size(); ++i) {
            tempSize += folders.get(i).size;
        }
        for (int i = 0; i < zipFiles.size(); ++i) {
            tempSize += zipFiles.get(i).size;
        }
        this.size = tempSize;
    }
}
