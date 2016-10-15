package net.appnews.widgets.pullableRecyclerView;

import android.support.design.widget.AppBarLayout;

/**
 * Created by DongNguyen on 10/15/16.
 */

public abstract class AppBarStateChangeListener implements AppBarLayout.OnOffsetChangedListener {
    public enum State {
        EXPANDED,
        COLLAPSED,
        IDLE
    }

    private State defaultState = State.IDLE;

    @Override
    public final void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        if (i == 0) {
            if (defaultState != State.EXPANDED) {
                onStateChanged(appBarLayout, State.EXPANDED);
            }
            defaultState = State.EXPANDED;
        } else if (Math.abs(i) >= appBarLayout.getTotalScrollRange()) {
            if (defaultState != State.COLLAPSED) {
                onStateChanged(appBarLayout, State.COLLAPSED);
            }
            defaultState = State.COLLAPSED;
        } else {
            if (defaultState != State.IDLE) {
                onStateChanged(appBarLayout, State.IDLE);
            }
            defaultState = State.IDLE;
        }
    }
    public abstract void onStateChanged(AppBarLayout appBarLayout, State state);
}
