package com.parkit.parkingsystem.dao;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


class ParkingSpotDAOTest {
    ParkingSpotDAO parkingSpotDAO = new ParkingSpotDAO();
    boolean isAvailable = true;
    ParkingSpot test = new ParkingSpot(1,ParkingType.CAR,isAvailable);
    @Test
public void setAvailableTest(){
       int result = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);
        assertEquals(1, result);
        }
        @Test
public void updateParkingTest(){
    boolean result = parkingSpotDAO.updateParking(test);
    assertTrue(result);
}
}