package com.hotel.model;

public class Suite extends Room {
    private int floorNumber;
    private boolean hasJacuzzi;
    private int maxOccupancy;

    public int getFloorNumber() { return floorNumber; }
    public void setFloorNumber(int floorNumber) { this.floorNumber = floorNumber; }

    public boolean isHasJacuzzi() { return hasJacuzzi; }
    public void setHasJacuzzi(boolean hasJacuzzi) { this.hasJacuzzi = hasJacuzzi; }

    public int getMaxOccupancy() { return maxOccupancy; }
    public void setMaxOccupancy(int maxOccupancy) { this.maxOccupancy = maxOccupancy; }

    @Override
    public void display() {
        System.out.println("Suite | No: " + getRoomNumber()
                + " | Floor: " + floorNumber
                + " | Jacuzzi: " + hasJacuzzi
                + " | Max Occupancy: " + maxOccupancy
                + " | Price: " + getPricePerNight()
                + " | Available: " + isAvailable());
    }
}
