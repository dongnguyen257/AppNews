package net.appnews.widgets.pullableRecyclerView;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import net.appnews.R;

/**
 * Created by DongNguyen on 10/15/16.
 */

public class FooterView extends LinearLayout implements LoadMoreListener{

    private int state = STATE_NORMAL;
    private LinearLayout container;

    public int mMeasuredHeight;
    private ProgressBar loading_ProgressBar;
    private boolean visibleFrag;

    public FooterView(Context context) {
        super(context);
        initView();
    }

    public FooterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public void initView(){
        container = (LinearLayout) LayoutInflater.from(getContext()).inflate(
                R.layout.item_load_more, null);
        loading_ProgressBar = (ProgressBar) container.findViewById(R.id.loading_ProgressBar);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, 0);
        this.setLayoutParams(lp);
        this.setPadding(0, 0, 0, 0);

        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM;

        addView(container, layoutParams);

        measure(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        mMeasuredHeight = getMeasuredHeight();
    }

    public void setState(int state) {
        if (this.state == state)
            return;
        switch(state) {
            case STATE_NOMORE:
                inVisible();
                break;
            default:
                visible();
        }
        this.state = state;
    }

    private void visible() {
        if (!visibleFrag) {
            loading_ProgressBar.setVisibility(VISIBLE);
            visibleFrag = true;
        }
    }

    private void inVisible() {
        if (visibleFrag) {
            loading_ProgressBar.setVisibility(INVISIBLE);
            visibleFrag = false;
        }
    }

    public int getState() {
        return state;
    }

    public void setVisibleHeight(int height) {
        if (height < 0) height = 0;
        LayoutParams lp = (LayoutParams) container.getLayoutParams();
        lp.height = height;
        container.setLayoutParams(lp);
    }

    public int getVisibleHeight() {
        LayoutParams lp = (LayoutParams) container.getLayoutParams();
        return lp.height;
    }

    @Override
    public void onMove(float delta) {
        if (getVisibleHeight() > 0 || delta > 0) {
            setVisibleHeight((int) delta + getVisibleHeight());
            if (state <= STATE_RELEASE_TO_LOAD_MORE) {
                if (getVisibleHeight() > mMeasuredHeight) {
                    setState(STATE_RELEASE_TO_LOAD_MORE);
                } else {
                    setState(STATE_NORMAL);
                }
            }
        }
    }

    @Override
    public boolean releaseAction() {
        boolean isOnLoadMore = false;
        int height = getVisibleHeight();
        if (height == 0)
            isOnLoadMore = false;
        if(height >= mMeasuredHeight &&  state < STATE_LOADING){
            setState(STATE_LOADING);
            isOnLoadMore = true;
        }
        int destHeight = 0;
        if (state == STATE_LOADING) {
            destHeight = mMeasuredHeight;
        }
        smoothScrollTo(height, destHeight);

        return isOnLoadMore;
    }

    @Override
    public void loadMoreComplete() {
        setState(STATE_DONE);
        new Handler().postDelayed(() -> reset(STATE_NORMAL), 200);
    }

    public void noMore() {
        setState(STATE_NOMORE);
        new Handler().postDelayed(() -> reset(STATE_NOMORE), 200);
    }

    public void reset(int state) {
        smoothScrollTo(getVisibleHeight(), 0);
        new Handler().postDelayed(() -> setState(state), 500);
    }

    private void smoothScrollTo(int height, int destHeight) {
        ValueAnimator animator = ValueAnimator.ofInt(height, destHeight);
        animator.setDuration(200).start();
        animator.addUpdateListener(animation -> setVisibleHeight((int) animation.getAnimatedValue()));
        animator.start();
    }
}
