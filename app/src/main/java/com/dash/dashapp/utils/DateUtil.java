package com.dash.dashapp.utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by sebas on 9/18/2017.
 */

public class DateUtil {



    public static final long SIX_HOURS_INTERVAL = 60 * 60 * 6;
    public static final long TWENTY_FOUR_HOURS_INTERVAL = 60 * 60 * 24;
    public static final long TWO_DAYS_INTERVAL = 60 * 60 * 24 * 2;
    public static final long FOUR_DAYS_INTERVAL = 60 * 60 * 24 * 4;
    public static final long ONE_WEEK_INTERVAL = 60 * 60 * 24 * 7;
    public static final long TWO_WEEKS_INTERVAL = 60 * 60 * 24 * 14;
    public static final long ONE_MONTH_INTERVAL = 60 * 60 * 24 * 30;
    public static final long THREE_MONTHS_INTERVAL = 60 * 60 * 24 * 90;

    public static long[] intervalArray = {
            SIX_HOURS_INTERVAL,
            TWENTY_FOUR_HOURS_INTERVAL,
            TWO_DAYS_INTERVAL,
            FOUR_DAYS_INTERVAL,
            ONE_WEEK_INTERVAL,
            TWO_WEEKS_INTERVAL,
            ONE_MONTH_INTERVAL,
            THREE_MONTHS_INTERVAL
    };

    public static int monthDifference(Date startDate, Date endDate){
        Calendar startCalendar = new GregorianCalendar();
        startCalendar.setTime(startDate);
        Calendar endCalendar = new GregorianCalendar();
        endCalendar.setTime(endDate);

        int diffYear = endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR);
        int diffMonth = diffYear * 12 + endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH);

        return diffMonth;
    }

    public static long dateStringToSecond(String dateAndTime){

        Timestamp timestamp = null;

        String date = dateAndTime.substring(0, dateAndTime.indexOf("T"));
        String time = dateAndTime.substring(dateAndTime.indexOf("T") + 1, dateAndTime.indexOf("Z") - 1);

        dateAndTime = date + " " + time;

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
            Date parsedDate = dateFormat.parse(dateAndTime);
            timestamp = new java.sql.Timestamp(parsedDate.getTime());
        } catch(Exception e) { //this generic but you can control another types of exception
            // look the origin of excption
            e.getMessage();
        }

        return timestamp.getTime()/1000;
    }

    public static String getDate(long timeStamp){

        try{
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date netDate = (new Date(timeStamp));
            return sdf.format(netDate);
        }
        catch(Exception ex){
            return "xx";
        }
    }

    public static long timestampMilliToSec(){
        return System.currentTimeMillis()/1000;
    }
}
