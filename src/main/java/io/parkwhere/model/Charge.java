package io.parkwhere.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Charge {
    private BlockRate blockRate;
    private LocalDateTime from;
    private LocalDateTime to;
    private int numBlocks;

    public Charge(BlockRate blockRate, int numBlocks, LocalDateTime from, LocalDateTime to) {
        this.blockRate = blockRate;
        this.from = from;
        this.to = to;
        this.numBlocks = numBlocks;
    }

    public double getAmount() {
        return this.blockRate.getAmount() * this.numBlocks;
    }

    @Override
    public String toString() {
        DateTimeFormatter f = DateTimeFormatter.ofPattern("EEE MM/dd HH:mm");
        String blockChargeString = (blockRate.isPerEntry())
            ? "$" + blockRate.getAmount() + " per entry"
            : "$" + blockRate.getAmount() + " per " + blockRate.getBlockMins() + "m"
        ;
        return "Charge{" +
                from.format(f) +
                " to " + to.format(f) + " - " +
                blockChargeString + " " +
                "= $" + getAmount() + " " +
                "(" + numBlocks + " blocks)" +
                "}";
    }

}
