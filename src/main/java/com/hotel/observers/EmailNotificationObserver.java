package com.hotel.observers;

import com.hotel.model.Reservation;
import com.hotel.util.EmailUtil;

import java.util.logging.Logger;

/**
 * EmailNotificationObserver — OBSERVER PATTERN concrete implementation.
 * Sends email notifications when reservation events occur.
 */
public class EmailNotificationObserver implements ReservationObserver {

    private static final Logger LOG = Logger.getLogger(EmailNotificationObserver.class.getName());

    @Override
    public void onReservationCreated(Reservation r) {
        // In production, r.getGuestEmail() would be available; using placeholder
        String subject = "Reservation Confirmed - " + r.getReservationNumber();
        String body = EmailUtil.buildReservationEmail(
                r.getGuestName(), r.getReservationNumber(),
                r.getRoomNumber() + " (" + r.getTypeName() + ")",
                r.getCheckinDate().toString(), r.getCheckoutDate().toString()
        );
        // EmailUtil.send(r.getGuestEmail(), subject, body);
        LOG.info("[EMAIL] Reservation confirmation would be sent for: " + r.getReservationNumber());
    }

    @Override
    public void onReservationCancelled(Reservation r) {
        LOG.info("[EMAIL] Cancellation notice for: " + r.getReservationNumber());
    }

    @Override
    public void onCheckOut(Reservation r) {
        LOG.info("[EMAIL] Checkout receipt for: " + r.getReservationNumber());
    }
}