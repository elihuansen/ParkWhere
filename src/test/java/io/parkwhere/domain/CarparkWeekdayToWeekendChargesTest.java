package io.parkwhere.domain;

import io.parkwhere.controllers.CarparkController;
import io.parkwhere.model.BlockRate;
import io.parkwhere.model.Carpark;
import io.parkwhere.model.RatesCollection;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Carpark Weekday To Weekend Charges Test")
public class CarparkWeekdayToWeekendChargesTest {

    @Test
    @DisplayName("Check weekday to weekend charge is correct 1")
    public void checkWeekdayToWeekendChargeIsCorrect1() {
        // From friday to mon
        LocalDateTime SEP_4_FRI_11AM = LocalDateTime.of(2020, 9, 4, 11, 0);
        LocalDateTime SEP_6_MON_9PM  = LocalDateTime.of(2020, 9, 7, 21, 0);

        RatesCollection ratesCollection = new RatesCollection()
            .addWeekdayBlockRates(
                new BlockRate(true, 5, 120),
                new BlockRate("04:01", "07:59", 3),
                new BlockRate("08:00", "04:00", 1.1, 15)
            )
            .addSatBlockRates(
                new BlockRate(true, 3.9, 60),
                new BlockRate("06:00", "11:59", 1.4, 30),
                new BlockRate("12:00", "18:59", 1.4, 60),
                new BlockRate("19:00", "06:59", 1.4, 60)
            )
            .addSunBlockRates(
                new BlockRate("07:00", "06:59", 2)
            );

        Carpark carpark = new Carpark(1, ratesCollection, "Carpark 1", "1 A Road", "...");
        CarparkController carparkController = new CarparkController();
        double result = carparkController.calculate(carpark, SEP_4_FRI_11AM, SEP_6_MON_9PM);
        double expected =
            5.0 * 1 +  // Fri 09/04 11:00 to Fri 09/04 13:00 - $5.0 per 120m = $5.0 (1 blocks)
            1.1 * 60 + // Fri 09/04 13:01 to Sat 09/05 04:00 - $1.1 per 15m = $66.0 (60 blocks)
            1.4 * 16 + // Sat 09/05 04:01 to Sat 09/05 11:59 - $1.4 per 30m = $22.4 (16 blocks)
            1.4 * 7 +  // Sat 09/05 12:00 to Sat 09/05 18:59 - $1.4 per 60m = $9.8 (7 blocks)
            1.4 * 12 + // Sat 09/05 19:00 to Sun 09/06 06:59 - $1.4 per 60m = $16.8 (12 blocks)
            2.0 * 1 +  // Sun 09/06 07:00 to Mon 09/07 06:59 - $2.0 per entry = $2.0 (1 blocks)
            3.0 * 1 +  // Mon 09/07 07:00 to Mon 09/07 07:59 - $3.0 per entry = $3.0 (1 blocks)
            1.1 * 52   // Mon 09/07 08:00 to Mon 09/07 21:00 - $1.1 per 15m = $57.2 (52 blocks)
        ;
        assertEquals(expected, result);
    }
}
