package net.appnews.support.image;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.LruCache;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by DongNguyen on 10/14/16.
 */

public class ImageWorker {
    private final int CORE_POOL_SIZE = 8;

    private final int MAXIMUM_POOL_SIZE = 8;

    private final int KEEP_ALIVE_TIME = 1;

    private final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;

    private final long MEMORY_MAX_SIZE = Runtime.getRuntime().maxMemory() / 8;

    private static final int FADE_IN_TIME = 200;

    private LruCache<String, BitmapDrawable> memoryCache;
    private FileCache fileCache;
    private Map<ImageView, String> imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
    private ThreadPoolExecutor executorService;
    private final BlockingQueue<Runnable> workQueue;

    static final Handler handler = new Handler();

    private Context context;

    protected Resources resources;

    public ImageWorker(Context context) {
        this.context = context;
        this.resources = this.context.getResources();
        fileCache = new FileCache(this.context);
        workQueue = new LinkedBlockingQueue<Runnable>() {

        };
        executorService = new ThreadPoolExecutor(
                CORE_POOL_SIZE,
                MAXIMUM_POOL_SIZE,
                KEEP_ALIVE_TIME,
                KEEP_ALIVE_TIME_UNIT,
                workQueue);
        memoryCache = new LruCache<String, BitmapDrawable>((int) MEMORY_MAX_SIZE) {
            @Override
            protected int sizeOf(String url, BitmapDrawable drawable) {
                final int bitmapSize = getBitmapSize(drawable) / 1024;
                return bitmapSize == 0 ? 1 : bitmapSize;
            }
        };
    }

    public void loadThumbFromLocal(int id, int type, int place_Id, ImageView imageView) {
        if (id == -1 || id == 0) {
            imageView.setImageResource(place_Id);
            return;
        }
        imageViews.put(imageView, id + "-" + type);
        BitmapDrawable bitmapDrawable = memoryCache.get(id + "-" + type);
        if (bitmapDrawable != null)
            imageView.setImageDrawable(bitmapDrawable);
        else {
            queueThumbPhoto(id, type, place_Id, imageView);
            imageView.setImageResource(place_Id);
        }
    }

    public void loadImageFromServer(String url, int place_Id, ImageView imageView, float offset, int showWidth, int showHeight, PhotoLoad.ScaleType scaleType, BitmapCallback callback) {
        if (url == null) {
            imageView.setImageResource(place_Id);
            return;
        }
        imageViews.put(imageView, url);
        BitmapDrawable bitmapDrawable = memoryCache.get(url + "-" + showWidth + "-" + showHeight);
        if (bitmapDrawable != null)
            imageView.setImageDrawable(bitmapDrawable);
        else {
            queueDownloadPhoto(url, place_Id, imageView, offset, showWidth, showHeight, scaleType, callback);
            imageView.setImageResource(place_Id);
        }
    }

    private void queueDownloadPhoto(String url, int place_id, ImageView imageView, float offset, int showWidth, int showHeight, PhotoLoad.ScaleType scaleType, BitmapCallback callback) {
        PhotoLoad p = new PhotoLoad.Builder().url(url).placeHolderId(place_id).into(imageView).offset(offset).width(showWidth).height(showHeight).scaleType(scaleType).isLocal(false).callBack(callback).build();
        executorService.submit(new PhotosLoader(p));
    }

    private void queueThumbPhoto(int id, int type, int place_id, ImageView imageView) {
        PhotoLoad p = new PhotoLoad.Builder().id(id).type(type).placeHolderId(place_id).scaleType(PhotoLoad.ScaleType.NOT_SCALE).into(imageView).isLocal(true).build();
        executorService.submit(new PhotosLoader(p));
    }

    private Bitmap getBitmap(PhotoLoad photoLoad) {
        File f = null;
        Bitmap b = null;
        if (photoLoad.isLocal) {
            f = fileCache.getFile(photoLoad.id + "-" + photoLoad.type);
        } else {
            f = fileCache.getFile(photoLoad.url);
            b = decodeFile(f, photoLoad);
        }

        if (b != null)
            return b;
        try {
            Bitmap bitmap = photoLoad.isLocal ? getThumbFromLocal(f, photoLoad, this.context) : getBitmapFromServer(f, photoLoad);
            return bitmap;
        } catch (Throwable ex) {
            ex.printStackTrace();
            if (ex instanceof OutOfMemoryError)
                memoryCache.evictAll();
            return null;
        }
    }

