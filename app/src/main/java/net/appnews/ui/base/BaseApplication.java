package net.appnews.ui.base;

import android.app.Application;

import net.appnews.support.image.ImageLoaderManager;

/**
 * Created by DongNguyen on 10/14/16.
 */

public class BaseApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        ImageLoaderManager.init(this);
    }
}
