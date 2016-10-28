package net.appnews.ui.fragment;

import net.appnews.Dependencies;
import net.appnews.data.ServerAPI;
import net.appnews.data.entities.NewsItem;
import net.appnews.ui.base.MvpView;
import net.appnews.ui.base.Presenter;

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
        void showLoadingView();

        void hideRefreshView();

        void showProgressDialog();

        void hideProgressDialog();

        void showCompleteData(NewsItem newsItem, boolean isNext);

        void showMessageError();

        void showEmptyNews(boolean isShowing);

        void showNews();

        void goToNewsDetail();
    }

    public void getListNews(int typeNews){
        if (typeNews == 0){
            getNews();
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

    public void getMoreNewsCategory(int pages, int typeNews){
        final View view = view();
        if (view != null){
            serverAPI.getCategoryNewsCategory(typeNews, pages)
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