    protected Bitmap getThumbFromLocal(File f, PhotoLoad photoLoad, Context context) {
        Bitmap bitmap = null;
        FileInputStream bStream = null;
        try {
            bStream = new FileInputStream(f);
            bitmap = BitmapFactory.decodeStream(bStream);
            bStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (bitmap != null) {
            return bitmap;
        }
        try {
            if (photoLoad.type == 0) {
                bitmap = MediaStore.Video.Thumbnails.getThumbnail(
                        context.getContentResolver(), photoLoad.id,
                        MediaStore.Video.Thumbnails.MICRO_KIND, null);
            } else {
                bitmap = MediaStore.Images.Thumbnails.getThumbnail(
                        context.getContentResolver(), photoLoad.id,
                        MediaStore.Images.Thumbnails.MICRO_KIND, null);
            }
            OutputStream oStream = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, oStream);
            oStream.flush();
            oStream.close();
        } catch (Throwable ex) {
            ex.printStackTrace();
            if (ex instanceof OutOfMemoryError)
                memoryCache.evictAll();
        }
        return bitmap;
    }

    private Bitmap getBitmapFromServer(File f, PhotoLoad photoLoad) {
        Bitmap bitmap = null;
        try {
            URL imageUrl = new URL(photoLoad.url);
            HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setInstanceFollowRedirects(true);
            long size = conn.getContentLength();
            InputStream is = conn.getInputStream();
            fileCache.saveFile(is, f, size);
            conn.disconnect();
            bitmap = decodeFile(f, photoLoad);
            return bitmap;
        } catch (Throwable ex) {
            ex.printStackTrace();
            if (ex instanceof OutOfMemoryError)
                memoryCache.evictAll();
            return null;
        }
    }

