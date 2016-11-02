package net.appnews;

import net.appnews.ui.base.BaseApplication;

/**
 * Created by DongNguyen on 10/14/16.
 */

public class AppNews extends BaseApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        Dependencies.getsInstance().init(this);
    }
}
