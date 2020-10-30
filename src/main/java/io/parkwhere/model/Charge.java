package io.parkwhere.model;

import java.time.LocalDateTime;

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
        return "Charge{" +
                "blockRate=" + blockRate +
                ", from=" + from +
                ", to=" + to +
                ", numBlocks=" + numBlocks +
                '}';
    }

}
