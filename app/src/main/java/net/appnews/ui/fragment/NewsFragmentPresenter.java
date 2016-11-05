package net.appnews.ui.fragment;

import net.appnews.Dependencies;
import net.appnews.data.DataMapper;
import net.appnews.data.ServerAPI;
import net.appnews.data.database.NewsDto;
import net.appnews.data.entities.NewsItem;
import net.appnews.ui.base.MvpView;
import net.appnews.ui.base.Presenter;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by DongNguyen on 10/20/16.
 */

public class NewsFragmentPresenter extends Presenter<NewsFragmentPresenter.View> {
    private ServerAPI serverAPI;
    public NewsFragmentPresenter() {
        serverAPI = Dependencies.getServerAPI();
    }
    public interface View extends MvpView {
        void hideRefreshView();

        void showProgressDialog();

        void hideProgressDialog();

        void showCompleteData(NewsItem newsItem, boolean isNext);

        void showCompleteDataOff(NewsItem newsItem, boolean isNext);

        void showMessageError();
    }

    public void getListNews(int typeNews){
        if (typeNews == 0){
            getNews();
        }else {
            getCategoriesNews(typeNews);
        }
    }

    public void getListNewsOff(int typeNews){
        if (typeNews == 0){
            getNewsOff();
        }else {
            getCategoriesNews(typeNews);
        }
    }

    public void getNews(){
        final View view = view();
        if (view != null){
            view.showProgressDialog();
            serverAPI.getNews()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(newsItemResponse -> {
                        if (newsItemResponse.next != null){
                            view.showCompleteData(newsItemResponse, true);
                            DataMapper.parseNews(newsItemResponse);
                        }else {
                            view.showCompleteData(newsItemResponse, false);
                            DataMapper.parseNews(newsItemResponse);
                        }
                        view.hideProgressDialog();
                    }, throwable -> {
                        view.hideProgressDialog();
                        view.showMessageError();
                    });
        }
    }

    public void getCategoriesNews(int typeNews){
        final View view = view();
        if (view != null){
            view.showProgressDialog();
            serverAPI.getCategoryNews(typeNews)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(newsItemResponse -> {
                        if (newsItemResponse.next != null){
                            view.showCompleteData(newsItemResponse, true);
                        }else {
                            view.showCompleteData(newsItemResponse, false);
                        }
                        view.hideProgressDialog();
                    }, throwable -> {
                        view.hideProgressDialog();
                        view.showMessageError();
                    });
        }
    }

    public void getMoreNews(int pages){
        final View view = view();
        if (view != null){
            serverAPI.getMoreNews(pages)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(newsItemResponse -> {
                        if (newsItemResponse.next != null){
                            view.showCompleteData(newsItemResponse, true);
                        }else {
                            view.showCompleteData(newsItemResponse, false);
                        }
                    }, throwable -> {
                        view.showMessageError();
                    });
        }
    }

    public void getNewsOff(){
        final View view = view();
        if (view != null){
            List<NewsDto> listNewsDtos = new ArrayList<>();
            for (NewsDto newsDto : NewsDto.all()) {
                if (newsDto != null) {
                    listNewsDtos.add(newsDto);
                }
            }

        }
    }

    public void getCategoriesNewsOff(int typeNews){
        final View view = view();
        if (view != null){
            view.showProgressDialog();
            serverAPI.getCategoryNews(typeNews)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(newsItemResponse -> {
                        if (newsItemResponse.next != null){
                            view.showCompleteData(newsItemResponse, true);
                        }else {
                            view.showCompleteData(newsItemResponse, false);
                        }
                        view.hideProgressDialog();
                    }, throwable -> {
                        view.hideProgressDialog();
                        view.showMessageError();
                    });
        }
    }

    public void getMoreNewsOff(int pages){
        final View view = view();
        if (view != null){
            serverAPI.getMoreNews(pages)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(newsItemResponse -> {
                        if (newsItemResponse.next != null){
                            view.showCompleteData(newsItemResponse, true);
                        }else {
                            view.showCompleteData(newsItemResponse, false);
                        }
                    }, throwable -> {
                        view.showMessageError();
                    });
        }
    }
}