    //decodes image and scales it to reduce memory consumption
    protected Bitmap decodeFile(File f, PhotoLoad photoLoad) {
        try {
            FileInputStream inputStream = new FileInputStream(f);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            Bitmap b = BitmapFactory.decodeStream(inputStream, null, options);
            switch (photoLoad.scaleType) {
                case SCALE:
                    b = cropBitmap(b, photoLoad.showWidth, photoLoad.showHeight);
                    break;
                case SHOW_WITH_OFFSET:
                    b = calculaBitmapWithOffset(b, photoLoad.offset, photoLoad.showWidth, photoLoad.showHeight);
                    break;
            }
            return b;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Bitmap calculaBitmapWithOffset(Bitmap bitmap, float offset, int showWidth, int showHeight) {
        int halfShowHeight = showHeight / 2;

        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();

        float scale = bitmapWidth / (float) showWidth;

        int destHeight = (int) (bitmapHeight / scale);

        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, showWidth, destHeight, false);

        Bitmap showBitmap = null;

        if (showHeight >= destHeight) {
            showBitmap = resizedBitmap;
            return showBitmap;
        }

        float partTwoHundred = destHeight / (float) 200;

        int centerPoint = (int) ((1 - offset) * 100);

        int centerPixel = (int) (partTwoHundred * centerPoint);

        if (centerPixel - halfShowHeight <= 0) {
            showBitmap = Bitmap.createBitmap(resizedBitmap, 0, 0, showWidth, showHeight);
            return showBitmap;
        } else if (centerPixel + halfShowHeight >= destHeight) {
            showBitmap = Bitmap.createBitmap(resizedBitmap, 0, destHeight - showHeight, showWidth, showHeight);
            return showBitmap;
        }

        showBitmap = Bitmap.createBitmap(resizedBitmap, 0, centerPixel - halfShowHeight, showWidth, showHeight);
        return showBitmap;
    }

    private Bitmap cropBitmap(Bitmap bitmap, int showWidth, int showHeight) {
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();

        Bitmap resizedBitmap = null;

        float scaleWidth = showWidth / (float) bitmapWidth;
        float scaleHeight = showHeight / (float) bitmapHeight;

        Bitmap showBitmap = null;

        if (scaleWidth < scaleHeight) {
            int destWidth = (int) (bitmapWidth * scaleHeight);
            int destHeight = (int) (bitmapHeight * scaleHeight);
            resizedBitmap = Bitmap.createScaledBitmap(bitmap, destWidth, destHeight, false);
            showBitmap =Bitmap.createBitmap(resizedBitmap, resizedBitmap.getWidth()/2 - showWidth/2, 0, showWidth, destHeight);
        } else if (scaleWidth >= scaleHeight) {
            int destWidth = (int) (bitmapWidth * scaleWidth);
            int destHeight = (int) (bitmapHeight * scaleWidth);
            resizedBitmap = Bitmap.createScaledBitmap(bitmap, destWidth, destHeight, false);
            if (scaleWidth > scaleHeight)
                showBitmap =Bitmap.createBitmap(resizedBitmap, 0, resizedBitmap.getHeight()/2 - showHeight/2, destWidth, showHeight);
            if (scaleWidth == scaleHeight)
                showBitmap =Bitmap.createBitmap(resizedBitmap, 0, 0, destWidth, destHeight);
        }
        showBitmap = Bitmap.createScaledBitmap(showBitmap, showWidth*2/3, showHeight*2/3, false);
        return showBitmap;
    }

    class PhotosLoader implements Runnable {
        PhotoLoad photoLoad;

        PhotosLoader(PhotoLoad photoLoad) {
            this.photoLoad = photoLoad;
        }

        @Override
        public void run() {
            try {
                if (Thread.currentThread().isInterrupted())
                    return;
                if (photoLoad.isLocal && localImageViewReused(photoLoad))
                    return;
                if (!photoLoad.isLocal && serverImageViewReused(photoLoad))
                    return;
                Bitmap bmp = getBitmap(photoLoad);
                BitmapDrawable drawable = null;
                if (bmp != null) {
                    if (FileUtil.hasHoneycomb()) {
                        drawable = new BitmapDrawable(resources, bmp);
                    } else {
                        drawable = new RecyclingBitmapDrawable(resources, bmp);
                    }
                }
                if (photoLoad.isLocal) {
                    memoryCache.put(photoLoad.id + "-" + photoLoad.type, drawable);
                } else {
                    memoryCache.put(photoLoad.url + "-" + photoLoad.showWidth + "-" + photoLoad.showHeight, drawable);
                }
                if (Thread.currentThread().isInterrupted())
                    return;
                if (photoLoad.isLocal && localImageViewReused(photoLoad))
                    return;
                if (!photoLoad.isLocal && serverImageViewReused(photoLoad))
                    return;
                BitmapDisplayer bd = new BitmapDisplayer(drawable, photoLoad);
                handler.post(bd);
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
    }

    boolean localImageViewReused(PhotoLoad photoLoad) {
        String tag = imageViews.get(photoLoad.imageView);
        if (tag == null || !tag.equals(photoLoad.id + "-" + photoLoad.type)) {
            return true;
        }
        return false;
    }

    boolean serverImageViewReused(PhotoLoad photoLoad) {
        String tag = imageViews.get(photoLoad.imageView);
        if (tag == null || !tag.equals(photoLoad.url))
            return true;
        return false;
    }

    private void setImageDrawable(PhotoLoad photoLoad, Drawable drawable) {
        if (photoLoad.isFadeIn) {
            final TransitionDrawable td = new TransitionDrawable(new Drawable[] {new ColorDrawable(resources.getColor(android.R.color.transparent)), drawable});
            ImageView imageView = photoLoad.imageView;
            if (imageView != null)
                imageView.setImageDrawable(td);
            td.startTransition(FADE_IN_TIME);
        } else {
            ImageView imageView = photoLoad.imageView;
            if (imageView != null)
                imageView.setImageDrawable(drawable);
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static int getBitmapSize(BitmapDrawable value) {
        Bitmap bitmap = value.getBitmap();
        if (FileUtil.hasKitKat()) {
            return bitmap.getAllocationByteCount();
        }
        if (FileUtil.hasHoneycombMR1()) {
            return bitmap.getByteCount();
        }
        return bitmap.getRowBytes() * bitmap.getHeight();
    }

    class BitmapDisplayer implements Runnable {
        BitmapDrawable drawable;
        PhotoLoad photoLoad;

        public BitmapDisplayer(BitmapDrawable drawable, PhotoLoad p) {
            this.drawable = drawable;
            photoLoad = p;
        }

        public void run() {
            if (photoLoad.callback == null) {
                if (photoLoad.isLocal && localImageViewReused(photoLoad))
                    return;
                if (!photoLoad.isLocal && serverImageViewReused(photoLoad))
                    return;
                if (drawable != null)
                    setImageDrawable(photoLoad, drawable);
                else {
                    if (photoLoad.placeHolderId != -1) {
                        ImageView imageView = photoLoad.imageView;
                        if (imageView != null)
                            imageView.setImageResource(photoLoad.placeHolderId);
                    } else {
                        ImageView imageView = photoLoad.imageView;
                        if (imageView != null)
                            imageView.setImageResource(android.R.drawable.stat_notify_error);
                    }
                }
            } else {
                if (drawable != null)
                    photoLoad.callback.callback(drawable);
            }
        }
    }
}
