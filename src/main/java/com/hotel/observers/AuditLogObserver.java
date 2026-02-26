package com.hotel.observers;

import com.hotel.model.Reservation;
import com.hotel.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.logging.Logger;

/**
 * AuditLogObserver — OBSERVER PATTERN concrete implementation.
 * Writes all reservation events to the audit_log table.
 */
public class AuditLogObserver implements ReservationObserver {

    private static final Logger LOG = Logger.getLogger(AuditLogObserver.class.getName());
    private final DatabaseConnection db = DatabaseConnection.getInstance();

    @Override
    public void onReservationCreated(Reservation r) {
        log(r.getStaffId(), "RESERVATION_CREATED", "reservations", r.getReservationId(),
                "Created: " + r.getReservationNumber() + " for guest: " + r.getGuestName());
    }

    @Override
    public void onReservationCancelled(Reservation r) {
        log(r.getStaffId(), "RESERVATION_CANCELLED", "reservations", r.getReservationId(),
                "Cancelled: " + r.getReservationNumber());
    }

    @Override
    public void onCheckOut(Reservation r) {
        log(r.getStaffId(), "GUEST_CHECKOUT", "reservations", r.getReservationId(),
                "Checkout: " + r.getReservationNumber());
    }

    private void log(int userId, String action, String table, int recordId, String details) {
        String sql = "INSERT INTO audit_log(user_id,action,table_name,record_id,details) VALUES(?,?,?,?,?)";
        try (Connection c = db.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, action);
            ps.setString(3, table);
            ps.setInt(4, recordId);
            ps.setString(5, details);
            ps.executeUpdate();
        } catch (Exception e) {
            LOG.warning("Audit log error: " + e.getMessage());
        }
    }
}