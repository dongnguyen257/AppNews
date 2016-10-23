package net.appnews.ui.media;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;

import java.security.Key;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by DongNguyen on 10/19/16.
 */

public class GeneralHelper {

    private static GeneralHelper generalHelper = null;
    private Point size = null;

    public static synchronized GeneralHelper getInstance() {
        if (generalHelper == null) {
            generalHelper = new GeneralHelper();
        }
        return generalHelper;
    }

    public GeneralHelper() {
    }

    /**
     * Convert dp in pixels
     *
     * @param dp
     * @return
     */
    public int getPx(int dp, Context context) {
        float scale = context.getResources().getDisplayMetrics().density;
        context = null;
        return ((int) (dp * scale + 0.5f));
    }

    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    public Point getScreenSize(Context context) {
        if (size != null) {
            return size;
        }
        calculateScreenSize(context);
        context = null;
        return size;
    }

    public void calculateScreenSize(Context context) {
        Point size = new Point();
        WindowManager w = ((Activity) context).getWindowManager();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            w.getDefaultDisplay().getSize(size);
        } else {
            Display d = w.getDefaultDisplay();
            size.x = d.getWidth();
            size.y = d.getHeight();
        }
        this.size = size;
        context = null;
        System.gc();
    }

    public static String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null,
                    null, null);
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            System.gc();
        }
    }

    public static long differenceBetweenTwoDateInDays(Date dateEarly, Date dateLater) {

        return (dateLater.getTime() - dateEarly.getTime()) / (24 * 60 * 60 * 1000);
    }

    public static void addWidthToView(final View view, final int widthToAdd) {

        Log.d("UI", "ADJUSTING WITH DIFF " + widthToAdd);

        if (widthToAdd <= 0) {
            Log.d("UI", "ADJUSTING WITH DIFF " + widthToAdd + " RETURNING");
            return;
        }
        //Handling also if this functions called before layout() is called for the view
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                try {
                    if (Build.VERSION.SDK_INT < 16) {
                        view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    } else {
                        view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                } catch (Exception e) {

                }
                try {
                    int width = view.getWidth() + widthToAdd;
                    ViewGroup.LayoutParams lp = view.getLayoutParams();
                    lp.width = width;
                    view.setLayoutParams(lp);
                } catch (Exception e) {

                }


            }
        });
    }

    public static boolean isSameDay(Date date1, Date date2) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
        return fmt.format(date1).equals(fmt.format(date2));
    }

    public static void runOnUiThreadWithDelay(final Context context, final int delay, final Runnable runnableForUiThread) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(delay);
                    ((Activity) context).runOnUiThread(runnableForUiThread);
                } catch (Exception e) {

                }
            }
        }).start();
    }

    private static final String ALGORITHM = "AES";
    private static final byte[] keyValue =
            new byte[]{'a', 'c', 'e', 'g', 'i', 'k', 'm', 'o', 'q', 's', 'u', 'w', 'y', 'z', '1', '3', '4', '5', '8', '0'};

    public String encrypt(String valueToEnc) throws Exception {
        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGORITHM);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encValue = c.doFinal(valueToEnc.getBytes());
        String encryptedValue = Base64.encodeToString(encValue, Base64.DEFAULT);
        return encryptedValue;
    }

    public String decrypt(String encryptedValue) throws Exception {
        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGORITHM);
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decordedValue = Base64.decode(encryptedValue, Base64.DEFAULT);
        byte[] decValue = c.doFinal(decordedValue);
        String decryptedValue = new String(decValue);
        return decryptedValue;
    }

    private static Key generateKey() throws Exception {
        Key key = new SecretKeySpec(keyValue, ALGORITHM);
        return key;
    }
}
