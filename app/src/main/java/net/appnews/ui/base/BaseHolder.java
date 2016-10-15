package net.appnews.ui.base;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.ButterKnife;

/**
 * Created by DongNguyen on 10/14/16.
 */

public class BaseHolder<V> extends RecyclerView.ViewHolder {

    public BaseHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bind(V data, int position){}
}
