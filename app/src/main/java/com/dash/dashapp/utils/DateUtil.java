package com.dash.dashapp.utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sebas on 9/18/2017.
 */

public class DateUtil {



    public static final long FIVE_MINUTES_GAP = 1000 * 60 * 5;
    public static final long FIFTEEN_MINUTES_GAP = 1000 * 60 * 15;
    public static final long THIRTY_MINUTES_GAP = 1000 * 60 * 30;
    public static final long TWO_HOURS_GAP = 1000 * 60 * 60 * 2;
    public static final long FOUR_HOURS_GAP = 1000 * 60 * 60 * 4;
    public static final long TWENTY_FOUR_HOURS_GAP = 1000 * 60 * 60 * 24;

    public static final long SIX_HOURS_INTERVAL = 1000 *60 * 60 * 6;
    public static final long TWENTY_FOUR_HOURS_INTERVAL = 1000 * 60 * 60 * 24;
    public static final long TWO_DAYS_INTERVAL = 1000 * 60 * 60 * 24 * 2;
    public static final long FOUR_DAYS_INTERVAL = 1000 * 60 * 60 * 24 * 4;
    public static final long ONE_WEEK_INTERVAL = 1000 * 60 * 60 * 24 * 7;
    public static final long TWO_WEEKS_INTERVAL = 1000 * 60 * 60 * 24 * 14;
    public static final long ONE_MONTH_INTERVAL = 1000L * 60L * 60L * 24L * 30L;
    public static final long THREE_MONTHS_INTERVAL = 1000L * 60L * 60L * 24L * 90L;


    public static final String FIVE_MINUTES_GAP_STRING = "5m";
    public static final String FIFTEEN_MINUTES_GAP_STRING = "15m";
    public static final String THIRTY_MINUTES_GAP_STRING = "30m";
    public static final String TWO_HOURS_GAP_STRING = "2h";
    public static final String FOUR_HOURS_GAP_STRING = "4h";
    public static final String TWENTY_FOUR_HOURS_GAP_STRING = "1d";

    public static final String SIX_HOURS_INTERVAL_STRING = "6h";
    public static final String TWENTY_FOUR_HOURS_INTERVAL_STRING = "24h";
    public static final String TWO_DAYS_INTERVAL_STRING = "2d";
    public static final String FOUR_DAYS_INTERVAL_STRING = "4d";
    public static final String ONE_WEEK_INTERVAL_STRING = "1w";
    public static final String TWO_WEEKS_INTERVAL_STRING = "2w";
    public static final String ONE_MONTH_INTERVAL_STRING = "1m";
    public static final String THREE_MONTHS_INTERVAL_STRING = "3m";

    public static long[] gapArray = {
            FIVE_MINUTES_GAP,
            FIFTEEN_MINUTES_GAP,
            THIRTY_MINUTES_GAP,
            TWO_HOURS_GAP,
            FOUR_HOURS_GAP,
            TWENTY_FOUR_HOURS_GAP
    };

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

    public static long dateStringToMilliseconds(String dateAndTime){

        Timestamp timestamp = null;

        /*String date = dateAndTime.substring(0, dateAndTime.indexOf("T"));
        String time = dateAndTime.substring(dateAndTime.indexOf("T") + 1, dateAndTime.indexOf("Z") - 1);

        dateAndTime = date + " " + time;*/

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            Date parsedDate = dateFormat.parse(dateAndTime);
            timestamp = new java.sql.Timestamp(parsedDate.getTime());
        } catch(Exception e) { //this generic but you can control another types of exception
            // look the origin of excption
            e.getMessage();
        }

        return timestamp.getTime();
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
