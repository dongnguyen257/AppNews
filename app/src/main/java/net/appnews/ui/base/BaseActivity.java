package net.appnews.ui.base;

import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by DongNguyen on 10/14/16.
 */

public class BaseActivity extends AppCompatActivity {
    Unbinder unbinder;
    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        unbinder = ButterKnife.bind(this);
    }

    @Override
    protected void onDestroy() {
        if (unbinder != null)
            unbinder.unbind();
        super.onDestroy();
    }
}
