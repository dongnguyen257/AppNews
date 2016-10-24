package net.appnews.ui.fragment;

/**
 * Created by DongNguyen on 10/20/16.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import net.appnews.R;
import net.appnews.data.entities.NewsItem;
import net.appnews.ui.base.BaseFragment;
import net.appnews.ui.fragment.adapter.EndlessRecyclerViewAdapter;
import net.appnews.ui.fragment.adapter.NewsFragmentAdapter;
import net.appnews.utils.Navigator;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static android.support.v7.widget.LinearLayoutManager.VERTICAL;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewsFragment extends BaseFragment implements NewsFragmentPresenter.View, NewsFragmentAdapter.NewsDetailListener, SwipeRefreshLayout.OnRefreshListener, EndlessRecyclerViewAdapter.RequestToLoadMoreListener {

    @BindView(R.id.recyclerNews)
    RecyclerView recyclerNews;
    @BindView(R.id.swipeContainer)
    SwipeRefreshLayout swipeContainer;
    @BindView(R.id.offNetwork)
    LinearLayout offNetwork;

    private ProgressDialog progressDialog;;
    private int typeNews;
    private NewsFragmentPresenter mNewsOneFragmentPresenter;
    private NewsFragmentAdapter mNewsOneFragmentAdapter;
    private EndlessRecyclerViewAdapter mEndlessRecyclerViewAdapter;
    private List<NewsItem.Results> listNews;
    private boolean isRefresh = false;
    private int pages = 1;

    public static NewsFragment newInstance(Context context, int typeNews) {
        NewsFragment mNewsOneFragment = new NewsFragment();
        mNewsOneFragment.typeNews = typeNews;
        return mNewsOneFragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mNewsOneFragmentPresenter = new NewsFragmentPresenter();
        mNewsOneFragmentPresenter.bindView(this);
        mNewsOneFragmentPresenter.getListNews(typeNews);

        setupRecyclerView();
    }

    public void setupRecyclerView(){
        mNewsOneFragmentAdapter = new NewsFragmentAdapter(getActivity().getLayoutInflater(), listNews, this);
        mEndlessRecyclerViewAdapter = new EndlessRecyclerViewAdapter(getContext(), mNewsOneFragmentAdapter,
                this, R.layout.image_loading, false);
        recyclerNews.setAdapter(mEndlessRecyclerViewAdapter);
        recyclerNews.setLayoutManager(new LinearLayoutManager(getContext(), VERTICAL, false));
        swipeContainer.setOnRefreshListener(this);
        swipeContainer.setColorSchemeResources(R.color.colorRed600);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_news;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void showLoadingView() {

    }

    @Override
    public void hideRefreshView() {
        getActivity().runOnUiThread(() -> {
            if (swipeContainer != null) {
                swipeContainer.post(() -> {
                    swipeContainer.setRefreshing(false);
                });
            }
        });
    }

    @Override
    public void showProgressDialog() {
        if (isRefresh){
            return;
        }
        hideProgressDialog();
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading... Please Wait");
        progressDialog.show();
    }

    @Override
    public void hideProgressDialog() {
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
        }
    }

    @Override
    public void showCompleteData(NewsItem newsItem, boolean isNext) {
        offNetwork.setVisibility(View.GONE);
        if (isRefresh){
            pages = 1;
            isRefresh = false;
            hideRefreshView();
            listNews = new ArrayList<>();
            listNews = newsItem.results;
            mNewsOneFragmentAdapter.setDataSource(listNews);
            if (isNext){
                pages++;
                mEndlessRecyclerViewAdapter.onDataReady(true);
            }
        }else {
            mNewsOneFragmentAdapter.appendItems(newsItem.results);
            if (isNext){
                pages++;
                mEndlessRecyclerViewAdapter.onDataReady(true);
            }else {
                mEndlessRecyclerViewAdapter.onDataReady(false);
            }
        }
    }

    @Override
    public void showMessageError() {
        Toast.makeText(getActivity(), "An error occurred.", Toast.LENGTH_SHORT).show();
        offNetwork.setVisibility(View.VISIBLE);
    }

    @Override
    public void showEmptyNews(boolean isShowing) {

    }

    @Override
    public void showNews() {

    }

    @Override
    public void goToNewsDetail() {

    }

    @Override
    public void showNewsDetail(NewsItem.Results newsDetail) {
        Navigator.openNewsDetailActivity(getActivity().getApplicationContext(), newsDetail);
    }

    @Override
    public void onRefresh() {
        isRefresh = true;
        listNews = new ArrayList<>();
        mNewsOneFragmentPresenter.getListNews(typeNews);
    }

    @Override
    public void onLoadMoreRequested() {
        if (typeNews == 0){
            mNewsOneFragmentPresenter.getMoreNews(pages);
        }else {
            mNewsOneFragmentPresenter.getCategoriesNews(typeNews);
        }
    }
}

