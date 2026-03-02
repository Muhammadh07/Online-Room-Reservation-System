package com.oceanview.model;

public class Guest {
    private int guestId;
    private String fullName;
    private String email;
    private String phone;
    private String nicPassport;
    private String address;

    public Guest() {}

    public int getGuestId() { return guestId; }
    public void setGuestId(int guestId) { this.guestId = guestId; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getNicPassport() { return nicPassport; }
    public void setNicPassport(String nicPassport) { this.nicPassport = nicPassport; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}