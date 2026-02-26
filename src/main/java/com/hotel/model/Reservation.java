package com.hotel.model;

import java.sql.Date;
import java.sql.Timestamp;

public class Reservation {
    private int reservationId;
    private String reservationNumber;
    private int guestId;
    private String guestName;
    private String contactNumber;
    private int roomId;
    private String roomNumber;
    private String typeName;
    private int staffId;
    private String staffName;
    private Date checkinDate;
    private Date checkoutDate;
    private String status;
    private Timestamp createdAt;
    private double ratePerNight;
    private int nights;

    public Reservation() {}

    public int getReservationId()               { return reservationId; }
    public void setReservationId(int v)         { reservationId = v; }
    public String getReservationNumber()        { return reservationNumber; }
    public void setReservationNumber(String v)  { reservationNumber = v; }
    public int getGuestId()                     { return guestId; }
    public void setGuestId(int v)               { guestId = v; }
    public String getGuestName()                { return guestName; }
    public void setGuestName(String v)          { guestName = v; }
    public String getContactNumber()            { return contactNumber; }
    public void setContactNumber(String v)      { contactNumber = v; }
    public int getRoomId()                      { return roomId; }
    public void setRoomId(int v)                { roomId = v; }
    public String getRoomNumber()               { return roomNumber; }
    public void setRoomNumber(String v)         { roomNumber = v; }
    public String getTypeName()                 { return typeName; }
    public void setTypeName(String v)           { typeName = v; }
    public int getStaffId()                     { return staffId; }
    public void setStaffId(int v)               { staffId = v; }
    public String getStaffName()                { return staffName; }
    public void setStaffName(String v)          { staffName = v; }
    public Date getCheckinDate()                { return checkinDate; }
    public void setCheckinDate(Date v)          { checkinDate = v; }
    public Date getCheckoutDate()               { return checkoutDate; }
    public void setCheckoutDate(Date v)         { checkoutDate = v; }
    public String getStatus()                   { return status; }
    public void setStatus(String v)             { status = v; }
    public Timestamp getCreatedAt()             { return createdAt; }
    public void setCreatedAt(Timestamp v)       { createdAt = v; }
    public double getRatePerNight()             { return ratePerNight; }
    public void setRatePerNight(double v)       { ratePerNight = v; }
    public int getNights()                      { return nights; }
    public void setNights(int v)                { nights = v; }
}