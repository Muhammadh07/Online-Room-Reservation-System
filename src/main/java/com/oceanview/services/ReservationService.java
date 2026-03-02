package com.oceanview.services;

import com.oceanview.dao.*;
import com.oceanview.model.*;

import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

public class ReservationService {
    private static ReservationService instance;
    private final ReservationDAO reservationDAO = new ReservationDAO();
    private final RoomDAO roomDAO = new RoomDAO();
    private final GuestDAO guestDAO = new GuestDAO();
    private final BillDAO billDAO = new BillDAO();

    private ReservationService() {}

    public static synchronized ReservationService getInstance() {
        if (instance == null) instance = new ReservationService();
        return instance;
    }

    public Reservation createReservation(int guestId, int roomId, String checkIn, String checkOut,
                                         int numAdults, int numChildren, String specialRequests,
                                         int userId) throws Exception {
        // Validate dates
        Date ciDate = Date.valueOf(checkIn);
        Date coDate = Date.valueOf(checkOut);
        if (!coDate.after(ciDate))
            throw new Exception("Check-out must be after check-in date.");

        // Fetch the room and verify it exists
        Room room = roomDAO.findById(roomId);
        if (room == null)
            throw new Exception("Selected room not found.");

        // Generate reservation number
        String resNo = "OVR" + System.currentTimeMillis();

        Reservation res = new Reservation();
        res.setReservationNo(resNo);
        res.setGuestId(guestId);
        res.setRoomId(roomId);
        res.setUserId(userId);
        res.setCheckIn(ciDate);
        res.setCheckOut(coDate);
        res.setNumAdults(numAdults);
        res.setNumChildren(numChildren);
        res.setSpecialRequests(specialRequests);
        res.setStatus("CONFIRMED");

        reservationDAO.save(res);
        long nights = res.getNights();
        double roomTotal = room.getBasePrice() * nights;
        double tax = roomTotal * 0.10; // 10% tax
        double total = roomTotal + tax;

        Bill bill = new Bill();
        bill.setReservationId(res.getReservationId());
        bill.setTotalAmount(total);
        bill.setTaxAmount(tax);
        bill.setDiscount(0);
        bill.setBalanceDue(total);
        billDAO.save(bill);

        return reservationDAO.findById(res.getReservationId());
    }

    public void cancelReservation(String reservationNo) throws Exception {
        Reservation res = reservationDAO.findByReservationNo(reservationNo);
        if (res == null || "CANCELLED".equals(res.getStatus()))
            throw new Exception("Invalid or already cancelled reservation.");

        Bill bill = billDAO.findByReservationId(res.getReservationId());
        if (bill != null && bill.getBalanceDue() > 0)
            throw new Exception("Cannot cancel - outstanding payment balance of $" +
                    String.format("%.2f", bill.getBalanceDue()));

        reservationDAO.updateStatus(res.getReservationId(), "CANCELLED");
        roomDAO.setAvailable(res.getRoomId(), true);
    }

    public List<Reservation> getAllReservations() throws SQLException {
        return reservationDAO.findAll();
    }

    public List<Reservation> searchReservations(String keyword) throws SQLException {
        return reservationDAO.search(keyword);
    }

    public Reservation getByReservationNo(String no) throws SQLException {
        return reservationDAO.findByReservationNo(no);
    }

    public Reservation getById(int id) throws SQLException {
        return reservationDAO.findById(id);
    }
}