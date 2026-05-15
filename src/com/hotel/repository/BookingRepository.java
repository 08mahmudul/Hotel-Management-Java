package com.hotel.repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import com.hotel.exception.FileWriteException;
import com.hotel.model.Booking;
import com.hotel.util.FileHandler;

public class BookingRepository {

    static final String FILE_PATH = "data/bookings.csv";
    static final String HEADER = "bookingId,guestId,roomId,checkInDate,checkOutDate,totalBill,status,createdAt";

    public void save(Booking booking) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(toRow(booking));
            writer.newLine();
        } catch (IOException e) {
            throw new FileWriteException("Could not save booking to: " + FILE_PATH);
        }
    }

    public List<Booking> findAll() {
        List<Booking> bookings = new ArrayList<>();
        for (String[] cols : FileHandler.readAll(FILE_PATH)) {
            if (cols.length < 8) continue;
            Booking b = new Booking();
            b.setId(cols[0].trim());
            b.setGuestId(cols[1].trim());
            b.setRoomId(cols[2].trim());
            b.setCheckInDate(cols[3].trim());
            b.setCheckOutDate(cols[4].trim());
            b.setTotalBill(Double.parseDouble(cols[5].trim()));
            b.setStatus(cols[6].trim());
            b.setCreatedAt(cols[7].trim());
            bookings.add(b);
        }
        return bookings;
    }

    public Booking findById(String id) {
        for (Booking b : findAll()) {
            if (b.getId().equals(id)) return b;
        }
        return null;
    }

    public void update(Booking booking) {
        List<Booking> all = findAll();
        List<String> rows = new ArrayList<>();
        for (Booking b : all) {
            rows.add(b.getId().equals(booking.getId()) ? toRow(booking) : toRow(b));
        }
        FileHandler.writeAll(FILE_PATH, HEADER, rows);
    }

    public void deleteById(String id) {
        List<Booking> all = findAll();
        List<String> rows = new ArrayList<>();
        for (Booking b : all) {
            if (!b.getId().equals(id)) rows.add(toRow(b));
        }
        FileHandler.writeAll(FILE_PATH, HEADER, rows);
    }

    public boolean hasActiveBookingForRoom(String roomId) {
        for (Booking b : findAll()) {
            if (b.getRoomId().equals(roomId) && "ACTIVE".equals(b.getStatus())) return true;
        }
        return false;
    }

    public boolean hasActiveBookingForGuest(String guestId) {
        for (Booking b : findAll()) {
            if (b.getGuestId().equals(guestId) && "ACTIVE".equals(b.getStatus())) return true;
        }
        return false;
    }

    private String toRow(Booking b) {
        return b.getId() + "," + b.getGuestId() + "," + b.getRoomId() + ","
                + b.getCheckInDate() + "," + b.getCheckOutDate() + ","
                + b.getTotalBill() + "," + b.getStatus() + "," + b.getCreatedAt();
    }
}
