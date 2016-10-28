package net.appnews.support.image;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by DongNguyen on 10/14/16.
 */

public class FileUtil {
    public static final int DEFAULT_IMAGE_SIZE = 1024;

    public static File getDiskCacheDir(Context context, String uniqueName) {
        final String cachePath =
                Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ||
                        !isExternalStorageRemovable() ? getExternalCacheDir(context).getPath() :
                        context.getCacheDir().getPath();

        return new File(cachePath + File.separator + uniqueName);
    }

    @TargetApi(Build.VERSION_CODES.FROYO)
    public static File getExternalCacheDir(Context context) {
        if (hasFroyo()) {
            return context.getExternalCacheDir();
        }

        final String cacheDir = "/Android/data/" + context.getPackageName() + "/cache/";
        return new File(Environment.getExternalStorageDirectory().getPath() + cacheDir);
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static boolean isExternalStorageRemovable() {
        if (hasGingerbread()) {
            return Environment.isExternalStorageRemovable();
        }
        return true;
    }

    public static boolean hasFroyo() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
    }

    public static boolean hasGingerbread() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
    }

    public static boolean hasHoneycomb() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    public static boolean hasHoneycombMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
    }

    public static boolean hasJellyBean() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    public static boolean hasKitKat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    public static File getOutputMediaFile(Context context) {
        File mediaStorageDir = FileUtil.getDiskCacheDir(context, "UploadPhotos");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator +
                "IMG_" + timeStamp + ".jpg");
    }

    public static String getPathToSaveFile(Context context, String url, String nameFolder) {
        Uri uri = Uri.parse(url);
        String fileName = uri.getLastPathSegment();
        File diskCacheDir = FileUtil.getDiskCacheDir(context, nameFolder);
        if (!diskCacheDir.exists()) {
            diskCacheDir.mkdirs();
        }
        String path = diskCacheDir.getPath() + "/" + fileName;
        return path;
    }

    public static File getLocalImageFile(Context context, String nameFile) {
        File diskCacheDir = FileUtil.getDiskCacheDir(context, "Photo");
        if (!diskCacheDir.exists()) {
            diskCacheDir.mkdirs();
        }
        File imageFilePath = new File(diskCacheDir.getPath() + "/"  + nameFile.replaceAll(" ", "_") + ".png");

        return imageFilePath;
    }

    public static String getImageToUpload(Context context, String path) {
        int degrees = 0;
        File fileImage = new File(path);
        try {
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            FileInputStream stream1 = new FileInputStream(fileImage);
            BitmapFactory.decodeStream(stream1, null, o);
            stream1.close();

            int scale = 1;
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            while (true) {
                if (width_tmp / 2 < DEFAULT_IMAGE_SIZE || height_tmp / 2 < DEFAULT_IMAGE_SIZE)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            FileInputStream stream2 = new FileInputStream(fileImage);
            Bitmap bm = BitmapFactory.decodeStream(stream2, null, o2);
            stream2.close();

            fileImage.deleteOnExit();
            fileImage.createNewFile();
            FileOutputStream fOut = new FileOutputStream(fileImage);

            ExifInterface exif = new ExifInterface(path);
            int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
            int rotationInDegrees = exifToDegrees(rotation);
            if (rotation == 0f) {
                bm.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            } else {
                degrees += rotationInDegrees;
                Matrix matrix = new Matrix();
                matrix.postRotate(degrees);
                bm = Bitmap.createBitmap(bm, 0, 0, o2.outWidth, o2.outHeight, matrix, true);
                bm.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            }

            bm.recycle();
            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fileImage.getPath();
    }

    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    public static String getPath(Context context, Uri uri) {
        String path = "";
        if (Build.VERSION.SDK_INT < 11) {
            path = getRealPathFromURI_BelowAPI11(context, uri);
        } else if (Build.VERSION.SDK_INT >= 11 && Build.VERSION.SDK_INT < 19) {
            path = getRealPathFromURI_API11to18(context, uri);
        } else if (Build.VERSION.SDK_INT >= 19) {
            path = getRealPathFromURI_API19(context, uri);
        }
        return path;
    }

    @SuppressLint("NewApi")
    private static String getRealPathFromURI_API19(Context context, Uri uri){
        String filePath = "";
        String wholeID = DocumentsContract.getDocumentId(uri);

        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];

        String[] column = { MediaStore.Images.Media.DATA };

        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, new String[]{ id }, null);
        if (cursor == null || cursor.getCount() <= 0)
            cursor = context.getContentResolver().query(MediaStore.Images.Media.INTERNAL_CONTENT_URI,
                    column, sel, new String[]{ id }, null);

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
    }


    @SuppressLint("NewApi")
    private static String getRealPathFromURI_API11to18(Context context, Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        String result = null;

        CursorLoader cursorLoader = new CursorLoader(
                context,
                contentUri, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();

        if(cursor != null){
            int column_index =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            result = cursor.getString(column_index);
        }
        return result;
    }

    private static String getRealPathFromURI_BelowAPI11(Context context, Uri contentUri){
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        int column_index
                = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
}
