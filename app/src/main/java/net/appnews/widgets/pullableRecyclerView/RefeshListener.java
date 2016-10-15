package net.appnews.widgets.pullableRecyclerView;

/**
 * Created by DongNguyen on 10/15/16.
 */

public interface RefeshListener {
    int STATE_NORMAL = 0;
    int STATE_RELEASE_TO_REFRESH = 1;
    int STATE_REFRESHING = 2;
    int STATE_DONE = 3;
    int STATE_NO_REFESH = 4;

    void onMove(float delta);

    boolean releaseAction();

    void refreshComplete();
}
