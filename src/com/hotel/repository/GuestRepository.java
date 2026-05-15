package com.hotel.repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import com.hotel.exception.FileWriteException;
import com.hotel.model.Guest;
import com.hotel.util.FileHandler;

public class GuestRepository {

    static final String FILE_PATH = "data/guests.csv";
    static final String HEADER = "guestId,fullName,phone,nidOrPassport,address,createdAt";

    public void save(Guest guest) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(toRow(guest));
            writer.newLine();
        } catch (IOException e) {
            throw new FileWriteException("Could not save guest to: " + FILE_PATH);
        }
    }

    public List<Guest> findAll() {
        List<Guest> guests = new ArrayList<>();
        for (String[] cols : FileHandler.readAll(FILE_PATH)) {
            if (cols.length < 6) continue;
            Guest g = new Guest();
            g.setId(cols[0].trim());
            g.setFullName(cols[1].trim());
            g.setPhone(cols[2].trim());
            g.setNidOrPassport(cols[3].trim());
            g.setAddress(cols[4].trim());
            g.setCreatedAt(cols[5].trim());
            guests.add(g);
        }
        return guests;
    }

    public Guest findById(String id) {
        for (Guest g : findAll()) {
            if (g.getId().equals(id)) return g;
        }
        return null;
    }

    public void update(Guest guest) {
        List<Guest> all = findAll();
        List<String> rows = new ArrayList<>();
        for (Guest g : all) {
            rows.add(g.getId().equals(guest.getId()) ? toRow(guest) : toRow(g));
        }
        FileHandler.writeAll(FILE_PATH, HEADER, rows);
    }

    public void deleteById(String id) {
        List<Guest> all = findAll();
        List<String> rows = new ArrayList<>();
        for (Guest g : all) {
            if (!g.getId().equals(id)) rows.add(toRow(g));
        }
        FileHandler.writeAll(FILE_PATH, HEADER, rows);
    }

    public boolean existsByNid(String nid) {
        for (Guest g : findAll()) {
            if (g.getNidOrPassport().equals(nid)) return true;
        }
        return false;
    }

    private String toRow(Guest g) {
        return g.getId() + "," + g.getFullName() + "," + g.getPhone() + ","
                + g.getNidOrPassport() + "," + (g.getAddress() == null ? "" : g.getAddress())
                + "," + g.getCreatedAt();
    }
}
