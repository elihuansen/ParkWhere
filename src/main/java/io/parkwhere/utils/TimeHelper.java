package io.parkwhere.utils;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static java.time.temporal.ChronoUnit.MINUTES;

public class TimeHelper {
    public static LocalTime MIDNIGHT = LocalTime.of(0, 0);

    public static boolean isBetweenInclusive(LocalTime first, LocalTime target, LocalTime second) {
        if (first.isAfter(second)) {
            return
                (target.isAfter(first) || target.equals(first)) ||
                (target.isBefore(second) || target.equals(second));
        }
        return
            (target.isAfter(first) || target.equals(first)) &&
            (target.isBefore(second) || target.equals(second))
        ;
    }

    public static long minutesBetween(LocalDateTime dateTime, DayOfWeek dayOfWeek, LocalTime time) {
        return minutesBetween(dateTime.getDayOfWeek(), dateTime.toLocalTime(), dayOfWeek, time);
    }

    public static long minutesBetween(DayOfWeek day1, LocalTime time1, DayOfWeek day2, LocalTime time2) {
        long minutes = 0;
        if (!day1.equals(day2)) {
            while (!day1.equals(day2)) {
                minutes += minutesToMidnight(time1);
                minutes += minutesFromMidnight(time2);
                day1 = day1.plus(1);
            }
        } else {
            minutes += minutesFromMidnight(time2) - minutesFromMidnight(time1);
        }
        return minutes;
    }

    public static long minutesToMidnight(LocalTime time) {
        if (MIDNIGHT.equals(time)) {
            return 0;
        }
        return MINUTES.between(time, MIDNIGHT.minusMinutes(1)) + 1;
    }

    public static long minutesFromMidnight(LocalTime time) {
        return MINUTES.between(MIDNIGHT, time);
    }
}
