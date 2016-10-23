package net.appnews.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import net.appnews.Constants;
import net.appnews.Dependencies;
import net.appnews.data.ServerAPI;
import net.appnews.data.entities.NewsItem;
import net.appnews.ui.base.MvpView;
import net.appnews.ui.base.Presenter;
import net.appnews.ui.media.OnCompleteListener;

import org.parceler.Parcels;

/**
 * Created by DongNguyen on 10/19/16.
 */

public class NewsDetailPresent extends Presenter<NewsDetailPresent.View>{

    public interface View extends MvpView, OnCompleteListener{
        void showDataIntentUi(NewsItem.Results newsDetail);
    }

    private ServerAPI serverAPI;

    public NewsDetailPresent() {
        serverAPI = Dependencies.getServerAPI();
    }

    public void getNewsDetail(Intent intent){
        NewsItem.Results newsDetail;
        final View view = view();
        if (view != null) {
            Bundle extra = intent.getExtras();
            if (extra != null) {
                if (extra.containsKey(Constants.NEWS_DETAIL)) {
                    newsDetail = Parcels.unwrap(extra.getParcelable(Constants.NEWS_DETAIL));
                    if (newsDetail != null && view != null) {
                        view.showDataIntentUi(newsDetail);
                    }
                }
            }
        }
    }
}
