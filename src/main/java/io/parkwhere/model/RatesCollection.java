package io.parkwhere.model;

import io.parkwhere.exceptions.MultipleFirstBlockException;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

public class RatesCollection implements Iterable<BlockRate> {

    private BlockRate firstBlockRate;

    private List<BlockRate> blockRates;

    public RatesCollection(List<BlockRate> blockRates) {
        for (BlockRate rate : blockRates) {
            if (rate.isFirstBlock()) {
                if (firstBlockRate != null) {
                    throw new MultipleFirstBlockException();
                }
                firstBlockRate = rate;
            }
        }
        this.blockRates = blockRates;
    }

    public RatesCollection(BlockRate... blockRates) {
        this(Arrays.asList(blockRates));
    }

    public List<BlockRate> getBlockRates() {
        return blockRates;
    }

    public void setBlockRates(List<BlockRate> blockRates) {
        this.blockRates = blockRates;
    }

    public BlockRate getFirstBlockRate() {
        return firstBlockRate;
    }

    public void setFirstBlockRate(BlockRate firstBlockRate) {
        this.firstBlockRate = firstBlockRate;
    }

    @Override
    public Iterator<BlockRate> iterator() {
        return blockRates.iterator();
    }

    @Override
    public void forEach(Consumer<? super BlockRate> action) {
        blockRates.forEach(action);
    }

    @Override
    public Spliterator<BlockRate> spliterator() {
        return blockRates.spliterator();
    }
}
