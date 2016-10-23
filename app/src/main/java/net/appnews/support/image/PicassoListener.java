package net.appnews.support.image;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Callback;

/**
 * Created by DongNguyen on 10/22/16.
 */

public class PicassoListener implements Callback {
    ProgressBar progressBar;
    ImageView imageView;

    public PicassoListener(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    public PicassoListener(ImageView imageView, ProgressBar progressBar) {
        this.imageView = imageView;
        this.progressBar = progressBar;
    }

    @Override
    public void onSuccess() {
        updateViews();
    }

    @Override
    public void onError() {
        updateViews();
    }

    private void updateViews(){
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }

        if (imageView != null) {
            imageView.setVisibility(View.VISIBLE);
        }
    }
}
