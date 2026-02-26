package com.hotel.model;

import java.sql.Timestamp;

public class Bill {
    private int billId;
    private String billNumber;
    private int reservationId;
    private String reservationNumber;
    private String guestName;
    private String roomNumber;
    private String typeName;
    private int nights;
    private double ratePerNight;
    private double subtotal;
    private double taxRate;
    private double taxAmount;
    private double discountAmount;
    private double totalAmount;
    private String paymentStatus;
    private Timestamp generatedAt;

    public Bill() {}

    public int getBillId()                      { return billId; }
    public void setBillId(int v)                { billId = v; }
    public String getBillNumber()               { return billNumber; }
    public void setBillNumber(String v)         { billNumber = v; }
    public int getReservationId()               { return reservationId; }
    public void setReservationId(int v)         { reservationId = v; }
    public String getReservationNumber()        { return reservationNumber; }
    public void setReservationNumber(String v)  { reservationNumber = v; }
    public String getGuestName()                { return guestName; }
    public void setGuestName(String v)          { guestName = v; }
    public String getRoomNumber()               { return roomNumber; }
    public void setRoomNumber(String v)         { roomNumber = v; }
    public String getTypeName()                 { return typeName; }
    public void setTypeName(String v)           { typeName = v; }
    public int getNights()                      { return nights; }
    public void setNights(int v)                { nights = v; }
    public double getRatePerNight()             { return ratePerNight; }
    public void setRatePerNight(double v)       { ratePerNight = v; }
    public double getSubtotal()                 { return subtotal; }
    public void setSubtotal(double v)           { subtotal = v; }
    public double getTaxRate()                  { return taxRate; }
    public void setTaxRate(double v)            { taxRate = v; }
    public double getTaxAmount()                { return taxAmount; }
    public void setTaxAmount(double v)          { taxAmount = v; }
    public double getDiscountAmount()           { return discountAmount; }
    public void setDiscountAmount(double v)     { discountAmount = v; }
    public double getTotalAmount()              { return totalAmount; }
    public void setTotalAmount(double v)        { totalAmount = v; }
    public String getPaymentStatus()            { return paymentStatus; }
    public void setPaymentStatus(String v)      { paymentStatus = v; }
    public Timestamp getGeneratedAt()           { return generatedAt; }
    public void setGeneratedAt(Timestamp v)     { generatedAt = v; }
}