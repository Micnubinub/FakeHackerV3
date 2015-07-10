package tbs.fakehackerv3.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import tbs.fakehackerv3.MainActivity;
import tbs.fakehackerv3.Message;
import tbs.fakehackerv3.P2PManager;
import tbs.fakehackerv3.R;
import tbs.fakehackerv3.Tools;
import tbs.fakehackerv3.custom_views.FilePagerSlidingTabStrip;

/**
 * Created by root on 31/07/14.
 */
public class FileManagerFragment extends Fragment {
    public static final String FILE_SEP = ":::";
    public static final String COMMAND_BROWSE = "COMMAND_BROWSE";
    public static final String COMMAND_OPEN = "COMMAND_OPEN";
    public static final String COMMAND_DELETE = "COMMAND_DELETE";
    public static final String COMMAND_COPY = "COMMAND_COPY";
    public static final String COMMAND_MOVE = "COMMAND_MOVE";
    public static final String COMMAND_OPEN_PARENT = "/.../";
    public static final String COMMAND_UPLOAD = "COMMAND_UPLOAD";
    public static final String COMMAND_DOWNLOAD = "COMMAND_DOWNLOAD";
    public static final String RESPONSE_BROWSE = "RESPONSE_BROWSE";
    public static final String RESPONSE_OPEN = "RESPONSE_OPEN";
    public static final String RESPONSE_DELETE = "RESPONSE_DELETE";
    public static final String RESPONSE_COPY = "RESPONSE_COPY";
    public static final String RESPONSE_MOVE = "RESPONSE_MOVE";
    public static final String RESPONSE_UPLOAD = "RESPONSE_UPLOAD";
    public static final String RESPONSE_DOWNLOAD = "RESPONSE_DOWNLOAD";

    public static final String FILE_ATTRIBUTE_SEP = "/:/";
    public static final String[] DOCUMENT_EXTENSIONS = {"doc", "docx", "txt", "rtf", "pdf", "odt", "wpd", "xls", "xlsx", "ods", "ppt", "pptx"};
    public static final String[] VIDEO_EXTENSIONS = {"webm", "mkv", "flv", "vob", "ogv", "ogg", "drc", "mng", "avi", "mov", "qt", "wmv", "yuv", "rm", "rmvb", "asf", "mp4", "m4p", "m4v", "mpg", "mp2", "mpeg", "mpe", "mpv", "m2v", "svi", "3gp", "3g2", "mxf", "roq", "nsv"};
    public static final String[] PICTURE_EXTENSIONS = {"jpg", "jpeg", "tif", "gif", "png", "raw"};
    public static final String[] MUSIC_EXTENSIONS = {"3gp", "act", "aiff", "aac", "amr", "au", "awb", "dct", "dss", "dvf", "flac", "gsm", "", "m4a", "m4p", "mmf", "mp3", "mpc", "msv", "ogg", "oga", "opus", "ra", "rm", "raw", "sln", "tta", "vox", "wav", "wma", "wv", "webm"};


    private static final Comparator<File> fileComp = new Comparator<File>() {
        @Override
        public int compare(File file1, File file2) {
            if (file1.isDirectory()) {
                if (file2.isDirectory()) {
                    return String.valueOf(file1.getName().toLowerCase()).compareTo(file2.getName().toLowerCase());
                } else {
                    return -1;
                }
            } else {
                if (file2.isDirectory()) {
                    return 1;
                } else {
                    return String.valueOf(file1.getName().toLowerCase()).compareTo(file2.getName().toLowerCase());
                }
            }
/*                int i;
                if (file.isDirectory() && !file2.isDirectory()) {
                    i = -1;
                } else if (!file.isDirectory() && file2.isDirectory()) {
                    i = 1;
                } else {
                    i = file.getName().compareToIgnoreCase(file2.getName());
                }
                return i;*/
        }
    };
    private static final Fragment[] fragments = new Fragment[2];
    private static final String[] titles = {"Local", "External"};
    private static final ArrayList<File> tmpTree = new ArrayList<File>();
    private static final View.OnClickListener dialogClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.delete:

