package com.hotel.model;

public class Booking extends BaseEntity {
    private String guestId;
    private String roomId;
    private String checkInDate;
    private String checkOutDate;
    private double totalBill;
    private String status;

    public String getGuestId() { return guestId; }
    public void setGuestId(String guestId) { this.guestId = guestId; }

    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }

    public String getCheckInDate() { return checkInDate; }
    public void setCheckInDate(String checkInDate) { this.checkInDate = checkInDate; }

    public String getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(String checkOutDate) { this.checkOutDate = checkOutDate; }

    public double getTotalBill() { return totalBill; }
    public void setTotalBill(double totalBill) { this.totalBill = totalBill; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public void display() {
        System.out.println("Booking | ID: " + getId()
                + " | Guest: " + guestId
                + " | Room: " + roomId
                + " | Check-in: " + checkInDate
                + " | Check-out: " + checkOutDate
                + " | Bill: " + totalBill
                + " | Status: " + status);
    }
}
