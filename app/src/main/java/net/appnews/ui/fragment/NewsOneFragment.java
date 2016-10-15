package net.appnews.ui.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;

import net.appnews.R;
import net.appnews.ui.base.BaseFragment;
import net.appnews.ui.fragment.adapter.EndlessRecyclerViewAdapter;

import butterknife.BindView;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewsOneFragment extends BaseFragment implements NewsOneFragmentPresenter.View{

    @BindView(R.id.recyclerNotification)
    RecyclerView recyclerNotification;
    @BindView(R.id.swipeContainer)
    SwipeRefreshLayout swipeContainer;
    @BindView(R.id.offNetwork)
    LinearLayout offNetwork;

    EndlessRecyclerViewAdapter mEndlessRecyclerViewAdapter;

    public NewsOneFragment() {
        // Required empty public constructor
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_news_one;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void showLoadingView() {

    }

    @Override
    public void showProgressDialog() {

    }

    @Override
    public void hideProgressDialog() {

    }

    @Override
    public void showCompleteView() {

    }

    @Override
    public void showMessageError() {

    }

    @Override
    public void showEmptyNews(boolean isShowing) {

    }

    @Override
    public void goToNewsDetail() {

    }
}
