package net.appnews.widgets.pullableRecyclerView;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import net.appnews.R;

/**
 * Created by DongNguyen on 10/15/16.
 */

public class RefeshView extends LinearLayout implements RefeshListener {

    private LinearLayout mContainer;
    private ProgressBar loading_ProgressBar;
    private boolean visibleFrag;
    private int mState = STATE_NORMAL;

    private static final int ROTATE_ANIM_DURATION = 180;

    public int mMeasuredHeight;

    public RefeshView(Context context) {
        super(context);
        initView();
    }

    public RefeshView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        mContainer = (LinearLayout) LayoutInflater.from(getContext()).inflate(
                R.layout.item_refeshing, null);
        loading_ProgressBar = (ProgressBar) mContainer.findViewById(R.id.loading_ProgressBar);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, 0);
        this.setLayoutParams(lp);
        this.setPadding(0, 0, 0, 0);

        addView(mContainer, new LayoutParams(LayoutParams.MATCH_PARENT, 0));

        measure(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        mMeasuredHeight = getMeasuredHeight();
    }

    public void setState(int state) {
        if (state == mState) return ;
        switch(state){
            case STATE_NO_REFESH:
                inVisible();
                break;
            default:
                visible();
        }

        mState = state;
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
        return mState;
    }

    @Override
    public void refreshComplete(){
        setState(STATE_DONE);
        new Handler().postDelayed(() -> reset(STATE_NORMAL), 200);
    }

    public void noRefesh() {
        setState(STATE_NO_REFESH);
        new Handler().postDelayed(() -> reset(STATE_NO_REFESH), 200);
    }

    public void reset(int state) {
        smoothScrollTo(0);
        new Handler().postDelayed(() -> setState(state), 500);
    }

    public void setVisibleHeight(int height) {
        if (height < 0) height = 0;
        LayoutParams lp = (LayoutParams) mContainer .getLayoutParams();
        lp.height = height;
        mContainer.setLayoutParams(lp);
    }

    public int getVisibleHeight() {
        LayoutParams lp = (LayoutParams) mContainer.getLayoutParams();
        return lp.height;
    }

    @Override
    public void onMove(float delta) {
        if (getVisibleHeight() > 0 || delta > 0) {
            setVisibleHeight((int)delta + getVisibleHeight());
            if (mState <= STATE_RELEASE_TO_REFRESH) {
                if (getVisibleHeight() > mMeasuredHeight) {
                    setState(STATE_RELEASE_TO_REFRESH);
                }else {
                    setState(STATE_NORMAL);
                }
            }
        }
    }

    @Override
    public boolean releaseAction() {
        boolean isOnRefresh = false;
        int height = getVisibleHeight();
        if (height == 0)
            isOnRefresh = false;
        if(height > mMeasuredHeight &&  mState < STATE_REFRESHING){
            setState(STATE_REFRESHING);
            isOnRefresh = true;
        }
        if (mState == STATE_REFRESHING && height <=  mMeasuredHeight) {

        }
        int destHeight = 0;
        if (mState == STATE_REFRESHING) {
            destHeight = mMeasuredHeight;
        }
        smoothScrollTo(destHeight);

        return isOnRefresh;
    }

    private void smoothScrollTo(int destHeight) {
        ValueAnimator animator = ValueAnimator.ofInt(getVisibleHeight(), destHeight);
        animator.setDuration(200).start();
        animator.addUpdateListener(animation -> setVisibleHeight((int) animation.getAnimatedValue()));
        animator.start();
    }
}