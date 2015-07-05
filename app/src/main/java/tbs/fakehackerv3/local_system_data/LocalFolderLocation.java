package tbs.fakehackerv3.local_system_data;

class LocalFolderLocation {
    private final String location;
    private final LocalFolder folder;

    public LocalFolderLocation(LocalFolder folder) {
        this.location = "C/:";
        this.folder = folder;
    }
}
