package net.appnews.ui.fragment;

import net.appnews.Dependencies;
import net.appnews.data.ServerAPI;
import net.appnews.ui.base.MvpView;
import net.appnews.ui.base.Presenter;

/**
 * Created by DongNguyen on 10/15/16.
 */

public class NewsOneFragmentPresenter extends Presenter<NewsOneFragmentPresenter.View>{
    private ServerAPI serverAPI;
    public NewsOneFragmentPresenter() {
        serverAPI = Dependencies.getServerAPI();
    }
    public interface View extends MvpView{
        void showLoadingView();

        void showProgressDialog();

        void hideProgressDialog();

        void showCompleteView();

        void showMessageError();

        void showEmptyNews(boolean isShowing);

        void goToNewsDetail();
    }

    public void getListNews(){
        final View view = view();

    }
}
