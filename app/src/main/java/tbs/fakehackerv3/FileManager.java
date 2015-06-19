package tbs.fakehackerv3;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by root on 31/07/14.
 */
public class FileManager {
    public static final String FILE_SEP = ":::";
    public static final String FILE_ATTRIBUTE_SEP = "/:/";

    private static Context context;
    private static String currentDirectory = Environment.getExternalStorageDirectory().getPath();
    private static boolean isInFileManagerMode = false;
    private static ArrayList<File> currentTree;

    public FileManager(Context c) {
        context = c;
    }

    public static boolean isIsInFileManagerMode() {
        return isInFileManagerMode;
    }

    private static void setIsInFileManagerMode(boolean isInFileManagerMode) {
        FileManager.isInFileManagerMode = isInFileManagerMode;
    }

    public static String getCurrentDirectory() {
        if (!(new File(currentDirectory).isDirectory()) || currentDirectory.length() < 1) {
            return Environment.getExternalStorageDirectory().toString();
        }
        return currentDirectory;
    }

    private static void setCurrentDirectory(String currentDirectory) {
        FileManager.currentDirectory = currentDirectory;
    }

    private static void openFile(Context context, String path) {
        try {
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("*/*");
            share.putExtra(Intent.EXTRA_STREAM, Uri.parse(path));
            context.startActivity(Intent.createChooser(share, "Share File"));
        } catch (Exception e) {
            print("Failed to open " + (new File(path).getName()));

        }
    }

    private static void openFile(File file) {
        openFile(context, file.getPath());
    }

