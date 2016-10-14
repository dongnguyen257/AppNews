package net.appnews.utils;

import android.content.Context;
import android.content.Intent;

import net.appnews.ui.main.MainActivity;

/**
 * Created by DongNguyen on 10/14/16.
 */

public class Navigator {
    public static void openMainActivity(Context context){
        Intent it = new Intent(context, MainActivity.class);
        it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(it);
    }
}
