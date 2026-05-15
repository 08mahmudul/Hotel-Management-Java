package com.hotel.model;

public class DeluxeRoom extends Room {
    private boolean hasAC;
    private boolean hasMinibar;

    public boolean isHasAC() { return hasAC; }
    public void setHasAC(boolean hasAC) { this.hasAC = hasAC; }

    public boolean isHasMinibar() { return hasMinibar; }
    public void setHasMinibar(boolean hasMinibar) { this.hasMinibar = hasMinibar; }

    @Override
    public void display() {
        System.out.println("Deluxe Room | No: " + getRoomNumber()
                + " | Price: " + getPricePerNight()
                + " | AC: " + hasAC
                + " | Minibar: " + hasMinibar
                + " | Available: " + isAvailable());
    }
}