    public static void showTree(File dir) {
        currentDirectory = dir.getPath();

        int i = 0;
        try {
            if (dir.listFiles() != null) {
                if (!(dir.listFiles().length < 1)) {
                    if (currentTree != null)
                        currentTree.clear();
                    else
                        currentTree = new ArrayList<File>();

                    Collections.addAll(currentTree, dir.listFiles());

                    sort(currentTree);
                    print(" File hierarchy " + dir.getAbsolutePath() + " >");

                    for (File file : currentTree) {
                        ++i;
                        if (file.isDirectory())
                            print("   " + i + ".  /" + file.getName() + "/");
                        else
                            print("   " + i + ".  " + file.getName());
                    }
                } else {
                    print("   Specified folder is empty.");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            print("Failed to show :" + dir.getPath());

        }
    }

    private static void showTree(String path) {
        showTree(new File(path));
    }

    public static void openFolder(String path) {
        try {
            openFolder(new File(path));
        } catch (Exception e) {
            if (path != null)
                print("   Failed to open :" + path);
            else
                print("   Failed to open folder");

        }
    }

    private static void openFolder(File file) {
        setCurrentDirectory(file.getPath());
        showTree(file);
    }

    public static void open(int path) {
        if (currentTree.get(path - 1).isDirectory())
            openFolder(currentTree.get(path - 1));
        else
            openFile(currentTree.get(path - 1));
    }

    public static void open(String path) {
        open(new File(path));
    }

    private static void open(File file) {

        if (file.isDirectory())
            openFolder(file);
        else
            openFile(file);
    }

    private static void showFileDetails(File file) {
        try {

            print("   Name : " + file.getName());
            print("   Size : " + RemoteTools.fileSize(file.length()));


            print("   Modified : " + RemoteTools.getDate(file.lastModified()));


        } catch (Exception e) {
            print("   Failed to show details of " + file.getPath());

        }
    }

    public static void showFileDetails(String path) {
        showFileDetails(new File(path));
    }

    public static void showFileDetails(int path) {
        showFileDetails(currentTree.get(path));
    }

    public static void createFolder(String path) {
        createFolder(new File(path));
    }

    public static void createFolder(File file) {
        print("creating : " + file.getPath());
        try {
            // file.createNewFile();
            file.mkdirs();
            showTree(getCurrentDirectory());
        } catch (Exception e) {
        }
    }

    public static void search(String name) {


    }

    public static void delete(int file) {
        delete(currentTree.get(file - 1));
    }

    public static void delete(String file) {
        delete(new File(getCurrentDirectory() + "/" + file));
    }

    private static void delete(File file) {

        file.delete();
        print(file.getName() + " deleted");

        showTree(currentDirectory);
    }

    public static void pop(Context context) {
    }


    public static long getFreeSpace(String path) {
        return (new File(path)).getFreeSpace();
    }

    public static long getTotalSpace(String path) {
        return (new File(path)).getTotalSpace();
    }

    public static long getSizeInBytes(String file) {

        return (new File(file)).length();
    }

    public static long getSizeInBytes(File file) {
        return file.length();
    }


    public static void createFile(String name) {
        createFile(new File(name));

    }

    public static void createFile(File file) {

        try {
            file.createNewFile();
        } catch (Exception e) {
        }
        showTree(getCurrentDirectory());
    }

    public static void startFilemanager() {
        //    help();
        setIsInFileManagerMode(true);
        //  showTree(Environment.getExternalStorageDirectory());
    }

    public static void exitFilemanager() {
        setIsInFileManagerMode(false);
        print("Exiting FileManager...");
        // currentTree.clear();
        currentTree = null;
    }

    private static void print(String string) {
        Log.e("wifidirecttools:", string);
    }

    public static void sort(ArrayList<File> list) {
        Collections.sort(list, new Comparator<File>() {
            @Override
            public int compare(File file, File file2) {
                return file.getName().compareToIgnoreCase(file2.getName());
            }
        });
    }

    public static String getCurrentTree() {
        final StringBuilder builder = new StringBuilder();


        //
        return builder.toString();
    }

    public static class FileAdapter extends BaseAdapter {
        private static ArrayList<MikeFile> files;
        private Context context;

        public static final AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MainActivity.toast("file clicked : " + files.get(position).toString());
            }
        };

        public static final AdapterView.OnItemLongClickListener onItemLongClickListener = new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                MainActivity.toast("file longClicked : " + files.get(position).toString());
                return false;
            }
        };

        public FileAdapter(ListView listView, ArrayList<MikeFile> files) {
            this.files = files;
            this.context = listView.getContext();
            listView.setOnItemClickListener(onItemClickListener);
            listView.setOnItemLongClickListener(onItemLongClickListener);
            listView.setAdapter(this);
        }

        @Override
        public int getCount() {
            return files == null ? 0 : files.size();
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
            final MikeFile mikeFile = files.get(position);
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.file_item, null);
            }
            final ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
            final TextView fileName = (TextView) convertView.findViewById(R.id.file_name);
            final TextView fileSize = (TextView) convertView.findViewById(R.id.file_size);

            icon.setImageResource(getImageResource(mikeFile.fileType));
            fileName.setText(mikeFile.name);
            fileSize.setText(mikeFile.fileSize);

            return convertView;

        }
    }


    public static int getImageResource(FileType type) {
        int res = R.drawable.file_file;

        switch (type) {
            case DOCUMENT:
                res = R.drawable.file_document;
                break;
            case MUSIC:
                res = R.drawable.file_music;
                break;
            case PICTURE:
                res = R.drawable.file_image;
                break;
            case VIDEO:
                res = R.drawable.file_video;
                break;
        }
        return res;
    }

    public class MikeFile {
        public final String path, name, fileSize;
        public final FileType fileType;

        public MikeFile(String mikeFile) {
            final String[] split = mikeFile.split(FILE_ATTRIBUTE_SEP, 4);
            this.fileSize = split[3];
            this.path = split[1];
            this.name = split[2];
            final String type = split[0];
            if (type.equals(String.valueOf(FileType.GENERIC))) {
                fileType = FileType.GENERIC;
            } else if (type.equals(String.valueOf(FileType.MUSIC))) {
                fileType = FileType.MUSIC;
            } else if (type.equals(String.valueOf(FileType.PICTURE))) {
                fileType = FileType.PICTURE;
            } else if (type.equals(String.valueOf(FileType.VIDEO))) {
                fileType = FileType.VIDEO;
            } else {
                fileType = FileType.DOCUMENT;
            }
        }

        public MikeFile(FileType fileType, String path, String name, String fileSize) {
            this.fileSize = fileSize;
            this.fileType = fileType;
            this.name = name;
            this.path = path;
        }

        @Override
        public String toString() {
            return name + " (" + fileSize + ")";
        }
    }


    private static String getFileTypeString(String fileName) {
        final String ext = getExtension(fileName);
        FileType fileType = FileType.GENERIC;

        if (MUSIC_EXTENSIONS.contains(ext)) {
            fileType = FileType.MUSIC;
        } else if (PICTURE_EXTENSIONS.contains(ext)) {
            fileType = FileType.PICTURE;
        } else if (VIDEO_EXTENSIONS.contains(ext)) {
            fileType = FileType.VIDEO;
        } else if (DOCUMENT_EXTENSIONS.contains(ext)) {
            fileType = FileType.DOCUMENT;
        }
        return String.valueOf(fileType);
    }

    public static String getExtension(String fileName) {
        String extension = "";

        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            extension = fileName.substring(i + 1);
        }

        return extension.toLowerCase();
    }

    public enum FileType {
        MUSIC, PICTURE, GENERIC, VIDEO, DOCUMENT
    }

    public static final String DOCUMENT_EXTENSIONS = "doc,docx,txt,rtf,pdf,odt,wpd,xls,xlsx,ods,ppt,pptx";
    public static final String VIDEO_EXTENSIONS = "webm,mkv,flv,vob,ogv,ogg,drc,mng,avi,mov,qt,wmv,yuv,rm,rmvb,asf,mp4,m4p,m4v,mpg,mp2,mpeg,mpe,mpv,m2v,svi,3gp,3g2,mxf,roq,nsv";
    public static final String PICTURE_EXTENSIONS = "jpg,jpeg,tif,gif,png,raw";
    public static final String MUSIC_EXTENSIONS = "3gp,act,aiff,aac,amr,au,awb,dct,dss,dvf,flac,gsm,,m4a,m4p,mmf,mp3,mpc,msv,ogg,oga,opus,ra,rm,raw,sln,tta,vox,wav,wma,wv,webm";
}
