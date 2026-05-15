package com.hotel;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import com.hotel.ui.MainFrame;
import com.hotel.util.FileHandler;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {}

        FileHandler.ensureFileExists("data/rooms.csv",
                "roomId,roomNumber,roomType,pricePerNight,isAvailable,extraData,createdAt");
        FileHandler.ensureFileExists("data/guests.csv",
                "guestId,fullName,phone,nidOrPassport,address,createdAt");
        FileHandler.ensureFileExists("data/bookings.csv",
                "bookingId,guestId,roomId,checkInDate,checkOutDate,totalBill,status,createdAt");

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
