package io.parkwhere.model;

import java.time.LocalTime;

public class BlockRate {
    private LocalTime startTime;
    private LocalTime endTime;
    private double amount;
    private int blockMins;
    private boolean isFirstBlock;
    private boolean isPartThereof;

    public BlockRate() {}

    public BlockRate(boolean isFirstBlock, double amount, int blockMins) {
        this.isFirstBlock = isFirstBlock;
        this.amount = amount;
        this.blockMins = blockMins;
    }

    public BlockRate(String startTime, String endTime, double amount) {
        this.startTime = LocalTime.parse(startTime);
        this.endTime   = LocalTime.parse(endTime);
        this.amount   = amount;
    }

    public BlockRate(String startTime, String endTime, double amount, int blockMins) {
        this(startTime, endTime, amount);
        this.blockMins = blockMins;
    }

    public BlockRate(String startTime, String endTime, double amount, int blockMins, boolean isFirstBlock) {
        this(startTime, endTime, amount, blockMins);
        this.isFirstBlock = isFirstBlock;
    }

    public boolean isPerEntry() {
        return this.blockMins == 0;
    }

    public boolean isPartThereof() {
        return true;
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
                "fromTime=" + startTime +
                ", toTime=" + endTime +
                ", amount=" + amount +
                ", blockMins=" + blockMins +
                ", isFirstBlock=" + isFirstBlock +
                '}';
    }
}
