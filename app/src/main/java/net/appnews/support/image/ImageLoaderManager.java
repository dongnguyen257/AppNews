package net.appnews.support.image;

import android.content.Context;

/**
 * Created by DongNguyen on 10/14/16.
 */

public class ImageLoaderManager {
    private static ImageLoaderManager sInstance;
    private static ImageLoader imageLoader;
    static {
        sInstance = new ImageLoaderManager();
    }

    private ImageLoaderManager() {}

    public static ImageLoaderManager getInstance() {
        return sInstance;
    }

    public static void init (Context context) {
        if (imageLoader == null) {
            imageLoader = new ImageLoader(context);
        }
    }

    public synchronized ImageLoader getImageLoader() {
        return imageLoader;
    }
}
