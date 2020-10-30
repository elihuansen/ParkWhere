package io.parkwhere.controllers;

import io.parkwhere.model.BlockRate;
import io.parkwhere.model.Carpark;
import io.parkwhere.model.Charge;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import static java.time.temporal.ChronoUnit.MINUTES;

import static io.parkwhere.utils.TimeHelper.isBetweenInclusive;

public class CarparkController {

    public CarparkController() {}

    public double calculate(Carpark carpark, String entranceTime, String exitTime) {

        LocalDateTime now = LocalDateTime.now();
        int entranceHour = Integer.parseInt(entranceTime.substring(0, 2));
        int entranceMin  = Integer.parseInt(entranceTime.substring(3));
        int exitHour     = Integer.parseInt(exitTime.substring(0, 2));
        int exitMin      = Integer.parseInt(exitTime.substring(3));

        LocalDateTime entranceDateTime = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), entranceHour, entranceMin);
        LocalDateTime exitDateTime = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), exitHour, exitMin);

        if (exitHour < entranceHour) {
            exitDateTime = exitDateTime.plusDays(1);
        }

        return calculate(carpark, entranceDateTime, exitDateTime);
    }

    public double calculate(Carpark carpark, LocalDateTime entranceDateTime, LocalDateTime exitDateTime) {
        List<Charge> charges = getCharges(carpark, entranceDateTime, exitDateTime);

        System.out.println("Entrance => " + entranceDateTime);
        System.out.println("Exit     => " + exitDateTime);
        double totalCharge = 0;
        int i = 0;
        for (Charge charge : charges) {
            System.out.println(++i + ". " + charge);
            totalCharge += charge.getAmount();
        }

        return totalCharge;
    }

    private List<Charge> getCharges(Carpark carpark, LocalDateTime entranceDateTime, LocalDateTime exitDateTime) {
        List<Charge> charges = new ArrayList<>();
        LocalDateTime currentDateTime = LocalDateTime.from(entranceDateTime);

        BlockRate firstBlock = carpark.getRatesCollection().getFirstBlockRate();
        if (firstBlock != null) {
            int blockMins = firstBlock.getBlockMins();
            charges.add(new Charge(firstBlock, 1, currentDateTime, LocalDateTime.from(currentDateTime.plusMinutes(blockMins))));
            currentDateTime = currentDateTime.plusMinutes(blockMins + 1);
        }

        while (currentDateTime.isBefore(exitDateTime)) {

            for (BlockRate rate : carpark.getRatesCollection()) {
                int blockMins = rate.getBlockMins();

                if (rate.isTimed()) {

                    LocalTime rateStartTime = rate.getStartTime();
                    LocalTime rateEndTime = rate.getEndTime();

                    boolean isOvernightRate = rateEndTime.isBefore(rateStartTime);
                    boolean isWithinRateTiming =
                        isBetweenInclusive(rateStartTime, currentDateTime.toLocalTime(), rateEndTime) ||
                        (
                            isOvernightRate &&
                            (rateEndTime.getHour() * 60 + rateEndTime.getMinute() < currentDateTime.getHour() * 60 + currentDateTime.getMinute())
                        )
                    ;

                    if (isWithinRateTiming) {

                        LocalTime exitTime = exitDateTime.toLocalTime();
                        LocalTime chargeEndTime;
                        if (currentDateTime.getDayOfYear() < exitDateTime.getDayOfYear()) {
                            chargeEndTime = rateEndTime;
                        } else {
                            if (isOvernightRate) {
                                chargeEndTime = exitTime;
                            } else {
                                chargeEndTime = rateEndTime.isBefore(exitTime) ? rateEndTime : exitTime;
                            }
                        }

                        long numMinsInBlock;
                        if (isOvernightRate && currentDateTime.getDayOfYear() < exitDateTime.getDayOfYear()) {
                            LocalDateTime nextDay = LocalDateTime.of(currentDateTime.getYear(), currentDateTime.getMonth(), currentDateTime.getDayOfMonth(), chargeEndTime.getHour(), chargeEndTime.getMinute()).plusDays(1);
                            numMinsInBlock = MINUTES.between(currentDateTime, nextDay);
                        } else {
                            numMinsInBlock = MINUTES.between(currentDateTime.toLocalTime(), chargeEndTime);
                        }

                        int numBlocks;
                        if (rate.isPerEntry()) {
                            numBlocks = 1;
                        } else {
                            numBlocks = (int) numMinsInBlock / blockMins;
                            boolean hasPartialBlock = numMinsInBlock % blockMins > 0;
                            if (rate.isPartThereof() && hasPartialBlock) {
                                numBlocks++;
                            }
                        }
                        charges.add(new Charge(rate, numBlocks, currentDateTime, currentDateTime.plusMinutes(numMinsInBlock)));

                        currentDateTime = currentDateTime.plusMinutes(numMinsInBlock + 1);
                        if (currentDateTime.isEqual(exitDateTime) || currentDateTime.isAfter(exitDateTime)) {
                            return charges;
                        }
                    }
                }
            }
        }

        return charges;
    }

//    private List<Charge> getCharges(Carpark carpark, TimeOfDay entranceTime, TimeOfDay exitTime) {
//        List<Charge> charges = new ArrayList<>();
//
//        entranceTime = entranceTime.copy();
//
//        for (BlockRate rate : carpark.getRatesCollection()) {
//
//            int blockMins = rate.getBlockMins();
//
//            if (rate.isFirstBlock()) {
//
//                charges.add(new Charge(rate, 1, entranceTime, new TimeOfDay(entranceTime).addMins(blockMins)));
//                // Move entrance time up to prevent overlap / double-counting
//                entranceTime.addMins(blockMins + 1);
//
//            } else if (rate.isTimed()) {
//
//                TimeOfDay fromTime = rate.getFromTime();
//                TimeOfDay toTime   = rate.getToTime();
//
//                boolean isOvernightRate = toTime.isBefore(fromTime);
//                if (isOvernightRate) {
//                    toTime.addHours(24);
//                }
//
//                boolean entranceWithinRateTiming =
//                    fromTime.isBeforeEq(entranceTime) && entranceTime.isBeforeEq(toTime) ||
//                    fromTime.isBeforeEq(exitTime) && exitTime.isBeforeEq(toTime);
//
//                if (entranceWithinRateTiming) {
//
//                    TimeOfDay chargeEndTime = toTime.isBefore(exitTime) ? toTime : exitTime;
//                    int numMinsInBlock = chargeEndTime.getDiffInMins(entranceTime);
//
//                    if (rate.isPerEntry()) {
//                        charges.add(new Charge(rate, 1, entranceTime, chargeEndTime));
//                    } else {
//                        int numBlocks = numMinsInBlock / blockMins;
//                        boolean hasPartialBlock = numMinsInBlock % blockMins > 0;
//                        if (rate.isPartThereof() && hasPartialBlock) {
//                            numBlocks++;
//                        }
//                        charges.add(new Charge(rate, numBlocks, entranceTime, chargeEndTime));
//                    }
//                    entranceTime.addMins(numMinsInBlock + 1);
//                }
//            }
//        }
//
//        return charges;
//    }
}
