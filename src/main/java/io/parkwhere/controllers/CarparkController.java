package io.parkwhere.controllers;

import io.parkwhere.model.Carpark;
import io.parkwhere.model.Charge;

import java.time.LocalDateTime;
import java.util.List;

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
        return carpark.getRatesCollection().calculateCharges(entranceDateTime, exitDateTime);
    }
}
