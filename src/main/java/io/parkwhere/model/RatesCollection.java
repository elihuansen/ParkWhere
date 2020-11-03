package io.parkwhere.model;

import io.parkwhere.exceptions.MultipleFirstBlockException;
import io.parkwhere.utils.TimeHelper;
import io.parkwhere.utils.datastructures.CircularLinkedList;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static io.parkwhere.utils.TimeHelper.isBetweenInclusive;
import static java.time.temporal.ChronoUnit.MINUTES;

public class RatesCollection {

    private CircularLinkedList<BlockRate> circularBlockRates;
    private HashMap<DayOfWeek, BlockRate> firstBlockByDay;

    public RatesCollection() {
        this.circularBlockRates = new CircularLinkedList<>();
        this.firstBlockByDay    = new HashMap<>();
    }

    public RatesCollection addWeekdayBlockRates(BlockRate... blockRates) {
        DayOfWeek[] weekdays = new DayOfWeek[]{
          DayOfWeek.MONDAY,
          DayOfWeek.TUESDAY,
          DayOfWeek.WEDNESDAY,
          DayOfWeek.THURSDAY,
          DayOfWeek.FRIDAY
        };
        for (DayOfWeek weekday : weekdays) {
            for (BlockRate blockRate : blockRates) {
                blockRate = blockRate.copy();
                blockRate.setStartDay(weekday);
                LocalTime rateStartTime = blockRate.getStartTime();
                LocalTime rateEndTime = blockRate.getEndTime();
                if (rateStartTime != null && rateEndTime != null) {
                    if (rateEndTime.isBefore(rateStartTime)) {
                        blockRate.setEndDay(weekday.plus(1));
                    } else {
                        blockRate.setEndDay(weekday);
                    }
                }
                if (blockRate.isFirstBlock()) {
                    setDayFirstBlock(blockRate.getStartDay(), blockRate);
                } else {
                    circularBlockRates.add(blockRate);
                }
            }
        }
        return this;
    }

    public RatesCollection addBlockRates(BlockRate... blockRates) {
        for (BlockRate blockRate : blockRates) {
            if (blockRate.isFirstBlock()) {
                setDayFirstBlock(blockRate.getStartDay(), blockRate);
            } else {
                circularBlockRates.add(blockRate);
            }
        }
        return this;
    }

    public BlockRate getDayFirstBlock(DayOfWeek day) {
        return firstBlockByDay.getOrDefault(day, null);
    }

    public List<Charge> calculateCharges(LocalDateTime fromDateTime, LocalDateTime toDateTime) {
        List<Charge> charges = new ArrayList<>();
        LocalDateTime currentDateTime = LocalDateTime.from(fromDateTime);

        BlockRate firstBlock = getDayFirstBlock(currentDateTime.getDayOfWeek());
        if (firstBlock != null) {
            int blockMins = firstBlock.getBlockMins();
            charges.add(new Charge(firstBlock, 1, currentDateTime, LocalDateTime.from(currentDateTime.plusMinutes(blockMins))));
            currentDateTime = currentDateTime.plusMinutes(blockMins + 1);
        }

        CircularLinkedList<BlockRate>.Node node = getStartingBlockRate(currentDateTime);

        while (currentDateTime.isBefore(toDateTime)) {
            BlockRate rate = node.getItem();
            int blockMins  = rate.getBlockMins();

            long numMinsInBlock = TimeHelper.minutesBetween(currentDateTime, rate.getEndDay(), rate.getEndTime());
            if (currentDateTime.plusMinutes(numMinsInBlock).isAfter(toDateTime)) {
                numMinsInBlock = MINUTES.between(currentDateTime, toDateTime);
            }

            if (numMinsInBlock > 0) {
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
            node = node.getNext();
        }
        return charges;
    }

    private boolean isBetweenDays(DayOfWeek start, DayOfWeek target, DayOfWeek end) {
        return start.getValue() <= target.getValue() && target.getValue() <= end.getValue();
    }

    private CircularLinkedList<BlockRate>.Node getStartingBlockRate(LocalDateTime dateTime) {
        CircularLinkedList<BlockRate>.Node node = circularBlockRates.getHead();
        DayOfWeek day = dateTime.getDayOfWeek();
        circularBlockRates.display();
        while (true) {
            BlockRate rate = node.getItem();
            if (isBetweenDays(rate.getStartDay(), day, rate.getEndDay()) &&
                isBetweenInclusive(rate.getStartTime(), dateTime.toLocalTime(), rate.getEndTime())) {
                return node;
            }
            node = node.getNext();
        }
    }


    private void setDayFirstBlock(DayOfWeek day, BlockRate firstBlock) {
        if (firstBlockByDay.containsKey(day)) {
            throw new MultipleFirstBlockException();
        } else {
            firstBlockByDay.put(day, firstBlock);
        }
    }
}
