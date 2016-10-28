package net.appnews.ui.fragment.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import net.appnews.R;
import net.appnews.ui.base.BaseAdapter;
import net.appnews.ui.base.BaseHolder;

import butterknife.BindView;

/**
 * Created by DongNguyen on 10/24/16.
 */

public class ImagesAdapter extends BaseAdapter<String, ImagesAdapter.NewsHolder> {
    ImagesListener mImagesListener;

    public ImagesAdapter(@NonNull LayoutInflater inflater) {
        super(inflater);
    }

    @Override
    public ImagesAdapter.NewsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ImagesAdapter.NewsHolder(inflater.inflate(R.layout.detail_images, parent, false));
    }

    public interface ImagesListener {
        void showImage(String url, ImageView imageView);
    }

    class NewsHolder extends BaseHolder<String> {
        Context mContext;
        @BindView(R.id.detailNewsImage)
        ImageView detailNewsImage;

        public NewsHolder(View itemView) {
            super(itemView);
            this.mContext = itemView.getContext();
        }

        @Override
        public void bind(String data, int position) {
            if (data != null){
//                ImageWorker.displayImage(mContext, detailNewsImage, progressBarImage, "https://lh4.googleusercontent.com/--dq8niRp7W4/URquVgmXvgI/AAAAAAAAAbs/-gnuLQfNnBA/s1024/A%252520Song%252520of%252520Ice%252520and%252520Fire.jpg");
                Picasso.with(mContext).load(data)
                        .placeholder(R.drawable.banerfull)
                        .error(R.drawable.banerfull)
                        .fit().centerCrop().into(detailNewsImage);
            }
        }
    }
}
