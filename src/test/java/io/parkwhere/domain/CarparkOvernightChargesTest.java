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

    @DisplayName("Check over-night charge for a typical carpark is correct")
    @Test
    public void checkTypicalCarparkCharge() {
        LocalDateTime entranceDateTime = LocalDateTime.of(2020, Month.JANUARY, 15, 9, 30);
        LocalDateTime exitDateTime     = LocalDateTime.of(2020, Month.JANUARY, 16, 12, 0);

        RatesCollection ratesCollection = new RatesCollection()
            .addBlockRates(
                entranceDateTime.getDayOfWeek(),
                new BlockRate(true, 2.0, 60),
                new BlockRate("00:00", "07:59", 1.2, 15),
                new BlockRate("08:00", "17:59", 1.4, 30),
                new BlockRate("18:00", "23:59", 3)
            )
            .addBlockRates(
                exitDateTime.getDayOfWeek(),
                new BlockRate(true, 2.0, 60),
                new BlockRate("00:00", "07:59", 1.2, 15),
                new BlockRate("08:00", "17:59", 1.4, 30),
                new BlockRate("18:00", "23:59", 3)
            );

        Carpark carpark = new Carpark(ratesCollection, "Carpark 1", "1 A Road", "...");
        CarparkController carparkController = new CarparkController();

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
        LocalDateTime entranceDateTime = LocalDateTime.of(2020, Month.JANUARY, 15, 9, 30);
        LocalDateTime exitDateTime     = LocalDateTime.of(2020, Month.JANUARY, 16, 12, 30);

        RatesCollection ratesCollection = new RatesCollection()
            .addBlockRates(
                entranceDateTime.getDayOfWeek(),
                new BlockRate(true, 2.0, 60),
                new BlockRate("00:00", "23:59", 1.2, 60)
            )
            .addBlockRates(
                exitDateTime.getDayOfWeek(),
                new BlockRate(true, 2.0, 60),
                new BlockRate("00:00", "23:59", 1.2, 60)
            );

        Carpark carpark = new Carpark(ratesCollection, "Carpark 1", "1 A Road", "...");
        CarparkController carparkController = new CarparkController();
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

    @DisplayName("Check over-night charge for a carpark with over-night charges is correct")
    @Test
    public void checkChargeForCarparkWithOverNightRates() {
        LocalDateTime entranceDateTime = LocalDateTime.of(2020, Month.JANUARY, 15, 9, 30);
        LocalDateTime exitDateTime     = LocalDateTime.of(2020, Month.JANUARY, 16, 12, 30);

        RatesCollection ratesCollection = new RatesCollection()
            .addBlockRates(
                entranceDateTime.getDayOfWeek(),
                new BlockRate(true, 2.0, 60),
                new BlockRate("06:00", "17:59", 1.2, 60),
                new BlockRate("18:00", "05:59", 2.4, 60)
            )
            .addBlockRates(
                entranceDateTime.getDayOfWeek().plus(1),
                new BlockRate(true, 2.0, 60),
                new BlockRate("06:00", "17:59", 1.2, 60),
                new BlockRate("18:00", "05:59", 2.4, 60)
            );

        Carpark carpark = new Carpark(ratesCollection, "Carpark 1", "1 A Road", "...");
        CarparkController carparkController = new CarparkController();
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
}