                    break;
                case R.id.move:

                    break;
                case R.id.copy:

                    break;
                case R.id.rename:

                    break;
            }
        }
    };
    public static boolean isInit;
    private static FragmentActivity context;
    //Todo local and external
    private static String currentExternalDirectory = Environment.getExternalStorageDirectory().getPath();
    private static String currentLocalDirectory = Environment.getExternalStorageDirectory().getPath();
    private static ViewPager pager;
    private static MyPagerAdapter pagerAdapter;
    private static FilePagerSlidingTabStrip tabs;
    private static Dialog dialog;
    private static MikeFileOperationType tmpMikeFileOperationType;
    private static MikeFile tmpMikeFile;

    public static String getCurrentExternalDirectory() {
        if (!(new File(currentExternalDirectory).isDirectory()) || currentExternalDirectory.length() < 1) {
            return Environment.getExternalStorageDirectory().toString();
        }
        return currentExternalDirectory;
    }

    private static void setCurrentExternalDirectory(String currentExternalDirectory) {
        FileManagerFragment.currentExternalDirectory = currentExternalDirectory;
    }

    private static void openFile(Context context, MikeFile mikeFile) {
        final Intent share = new Intent(Intent.ACTION_VIEW);
        try {
            switch (mikeFile.fileType) {
                case DOCUMENT:
                    share.setDataAndType(Uri.parse(mikeFile.path), "text/*");
                    break;
                case GENERIC:
                    share.setDataAndType(Uri.parse(mikeFile.path), "*/*");
                    break;
                case MUSIC:
                    share.setDataAndType(Uri.parse(mikeFile.path), "audio/*");
                    break;
                case VIDEO:
                    share.setDataAndType(Uri.parse(mikeFile.path), "video/*");
                    break;
                case PICTURE:
                    share.setDataAndType(Uri.parse(mikeFile.path), "image/*");
                    break;
            }
            context.startActivity(share);
        } catch (Exception e) {
            print("Failed to open " + mikeFile.toString());
        }
    }

    private static void openFile(MikeFile file) {
        openFile(context, file);
    }

    public static String showTree(File dir, MikeFileOperationType mikeFileOperationType) {
        if (!dir.exists()) {
            //TODO
            currentExternalDirectory = Environment.getExternalStorageDirectory().getPath();
            dir = new File(currentExternalDirectory);
        } else {
            currentExternalDirectory = dir.getPath();
        }

        switch (mikeFileOperationType) {
            case EXTERNAL:
                currentExternalDirectory = dir.getAbsolutePath();
                break;
            case LOCAL:
                currentLocalDirectory = dir.getAbsolutePath();
                break;
        }

        final StringBuilder builder = new StringBuilder();
        final File[] files = dir.listFiles();
        if (files != null) {
            if (!(files.length < 1)) {
                tmpTree.clear();
                tmpTree.ensureCapacity(files.length);

                Collections.addAll(tmpTree, dir.listFiles());
                sortFiles(tmpTree);

                switch (mikeFileOperationType) {
                    case EXTERNAL:
                        for (int i = 0; i < files.length; i++) {
                            final File file = files[i];
                            if ((i < files.length - 1)) {
                                builder.append(MikeFile.getFileString(file));
                                builder.append(FILE_SEP);
                            } else {
                                builder.append(MikeFile.getFileString(file));
                            }
                        }
                        builder.toString();
                        break;
                    case LOCAL:
                        ((LocalFileManager) fragments[0]).parseLocalFile(tmpTree);
                        break;
                }


            } else {
                //Todo think about what to do whne the folder is empty
                print("   Specified folder is empty.");
            }
        }
        return "";
    }

    private static void showTree(String path, MikeFileOperationType mikeFileOperationType) {
        showTree(new File(path), mikeFileOperationType);
    }

    private static void openFolder(File file, MikeFileOperationType mikeFileOperationType) {
        //Todo do this for both local and external files
        switch (mikeFileOperationType) {
            case LOCAL:
                showTree(file, mikeFileOperationType);
                break;
            case EXTERNAL:
                showTree(file, mikeFileOperationType);
                break;
        }

    }

    public static void open(File file, MikeFileOperationType mikeFileOperationType) {
        if (file.isDirectory())
            openFolder(file, mikeFileOperationType);
        else
            openFile(new MikeFile(MikeFile.getFileString(file)));
    }

    public static void createFolder(String path, MikeFileOperationType mikeFileOperationType) {
        createFolder(new File(path), mikeFileOperationType);
    }

    public static void createFolder(File file, MikeFileOperationType mikeFileOperationType) {
        print("creating : " + file.getPath());
        try {
            // file.createNewFile();
            file.mkdirs();
            showTree(getCurrentExternalDirectory(), mikeFileOperationType);
        } catch (Exception e) {
        }
    }

    public static void search(String name) {
    }

    public static void delete(String file, MikeFileOperationType mikeFileOperationType) {
        delete(new File(getCurrentExternalDirectory() + "/" + file), mikeFileOperationType);
    }

    private static void delete(File file, MikeFileOperationType mikeFileOperationType) {
        file.delete();
        print(file.getName() + " deleted");
        showTree(currentExternalDirectory, mikeFileOperationType);
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

    public static void createFile(String name, MikeFileOperationType mikeFileOperationType) {
        createFile(new File(name), mikeFileOperationType);
    }

    public static void createFile(File file, MikeFileOperationType mikeFileOperationType) {
        try {
            file.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        showTree(getCurrentExternalDirectory(), mikeFileOperationType);
    }

    private static void print(String string) {
        Log.e("wifidirecttools:", string);
    }

    public static void sortFiles(ArrayList<File> list) {
        Collections.sort(list, fileComp);
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

    public static void handleMessage(String msg) {
        log("handleFileManagerMsg > " + msg);
        if (msg == null || msg.length() < 1) {
            log("handleMessage > msg == null or len < 1");
            return;
        }

        if (msg.startsWith("COMMAND")) {
            handleCommand(msg);
        } else {
            handleResponse(msg);
        }
    }

    public static void copyFile(File src, File dst) {
        try {
            final FileInputStream inStream = new FileInputStream(src);
            final FileOutputStream outStream = new FileOutputStream(dst);
            final FileChannel inChannel = inStream.getChannel();
            final FileChannel outChannel = outStream.getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);
            inStream.close();
            outStream.close();
        } catch (IOException e) {
            try {
                final InputStream in = new FileInputStream(src);
                final OutputStream out = new FileOutputStream(dst);
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();
            } catch (Exception e1) {
                log("failed to copy file > from " + src.getAbsolutePath() + " to " + dst.getAbsolutePath());
                e.printStackTrace();
                e1.printStackTrace();
            }
        }
    }

    public static void moveFile(File from, File to) {
        try {
            from.renameTo(to);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void handleCommand(String msg) {
        log("handleCommands");
        final String[] split = msg.split(FILE_SEP);
        if (msg.startsWith(COMMAND_BROWSE)) {
            //todo command name + filesep+filepath
            if (split.length < 2 || split[1] == null || split[1].length() < 1) {
                sendFileCommand(RESPONSE_BROWSE + FILE_SEP + showTree(new File(Environment.getExternalStorageDirectory().getPath()), MikeFileOperationType.EXTERNAL));
            } else {
                final String input = split[1];
                if (input.startsWith(COMMAND_OPEN_PARENT)) {
                    sendFileCommand(RESPONSE_BROWSE + FILE_SEP + showTree(new File(currentExternalDirectory).getParentFile(), MikeFileOperationType.EXTERNAL));
                } else {
                    sendFileCommand(RESPONSE_BROWSE + FILE_SEP + showTree(new File(input), MikeFileOperationType.EXTERNAL));
                }
            }
        } else if (msg.startsWith(COMMAND_OPEN)) {
            //todo command name + filesep+filepath
            open(new File(split[1]), MikeFileOperationType.EXTERNAL);
        } else if (msg.startsWith(COMMAND_DELETE)) {
            //todo command name + filesep+filepath
            delete(new File(split[1]), MikeFileOperationType.EXTERNAL);
        } else if (msg.startsWith(COMMAND_COPY)) {
            //todo command name + filesep+filepathFrom+fileSep+fileTo
            copyFile(new File(split[1]), new File(split[2]));
        } else if (msg.startsWith(COMMAND_MOVE)) {
            //todo command name + filesep+filepathFrom+fileSep+fileTo
            moveFile(new File(split[1]), new File(split[2]));
        } else if (msg.startsWith(COMMAND_DOWNLOAD)) {
            //todo command name + fileSep+fileTo
        } else if (msg.startsWith(COMMAND_UPLOAD)) {
            //todo command name + fileSep+fileFrom

        }
    }

    public static void handleResponse(String msg) {
        if (msg.startsWith(RESPONSE_BROWSE)) {
            //todo RESPONSE name + filesep+filepath
            final String[] split = msg.split(FILE_SEP, 2);
            ((ExternalFileManager) fragments[1]).parseReceivedFiles(split[1]);
        } else if (msg.startsWith(RESPONSE_OPEN)) {
            //todo RESPONSE name + filesep+filepath

        } else if (msg.startsWith(RESPONSE_DELETE)) {
            //todo RESPONSE name + filesep+filepath

        } else if (msg.startsWith(RESPONSE_COPY)) {
            //todo RESPONSE name + filesep+filepathFrom+fileSep+fileTo

        } else if (msg.startsWith(RESPONSE_MOVE)) {
            //todo RESPONSE name + filesep+filepathFrom+fileSep+fileTo

        } else if (msg.startsWith(RESPONSE_DOWNLOAD)) {
            //todo RESPONSE name + fileSep+fileTo

        } else if (msg.startsWith(RESPONSE_UPLOAD)) {
            //todo RESPONSE name + fileSep+fileFrom

        }
    }

    private static String getFileTypeString(File file) {
        if (file.isDirectory()) {
            return String.valueOf(FileType.FOLDER);
        }
        final String ext = getExtension(file.getName());
        FileType fileType = FileType.GENERIC;

        if (isMusic(ext)) {
            fileType = FileType.MUSIC;
        } else if (isPicture(ext)) {
            fileType = FileType.PICTURE;
        } else if (isVideo(ext)) {
            fileType = FileType.VIDEO;
        } else if (isDocument(ext)) {
            fileType = FileType.DOCUMENT;
        }
        return String.valueOf(fileType);
    }

    public static boolean isVideo(String extention) {
        for (String videoExtension : VIDEO_EXTENSIONS) {
            if (videoExtension.equals(extention)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isDocument(String extention) {
        for (String documentExt : DOCUMENT_EXTENSIONS) {
            if (documentExt.equals(extention)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isPicture(String extention) {
        for (String pictureExt : PICTURE_EXTENSIONS) {
            if (pictureExt.equals(extention)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isMusic(String extention) {
        for (String musicExt : MUSIC_EXTENSIONS) {
            if (musicExt.equals(extention)) {
                return true;
            }
        }
        return false;
    }

    public static String getExtension(String fileName) {
        String extension = "";

        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            extension = fileName.substring(i + 1);
        }

        return extension.toLowerCase();
    }

    public static void sendFileCommand(String command) {
        if (command != null && command.length() > 0) {
            P2PManager.enqueueMessage(new Message(command, Message.MessageType.FILE));
        } else {
            log("please enter a message_background string or please init p2pManager");
        }
    }

    private static void log(String msg) {
        LogFragment.log(msg);
        Log.e("File Manager", msg);
    }

    private static void showDialog(final MikeFile mikeFile, final MikeFileOperationType mikeFileOperationType) {
        tmpMikeFile = mikeFile;
        tmpMikeFileOperationType = mikeFileOperationType;
        if (dialog != null) {
            try {
                dialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        dialog = new Dialog(context, R.style.CustomDialog);
        dialog.setContentView(R.layout.file_manager_dialog);
        dialog.findViewById(R.id.delete).setOnClickListener(dialogClickListener);
        dialog.findViewById(R.id.rename).setOnClickListener(dialogClickListener);
        dialog.findViewById(R.id.move).setOnClickListener(dialogClickListener);
        dialog.findViewById(R.id.copy).setOnClickListener(dialogClickListener);

        dialog.show();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        setRetainInstance(true);
    }

    public void init() {
        //TODO

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Todo
        final View view = inflater.inflate(R.layout.file_manager_fragment, null);
        //Todo
        setUpFragments(view);
        return view;
    }

    private void setUpFragments(View v) {
        fragments[0] = new LocalFileManager();
        fragments[1] = new ExternalFileManager();

        tabs = (FilePagerSlidingTabStrip) v.findViewById(R.id.tabs);
        pager = (ViewPager) v.findViewById(R.id.view_pager);
        pager.setOffscreenPageLimit(4);

        pagerAdapter = new MyPagerAdapter(getChildFragmentManager());

        pager.setAdapter(pagerAdapter);
        tabs.setViewPager(pager);

    }

    private enum MikeFileOperationType {
        LOCAL, EXTERNAL
    }

    public enum FileType {
        //Todo do the folders
        MUSIC, PICTURE, GENERIC, VIDEO, DOCUMENT, FOLDER
    }

    public static class FileAdapter extends BaseAdapter {

        private static ListView listView;
        private static FileAdapter fileAdapter;
        private ArrayList<MikeFile> files;

        public FileAdapter(ListView listView, ArrayList<MikeFile> files, AdapterView.OnItemClickListener onItemClickListener, AdapterView.OnItemLongClickListener onItemLongClickListener) {
            this.files = files;
            this.listView = listView;
            listView.setOnItemClickListener(onItemClickListener);
            listView.setOnItemLongClickListener(onItemLongClickListener);
            listView.setAdapter(this);

            fileAdapter = this;
        }

        public static FileAdapter getFileAdapter() {
            return fileAdapter;
        }

        public static ListView getListView() {
            return listView;
        }

        public ArrayList<MikeFile> getFiles() {
            return files;
        }

        public void setFiles(ArrayList<MikeFile> files) {
            this.files = files;
            try {
                notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public Context getContext() {
            return context;
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

    public static class MikeFile {
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

        public static String getParentFileString(File file) {
            final StringBuilder builder = new StringBuilder(getFileTypeString(file));
            //Todo
            builder.append(FILE_ATTRIBUTE_SEP);
            builder.append(COMMAND_OPEN_PARENT);
            builder.append(FILE_ATTRIBUTE_SEP);
            builder.append(COMMAND_OPEN_PARENT);
            builder.append(FILE_ATTRIBUTE_SEP);
            builder.append("...");
            return builder.toString();
        }

        @Override
        public String toString() {
            return name + " (" + fileSize + ")";
        }
    }

    //TODO
    public static class LocalFileManager extends Fragment {
        public static FileAdapter fileAdapter;
        private static final Runnable update = new Runnable() {
            @Override
            public void run() {
                try {
                    fileAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        private static ArrayList<MikeFile> files = new ArrayList<MikeFile>();
        //TODO ASAP
        public static final AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                open(new File(files.get(position).path), MikeFileOperationType.LOCAL);
            }
        };
        public static final AdapterView.OnItemLongClickListener onItemLongClickListener = new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showDialog(files.get(position), MikeFileOperationType.LOCAL);
//    Todo mke dialog            //todo command name + filesep+filepath
//                sendFileCommand(COMMAND_BROWSE + FILE_SEP + files.get(position).path);
//                //todo command name + filesep+filepath
//                open(new File(split[1]));
//                //todo command name + filesep+filepath
//                delete(new File(split[1]));
//                //todo command name + filesep+filepathFrom+fileSep+fileTo
//                copyFile(new File(split[1]), new File(split[2]));
//                //todo command name + filesep+filepathFrom+fileSep+fileTo
//                moveFile(new File(split[1]), new File(split[2]));
//                //TODO handle upload and download
                return false;
            }
        };

        public static void updateAdapter() {
            context.runOnUiThread(update);
        }

        public static void parseLocalFile(final ArrayList<File> files) {
            if (LocalFileManager.files == null) {
                LocalFileManager.files = new ArrayList<MikeFile>(files.size());
            } else {
                try {
                    LocalFileManager.files.clear();
                    updateAdapter();
                    LocalFileManager.files.ensureCapacity(files.size());
                    updateAdapter();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    for (File file : files) {
                        try {
                            LocalFileManager.files.add(new MikeFile(MikeFile.getFileString(file)));
                            updateAdapter();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

        }

        @Nullable
        @Override
        public View getView() {
            ListView listView = (ListView) View.inflate(getActivity(), R.layout.file_manager_fragment_item, null);
            fileAdapter = new FileAdapter(listView, files, onItemClickListener, onItemLongClickListener);
            return listView;
        }
    }

    public static class ExternalFileManager extends Fragment {
        //Todo add this to onCreateView
        public static final View.OnClickListener placeHolderListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!P2PManager.isActive()) {
                    MainActivity.toast("click the refresh button on both devices to connect");
                    return;
                }
                isInit = true;
                v.setVisibility(View.GONE);
                sendFileCommand(COMMAND_BROWSE + FILE_SEP);
            }
        };
        public static FileAdapter fileAdapter;
        private static final Runnable update = new Runnable() {
            @Override
            public void run() {
                try {
                    fileAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        private static ArrayList<MikeFile> files = new ArrayList<MikeFile>();
        //TODO ASAP
        public static final AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MainActivity.toast("file clicked : " + files.get(position).toString());
                final MikeFile file = files.get(position);
                if (file.fileType == FileType.FOLDER)
                    sendFileCommand(COMMAND_BROWSE + FILE_SEP + file.path);
                else sendFileCommand(COMMAND_OPEN + FILE_SEP + file.path);
            }
        };
        public static final AdapterView.OnItemLongClickListener onItemLongClickListener = new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//    Todo mke dialog            //todo command name + filesep+filepath
//                sendFileCommand(COMMAND_BROWSE + FILE_SEP + files.get(position).path);
//                //todo command name + filesep+filepath
//                open(new File(split[1]));
//                //todo command name + filesep+filepath
//                delete(new File(split[1]));
//                //todo command name + filesep+filepathFrom+fileSep+fileTo
//                copyFile(new File(split[1]), new File(split[2]));
//                //todo command name + filesep+filepathFrom+fileSep+fileTo
//                moveFile(new File(split[1]), new File(split[2]));
//                //TODO handle upload and download

                showDialog(files.get(position), MikeFileOperationType.LOCAL);
                return false;
            }
        };

        public static void updateAdapter() {
            context.runOnUiThread(update);
        }

        public static void parseReceivedFiles(String files) {
            final String[] fileA = files.split(FILE_SEP);

            if (ExternalFileManager.files == null) {
                ExternalFileManager.files = new ArrayList<MikeFile>(fileA.length);
            } else {
                try {
                    ExternalFileManager.files.clear();
                    updateAdapter();
                    ExternalFileManager.files.ensureCapacity(fileA.length);
                    updateAdapter();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            updateAdapter();

            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    for (String s : fileA) {
                        try {
                            ExternalFileManager.files.add(new MikeFile(s));
                            updateAdapter();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            log("receivedFiles > " + ExternalFileManager.files.toString());
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            ListView listView = (ListView) inflater.inflate(R.layout.file_manager_fragment_item, null);
            fileAdapter = new FileAdapter(listView, files, onItemClickListener, onItemLongClickListener);
            return listView;
        }
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }
    }

}
