package com.hotel.observers;

import com.hotel.model.Reservation;

/**
 * ReservationObserver — OBSERVER PATTERN interface.
 * Any class that needs to react to reservation events implements this.
 */
public interface ReservationObserver {
    void onReservationCreated(Reservation reservation);
    void onReservationCancelled(Reservation reservation);
    void onCheckOut(Reservation reservation);
}