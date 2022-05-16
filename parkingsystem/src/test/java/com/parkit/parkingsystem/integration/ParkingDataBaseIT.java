package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    private static final DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;

    @Mock
    private static InputReaderUtil inputReaderUtil;


    @BeforeAll
    private static void setUp(){
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
        dataBasePrepareService.clearDataBaseEntries();
    }

    @BeforeEach
    private void setUpPerTest() throws Exception {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

    }

    @AfterAll
    private static void tearDown(){

    }
    public int discount(String vehicleRegNumber){
        Connection con = null;
        int result= 0;
        try {
            con = dataBaseTestConfig.getConnection();
            PreparedStatement ps = con.prepareStatement(DBConstants.GET_DISCOUNT);
            ps.setString(1, vehicleRegNumber);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                result = rs.getInt(1);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Test
    public void testParkingACar(){

        //Given
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

        //when
        parkingService.processIncomingVehicle();

        //then
        final Ticket ticket = ticketDAO.getTicket("ABCDEF");
        final int parkingSpot = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);

        assertAll(
                ()  -> assertNotNull(ticket),
                ()  -> assertEquals("ABCDEF", ticket.getVehicleRegNumber()),
                ()  -> assertEquals(2,parkingSpot)
        );

    }

    @Test
    public void testParkingLotExit() throws InterruptedException {

        testParkingACar();
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        Thread.sleep(500);
        parkingService.processExitingVehicle();
        final Ticket ticket = ticketDAO.getTicket("ABCDEF");
        assertAll(
                () -> assertNotNull(ticket.getOutTime(), "OutTime"),
                () -> assertEquals("ABCDEF", ticket.getVehicleRegNumber(),"vehicleNumber"),
                () -> assertEquals(0,ticket.getPrice(), "price")
        );
    }

    @Test
    public void testDiscountPark() throws InterruptedException{
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

        parkingService.processIncomingVehicle();
        Thread.sleep(500);
        parkingService.processExitingVehicle();
        final Ticket ticket0 = ticketDAO.getTicket("ABCDEF");
        final int discount0 = discount("ABCDEF");
        parkingService.processIncomingVehicle();
        Thread.sleep(500);
        parkingService.processExitingVehicle();
        final Ticket ticket = ticketDAO.getTicket("ABCDEF");
        final int discount = discount("ABCDEF");
        assertAll(
                () -> assertEquals("ABCDEF", ticket0.getVehicleRegNumber(),"vehicleNumber"),
                () -> assertEquals("ABCDEF", ticket.getVehicleRegNumber(),"vehicleNumber"),
                () -> assertEquals(1,discount0, "discount"),
                () -> assertEquals(2,discount, "discount")
        );
    }
 }
