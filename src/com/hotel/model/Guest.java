package com.hotel.model;

public class Guest extends BaseEntity {
    private String fullName;
    private String phone;
    private String nidOrPassport;
    private String address;

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getNidOrPassport() { return nidOrPassport; }
    public void setNidOrPassport(String nidOrPassport) { this.nidOrPassport = nidOrPassport; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    @Override
    public void display() {
        System.out.println("Guest | ID: " + getId()
                + " | Name: " + fullName
                + " | Phone: " + phone
                + " | NID: " + nidOrPassport);
    }
}
