package net.appnews.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by DongNguyen on 10/23/16.
 */

public class Utils {

    public static String getTimeZone(String time){
        //convert time fortmat
        final String OLD_FORMAT = "yyyy-MM-dd HH:mm:ss";
        final String NEW_FORMAT = "MMM d, h:mm a";
        String oldDateString = time;
        String newDateString;
        SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);
        Date d = null;
        try {
            d = sdf.parse(oldDateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        sdf.applyPattern(NEW_FORMAT);
        newDateString = sdf.format(d);
        //
        SimpleDateFormat df = new SimpleDateFormat("MMM d, h:mm a");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = null;
        try {
            date = df.parse(newDateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        df.setTimeZone(TimeZone.getTimeZone("GMT+07:00"));
        String formattedDate = df.format(date);

        return formattedDate;
    }
}
