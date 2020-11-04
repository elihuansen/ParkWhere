package io.parkwhere.model;

import io.parkwhere.exceptions.MultipleFirstBlockException;
import io.parkwhere.exceptions.OverlappingBlockRatesException;
import io.parkwhere.utils.TimeHelper;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Objects;

public class BlockRate implements Comparable<BlockRate> {
    private DayOfWeek startDay;
    private LocalTime startTime;
    private DayOfWeek endDay;
    private LocalTime endTime;
    private long totalMins;
    private double amount;
    private int blockMins;
    private boolean isFirstBlock;
    private boolean isPartThereof;

    public BlockRate() {}

    public BlockRate(boolean isFirstBlock, double amount, int blockMins) {
        this.isFirstBlock = isFirstBlock;
        this.amount = amount;
        this.blockMins = blockMins;
        this.isPartThereof = true;
    }

    public BlockRate(DayOfWeek dayOfWeek, boolean isFirstBlock, double amount, int blockMins) {
        this(isFirstBlock, amount, blockMins);
        this.startDay = dayOfWeek;
    }

    public BlockRate(String startTime, String endTime, double amount) {
        this.startTime = LocalTime.parse(startTime);
        this.endTime   = LocalTime.parse(endTime);
        this.amount    = amount;
        this.isPartThereof = true;
    }

    public BlockRate(String startTime, String endTime, double amount, int blockMins) {
        this(startTime, endTime, amount);
        this.blockMins = blockMins;
    }

    public BlockRate(DayOfWeek startDay, String startTime, DayOfWeek endDay, String endTime, double amount) {
        this(startTime, endTime, amount);
        this.startDay  = startDay;
        this.endDay    = endDay;
    }

    public BlockRate(DayOfWeek startDay, String startTime, DayOfWeek endDay, String endTime, double amount, int blockMins) {
        this(startDay, startTime, endDay, endTime, amount);
        this.blockMins = blockMins;
    }

    public BlockRate(DayOfWeek startDay, String startTime, DayOfWeek endDay, String endTime, double amount, int blockMins, boolean isFirstBlock) {
        this(startDay, startTime, endDay, endTime, amount, blockMins);
        this.isFirstBlock = isFirstBlock;
    }

    public BlockRate(DayOfWeek startDay, String startTime, DayOfWeek endDay, String endTime, double amount, int blockMins, boolean isFirstBlock, boolean isPartThereof) {
        this(startDay, startTime, endDay, endTime, amount, blockMins, isFirstBlock);
        this.isPartThereof = isPartThereof;
    }

    public BlockRate(DayOfWeek startDay, LocalTime startTime, DayOfWeek endDay, LocalTime endTime, double amount, int blockMins, boolean isFirstBlock, boolean isPartThereof) {
        this.startDay = startDay;
        this.startTime = startTime;
        this.endDay = endDay;
        this.endTime = endTime;
        this.amount = amount;
        this.blockMins = blockMins;
        this.isFirstBlock = isFirstBlock;
        this.isPartThereof = isPartThereof;
    }

    public long getTotalMins() {
        this.totalMins = TimeHelper.minutesBetween(startDay, this.startTime, endDay, this.endTime);
        return totalMins;
    }

    public BlockRate copy() {
        return new BlockRate(startDay, startTime, endDay, endTime, amount, blockMins, isFirstBlock, isPartThereof);
    }

    public DayOfWeek getStartDay() {
        return startDay;
    }

    public void setStartDay(DayOfWeek startDay) {
        this.startDay = startDay;
    }

    public DayOfWeek getEndDay() {
        return endDay;
    }

    public void setEndDay(DayOfWeek endDay) {
        this.endDay = endDay;
    }

    public boolean isPerEntry() {
        return this.blockMins == 0;
    }

    public boolean isPartThereof() {
        return isPartThereof;
    }

    public void setPartThereof(boolean partThereof) {
        isPartThereof = partThereof;
    }

    public boolean isTimed() {
        return startTime != null && endTime != null;
    }

    public double getAmount() {
        return amount;
    }

    public BlockRate setAmount(double amount) {
        this.amount = amount;
        return this;
    }

    public int getBlockMins() {
        return blockMins;
    }

    public BlockRate setBlockMins(int blockMins) {
        this.blockMins = blockMins;
        return this;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public void setFirstBlock(boolean firstBlock) {
        isFirstBlock = firstBlock;
    }

    public boolean isFirstBlock() {
        return isFirstBlock;
    }

    public BlockRate setIsFirstBlock(boolean isFirstBlock) {
        this.isFirstBlock = isFirstBlock;
        return this;
    }

    @Override
    public String toString() {
        return "BlockRate{" +
                "startDay=" + startDay +
                ", startTime=" + startTime +
                ", endDay=" + endDay +
                ", endTime=" + endTime +
                ", amount=" + amount +
                ", blockMins=" + blockMins +
                ", isFirstBlock=" + isFirstBlock +
                ", isPartThereof=" + isPartThereof +
                '}';
    }

    @Override
    public int compareTo(BlockRate other) {
        // System.out.println("================");
        // System.out.println(this);
        // System.out.println(other);
        // System.out.println("================");
        // System.out.println();

        if (isFirstBlock) {
            if (other.isFirstBlock) {
                throw new MultipleFirstBlockException();
            }
            return -1;
        } else if (other.isFirstBlock) {
            return 1;
        }

        if (startDay == null) {
            if (other.startDay == null) {
                int startTimeComparison = startTime.compareTo(other.startTime);
                if (startTimeComparison == 0) {
                    throw new OverlappingBlockRatesException("Multiple block rates detected: " + this + " and " + other);
                }
                return startTimeComparison;
            }
            return -1;
        }

        int startDayComparison = startDay.compareTo(other.startDay);
        if (startDayComparison == 0) {
            return startTime.compareTo(other.startTime);
        }
        return startDayComparison;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlockRate blockRate = (BlockRate) o;
        return totalMins == blockRate.totalMins &&
                Double.compare(blockRate.amount, amount) == 0 &&
                blockMins == blockRate.blockMins &&
                startDay == blockRate.startDay &&
                Objects.equals(startTime, blockRate.startTime) &&
                endDay == blockRate.endDay &&
                Objects.equals(endTime, blockRate.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startDay, startTime, endDay, endTime);
    }
}
