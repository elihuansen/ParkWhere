package io.parkwhere.domain;

import io.parkwhere.model.BlockRate;
import io.parkwhere.model.RatesCollection;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class BlockRatesOrderingTest {
    @Test
    @DisplayName("Check BlockRates are ordered correctly when created in order")
    void checkBlockRatesOrdering1() {
        BlockRate[] blockRates = new BlockRate[]{
            new BlockRate(true, 2.0, 60),
            new BlockRate("00:00", "07:59", 1.2, 30),
            new BlockRate("08:00", "17:59", 1.4, 30),
            new BlockRate("18:00", "23:59", 3)
        };
        RatesCollection ratesCollection = new RatesCollection().addWeekdayBlockRates(blockRates);
        List<BlockRate> orderedBlockRates = ratesCollection.getBlockRatesList();
        BlockRate[] expected = blockRates;
        BlockRate[] actual = orderedBlockRates.toArray(new BlockRate[0]);
        assertArrayEquals(expected, actual);
    }

    @Test
    @DisplayName("Check BlockRates are ordered correctly when not created in order 1")
    void checkBlockRatesOrdering2() {
        BlockRate[] blockRates = new BlockRate[]{
                new BlockRate("18:00", "23:59", 3),
                new BlockRate("00:00", "07:59", 1.2, 30),
                new BlockRate(true, 2.0, 60),
                new BlockRate("08:00", "17:59", 1.4, 30),
        };
        RatesCollection ratesCollection = new RatesCollection().addWeekdayBlockRates(blockRates);
        List<BlockRate> orderedBlockRates = ratesCollection.getBlockRatesList();

        BlockRate[] expected = new BlockRate[]{
                new BlockRate(true, 2.0, 60),
                new BlockRate("00:00", "07:59", 1.2, 30),
                new BlockRate("08:00", "17:59", 1.4, 30),
                new BlockRate("18:00", "23:59", 3),
        };
        BlockRate[] actual = orderedBlockRates.toArray(new BlockRate[0]);
        assertArrayEquals(expected, actual);
    }

    @Test
    @DisplayName("Check BlockRates are ordered correctly when not created in order 2")
    void checkBlockRatesOrdering3() {
        BlockRate[] blockRates = new BlockRate[]{
            new BlockRate("23:51", "03:27", 40),
            new BlockRate("05:00", "07:27", 1.4, 2),
            new BlockRate("07:28", "08:08", 0.24, 3),
            new BlockRate("18:54", "23:50", 4.08),
            new BlockRate("03:28", "04:53", 1.4, 12),
            new BlockRate("08:09", "12:33", 0.1, 1),
            new BlockRate("12:34", "18:53", 0.33, 12),
            new BlockRate(true, 1, 43),
            new BlockRate("04:54", "04:59", 1.8, 12),
        };
        RatesCollection ratesCollection = new RatesCollection().addWeekdayBlockRates(blockRates);
        List<BlockRate> orderedBlockRates = ratesCollection.getBlockRatesList();

        BlockRate[] expected = new BlockRate[]{
            new BlockRate(true, 1, 43),
            new BlockRate("03:28", "04:53", 1.4, 12),
            new BlockRate("04:54", "04:59", 1.8, 12),
            new BlockRate("05:00", "07:27", 1.4, 2),
            new BlockRate("07:28", "08:08", 0.24, 3),
            new BlockRate("08:09", "12:33", 0.1, 1),
            new BlockRate("12:34", "18:53", 0.33, 12),
            new BlockRate("18:54", "23:50", 4.08),
            new BlockRate("23:51", "03:27", 40),
        };
        BlockRate[] actual = orderedBlockRates.toArray(new BlockRate[0]);
        assertArrayEquals(expected, actual);
    }
}
