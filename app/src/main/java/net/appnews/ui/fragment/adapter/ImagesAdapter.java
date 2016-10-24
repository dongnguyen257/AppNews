package net.appnews.ui.fragment.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import net.appnews.R;
import net.appnews.data.entities.NewsItem;
import net.appnews.ui.base.BaseAdapter;
import net.appnews.ui.base.BaseHolder;

import java.util.List;

import butterknife.BindView;

/**
 * Created by DongNguyen on 10/24/16.
 */

public class ImagesAdapter extends BaseAdapter<NewsItem.Results, ImagesAdapter.NewsHolder> {
    private NewsFragmentAdapter.NewsDetailListener listener;
    private List<String> listUrlImages;
    public static int HEADER_SIZE;

    public ImagesAdapter(@NonNull LayoutInflater inflater, List<String> listUrlImages) {
        super(inflater);
        this.listUrlImages = listUrlImages;
    }

    @Override
    public ImagesAdapter.NewsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ImagesAdapter.NewsHolder(inflater.inflate(R.layout.detail_images, parent, false));
    }

    class NewsHolder extends BaseHolder<List<String>> {
        Context mContext;
        @BindView(R.id.tvTitle)
        ImageView detailNewsImage;

        public NewsHolder(View itemView) {
            super(itemView);
            this.mContext = itemView.getContext();
        }

        @Override
        public void bind(List<String> data, int position) {
            if (data != null){

            }
        }
    }
}
