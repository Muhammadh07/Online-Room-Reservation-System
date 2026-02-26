package com.hotel.model;

public class Guest {
    private int guestId;
    private String guestName;
    private String address;
    private String contactNumber;
    private String email;

    public Guest() {}
    public Guest(String guestName, String address, String contactNumber, String email) {
        this.guestName = guestName; this.address = address;
        this.contactNumber = contactNumber; this.email = email;
    }

    public int getGuestId()                     { return guestId; }
    public void setGuestId(int v)               { guestId = v; }
    public String getGuestName()                { return guestName; }
    public void setGuestName(String v)          { guestName = v; }
    public String getAddress()                  { return address; }
    public void setAddress(String v)            { address = v; }
    public String getContactNumber()            { return contactNumber; }
    public void setContactNumber(String v)      { contactNumber = v; }
    public String getEmail()                    { return email; }
    public void setEmail(String v)              { email = v; }
}