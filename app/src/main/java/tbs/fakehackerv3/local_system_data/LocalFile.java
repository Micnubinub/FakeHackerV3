package tbs.fakehackerv3.local_system_data;

import tbs.fakehackerv3.fragments.Console;

public class LocalFile {
    public final String name;
    public final SidFileType type;
    private final int rarity; // How rare the file is out of 10
    public double size;

    public LocalFile(String name, SidFileType type, double size, int rar) {
        this.name = name;
        this.type = type;
        this.size = size;
        this.rarity = rar;
        this.size += Console.randInt(0, 10);

    }
}
