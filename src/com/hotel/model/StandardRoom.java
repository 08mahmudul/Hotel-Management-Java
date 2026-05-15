package com.hotel.model;

public class StandardRoom extends Room {
    private String amenities;

    public String getAmenities() { return amenities; }
    public void setAmenities(String amenities) { this.amenities = amenities; }

    @Override
    public void display() {
        System.out.println("Standard Room | No: " + getRoomNumber()
                + " | Price: " + getPricePerNight()
                + " | Amenities: " + amenities
                + " | Available: " + isAvailable());
    }
}
