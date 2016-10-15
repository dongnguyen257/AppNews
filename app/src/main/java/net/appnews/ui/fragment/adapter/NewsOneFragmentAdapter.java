package net.appnews.ui.fragment.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.appnews.R;
import net.appnews.data.entities.NewsItem;
import net.appnews.ui.base.BaseAdapter;
import net.appnews.ui.base.BaseHolder;

import butterknife.BindView;

/**
 * Created by DongNguyen on 10/15/16.
 */

public class NewsOneFragmentAdapter extends BaseAdapter<NewsItem, NewsOneFragmentAdapter.ActivityHolder> {
    NewsListener listener;

    public NewsOneFragmentAdapter(LayoutInflater inflater, NewsListener activityListener) {
        super(inflater);
        listener = activityListener;
    }

    @Override
    public ActivityHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ActivityHolder(inflater.inflate(R.layout.item_news, parent, false));
    }

    class ActivityHolder extends BaseHolder<NewsItem> {
        Context mContext;
        @BindView(R.id.ivNews)
        ImageView ivNews;
        @BindView(R.id.tvContentNews)
        TextView tvContentNews;

        public ActivityHolder(View itemView) {
            super(itemView);
            this.mContext = itemView.getContext();
        }

        @Override
        public void bind(NewsItem data, int position) {
            if (data != null){
                Glide.with(mContext)
                        .load(data.imageUrl)
                        .centerCrop()
                        .placeholder(R.drawable.blue_progress_bar)
                        .crossFade()
                        .into(ivNews);
                tvContentNews.setText(data.contents);
                //if (listener != null)
                // listener.showImage(imageUrls[position], iv_Activity);
            }
        }
    }

    public interface NewsListener {
        void showImage(String url, ImageView imageView);
    }
}
