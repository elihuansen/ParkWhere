package io.parkwhere.utils;

import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TimeHelperTest {
    @Test
    public void checkMinutesToMidnightIsCorrect1() {
        long expected = 0;
        long actual   = TimeHelper.minutesToMidnight(LocalTime.of(0, 0));
        assertEquals(expected, actual);
    }

    @Test
    public void checkMinutesToMidnightIsCorrect2() {
        long expected = 1439;
        long actual   = TimeHelper.minutesToMidnight(LocalTime.of(0, 1));
        assertEquals(expected, actual);
    }

    @Test
    public void checkMinutesToMidnightIsCorrect3() {
        long expected = 1;
        long actual   = TimeHelper.minutesToMidnight(LocalTime.of(23, 59));
        assertEquals(expected, actual);
    }

    @Test
    public void checkMinutesFromMidnightIsCorrect1() {
        long expected = 0;
        long actual   = TimeHelper.minutesFromMidnight(LocalTime.of(0, 0));
        assertEquals(expected, actual);
    }

    @Test
    public void checkMinutesFromMidnightIsCorrect2() {
        long expected = 1;
        long actual   = TimeHelper.minutesFromMidnight(LocalTime.of(0, 1));
        assertEquals(expected, actual);
    }

    @Test
    public void checkMinutesFromMidnightIsCorrect3() {
        long expected = 1439;
        long actual   = TimeHelper.minutesFromMidnight(LocalTime.of(23, 59));
        assertEquals(expected, actual);
    }

    @Test
    public void checkMinutesBetweenTimesIsCorrect1() {
        long expected = 1440;
        long actual = TimeHelper.minutesBetween(
            DayOfWeek.MONDAY,
            LocalTime.of(12, 0),
            DayOfWeek.TUESDAY,
            LocalTime.of(12, 0)
        );
        assertEquals(expected, actual);
    }

    @Test
    public void checkMinutesBetweenTimesOfSameDayIsCorrect1() {
        long expected = 390;
        long actual = TimeHelper.minutesBetween(
                DayOfWeek.MONDAY,
                LocalTime.of(11, 30),
                DayOfWeek.MONDAY,
                LocalTime.of(18, 0)
        );
        assertEquals(expected, actual);
    }

    @Test
    public void checkMinutesBetweenTimesOfSameDayIsCorrect2() {
        long expected = 360;
        long actual = TimeHelper.minutesBetween(
                DayOfWeek.TUESDAY,
                LocalTime.of(8, 0),
                DayOfWeek.TUESDAY,
                LocalTime.of(14, 0)
        );
        assertEquals(expected, actual);
    }

    @Test
    public void checkMinutesBetweenTimesIsCorrect2() {
        long expected = 2071;
        long actual = TimeHelper.minutesBetween(
                DayOfWeek.MONDAY,
                LocalTime.of(11, 28),
                DayOfWeek.TUESDAY,
                LocalTime.of(21, 59)
        );
        assertEquals(expected, actual);
    }

    @Test
    public void checkMinutesBetweenTimesIsCorrect3() {
        long expected = 2880;
        long actual = TimeHelper.minutesBetween(
                DayOfWeek.MONDAY,
                LocalTime.of(12, 0),
                DayOfWeek.WEDNESDAY,
                LocalTime.of(12, 0)
        );
        assertEquals(expected, actual);
    }
}
