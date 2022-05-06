package com.parkit.parkingsystem.dao;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;



import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class TicketDAOTest {
    private final static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static TicketDAO ticketDAO;
    Ticket test = new Ticket();
    String vehicleRegNumber = "ABCD";
    Date inTime = new Date();
    @BeforeAll
    private static void setUp(){
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = new DataBaseTestConfig();

    }
    @AfterAll
    private static void after(){

    }

    @Test
    public void saveTicketTest(){
        boolean isAvailable = true;
        ParkingSpot testp = new ParkingSpot(1, ParkingType.CAR,isAvailable);
        test.setParkingSpot(testp);
        test.setVehicleRegNumber(vehicleRegNumber);
        test.setPrice(0);
        test.setInTime(inTime);
        test.setOutTime(null);
        boolean result = ticketDAO.saveTicket(test);
        assertFalse(result);
    }

    @Test
    public void getTicketTest(){
        Ticket result = ticketDAO.getTicket(vehicleRegNumber);

        assertNotNull(result);
    }

    @Test
   public void updateTicketTest() throws InterruptedException {
        Thread.sleep(500);
        test.setOutTime(inTime);
        boolean result = ticketDAO.updateTicket(test);
        assertTrue(result);
   }
}