package net.appnews.ui.fragment.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.appnews.R;
import net.appnews.data.entities.NewsItem;
import net.appnews.ui.base.BaseAdapter;
import net.appnews.ui.base.BaseHolder;
import net.appnews.utils.Utils;

import java.util.List;

import butterknife.BindView;

/**
 * Created by DongNguyen on 10/20/16.
 */

public class NewsFragmentAdapter extends BaseAdapter<NewsItem.Results, NewsFragmentAdapter.NewsHolder> {
    private NewsDetailListener listener;
    private List<NewsItem.Results> listNews;
    public static int HEADER_SIZE;

    public NewsFragmentAdapter(@NonNull LayoutInflater inflater, List<NewsItem.Results> listNews, NewsDetailListener activityListener) {
        super(inflater);
        listener = activityListener;
        this.listNews = listNews;
        if (listNews != null)
        HEADER_SIZE = listNews.size();
    }

    public interface NewsDetailListener {
        void showNewsDetail(NewsItem.Results news);
    }

    @Override
    public NewsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NewsHolder(inflater.inflate(R.layout.news_card, parent, false));
    }

    class NewsHolder extends BaseHolder<NewsItem.Results> {
        Context mContext;
        @BindView(R.id.tvTitle)
        TextView tvTitle;
        @BindView(R.id.tvContent)
        TextView tvContent;
        @BindView(R.id.tvDatePost)
        TextView tvDatePost;
        @BindView(R.id.card_view)
        CardView card_view;

        public NewsHolder(View itemView) {
            super(itemView);
            this.mContext = itemView.getContext();
        }

        @Override
        public void bind(NewsItem.Results data, int position) {
            if (data != null){
                tvTitle.setText(Html.fromHtml(data.title));
                tvContent.setText(Html.fromHtml(data.content));
                tvDatePost.setText(Utils.getTimeZone(data.created_at));
                card_view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.showNewsDetail(data);
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return dataSource.size();
    }
}
