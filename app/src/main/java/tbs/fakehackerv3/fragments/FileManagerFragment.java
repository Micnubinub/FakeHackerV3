package tbs.fakehackerv3.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
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

import tbs.fakehackerv3.MainActivity;
import tbs.fakehackerv3.Message;
import tbs.fakehackerv3.P2PManager;
import tbs.fakehackerv3.R;
import tbs.fakehackerv3.Tools;

/**
 * Created by root on 31/07/14.
 */
public class FileManagerFragment extends Fragment {
    public static final String FILE_SEP = ":::";
    //todo command name + filesep+filepath
    public static final String COMMAND_DELETE = "COMMAND_DELETE";
    //todo command name + filesep+filepathFrom+fileSep+fileTo
    public static final String COMMAND_COPY = "COMMAND_COPY";
    //todo command name + filesep+filepathFrom+fileSep+fileTo
    public static final String COMMAND_MOVE = "COMMAND_MOVE";
    //todo command name + fileSep+fileTo
    public static final String COMMAND_UPLOAD = "COMMAND_UPLOAD";
    //todo command name + fileSep+fileFrom
    public static final String COMMAND_DOWNLOAD = "COMMAND_DOWNLOAD";
    //Todo parse all the commands
    public static final String FILE_ATTRIBUTE_SEP = "/:/";
    private static FragmentActivity context;
    private static String currentDirectory = Environment.getExternalStorageDirectory().getPath();
    private static boolean isInFileManagerMode = false;
    private static final Intent share = new Intent(Intent.ACTION_SEND);
    private static ArrayList<File> currentTree;

    public static ListView listView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Todo
        final View view = inflater.inflate(R.layout.file_manager_fragment, null);
        return view;
    }

    public static boolean isIsInFileManagerMode() {
        return isInFileManagerMode;
    }

    private static void setIsInFileManagerMode(boolean isInFileManagerMode) {
        FileManagerFragment.isInFileManagerMode = isInFileManagerMode;
    }

    public static String getCurrentDirectory() {
        if (!(new File(currentDirectory).isDirectory()) || currentDirectory.length() < 1) {
            return Environment.getExternalStorageDirectory().toString();
        }
        return currentDirectory;
    }

    private static void setCurrentDirectory(String currentDirectory) {
        FileManagerFragment.currentDirectory = currentDirectory;
    }

    private static void openFile(Context context, String path) {
        try {
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

    public static String showTree(File dir) {
        if (!dir.exists()) {
            //TODO
        }
        currentDirectory = dir.getPath();

        final StringBuilder builder = new StringBuilder();
        if (dir.listFiles() != null) {
            if (!(dir.listFiles().length < 1)) {
                if (currentTree != null)
                    currentTree.clear();
                else
                    currentTree = new ArrayList<File>();

                Collections.addAll(currentTree, dir.listFiles());

                sort(currentTree);
                print(" File hierarchy " + dir.getAbsolutePath() + " >");

                for (int i = 0; i < currentTree.size(); i++) {
                    final File file = currentTree.get(i);

                    if ((i == currentTree.size() - 1)) {
                        builder.append(MikeFile.getFileString(file));
                        builder.append(FILE_SEP);
                    } else {
                        builder.append(MikeFile.getFileString(file));
                    }
                }
            } else {
                //Todo think about what to do whne the folder is empty
                print("   Specified folder is empty.");
            }
        }
        return builder.toString();
    }

    private static void showTree(String path) {
        showTree(new File(path));
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

    public static class FileAdapter extends BaseAdapter {
        private static ArrayList<MikeFile> files;
        private static ListView listView;
        private Context context;
        private static FileAdapter fileAdapter;

        public Context getContext() {
            return context;
        }

        public static FileAdapter getFileAdapter() {
            return fileAdapter;
        }

        public static ListView getListView() {
            return listView;
        }

        public static ArrayList<MikeFile> getFiles() {
            return files;
        }

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

        public static void setFiles(ArrayList<MikeFile> files) {
            FileAdapter.files = files;
        }

        public FileAdapter(ListView listView, ArrayList<MikeFile> files) {
            this.files = files;
            this.context = listView.getContext();
            this.listView = listView;
            listView.setOnItemClickListener(onItemClickListener);
            listView.setOnItemLongClickListener(onItemLongClickListener);
            listView.setAdapter(this);
            fileAdapter = this;
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
            case FOLDER:
                res = R.drawable.file_folder;
                break;
        }
        return res;
    }

    public static class MikeFile {
        public final String path, name, fileSize;
        public final FileType fileType;

        public static String getFileString(File file) {
            final StringBuilder builder = new StringBuilder(getFileTypeString(file));
            //Todo
            builder.append(FILE_ATTRIBUTE_SEP);
            builder.append(file.getAbsolutePath());
            builder.append(FILE_ATTRIBUTE_SEP);
            builder.append(file.getName());
            builder.append(FILE_ATTRIBUTE_SEP);
            builder.append(Tools.getFileSize(file.length()));

            return builder.toString();
        }

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
            } else if (type.equals(String.valueOf(FileType.FOLDER))) {
                fileType = FileType.FOLDER;
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

    public static void setReceivedFiles(String files) {
        final String[] fileA = files.split(FILE_SEP);
        ArrayList<MikeFile> mikeFiles = FileAdapter.getFiles();

        if (mikeFiles == null) {
            mikeFiles = new ArrayList<MikeFile>();
        }

        for (String s : fileA) {
            try {
                mikeFiles.add(new MikeFile(s));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        FileAdapter.setFiles(mikeFiles);
        if (FileAdapter.getFileAdapter() != null) {
            try {
                FileAdapter.getListView().post(new Runnable() {
                    @Override
                    public void run() {
                        FileAdapter.getFileAdapter().notifyDataSetChanged();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static String getFileTypeString(File file) {
        if (file.isDirectory()) {
            return String.valueOf(FileType.FOLDER);
        }
        final String ext = getExtension(file.getName());
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
        //Todo do the folders
        MUSIC, PICTURE, GENERIC, VIDEO, DOCUMENT, FOLDER
    }

    public static void sendMessage(String command) {
        if (command != null && command.length() > 0) {
            P2PManager.enqueueMessage(new Message(command, Message.MessageType.SEND_COMMAND));
        } else {
            log("please enter a message_background string or please init p2pManager");
        }
    }

    private static void log(String msg) {
        Log.e("File Manager", msg);
    }

    public static final String DOCUMENT_EXTENSIONS = "doc,docx,txt,rtf,pdf,odt,wpd,xls,xlsx,ods,ppt,pptx";
    public static final String VIDEO_EXTENSIONS = "webm,mkv,flv,vob,ogv,ogg,drc,mng,avi,mov,qt,wmv,yuv,rm,rmvb,asf,mp4,m4p,m4v,mpg,mp2,mpeg,mpe,mpv,m2v,svi,3gp,3g2,mxf,roq,nsv";
    public static final String PICTURE_EXTENSIONS = "jpg,jpeg,tif,gif,png,raw";
    public static final String MUSIC_EXTENSIONS = "3gp,act,aiff,aac,amr,au,awb,dct,dss,dvf,flac,gsm,,m4a,m4p,mmf,mp3,mpc,msv,ogg,oga,opus,ra,rm,raw,sln,tta,vox,wav,wma,wv,webm";
}