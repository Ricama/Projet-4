package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {
    //calcule le prix selon le temps de stationnement récupére le ticket et le nombre de venu de ce client en paramétre

    /**
     * calculates the price according to the parking time
     * @param ticket Vehicle entry ticket
     * @param discount number of entries for this vehicle in the car park
     */
    public void calculateFare(Ticket ticket, int discount){

        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

        int inHour = (int) ticket.getInTime().getTime();
        int outHour = (int) ticket.getOutTime().getTime();
        double freeTime = (outHour - inHour) / 3.6e+6 ;
        double duration = freeTime - 0.5;

        if (discount > 1){
            duration = duration / 1.05;
        }
        if (duration < 0 ){
            duration = 0;
        }

        switch (ticket.getParkingSpot().getParkingType()){
            case CAR: {
                ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR);
                break;
            }
            case BIKE: {
                ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR);
                break;
            }
            default: throw new IllegalArgumentException("Unkown Parking Type");
        }
    }
}