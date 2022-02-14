package com.demon.yu.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtils {
    private static final String timeStyle = "MM-dd HH-mm-ss";

    public static String getCurrentTimeWithStyle() {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(timeStyle);
        return simpleDateFormat.format(date);
    }
}
