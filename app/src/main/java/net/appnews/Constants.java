package net.appnews;

/**
 * Created by DongNguyen on 10/14/16.
 */

public class Constants {
    public static final String API_YOUTUBE = "AIzaSyBrd6QrH6kWG-1wvbKxvtWCreCf4_d39Aw";
    public static final String API_BASE_URL = "https://tvfbasia.com";
    public static final String NEWS_DETAIL = "news_detail";
    public static final int REQUEST_CODE_NEWS_DETAIL = 1;

    // global topic to receive app wide push notifications
    public static final String TOPIC_GLOBAL = "global";

    // broadcast receiver intent filters
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";

    // id to handle the notification in the notification tray
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;

    public static final String SHARED_PREF = "ah_firebase";
}
