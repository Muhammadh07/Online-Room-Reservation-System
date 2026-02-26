package com.hotel.services;

import com.hotel.dao.*;
import com.hotel.model.*;
import com.hotel.observers.*;
import com.hotel.util.ValidationUtil;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ReservationService — SINGLETON + FACADE + OBSERVER PATTERN
 *
 * Design Pattern Evidence:
 *   - Singleton:  one shared service instance across all servlets.
 *   - Facade:     hides DAO complexity; servlets call simple high-level methods.
 *   - Observer:   maintains a list of observers; notifies on reservation events.
 */
public class ReservationService {

    private static volatile ReservationService instance;
    private final ReservationDAO reservationDAO;
    private final BillDAO        billDAO;

    // Observer list — OBSERVER PATTERN
    private final List<ReservationObserver> observers = new ArrayList<>();

    private ReservationService() {
        this.reservationDAO = new ReservationDAOImpl();
        this.billDAO        = new BillDAOImpl();
        // Register default observers
        observers.add(new EmailNotificationObserver());
        observers.add(new AuditLogObserver());
    }

    public static ReservationService getInstance() {
        if (instance == null) {
            synchronized (ReservationService.class) {
                if (instance == null) instance = new ReservationService();
            }
        }
        return instance;
    }

    public void addObserver(ReservationObserver o)    { observers.add(o); }
    public void removeObserver(ReservationObserver o) { observers.remove(o); }

    // ---- Notify helpers ----
    private void notifyCreated(Reservation r)    { observers.forEach(o -> o.onReservationCreated(r)); }
    private void notifyCancelled(Reservation r)  { observers.forEach(o -> o.onReservationCancelled(r)); }
    private void notifyCheckOut(Reservation r)   { observers.forEach(o -> o.onCheckOut(r)); }

    // ================================================================
    // RESERVATION OPERATIONS
    // ================================================================

    public String addReservation(String guestName, String contact, String address,
                                 String email, int roomId, int staffId,
                                 String checkinStr, String checkoutStr) throws Exception {
        if (!ValidationUtil.isNotEmpty(guestName)) throw new Exception("Guest name is required");
        if (!ValidationUtil.isValidPhone(contact)) throw new Exception("Valid contact number is required");
        if (!ValidationUtil.isNotEmpty(checkinStr))  throw new Exception("Check-in date is required");
        if (!ValidationUtil.isNotEmpty(checkoutStr)) throw new Exception("Check-out date is required");
        if (!ValidationUtil.isValidDate(checkinStr))  throw new Exception("Invalid check-in date format");
        if (!ValidationUtil.isValidDate(checkoutStr)) throw new Exception("Invalid check-out date format");
        if (!ValidationUtil.isFutureOrToday(checkinStr)) throw new Exception("Check-in date cannot be in the past");
        if (!ValidationUtil.isCheckoutAfterCheckin(checkinStr, checkoutStr))
            throw new Exception("Check-out must be after check-in");
        if (roomId <= 0) throw new Exception("Please select a valid room");

        Date checkin  = Date.valueOf(checkinStr);
        Date checkout = Date.valueOf(checkoutStr);

        Guest guest = reservationDAO.findOrCreateGuest(
                ValidationUtil.sanitize(guestName), contact.trim(),
                ValidationUtil.sanitize(address), email);
        if (guest == null) throw new Exception("Failed to process guest information");

        String resNumber = reservationDAO.addReservation(guest.getGuestId(), roomId, staffId, checkin, checkout);

        // Notify observers
        Reservation created = reservationDAO.findByNumber(resNumber);
        if (created != null) notifyCreated(created);

        return resNumber;
    }

    public List<Room> getAvailableRooms(String roomType, String checkinStr, String checkoutStr) throws Exception {
        if (!ValidationUtil.isNotEmpty(checkinStr) || !ValidationUtil.isNotEmpty(checkoutStr))
            throw new Exception("Both dates are required");
        if (!ValidationUtil.isValidDate(checkinStr))  throw new Exception("Invalid check-in date");
        if (!ValidationUtil.isValidDate(checkoutStr)) throw new Exception("Invalid check-out date");
        if (!ValidationUtil.isCheckoutAfterCheckin(checkinStr, checkoutStr))
            throw new Exception("Check-out must be after check-in");
        return reservationDAO.getAvailableRooms(
                roomType == null ? "" : roomType,
                Date.valueOf(checkinStr), Date.valueOf(checkoutStr));
    }

    public Reservation getReservation(String number) throws Exception {
        if (!ValidationUtil.isNotEmpty(number)) throw new Exception("Reservation number is required");
        Reservation r = reservationDAO.findByNumber(number.trim().toUpperCase());
        if (r == null) throw new Exception("Reservation not found: " + number);
        return r;
    }

    public List<Reservation> getAllReservations()              { return reservationDAO.findAll(); }
    public List<Reservation> searchByGuest(String name)       { return reservationDAO.findByGuestName(name); }
    public List<Reservation> findByStatus(String status)      { return reservationDAO.findByStatus(status); }
    public List<Reservation> recentReservations(int limit)    { return reservationDAO.findRecentReservations(limit); }

    public boolean cancelReservation(String number) throws Exception {
        Reservation r = getReservation(number);
        if ("CHECKED_IN".equals(r.getStatus()))  throw new Exception("Cannot cancel a checked-in guest");
        if ("CHECKED_OUT".equals(r.getStatus())) throw new Exception("Reservation already completed");
        if ("CANCELLED".equals(r.getStatus()))   throw new Exception("Already cancelled");
        boolean ok = reservationDAO.cancelReservation(number);
        if (ok) notifyCancelled(r);
        return ok;
    }

    public boolean checkIn(String number) throws Exception {
        Reservation r = getReservation(number);
        if (!"CONFIRMED".equals(r.getStatus()))
            throw new Exception("Only CONFIRMED reservations can be checked in");
        return reservationDAO.checkIn(number);
    }

    // ================================================================
    // BILL OPERATIONS
    // ================================================================

    public Bill generateBill(String number) throws Exception {
        Reservation r = getReservation(number);
        if ("CANCELLED".equals(r.getStatus())) throw new Exception("Cannot bill a cancelled reservation");
        Bill bill = billDAO.generateBill(r.getReservationId());
        reservationDAO.checkOut(number);
        notifyCheckOut(r);
        return bill;
    }

    public boolean recordPayment(int billId, double amount, String method) throws Exception {
        if (amount <= 0)                       throw new Exception("Payment amount must be positive");
        if (!ValidationUtil.isNotEmpty(method)) throw new Exception("Payment method is required");
        return billDAO.recordPayment(billId, amount, method);
    }

    public List<Bill>        getAllBills()       { return billDAO.findAll(); }
    public Map<String,Object> getRevenueSummary(){ return billDAO.getRevenueSummary(); }
    public Bill getBillByNumber(String n)        { return billDAO.findByBillNumber(n); }
}