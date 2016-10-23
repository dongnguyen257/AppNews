package net.appnews.utils;

import android.content.Context;
import android.content.Intent;

import net.appnews.Constants;
import net.appnews.data.entities.NewsItem;
import net.appnews.ui.activity.MainActivity;
import net.appnews.ui.activity.NewsDetailActivity;

import org.parceler.Parcels;

/**
 * Created by DongNguyen on 10/14/16.
 */

public class Navigator {
    public static void openMainActivity(Context context){
        Intent it = new Intent(context, MainActivity.class);
        it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(it);
    }

    public static void openNewsDetailActivity(Context context, NewsItem.Results newsDetail){
        Intent intent = new Intent(context, NewsDetailActivity.class);
        intent.putExtra(Constants.NEWS_DETAIL, Parcels.wrap(newsDetail));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
