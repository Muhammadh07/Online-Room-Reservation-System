package com.hotel.dao;

import com.hotel.model.Guest;
import com.hotel.model.Reservation;
import com.hotel.model.Room;

import java.sql.Date;
import java.util.List;

public interface ReservationDAO {
    String            addReservation(int guestId, int roomId, int staffId, Date checkin, Date checkout) throws Exception;
    Reservation       findByNumber(String reservationNumber);
    List<Reservation> findAll();
    List<Reservation> findByGuestName(String name);
    List<Reservation> findByStatus(String status);
    List<Room>        getAvailableRooms(String roomType, Date checkin, Date checkout);
    boolean           cancelReservation(String reservationNumber);
    boolean           checkIn(String reservationNumber);
    String            checkOut(String reservationNumber) throws Exception;
    Guest             findOrCreateGuest(String name, String contact, String address, String email);
    List<Reservation> findRecentReservations(int limit);
}