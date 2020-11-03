package io.parkwhere.model;

import io.parkwhere.exceptions.MultipleFirstBlockException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static io.parkwhere.utils.TimeHelper.isBetweenInclusive;
import static java.time.temporal.ChronoUnit.MINUTES;

public class RatesCollection {

    private HashMap<DayOfWeek, List<BlockRate>> blockRatesByDay;
    private HashMap<DayOfWeek, BlockRate> firstBlockByDay;

    public RatesCollection() {
        this.blockRatesByDay = new HashMap<>();
        this.firstBlockByDay = new HashMap<>();
    }

    public RatesCollection(HashMap<DayOfWeek, List<BlockRate>> blockRatesByDay, HashMap<DayOfWeek, BlockRate> firstBlockByDay) {
        this.blockRatesByDay = blockRatesByDay;
        this.firstBlockByDay = firstBlockByDay;
    }

    public RatesCollection addBlockRate(DayOfWeek day, BlockRate blockRate) {
        putDayIfAbsent(day);
        if (blockRate.isFirstBlock()) {
            setDayFirstBlock(day, blockRate);
        }
        blockRatesByDay.get(day).add(blockRate);
        return this;
    }

    public RatesCollection addBlockRates(DayOfWeek day, BlockRate... blockRates) {
        putDayIfAbsent(day);
        List<BlockRate> dayBlockRates = blockRatesByDay.get(day);
        for (BlockRate blockRate : blockRates) {
            if (blockRate.isFirstBlock()) {
                setDayFirstBlock(day, blockRate);
            } else {
                dayBlockRates.add(blockRate);
            }
        }
        return this;
    }

    public List<BlockRate> getDayBlockRates(DayOfWeek day) {
        return blockRatesByDay.getOrDefault(day, null);
    }

    public BlockRate getDayFirstBlock(DayOfWeek day) {
        return firstBlockByDay.getOrDefault(day, null);
    }

    public List<Charge> computeCharges(LocalDateTime fromDateTime, LocalDateTime toDateTime) {
        List<Charge> charges = new ArrayList<>();
        LocalDateTime currentDateTime = LocalDateTime.from(fromDateTime);

        BlockRate firstBlock = getDayFirstBlock(currentDateTime.getDayOfWeek());
        if (firstBlock != null) {
            int blockMins = firstBlock.getBlockMins();
            charges.add(new Charge(firstBlock, 1, currentDateTime, LocalDateTime.from(currentDateTime.plusMinutes(blockMins))));
            currentDateTime = currentDateTime.plusMinutes(blockMins + 1);
        }

        while (currentDateTime.isBefore(toDateTime)) {
            for (BlockRate rate : blockRatesByDay.get(currentDateTime.getDayOfWeek())) {
                int blockMins = rate.getBlockMins();

                if (rate.isTimed()) {

                    LocalTime rateStartTime = rate.getStartTime();
                    LocalTime rateEndTime = rate.getEndTime();

                    boolean isOvernightRate = rateEndTime.isBefore(rateStartTime);
                    boolean isWithinRateTiming =
                            isBetweenInclusive(rateStartTime, currentDateTime.toLocalTime(), rateEndTime) ||
                                    (
                                            isOvernightRate
                                    )
                            ;

                    if (isWithinRateTiming) {

                        LocalTime exitTime = toDateTime.toLocalTime();
                        LocalTime chargeEndTime;
                        if (currentDateTime.getDayOfYear() < toDateTime.getDayOfYear()) {
                            chargeEndTime = rateEndTime;
                        } else {
                            if (isOvernightRate) {
                                chargeEndTime = exitTime;
                            } else {
                                chargeEndTime = rateEndTime.isBefore(exitTime) ? rateEndTime : exitTime;
                            }
                        }

                        long numMinsInBlock;
                        if (isOvernightRate && currentDateTime.getDayOfYear() < toDateTime.getDayOfYear()) {
                            LocalDateTime nextDay = LocalDateTime.of(currentDateTime.getYear(), currentDateTime.getMonth(), currentDateTime.getDayOfMonth(), chargeEndTime.getHour(), chargeEndTime.getMinute()).plusDays(1);
                            numMinsInBlock = MINUTES.between(currentDateTime, nextDay);
                        } else {
                            numMinsInBlock = MINUTES.between(currentDateTime.toLocalTime(), chargeEndTime);
                        }

                        int numBlocks;
                        if (rate.isPerEntry()) {
                            numBlocks = 1;
                        } else {
                            numBlocks = (int) numMinsInBlock / blockMins;
                            boolean hasPartialBlock = numMinsInBlock % blockMins > 0;
                            if (rate.isPartThereof() && hasPartialBlock) {
                                numBlocks++;
                            }
                        }
                        charges.add(new Charge(rate, numBlocks, currentDateTime, currentDateTime.plusMinutes(numMinsInBlock)));

                        currentDateTime = currentDateTime.plusMinutes(numMinsInBlock + 1);
                        if (currentDateTime.isEqual(toDateTime) || currentDateTime.isAfter(toDateTime)) {
                            return charges;
                        }
                    }
                }
            }
        }
        return charges;
    }

    private void putDayIfAbsent(DayOfWeek day) {
        blockRatesByDay.putIfAbsent(day, new ArrayList<>());
    }

    private void setDayFirstBlock(DayOfWeek day, BlockRate firstBlock) {
        if (firstBlockByDay.containsKey(day)) {
            throw new MultipleFirstBlockException();
        } else {
            firstBlockByDay.put(day, firstBlock);
        }
    }
}
