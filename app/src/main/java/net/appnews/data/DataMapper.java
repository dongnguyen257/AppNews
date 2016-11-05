package net.appnews.data;

import android.content.Context;
import android.support.annotation.NonNull;

import com.activeandroid.ActiveAndroid;

import net.appnews.Constants;
import net.appnews.data.database.NewsDto;
import net.appnews.data.entities.NewsItem;
import net.appnews.support.image.FileUtil;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by DongNguyen on 11/3/16.
 */

public class DataMapper {
    public static Context context;

    public static void parseNews(@NonNull NewsItem listItem){
        ActiveAndroid.beginTransaction();
        try {
            HashMap<String, String> newsID = new HashMap<String, String>();
            for (NewsItem.Results newsItem : listItem.results){
                if (newsItem != null){
                    NewsDto newsDto = NewsDto.getById(newsItem.id);
                    newsID.put(String.valueOf(newsItem.id), String.valueOf(newsItem.id));
                    if (newsDto == null){
                        newsDto = new NewsDto();
                    }
                    newsDto.news_id = newsItem.id;
                    newsDto.url = newsItem.url;
                    newsDto.category = newsItem.category;
                    newsDto.title = newsItem.title;
                    newsDto.slug = newsItem.slug;
                    newsDto.content = newsItem.content;
                    newsDto.images = newsItem.images;
                    for (int i=0; i<newsDto.images.size(); i++){
                        String url = Constants.API_BASE_URL + newsDto.images.get(i);
                        String pathToSaveFile = FileUtil.getPathToSaveFile(context, url, "Photo");
                        File file = new File(pathToSaveFile);
                        if (!file.exists()){
                            downloadFile(url, file);
                        }
                    }
                    newsDto.video = newsItem.video;
                    newsDto.created_at = newsItem.created_at;
                    newsDto.save();
                }
            }

            try {
                List<NewsDto> newsDtos = NewsDto.all();
                for (NewsDto newsDto : newsDtos) {
                    if (!newsID.containsKey(String.valueOf(newsDto.news_id))) {
                        newsDto.delete();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            ActiveAndroid.setTransactionSuccessful();
        }
        finally {
            ActiveAndroid.endTransaction();
        }
    }

    private static void downloadFile(String url, File file){
        Observable<Boolean> observable = Observable.create(subscriber -> {
            if (FileUtil.downloadAndSaveFile(url, file)) {
                subscriber.onNext(true);
            } else {
                subscriber.onNext(false);
            }
        });
        observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> {
                    if (aBoolean) {
//                        saveStatusDownload(3, attachment);
                    }else {
//                        saveStatusDownload(0,attachment);
                    }
                });
    }
}
