package com.oceanview.model;

import java.sql.Date;
import java.sql.Timestamp;

public class Reservation {
    private int reservationId;
    private String reservationNo;
    private int guestId;
    private String guestName;
    private String guestPhone;
    private int roomId;
    private String roomNumber;
    private String roomTypeName;
    private double roomPrice;
    private int userId;
    private String createdByName;
    private Date checkIn;
    private Date checkOut;
    private int numAdults;
    private int numChildren;
    private String status;
    private String specialRequests;
    private Timestamp createdAt;

    public Reservation() {}

    public long getNights() {
        if (checkIn != null && checkOut != null) {
            long diff = checkOut.getTime() - checkIn.getTime();
            return diff / (1000 * 60 * 60 * 24);
        }
        return 0;
    }

    public int getReservationId() { return reservationId; }
    public void setReservationId(int reservationId) { this.reservationId = reservationId; }
    public String getReservationNo() { return reservationNo; }
    public void setReservationNo(String reservationNo) { this.reservationNo = reservationNo; }
    public int getGuestId() { return guestId; }
    public void setGuestId(int guestId) { this.guestId = guestId; }
    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }
    public String getGuestPhone() { return guestPhone; }
    public void setGuestPhone(String guestPhone) { this.guestPhone = guestPhone; }
    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }
    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
    public String getRoomTypeName() { return roomTypeName; }
    public void setRoomTypeName(String roomTypeName) { this.roomTypeName = roomTypeName; }
    public double getRoomPrice() { return roomPrice; }
    public void setRoomPrice(double roomPrice) { this.roomPrice = roomPrice; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getCreatedByName() { return createdByName; }
    public void setCreatedByName(String createdByName) { this.createdByName = createdByName; }
    public Date getCheckIn() { return checkIn; }
    public void setCheckIn(Date checkIn) { this.checkIn = checkIn; }
    public Date getCheckOut() { return checkOut; }
    public void setCheckOut(Date checkOut) { this.checkOut = checkOut; }
    public int getNumAdults() { return numAdults; }
    public void setNumAdults(int numAdults) { this.numAdults = numAdults; }
    public int getNumChildren() { return numChildren; }
    public void setNumChildren(int numChildren) { this.numChildren = numChildren; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getSpecialRequests() { return specialRequests; }
    public void setSpecialRequests(String specialRequests) { this.specialRequests = specialRequests; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}