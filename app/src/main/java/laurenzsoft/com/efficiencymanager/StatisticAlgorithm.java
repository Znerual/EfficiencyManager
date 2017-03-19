package laurenzsoft.com.efficiencymanager;

import android.content.Context;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Laurenz on 02.03.2017.
 */

public class StatisticAlgorithm {


    public static int minutesWorkedToday(Context applicationContext) {
        return (int) DataBaseHolder.getInstance(applicationContext).getSessionTime(getMidnight(), getCurrentTime()) / 60;
    }
    public static int sessonsWorkedToday(Context applicationContext) {
        return DataBaseHolder.getInstance(applicationContext).getSessionAmount(getMidnight(), getCurrentTime());
    }
    public static int minutesWatchedToday(Context applicationContext) {
        return DataBaseHolder.getInstance(applicationContext).getMinutesLeisure(getMidnight(), getCurrentTime(), LeisureTimeScreen.YOUTUBESTATE) / 60;
    }
    public static int minutesReadToday(Context applicationContext) {
        return DataBaseHolder.getInstance(applicationContext).getMinutesLeisure(getMidnight(), getCurrentTime(), LeisureTimeScreen.BOOKSTATE) / 60;
    }
    public static int minutesWatchedWeek(Context applicationContext) {
        return DataBaseHolder.getInstance(applicationContext).getMinutesLeisure(getLast7Day(), getCurrentTime(), LeisureTimeScreen.YOUTUBESTATE) / 60;
    }
    public static float workRatioToday(Context applicationContext) {
        return minutesWorkedToday(applicationContext) / (minutesWatchedToday(applicationContext) + 1);
    }
    public static float workRatioWeek(Context applicationContext) {
        return hoursWorkedWeek(applicationContext) / ((minutesWatchedWeek(applicationContext) / 60) + 1);
    }
    public static float readWatchRatioToday(Context applicationContext) {
        return minutesReadToday(applicationContext) / (minutesWatchedToday(applicationContext) +1);
    }
    public static float readWatchRatioWeek(Context applicationContext) {
        return minutesReadWeek(applicationContext) / (minutesWatchedWeek(applicationContext) +1);
    }
    public static String averageToday(Context applicationContext) {
        int worked = minutesWorkedToday(applicationContext);
        float weekAv = hoursWorkedWeek(applicationContext) * 8.5f;
        if (worked >= weekAv * 2) {
            return applicationContext.getResources().getString(R.string.highover);
        } else if(worked >= weekAv) {
            return applicationContext.getResources().getString(R.string.over);
        }else if(worked >= (weekAv * 0.85)) {
            return "";
        } else {
            return applicationContext.getResources().getString(R.string.under);
        }
    }
    public static int productivityToday(Context applicationContext) {
        int worked = minutesWorkedToday(applicationContext);
        float weekAv = hoursWorkedWeek(applicationContext) * 8.5f;
        if (worked >= weekAv * 2) {
            return 2;
        } else if(worked >= weekAv) {
            return 1;
        }else if(worked >= (weekAv * 0.85)) {
            return 0;
        } else {
            return -1;
        }
    }
    public static String productiveWeek(Context applicationContext) {
        int worked = hoursWorkedWeek(applicationContext);
        float weekAv = hoursWorkedMonth(applicationContext) / 30;
        if (worked >= weekAv * 2) {
            return applicationContext.getResources().getString(R.string.very);
        } else if(worked >= weekAv) {
            return "";
        }else if(worked >= (weekAv * 0.85)) {
            return applicationContext.getResources().getString(R.string.notvery);
        } else {
            return applicationContext.getResources().getString(R.string.not);
        }

    }
    public static int daysWorkedWeek(Context applicationContext) {
        return DataBaseHolder.getInstance(applicationContext).getSessionAmount(getLast7Day(), getCurrentTime());
    }
    public static int hoursWorkedWeek(Context applicationContext) {
        return (int) DataBaseHolder.getInstance(applicationContext).getSessionTime(getLast7Day(), getCurrentTime()) / (60*24) ;
    }
    public static int hoursWorkedMonth(Context applicationContext) {
        return (int) DataBaseHolder.getInstance(applicationContext).getSessionTime(getLastMonth(), getCurrentTime()) / (60*24) ;
    }
    public static int minutesReadWeek(Context applicationContext) {
        return DataBaseHolder.getInstance(applicationContext).getMinutesLeisure(getLast7Day(), getCurrentTime(), LeisureTimeScreen.BOOKSTATE) / 60;
    }

    private static long getMidnight() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        return cal.getTime().getTime();
    }
    private static long getLast7Day() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.roll(Calendar.DAY_OF_MONTH, -7);
        return cal.getTime().getTime();
    }
    private static long getLastMonth() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.DAY_OF_MONTH, 0);
        cal.roll(Calendar.MONTH, -1);
        return cal.getTime().getTime();
    }
    private static long getCurrentTime() {
        return new Date().getTime();
    }
}
