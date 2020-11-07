package io.parkwhere.domain;

import io.parkwhere.controllers.CarparkController;
import io.parkwhere.model.BlockRate;
import io.parkwhere.model.Carpark;
import io.parkwhere.model.RatesCollection;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;

@DisplayName("Carpark Over-night Charges Test")
public class CarparkOvernightChargesTest {

    @Test
    @DisplayName("Check over-night for a typical carpark is correct 1")
    public void checkOverNightCharge1() {
        RatesCollection ratesCollection = new RatesCollection()
            .addWeekdayBlockRates(
                new BlockRate(true, 5, 120),
                new BlockRate("04:01", "07:59", 3),
                new BlockRate("08:00", "04:00", 20)
            );

        Carpark carpark = new Carpark(ratesCollection, "Carpark 1", "1 A Road", "...");

        CarparkController carparkController = new CarparkController();
        LocalDateTime entranceDateTime = LocalDateTime.of(2020, Month.JANUARY, 15, 12, 30);
        LocalDateTime exitDateTime     = LocalDateTime.of(2020, Month.JANUARY, 16, 4, 0);
        double result = carparkController.calculate(carpark, entranceDateTime, exitDateTime);
        double expected = 5 + 20;

        Assertions.assertEquals(expected, result);
    }

    @DisplayName("Check over-night charge for a typical carpark is correct 2")
    @Test
    public void checkOverNightCharge2() {
        RatesCollection ratesCollection = new RatesCollection()
            .addWeekdayBlockRates(
                new BlockRate(true, 2.0, 60),
                new BlockRate("00:00", "07:59", 1.2, 15),
                new BlockRate("08:00", "17:59", 1.4, 30),
                new BlockRate("18:00", "23:59", 3)
            );

        Carpark carpark = new Carpark(ratesCollection, "Carpark 1", "1 A Road", "...");
        CarparkController carparkController = new CarparkController();

        LocalDateTime entranceDateTime = LocalDateTime.of(2020, Month.JANUARY, 15, 9, 30);
        LocalDateTime exitDateTime     = LocalDateTime.of(2020, Month.JANUARY, 16, 12, 0);
        double result = carparkController.calculate(carpark, entranceDateTime, exitDateTime);
        double expected =
            2.0 +      // 15 Jan 2020 09:30 to 10:30 - $2 first 1h
            1.4 * 15 + // 15 Jan 2020 10:31 to 17:59 - $1.40 per 30m (15 blocks)
            3 +        // 15 Jan 2020 18:00 to 23:59 - $3
            1.2 * 32 + // 16 Jan 2020 00:00 to 07:59 - $1.20 per 15m (32 blocks)
            1.4 * 8    // 16 Jan 2020 08:00 to 12:00 - $1.40 per 30m (8 blocks)
        ;

        Assertions.assertEquals(expected, result);
    }

    @DisplayName("Check overnight charge for a simple carpark is correct")
    @Test
    public void checkSimpleCarparkCharge() {
        RatesCollection ratesCollection = new RatesCollection()
            .addWeekdayBlockRates(
                new BlockRate(true, 2.0, 60),
                new BlockRate("00:00", "23:59", 1.2, 60)
            );

        Carpark carpark = new Carpark(ratesCollection, "Carpark 1", "1 A Road", "...");
        CarparkController carparkController = new CarparkController();

        LocalDateTime entranceDateTime = LocalDateTime.of(2020, Month.JANUARY, 15, 9, 30);
        LocalDateTime exitDateTime     = LocalDateTime.of(2020, Month.JANUARY, 16, 12, 30);
        double result = carparkController.calculate(
            carpark,
            entranceDateTime,
            exitDateTime
        );
        double expected =
            2.0 +     // 15 Jan 2020 09:30 to 10:30 - $2 first 1h
            1.2 * 27  // 15 Jan 2020 10:30 to 16 Jan 2020 9:30 - $1.20 per 60m (26 blocks)
        ;

        Assertions.assertEquals(expected, result);
    }

    @DisplayName("Check charge for a parking session spanning multiple days is correct")
    @Test
    public void checkChargeForCarparkWithOverNightRates() {
        RatesCollection ratesCollection = new RatesCollection()
            .addWeekdayBlockRates(
                new BlockRate(true, 2.0, 60),
                new BlockRate("06:00", "17:59", 1.2, 60),
                new BlockRate("18:00", "05:59", 2.4, 60)
            );

        Carpark carpark = new Carpark(ratesCollection, "Carpark 1", "1 A Road", "...");
        CarparkController carparkController = new CarparkController();

        LocalDateTime entranceDateTime = LocalDateTime.of(2020, Month.JANUARY, 15, 4, 30);
        LocalDateTime exitDateTime     = LocalDateTime.of(2020, Month.JANUARY, 17, 12, 1);
        double result = carparkController.calculate(carpark, entranceDateTime, exitDateTime);
        double expected =
            2 +        // 15 Jan 2020 04:30 to 05:30 - $2 for first 60m (1 block)
            2.4 * 1 +  // 15 Jan 2020 05:31 to 05:59 - $2.40 per 60m (1 block)
            1.2 * 12 + // 15 Jan 2020 06:00 to 17:59 - $1.20 per 60m (12 blocks)
            2.4 * 12 + // 15 Jan 2020 18:00 to 16 Jan 2020 05:59 - $2.40 per 60m (12 blocks)
            1.2 * 12 + // 16 Jan 2020 06:00 to 17:59 - $1.20 per 60m (12 blocks)
            2.4 * 12 + // 16 Jan 2020 18:00 to 17 Jan 2020 05:59 - $2.40 per 60m (12 blocks)
            1.2 * 7    // 17 Jan 2020 06:00 to 12:01 - $1.20 per 60m (7 blocks)
        ;

        Assertions.assertEquals(expected, result);
    }
}
