package io.parkwhere.domain;

import io.parkwhere.controllers.CarparkController;
import io.parkwhere.model.BlockRate;
import io.parkwhere.model.Carpark;
import io.parkwhere.model.RatesCollection;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Carpark Intraday Charges Test")
public class CarparkIntradayChargesTest {
    @Test
    @DisplayName("Check that the correct charge is calculated for a mix of different rates 1")
    void checkCarparkCharge1() {
        RatesCollection ratesCollection = new RatesCollection()
            .addWeekdayBlockRates(
                new BlockRate(true, 2.0, 60),
                new BlockRate("00:00", "07:59", 1.2, 30),
                new BlockRate("08:00", "17:59", 1.4, 30),
                new BlockRate("18:00", "23:59", 3)
            );

        Carpark carpark = new Carpark(ratesCollection, "Carpark 1", "1 A Road", "...");

        CarparkController carparkController = new CarparkController();
        double result = carparkController.calculate(carpark, "12:00", "19:30");
        double expected =
            2.0 +      // first block of 60m - 1200 to 1300
            1.4 * 10 + // $1.40 for 10 blocks of 30m - 1300 to 1759, 1730 to 1759 included
            3          // per entry - 1800 to 1930
        ;

        Assertions.assertEquals(expected, result);
    }

    @Test
    @DisplayName("Check that the correct charge is calculated for a mix of different rates 2")
    void checkCarparkCharge2() {
        RatesCollection ratesCollection = new RatesCollection()
            .addWeekdayBlockRates(
                new BlockRate(true, 5, 120),
                new BlockRate("08:00", "07:59", 0.1, 1)
            );

        Carpark carpark = new Carpark(ratesCollection, "Carpark 1", "1 A Road", "...");

        CarparkController carparkController = new CarparkController();
        double result = carparkController.calculate(carpark, "12:00", "19:30");
        double expected = 5 + 32.9;

        Assertions.assertEquals(expected, result);
    }

    @Test
    @DisplayName("Check that the correct charge is calculated for a mix of different convoluted rates")
    void checkCarparkChargeConvulatedRates() {
        RatesCollection ratesCollection = new RatesCollection()
            .addWeekdayBlockRates(
                new BlockRate(true, 1, 43),
                new BlockRate("07:28", "08:08", 0.24, 3),
                new BlockRate("08:09", "12:33", 0.1, 1),
                new BlockRate("12:34", "18:53", 0.33, 12),
                new BlockRate("18:54", "23:50", 4.08),
                new BlockRate("23:51", "07:27", 40)
            );
        Carpark carpark = new Carpark(ratesCollection, "Carpark 1", "1 A Road", "...");

        CarparkController carparkController = new CarparkController();
        double result = carparkController.calculate(carpark, "08:05", "23:53");
        double expected =
            1 + // first block of 60m - 0805 to 0848
            0.1 * 224 + // $0.10 for 224 blocks of 1m - 0849 to 1233
            0.33 * 32 + // $0.33 for 32 blocks (1 partial) of 12m - 1243 to 1853
            4.08 + // $4.08 per entry - 1854 to 2350
            40 // $40 per entry - 2351 to 2353
        ;

        Assertions.assertEquals(expected, result);
    }

    @Test
    @DisplayName("Check that the charge for a carpark with whole day parking is correct")
    void checkCorrectChargeIsCalculatedForSimpleCarpark() {
        RatesCollection ratesCollection = new RatesCollection()
            .addWeekdayBlockRates(
                new BlockRate("06:00", "05:59", 2.4)
            );

        Carpark carpark = new Carpark(ratesCollection, "Carpark 1", "1 A Road", "...");
        CarparkController carparkController = new CarparkController();
        double result = carparkController.calculate(carpark, "12:00", "19:30");
        double expected = 2.4;

        Assertions.assertEquals(expected, result);
    }
}
