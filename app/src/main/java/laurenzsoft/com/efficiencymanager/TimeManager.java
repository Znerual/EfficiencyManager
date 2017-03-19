package laurenzsoft.com.efficiencymanager;

import java.util.Date;

/**
 * Created by Laurenz on 22.02.2017.
 */

public class TimeManager {
    private static TimeManager instance;
    private long lessonCount, leisureTime, lessonTime, abortedLessonCount;
    private int lastConecentration;
    private int lastWorkingTime, lastBreakTime;
    private TimeManager() {
        leisureTime = 0;
        lessonCount = 0;

    }
    public static TimeManager getInstance() {
        if (instance == null) {
            instance = new TimeManager();
        }
        return instance;
    }
    public int getWorkingTime() {
        if (lessonTime < toSec(45)) {
            lastWorkingTime = toSec(45);
        } else if (lessonTime < toSec(150)) {
            lastWorkingTime = toSec(40);
        } else if (lessonTime < toSec(240)) {
            lastWorkingTime = toSec(35);
        } else if(lessonTime < toSec(360)) {
            lastWorkingTime = toSec(30);
        } else {
            lastWorkingTime = toSec(25);
        }
        if (abortedLessonCount * 2 > lessonCount) lastWorkingTime -= toSec(5);
        if (abortedLessonCount == 0) lastWorkingTime += toSec(5);
        return lastWorkingTime;
    }
    public int getBreakTime() {
        if (lessonTime < toSec(45)) {
            lastBreakTime = toSec(5);
        } else if (lessonTime < toSec(150)) {
            lastBreakTime = toSec(5);
        } else if (lessonTime < toSec(240)) {
            lastBreakTime = toSec(10);
        } else if(lessonTime < toSec(360)) {
            lastBreakTime = toSec(15);
        } else {
            lastBreakTime = toSec(10);
        }
        if (lessonCount % 3 == 0) lastBreakTime += toSec(5);
        if (lessonCount % 5 == 0) lastBreakTime += toSec(2);
        if (leisureTime > toSec(60)) {
            lastBreakTime = toSec(5);
            leisureTime /= 2;
        }
        if (lastConecentration <= 2) lastBreakTime += toSec(2);
        if (lastConecentration >= 4) lastBreakTime -= toSec(2);
        lastConecentration = 3;
        return lastBreakTime;
    }
    public void completedLesson(long workingTime) {
        lessonTime += workingTime;
        lessonCount++;
    }
    public void abortedLesson(long completedTime) {
        abortedLessonCount++;
        lessonTime += completedTime;

    }
    public void tookLeisureTime(long amount) {
        leisureTime += amount;
    }
    public void setConcentration(int concentration) {
        lastConecentration = concentration;
    }
    public int getLastWorkingTime() { return lastWorkingTime;}
    public static String format(long value) {
        String result;
        if ((result = String.valueOf(value)).length() == 1) {
            result = "0" + result;
        }
        return result;
    }
    private int toSec(int min) {
        return min * 60;
    }
}
