package net.appnews.widgets.pullableRecyclerView;

/**
 * Created by DongNguyen on 10/15/16.
 */

public interface LoadMoreListener {
    int STATE_NORMAL = 0;
    int STATE_RELEASE_TO_LOAD_MORE = 1;
    int STATE_LOADING = 2;
    int STATE_DONE = 3;
    int STATE_NOMORE = 4;

    void onMove(float delta);

    boolean releaseAction();

    void loadMoreComplete();
}
