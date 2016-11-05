package net.appnews;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;

import net.appnews.data.DataMapper;
import net.appnews.data.database.NewsDto;
import net.appnews.ui.base.BaseApplication;

/**
 * Created by DongNguyen on 10/14/16.
 */

public class AppNews extends BaseApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        Dependencies.getsInstance().init(this);
        DataMapper.context = this;
        initActiveAndroid();
    }

    @SuppressWarnings("unchecked")
    private void initActiveAndroid(){
        Configuration.Builder configurationBuilder = new Configuration.Builder(this);
        configurationBuilder.addModelClasses(NewsDto.class);
        ActiveAndroid.initialize(configurationBuilder.create());
        ActiveAndroid.setLoggingEnabled(false);
    }
}
