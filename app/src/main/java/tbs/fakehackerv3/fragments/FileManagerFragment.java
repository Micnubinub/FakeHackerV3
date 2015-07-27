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
import java.util.LinkedList;
import java.util.List;

import tbs.fakehackerv3.MainActivity;
import tbs.fakehackerv3.Message;
import tbs.fakehackerv3.P2PManager;
import tbs.fakehackerv3.R;
import tbs.fakehackerv3.Tools;
import tbs.fakehackerv3.custom_views.DisconnectedButton;
import tbs.fakehackerv3.custom_views.FilePagerSlidingTabStrip;
import tbs.fakehackerv3.custom_views.HackerEditText;
import tbs.fakehackerv3.custom_views.HackerTextView;
import tbs.fakehackerv3.custom_views.ProgressBar;

/**
 * Crffileeated by root on 31/07/14.
 */
public class FileManagerFragment extends P2PFragment {
    public static final String FILE_SEP = ":::";
    public static final String COMMAND_BROWSE = "COMMAND_BROWSE";
    public static final String COMMAND_OPEN = "COMMAND_OPEN";
    public static final String COMMAND_DELETE = "COMMAND_DELETE";
    public static final String COMMAND_COPY = "COMMAND_COPY";
    public static final String COMMAND_MOVE = "COMMAND_MOVE";
    public static final String COMMAND_RENAME = "COMMAND_RENAME";
    public static final String PARENT_NAME = "/...../";
    public static final String COMMAND_UPLOAD = "COMMAND_UPLOAD";
    public static final String COMMAND_DOWNLOAD = "COMMAND_DOWNLOAD";
    public static final String READY_TO_RECEIVE_FILE = "/READY_TO_RECEIVE_FILE\\";
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
    public static final String[] MUSIC_EXTENSIONS = {"3gp", "act", "aiff", "aac", "amr", "au", "awb", "dct", "dss", "dvf", "flac", "gsm", "m4a", "m4p", "mmf", "mp3", "mpc", "msv", "ogg", "oga", "opus", "ra", "rm", "raw", "sln", "tta", "vox", "wav", "wma", "wv", "webm"};
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
        }
    };
    private static final Fragment[] fragments = new Fragment[2];
    private static final ArrayList<File> tmpTree = new ArrayList<File>();
    public static long fileLength;
    public static String tmpFileBeingDownloadedPath, tmpFileBeingUploadedPath;
    private static FragmentActivity context;
    //Todo local and external, make sure these are correct, as they are important for copy/move
    private static String currentExternalDirectory = Environment.getExternalStorageDirectory().getPath();
    private static String currentLocalDirectory = Environment.getExternalStorageDirectory().getPath();
    private static ViewPager pager;
    private static MyPagerAdapter pagerAdapter;
    private static FilePagerSlidingTabStrip tabs;
    private static View pasteButton;
    private static final Runnable showPasteButtonRunnable = new Runnable() {
        @Override
        public void run() {
            pasteButton.setVisibility(View.VISIBLE);
        }
    };
    private static final Runnable hidePasteButtonRunnable = new Runnable() {
        @Override
        public void run() {
            pasteButton.setVisibility(View.GONE);
        }
    };
    private static Dialog dialog;
    private static final Runnable dialogDismissal = new Runnable() {
        @Override
        public void run() {
            if (dialog != null) {
                try {
                    dialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };
    private static MikeFileOperationType tmpMikeFileOperationType;
    private static MikeFile tmpMikeFile;
    private static MikeFileOperation mikeFileOperation;
    private static final View.OnClickListener dialogClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (tmpMikeFileOperationType) {
                case LOCAL:
                    handleLocalLongClick(v.getId());
                    break;
                case EXTERNAL:
                    handleExternalLongClick(v.getId());
                    break;
            }
        }
    };
    private static final View.OnClickListener pasteClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final MikeFileOperationType clipBoardOutPutLocation = (pager.getCurrentItem() == 0) ? MikeFileOperationType.LOCAL : MikeFileOperationType.EXTERNAL;
            switch (clipBoardOutPutLocation) {
                case EXTERNAL:
                    log("pasteClickListener > external");
                    handleExternalOutPutLocation();
                    break;
                case LOCAL:
                    log("pasteClickListener > local");
                    handleLocalOutPutLocation();
                    break;
            }
            hidePasteButton();
        }
    };
    private static ProgressBar progressBar;

    public static String getCurrentExternalDirectory() {
        if (!(new File(currentExternalDirectory).isDirectory()) || currentExternalDirectory.length() < 1) {
            return Environment.getExternalStorageDirectory().toString();
        }
        return currentExternalDirectory;
    }

    private static void handleLocalLongClick(int id) {
        switch (id) {
            case R.id.delete:
                delete(new File(tmpMikeFile.path), MikeFileOperationType.LOCAL);
                dismissDialog();
                break;
            case R.id.copy:
                mikeFileOperation = MikeFileOperation.COPY_LOCAL;
                showPasteButton();
                dismissDialog();
                break;
            case R.id.rename:
                showRenameDialog(tmpMikeFile, MikeFileOperationType.LOCAL);
                break;
            case R.id.move:
                mikeFileOperation = MikeFileOperation.MOVE_LOCAL;
                showPasteButton();
                dismissDialog();
                break;
        }
    }

    public static void handleLocalOutPutLocation() {
        switch (mikeFileOperation) {
            case COPY_EXTERNAL:
                fileLength = getFileSize(new File(tmpMikeFile.path));
                if ((fileLength > 2048000) && !MainActivity.isPro) {
                    MainActivity.toast("Get pro version to upload/download files bigger than 2MB");
                    return;
                }
                sendFileCommand(COMMAND_DOWNLOAD + FILE_SEP + tmpMikeFile.path + FILE_SEP + currentLocalDirectory + "/" + tmpMikeFile.name + FILE_SEP + fileLength);
                break;
            case COPY_LOCAL:
                copyFile(tmpMikeFile.path, currentLocalDirectory + "/" + tmpMikeFile.name);
                break;
            case MOVE_EXTERNAL:
                fileLength = getFileSize(new File(tmpMikeFile.path));
                if ((fileLength > 2048000) && !MainActivity.isPro) {
                    MainActivity.toast("Get pro version to upload/download files bigger than 2MB");
                    return;
                }
                sendFileCommand(COMMAND_DOWNLOAD + FILE_SEP + tmpMikeFile.path + FILE_SEP + currentLocalDirectory + "/" + tmpMikeFile.name + FILE_SEP + fileLength);
                break;
            case MOVE_LOCAL:
                moveFile(tmpMikeFile.path, currentLocalDirectory + "/" + tmpMikeFile.name);
                break;
        }
        showTree(currentLocalDirectory, MikeFileOperationType.LOCAL);
    }

    public static long getFileSize(final File file) {
        if (file == null || !file.exists())
            return 0;
        if (!file.isDirectory())
            return file.length();
        final List<File> dirs = new LinkedList<File>();
        dirs.add(file);
        long result = 0;
        while (!dirs.isEmpty()) {
            final File dir = dirs.remove(0);
            if (!dir.exists())
                continue;
            final File[] listFiles = dir.listFiles();
            if (listFiles == null || listFiles.length == 0)
                continue;
            for (final File child : listFiles) {
                result += child.length();
                if (child.isDirectory())
                    dirs.add(child);
            }
        }
        return result;
    }

    public static void handleExternalOutPutLocation() {
        switch (mikeFileOperation) {
            case COPY_EXTERNAL:
                sendFileCommand(COMMAND_COPY + FILE_SEP + tmpMikeFile.path + FILE_SEP + currentExternalDirectory + "/" + tmpMikeFile.name);
                break;
            case COPY_LOCAL:
                tmpFileBeingUploadedPath = tmpMikeFile.path;
                fileLength = getFileSize(new File(tmpMikeFile.path));
                if ((fileLength > 2048000) && !MainActivity.isPro) {
                    MainActivity.toast("Get pro version to upload/download files bigger than 2MB");
                    return;
                }
                sendFileCommand(COMMAND_UPLOAD + FILE_SEP + currentExternalDirectory + "/" + tmpMikeFile.name + FILE_SEP + fileLength);
                break;
            case MOVE_EXTERNAL:
                sendFileCommand(COMMAND_MOVE + FILE_SEP + tmpMikeFile.path + FILE_SEP + currentExternalDirectory + "/" + tmpMikeFile.name);
//                P2PManager.addLocalFileMovedListener(new LocalFileMoved() {
//                    @Override
//                    public void onLocalFileMoved(String path) {
//                        //Todo
//
//                        if (true) {
//                            P2PManager.removeLocalFileMovedListener(this);
//                        }
//                    }
//                });
                break;
            case MOVE_LOCAL:
                tmpFileBeingUploadedPath = tmpMikeFile.path;
                fileLength = getFileSize(new File(tmpMikeFile.path));
                if ((fileLength > 2048000) && !MainActivity.isPro) {
                    MainActivity.toast("Get pro version to upload/download files bigger than 2MB");
                    return;
                }
                sendFileCommand(COMMAND_UPLOAD + FILE_SEP + currentExternalDirectory + "/" + tmpMikeFile.name + FILE_SEP + fileLength);
                break;
        }
        showTree(currentExternalDirectory, MikeFileOperationType.EXTERNAL);
    }

    private static void showPasteButton() {
        try {
            context.runOnUiThread(showPasteButtonRunnable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void hidePasteButton() {
        try {
            context.runOnUiThread(hidePasteButtonRunnable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void handleExternalLongClick(int id) {
        switch (id) {
            case R.id.delete:
                sendFileCommand(COMMAND_DELETE + FILE_SEP + tmpMikeFile.path);
                dismissDialog();
                break;
            case R.id.copy:
                mikeFileOperation = MikeFileOperation.COPY_EXTERNAL;
                showPasteButton();
                dismissDialog();
                break;
            case R.id.rename:
                showRenameDialog(tmpMikeFile, MikeFileOperationType.EXTERNAL);
                break;
            case R.id.move:
                mikeFileOperation = MikeFileOperation.MOVE_EXTERNAL;
                showPasteButton();
                dismissDialog();
                break;
        }
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
        Log.e("showTree > ", dir.toString());

        switch (mikeFileOperationType) {
            case LOCAL:
                if (!dir.exists()) {
                    //TODO
                    currentLocalDirectory = Environment.getExternalStorageDirectory().getPath();
                    dir = new File(currentLocalDirectory);
                } else {
                    currentLocalDirectory = dir.getPath();
                }
                break;
            case EXTERNAL:
                if (!dir.exists()) {
                    //TODO
                    currentExternalDirectory = Environment.getExternalStorageDirectory().getPath();
                    dir = new File(currentExternalDirectory);
                } else {
                    currentExternalDirectory = dir.getPath();
                }
                break;
        }
        final StringBuilder builder = new StringBuilder();

        final File[] files = dir.listFiles();

        tmpTree.clear();
        tmpTree.ensureCapacity(files.length);

        if (files != null) {
            if (!(files.length < 1)) {

                Collections.addAll(tmpTree, files);
                sortFiles(tmpTree);

                switch (mikeFileOperationType) {
                    case EXTERNAL:
                        builder.append(new MikeFile(dir.getParent(), getFileSize(dir.getParentFile())).toString());
                        builder.append(FILE_SEP);

                        for (int i = 0; i < tmpTree.size(); i++) {
                            final File file = tmpTree.get(i);
                            if ((i < files.length - 1)) {
                                builder.append(MikeFile.getFileString(file));
                                builder.append(FILE_SEP);
                            } else {
                                builder.append(MikeFile.getFileString(file));
                            }
                        }
                        return builder.toString();
                    case LOCAL:
                        printTree();
                        ((LocalFileManager) fragments[0]).parseLocalFile(tmpTree);
                        break;
                }


            } else {
                //Todo think about what to do when the folder is empty
                log("   Specified folder is empty.");

                switch (mikeFileOperationType) {
                    case EXTERNAL:
                        //Todo test
                        builder.append(new MikeFile(dir.getParent(), getFileSize(dir.getParentFile())).toString());
                        return builder.toString();
                    case LOCAL:
                        tmpTree.clear();
                        printTree();
                        ((LocalFileManager) fragments[0]).parseEmptyFolder(dir);
                        break;
                }

            }
        }
        return "";
    }

    private static void printTree() {
        seperator();
        print("File hierarchy " + currentLocalDirectory + " >");
        seperator();

        for (int i = 0; i < tmpTree.size(); i++) {
            final File f = tmpTree.get(i);
            print("" + (1 + i) + ". " + (f.isDirectory() ? "/" : "") + f.getName() + (f.isDirectory() ? "/" : ""));
        }

        seperator();
    }

    private static void initExternal() {
        sendFileCommand(COMMAND_BROWSE + FILE_SEP);
    }

    private static void showTree(String path, MikeFileOperationType mikeFileOperationType) {
        showTree(new File(path), mikeFileOperationType);
    }

    private static void openFolder(File file, MikeFileOperationType mikeFileOperationType) {
        showTree(file, mikeFileOperationType);
    }

    private static void open(int i) {
        try {
            final File f = new File(currentLocalDirectory);
            final File[] files = f.listFiles();
            tmpTree.clear();
            tmpTree.ensureCapacity(files.length);
            Collections.addAll(tmpTree, files);
            sortFiles(tmpTree);
            open(tmpTree.get(i - 1), MikeFileOperationType.LOCAL);
        } catch (Exception e) {
            e.printStackTrace();
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
        deleteFileOrDirectory(file);
        print(file.getName() + " was deleted");
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
        ConsoleFragment.addConsoleItem(string);
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
        if (msg.startsWith(READY_TO_RECEIVE_FILE)) {
            P2PManager.sendFile();
        } else {
            if (msg.startsWith("COMMAND")) {
                handleCommand(msg);
            } else {
                handleResponse(msg);
            }
        }
    }

    public static void renameFile(MikeFile file, String newName) {
        renameFile(new File(file.path), newName);
    }

    public static void renameFile(String path, String newName) {
        renameFile(new File(path), newName);
    }

    public static void renameFile(File file, String newName) {
        file.renameTo(new File(file.getParent() + "/" + newName));
        showTree(file.getParentFile(), MikeFileOperationType.LOCAL);
    }

    public static void handleRename(MikeFile file, String newName, MikeFileOperationType mikeFileOperationType) {
        switch (mikeFileOperationType) {
            case EXTERNAL:
                sendFileCommand(COMMAND_RENAME + FILE_SEP + file.path + FILE_ATTRIBUTE_SEP + newName);
                break;
            case LOCAL:
                renameFile(file, newName);
                break;

        }
    }

    public static void copyFile(String from, String to) {
        copyFile(new File(from), new File(to));
    }

    public static void copyFile(final File from, final File to) {
        log("moving from > " + from.getAbsolutePath() + " to " + to.getAbsolutePath());

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    to.getParentFile().mkdirs();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (from.isDirectory()) {
                    copyDirectory(from, to);
                } else {
                    try {
                        final FileInputStream inStream = new FileInputStream(from);
                        final FileOutputStream outStream = new FileOutputStream(to);
                        final FileChannel inChannel = inStream.getChannel();
                        final FileChannel outChannel = outStream.getChannel();
                        inChannel.transferTo(0, inChannel.size(), outChannel);
                        inStream.close();
                        outStream.close();
                    } catch (IOException e) {
                        log("failedFirst > " + e.getMessage());
                        try {
                            final InputStream in = new FileInputStream(from);
                            final OutputStream out = new FileOutputStream(to);
                            // Transfer bytes from in to out
                            byte[] buf = new byte[1024];
                            int len;
                            while ((len = in.read(buf)) > 0) {
                                out.write(buf, 0, len);
                            }
                            in.close();
                            out.close();
                        } catch (Exception e1) {
                            log("failed to copy file > from " + from.getAbsolutePath() + " to " + to.getAbsolutePath());
                            log("failedSecond > " + e1.getMessage());
                            e1.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }

    public static void moveFile(String from, String to) {
        moveFile(new File(from), new File(to));
    }

    public static void moveFile(File from, File to) {
        try {
            try {
                to.getParentFile().mkdirs();
            } catch (Exception e) {
                e.printStackTrace();
            }
            log("moving from > " + from.getAbsolutePath() + " to " + to.getAbsolutePath());
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
                sendFileCommand(RESPONSE_BROWSE + FILE_SEP + showTree(new File(input), MikeFileOperationType.EXTERNAL));

            }
        } else if (msg.startsWith(COMMAND_OPEN)) {
            //todo command name + filesep+filepath
            open(new File(split[1]), MikeFileOperationType.EXTERNAL);
        } else if (msg.startsWith(COMMAND_DELETE)) {
            //todo command name + filesep+filepath
            delete(new File(split[1]), MikeFileOperationType.EXTERNAL);
            showTree(currentExternalDirectory, MikeFileOperationType.EXTERNAL);
        } else if (msg.startsWith(COMMAND_COPY)) {
            //todo command name + filesep+filepathFrom+fileSep+fileTo
            copyFile(new File(split[1]), new File(split[2]));
        } else if (msg.startsWith(COMMAND_RENAME)) {
            try {
                final String[] rename = split[1].split(FILE_ATTRIBUTE_SEP, 2);
                final File file = new File(rename[0]);
                if (!file.exists()) {
                    MainActivity.toast("the file you just tried to rename doesn't exist");
                    log(file.getAbsolutePath() + " doesn't exist");
                    return;
                }
                renameFile(file, rename[1]);
                showTree(currentExternalDirectory, MikeFileOperationType.EXTERNAL);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (msg.startsWith(COMMAND_MOVE)) {
            //todo command name + filesep+filepathFrom+fileSep+fileTo
            moveFile(new File(split[1]), new File(split[2]));
        } else if (msg.startsWith(COMMAND_DOWNLOAD)) {
            //todo command name + fileSep +filePath + fileSep +filePathTo
            sendFileCommand(COMMAND_UPLOAD + FILE_SEP + split[2] + FILE_SEP + split[3]);
            try {
                fileLength = Long.parseLong(split[3]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            tmpFileBeingUploadedPath = split[1];
        } else if (msg.startsWith(COMMAND_UPLOAD)) {
            //todo command name + fileSep+fileFrom
            tmpFileBeingDownloadedPath = split[1];
            try {
                fileLength = Long.parseLong(split[2]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            try {
                final File file = new File(tmpFileBeingDownloadedPath);
                file.getParentFile().mkdirs();
            } catch (Exception e) {
                e.printStackTrace();
            }
            sendFileCommand(READY_TO_RECEIVE_FILE);
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

    private static void dismissDialog() {
        context.runOnUiThread(dialogDismissal);
    }

    private static void showDialog(final MikeFile mikeFile, final MikeFileOperationType mikeFileOperationType) {
        tmpMikeFile = mikeFile;
        tmpMikeFileOperationType = mikeFileOperationType;
        dismissDialog();

        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog = new Dialog(context, R.style.CustomDialog);
                dialog.setContentView(R.layout.file_manager_dialog);
                dialog.findViewById(R.id.delete).setOnClickListener(dialogClickListener);
                dialog.findViewById(R.id.rename).setOnClickListener(dialogClickListener);
                dialog.findViewById(R.id.move).setOnClickListener(dialogClickListener);
                dialog.findViewById(R.id.copy).setOnClickListener(dialogClickListener);

                dialog.show();
            }
        });
    }

    public static void deleteFileOrDirectory(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteFileOrDirectory(child);

        fileOrDirectory.delete();
    }

    public static void showRenameDialog(final MikeFile file, final MikeFileOperationType mikeFileOperationType) {
        tmpMikeFile = file;
        tmpMikeFileOperationType = mikeFileOperationType;
        if (dialog != null) {
            try {
                dialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        context.runOnUiThread(new Runnable() {
                                  @Override
                                  public void run() {
                                      dialog = new Dialog(context, R.style.CustomDialog);
                                      dialog.setContentView(R.layout.file_manager_dialog_rename);
                                      final HackerTextView old_file_name = (HackerTextView) dialog.findViewById(R.id.old_filename);
                                      old_file_name.setText(file.name);
                                      old_file_name.setSelected(true);
                                      final HackerEditText editText = (HackerEditText) dialog.findViewById(R.id.new_filename);
                                      editText.setText(file.name);
                                      dialog.findViewById(R.id.save_cancel).findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View v) {
                                              try {
                                                  final String newName = editText.getText().toString();

                                                  if (newName != null && newName.length() > 0) {
                                                      switch (mikeFileOperationType) {
                                                          case EXTERNAL:
                                                              sendFileCommand(COMMAND_RENAME + FILE_SEP + file.path + FILE_ATTRIBUTE_SEP + newName);
                                                              break;
                                                          case LOCAL:
                                                              renameFile(tmpMikeFile, newName);
                                                              break;
                                                      }
                                                      dialog.dismiss();
                                                  } else {
                                                      MainActivity.toast("please enter a valid name");
                                                  }
                                              } catch (Exception e) {
                                                  e.printStackTrace();
                                              }

                                          }
                                      });
                                      dialog.findViewById(R.id.save_cancel).findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View v) {
                                              dialog.dismiss();
                                          }
                                      });
                                      dialog.show();
                                  }
                              }
        );
    }

    private static void addParentFile(ArrayList<MikeFile> mikeFiles) {
        if (mikeFiles == null || mikeFiles.size() < 1)
            return;

        final File file = new File(mikeFiles.get(0).path);
        mikeFiles.add(0, new MikeFile(file.getParentFile().getParent(), getFileSize(file)));
    }

    private static void delete(int i) {
        try {
            final File f = new File(currentLocalDirectory);
            final File[] files = f.listFiles();
            tmpTree.clear();
            tmpTree.ensureCapacity(files.length);
            Collections.addAll(tmpTree, files);
            sortFiles(tmpTree);
            delete(tmpTree.get(i - 1), MikeFileOperationType.LOCAL);
            showTree(currentLocalDirectory, MikeFileOperationType.LOCAL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void copy(int i) {
        final File f = new File(currentLocalDirectory);
        final File[] files = f.listFiles();
        tmpTree.clear();
        tmpTree.ensureCapacity(files.length);
        Collections.addAll(tmpTree, files);
        sortFiles(tmpTree);
        tmpMikeFile = new MikeFile(tmpTree.get(i - 1).getAbsolutePath(), 0);
        mikeFileOperation = MikeFileOperation.COPY_LOCAL;
        print(tmpMikeFile.path + " copied, paste in desired directory");
    }

    private static void move(int i) {
        final File f = new File(currentLocalDirectory);
        Collections.addAll(tmpTree, f.listFiles());
        sortFiles(tmpTree);
        tmpMikeFile = new MikeFile(tmpTree.get(i - 1).getAbsolutePath(), 0);
        mikeFileOperation = MikeFileOperation.MOVE_LOCAL;
        print(tmpMikeFile.path + " moved, paste in desired directory");
    }

    private static void paste() {
        switch (mikeFileOperation) {
            case MOVE_LOCAL:
                moveFile(tmpMikeFile.path, currentLocalDirectory);
                break;
            case COPY_LOCAL:
                copyFile(tmpMikeFile.path, currentLocalDirectory);
                break;
        }
    }

    public static boolean handleConsoleCommand(String command) {
        log("handlng > " + command);

        if (command.startsWith("tree")) {
            showTree(currentLocalDirectory, MikeFileOperationType.LOCAL);
            return true;
        } else if (command.startsWith("open")) {
            try {
                open(Integer.parseInt(command.replace("open ", "").trim()));
            } catch (NumberFormatException e) {
                e.printStackTrace();
                MainActivity.toast("Please enter a valid number");
            }
            return true;
        } else if (command.startsWith("del")) {
            delete(Integer.parseInt(command.replace("del ", "").trim()));
            return true;
        } else if (command.startsWith("back")) {
            try {
                showTree(new File(currentLocalDirectory).getParent(), MikeFileOperationType.LOCAL);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        } else if (command.startsWith("mkdir")) {
            try {
                final String fileName = command.replace("mkdir ", "").trim();
                final File file = new File(currentLocalDirectory + "/" + fileName);
                file.mkdirs();
                print(file.getAbsolutePath() + " was created");
                showTree(currentLocalDirectory, MikeFileOperationType.LOCAL);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        } else if (command.startsWith("mkfile")) {
            try {
                final String fileName = command.replace("mkfile ", "").trim();
                final File file = new File(currentLocalDirectory + "/" + fileName);
                file.createNewFile();
                print(file.getAbsolutePath() + " was created");
                showTree(currentLocalDirectory, MikeFileOperationType.LOCAL);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        } else if (command.startsWith("copy")) {
            copy(Integer.parseInt(command.replace("copy ", "").trim()));
            return true;
        } else if (command.startsWith("move")) {
            move(Integer.parseInt(command.replace("move ", "").trim()));
            return true;
        } else if (command.startsWith("paste")) {
            paste();
            return true;
        }
        return false;
    }

    private static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return false;
        }
        // only got here if we didn't return false
        return true;
    }

    public static void printHelp() {
        seperator();
        print(" FileManager");
        seperator(); // Files

        print("tree - displays list of files/folders in current directory");
        print("number - enter a number to open a file or directory that corresponds with it in the tree");
        print("del number - deletes the file that corresponds with num in the tree");
        print("mkdir name - creates a folder in the current directory");
        print("mkfile name.extension - creates a file in the current directory");
        print("copy number - copies the corresponding file, then waits for the paste command");
        print("move number - moves the corresponding file, then waits for the paste command");
        print("paste - pastes the copied/moved file in the current directory");
        print("back - open the parent directory");
    }

    public static void seperator() {
        ConsoleFragment.addConsoleItem("--------------------------------------------------------------------------------------------------");
    }

    public static void setProgress(float progress) {
        if (progressBar != null)
            ProgressBar.setProgress(progress);
    }

    public static void copyDirectory(File sourceLocation, File targetLocation) {
        try {
            if (sourceLocation.isDirectory()) {
                if (!targetLocation.exists() && !targetLocation.mkdirs()) {
                    throw new IOException("Cannot create dir " + targetLocation.getAbsolutePath());
                }

                String[] children = sourceLocation.list();
                for (int i = 0; i < children.length; i++) {
                    copyDirectory(new File(sourceLocation, children[i]),
                            new File(targetLocation, children[i]));
                }
            } else {

                // make sure the directory we plan to store the recording in exists
                File directory = targetLocation.getParentFile();
                if (directory != null && !directory.exists() && !directory.mkdirs()) {
                    throw new IOException("Cannot create dir " + directory.getAbsolutePath());
                }

                InputStream in = new FileInputStream(sourceLocation);
                OutputStream out = new FileOutputStream(targetLocation);

                // Copy the bits from instream to outstream
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        pager.setOffscreenPageLimit(3);
        progressBar = (ProgressBar) v.findViewById(R.id.progress_bar);
        pagerAdapter = new MyPagerAdapter(getChildFragmentManager());

        pager.setAdapter(pagerAdapter);
        tabs.setViewPager(pager);
        pasteButton = v.findViewById(R.id.paste);
        pasteButton.setOnClickListener(pasteClickListener);
    }

    @Override
    public void onP2PDisconnected() {
        ((P2PFragment) fragments[1]).onP2PDisconnected();
    }

    @Override
    public void onP2PConnected() {
        init();
        ((P2PFragment) fragments[1]).onP2PConnected();
    }

    private enum MikeFileOperationType {
        LOCAL, EXTERNAL
    }

    private enum MikeFileOperation {
        MOVE_LOCAL, COPY_LOCAL, MOVE_EXTERNAL, COPY_EXTERNAL
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

            this.path = split[1];
            this.name = split[2];
            this.fileSize = split[3];

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

        public MikeFile(String path, long size) {
            this.fileSize = Tools.getFileSize(size);
            this.path = path;
            this.name = PARENT_NAME;
            fileType = FileType.FOLDER;
        }

        public static String getFileString(File file) {
            final StringBuilder builder = new StringBuilder(getFileTypeString(file));
            //Todo
            builder.append(FILE_ATTRIBUTE_SEP);
            builder.append(file.getAbsolutePath());
            builder.append(FILE_ATTRIBUTE_SEP);
            builder.append(file.getName());
            builder.append(FILE_ATTRIBUTE_SEP);
            builder.append(Tools.getFileSize(getFileSize(file)));

            return builder.toString();
        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder(String.valueOf(fileType));
            //Todo
            builder.append(FILE_ATTRIBUTE_SEP);
            builder.append(path);
            builder.append(FILE_ATTRIBUTE_SEP);
            builder.append(name);
            builder.append(FILE_ATTRIBUTE_SEP);
            builder.append(fileSize);

            return builder.toString();
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
                try {
                    open(new File(files.get(position).path), MikeFileOperationType.LOCAL);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        public static final AdapterView.OnItemLongClickListener onItemLongClickListener = new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showDialog(files.get(position), MikeFileOperationType.LOCAL);
                return true;
            }
        };

        public static void updateAdapter() {
            context.runOnUiThread(update);
        }

        public static void parseLocalFile(final ArrayList<File> files) {
            try {
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
                        addParentFile(LocalFileManager.files);
                        updateAdapter();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public static void parseEmptyFolder(final File file) {
            if (LocalFileManager.files == null) {
                LocalFileManager.files = new ArrayList<MikeFile>(1);
            } else {
                try {
                    LocalFileManager.files.clear();
                    updateAdapter();
                    LocalFileManager.files.ensureCapacity(1);
                    updateAdapter();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        LocalFileManager.files.add(new MikeFile(file.getParent(), getFileSize(file)));
                        updateAdapter();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    updateAdapter();
                }
            });
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            final View view = inflater.inflate(R.layout.file_manager_fragment_item, null);
            final ListView listView = (ListView) view.findViewById(R.id.list);
            view.findViewById(R.id.placeholder).setVisibility(View.GONE);
            fileAdapter = new FileAdapter(listView, files, onItemClickListener, onItemLongClickListener);
            fileAdapter = new FileAdapter(listView, files, onItemClickListener, onItemLongClickListener);
            showTree(Environment.getExternalStorageDirectory(), MikeFileOperationType.LOCAL);
            return listView;
        }

    }

    public static class ExternalFileManager extends P2PFragment {
        //Todo add this to onCreateView
        public static final View.OnClickListener placeHolderListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!P2PManager.isActive()) {
                    DisconnectedButton.show();
                    return;
                }
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
                try {
                    final MikeFile file = files.get(position);
                    if (file.fileType == FileType.FOLDER) {
                        sendFileCommand(COMMAND_BROWSE + FILE_SEP + file.path);
                    } else sendFileCommand(COMMAND_OPEN + FILE_SEP + file.path);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        public static final AdapterView.OnItemLongClickListener onItemLongClickListener = new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showDialog(files.get(position), MikeFileOperationType.EXTERNAL);
                return true;
            }
        };

        public static void updateAdapter() {
            context.runOnUiThread(update);
        }

        public static void parseReceivedFiles(final String files) {
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

        public static void parseEmptyFolder(final File file) {
            if (ExternalFileManager.files == null) {
                ExternalFileManager.files = new ArrayList<MikeFile>(1);
            } else {
                try {
                    ExternalFileManager.files.clear();
                    updateAdapter();
                    ExternalFileManager.files.ensureCapacity(1);
                    updateAdapter();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        ExternalFileManager.files.add(new MikeFile(MikeFile.getFileString(file.getParentFile().getParentFile())));
                        updateAdapter();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    updateAdapter();
                }
            });
        }


        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            //Todo p2pfragment
            final View view = inflater.inflate(R.layout.file_manager_fragment_item, null);
            final ListView listView = (ListView) view.findViewById(R.id.list);
            placeholder = view.findViewById(R.id.placeholder);
            placeholder.setOnClickListener(placeHolderListener);
            fileAdapter = new FileAdapter(listView, files, onItemClickListener, onItemLongClickListener);
            return view;
        }

        @Override
        public void onP2PDisconnected() {
            placeholder.post(new Runnable() {
                @Override
                public void run() {
                    placeholder.setVisibility(View.VISIBLE);
                }
            });
        }

        @Override
        public void onP2PConnected() {
//            placeholder.post(new Runnable() {
//                @Override
//                public void run() {
//                    placeholder.setVisibility(View.GONE);
//                }
//            });
        }
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }
    }
}
