package net.appnews.ui.base;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import net.appnews.R;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by DongNguyen on 10/15/16.
 */

public abstract class BaseFragment extends Fragment {
    protected Unbinder unbinder;

    public boolean isInLeft;
    public boolean isOutLeft;
    public boolean isCurrentScreen;

    public boolean isLoaded = false;
    public boolean isDead = false;
    private Object object = new Object();

    protected abstract int getLayoutId();

    public int getPopDownAnimId() {
        return R.anim.exit;
    }

    public int getPopUpAnimId() {
        return R.anim.enter;
    }

    public int getLeftInAnimId() {
        return R.anim.slide_in_left;
    }

    public int getRightInAnimId() {
        return R.anim.slide_in_right;
    }

    public int getLeftOutAnimId() {
        return R.anim.slide_out_left;
    }

    public int getRightOutAnimId() {
        return R.anim.slide_out_right;
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        Animation animation = null;

        if (isCurrentScreen) {
            int out = getPopDownAnimId();
            int in = getPopUpAnimId();
            animation = AnimationUtils.loadAnimation(getContext(), enter ? in : out);
        } else {
            if (enter) {
                int left = getLeftInAnimId();
                int right = getRightInAnimId();
                animation = AnimationUtils.loadAnimation(getContext(), isInLeft ? left : right);
            } else {
                int left = getLeftOutAnimId();
                int right = getRightOutAnimId();
                animation = AnimationUtils.loadAnimation(getContext(), isOutLeft ? left : right);
            }
        }

        if (enter) {
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}
                @Override
                public void onAnimationEnd(Animation animation) {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(() -> {
                        Thread thread = new Thread(() -> {
                            while (!isLoaded) {
                                try {
                                    object.wait();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            getActivity().runOnUiThread(() -> {
                                if (!isDead)
                                    work();
                            });
                        });
                        thread.start();
                    }, 100);
                }
                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
        }
        return animation;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        isDead = false;
        View view = inflater.inflate(getLayoutId(), container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        synchronized (object) {
            isLoaded = true;
            object.notifyAll();
        }
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        isDead = true;
        super.onDestroyView();
        if (unbinder != null)
            unbinder.unbind();
        hide();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        remove();
        isLoaded = false;
    }

    protected void remove(){}

    protected void hide(){}

    protected void work() {
    }
}
