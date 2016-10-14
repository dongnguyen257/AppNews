package net.appnews.support.image;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by DongNguyen on 10/14/16.
 */

public class FileCache {
    private static final String IMAGE_CACHE_DIR_NAME = "cached";

    private static final long MAX_SIZE = 52428800L;

    private List<String> accessingList;

    private File cacheDir;

    private Object object;

    private boolean notified;

    long size;

    public FileCache(Context context) {
        cacheDir = FileUtil.getDiskCacheDir(context, IMAGE_CACHE_DIR_NAME);
        if (!cacheDir.exists())
            cacheDir.mkdirs();
        accessingList = new ArrayList<>();
        object = new Object();
        size = getDirSize(cacheDir);
    }

    public File getCacheDir() {
        return cacheDir;
    }

    public File getFile(String url) {
        String filename = String.valueOf(url.hashCode());
        File f = new File(cacheDir, filename);
        return f;
    }

    public void saveFile(InputStream is, File file, long fileSize) {

        long newSize = fileSize + size;
        if (newSize > MAX_SIZE) {
            cleanDir(cacheDir, newSize - MAX_SIZE);
        }

        final int buffer_size = 1024;
        FileOutputStream os = null;
        if (accessingList.contains(file.getName())) {
            synchronized (object) {
                while (accessingList.contains(file.getName())) {
                    try {
                        object.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        accessingList.add(file.getName());
        if (!file.exists() || file.length() < fileSize) {
            if (file.exists())
                file.delete();
            try {
                os = new FileOutputStream(file);
                byte[] bytes = new byte[buffer_size];
                for (; ; ) {
                    int count = is.read(bytes, 0, buffer_size);
                    if (count == -1)
                        break;
                    os.write(bytes, 0, count);

                }
                size += fileSize;
            } catch (Exception ex) {
            } finally {
                if (os != null) {
                    try {
                        os.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        accessingList.remove(file.getName());
        synchronized (object) {
            notified = false;
            object.notifyAll();
        }
    }

    private synchronized void cleanDir(File dir, long bytes) {
        long bytesDeleted = 0;
        File[] files = dir.listFiles();
        for (File file : files) {
            if (!isUsing(file.getName())) {
                bytesDeleted += file.length();
                file.delete();
            }

            if (bytesDeleted >= bytes) {
                break;
            }
        }
        size -= bytesDeleted;
    }

    private boolean isUsing (String fileName) {
        boolean isUsing = false;
        for (String name : accessingList) {
            if (name.equals(fileName)) {
                isUsing = true;
                break;
            }
        }
        return isUsing;
    }

    public void clear() {
        File[] files = cacheDir.listFiles();
        if (files == null)
            return;
        for (File f : files)
            f.delete();
    }

    private long getDirSize(File dir) {

        long size = 0;
        File[] files = dir.listFiles();

        for (File file : files) {
            if (file.isFile()) {
                size += file.length();
            }
        }

        return size;
    }
}
