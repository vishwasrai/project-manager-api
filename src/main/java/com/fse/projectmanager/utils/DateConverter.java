package com.fse.projectmanager.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateConverter {
    private static final String DATE_FORMAT = "yyyy-MM-dd";

    public static Date convert(String date) {
        try {
            SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
            return format.parse(date);
        } catch (Exception e) {
            return null;
        }
    }

    public static String convert(Date date) {
        try {
            SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
            return format.format(date);
        } catch (Exception e) {
            return null;
        }
    }
}
