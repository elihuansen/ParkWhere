package io.parkwhere.utils;

import java.time.LocalTime;

public class TimeHelper {
    public static boolean isBetweenInclusive(LocalTime first, LocalTime target, LocalTime second) {
        return
            (target.isAfter(first) || target.equals(first)) &&
            (target.isBefore(second) || target.equals(second))
        ;
    }
}
