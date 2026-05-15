package com.hotel.repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import com.hotel.exception.FileWriteException;
import com.hotel.model.*;
import com.hotel.util.FileHandler;

public class RoomRepository {

    static final String FILE_PATH = "data/rooms.csv";
    static final String HEADER = "roomId,roomNumber,roomType,pricePerNight,isAvailable,extraData,createdAt";

    public void save(Room room) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(toRow(room));
            writer.newLine();
        } catch (IOException e) {
            throw new FileWriteException("Could not save room to: " + FILE_PATH);
        }
    }

    public List<Room> findAll() {
        List<Room> rooms = new ArrayList<>();
        for (String[] cols : FileHandler.readAll(FILE_PATH)) {
            if (cols.length < 7) continue;
            Room room = buildRoom(cols);
            if (room != null) rooms.add(room);
        }
        return rooms;
    }

    public Room findById(String id) {
        for (Room room : findAll()) {
            if (room.getId().equals(id)) return room;
        }
        return null;
    }

    public void update(Room room) {
        List<Room> all = findAll();
        List<String> rows = new ArrayList<>();
        for (Room r : all) {
            rows.add(r.getId().equals(room.getId()) ? toRow(room) : toRow(r));
        }
        FileHandler.writeAll(FILE_PATH, HEADER, rows);
    }

    public void deleteById(String id) {
        List<Room> all = findAll();
        List<String> rows = new ArrayList<>();
        for (Room r : all) {
            if (!r.getId().equals(id)) rows.add(toRow(r));
        }
        FileHandler.writeAll(FILE_PATH, HEADER, rows);
    }

    public boolean existsByRoomNumber(String roomNumber) {
        for (Room r : findAll()) {
            if (r.getRoomNumber().equals(roomNumber)) return true;
        }
        return false;
    }

    private Room buildRoom(String[] cols) {
        String type = cols[2].trim();
        String extraData = cols[5].trim();
        Room room;

        switch (type) {
            case "STANDARD": {
                StandardRoom sr = new StandardRoom();
                sr.setAmenities(extraData.replace("|", ", "));
                room = sr;
                break;
            }
            case "DELUXE": {
                DeluxeRoom dr = new DeluxeRoom();
                for (String part : extraData.split("\\|")) {
                    String[] kv = part.split("=");
                    if (kv[0].equals("AC"))      dr.setHasAC(Boolean.parseBoolean(kv[1]));
                    if (kv[0].equals("Minibar")) dr.setHasMinibar(Boolean.parseBoolean(kv[1]));
                }
                room = dr;
                break;
            }
            case "SUITE": {
                Suite s = new Suite();
                for (String part : extraData.split("\\|")) {
                    String[] kv = part.split("=");
                    if (kv[0].equals("Floor"))   s.setFloorNumber(Integer.parseInt(kv[1]));
                    if (kv[0].equals("Jacuzzi")) s.setHasJacuzzi(Boolean.parseBoolean(kv[1]));
                    if (kv[0].equals("MaxOcc"))  s.setMaxOccupancy(Integer.parseInt(kv[1]));
                }
                room = s;
                break;
            }
            default:
                return null;
        }

        room.setId(cols[0].trim());
        room.setRoomNumber(cols[1].trim());
        room.setRoomType(type);
        room.setPricePerNight(Double.parseDouble(cols[3].trim()));
        room.setAvailable(Boolean.parseBoolean(cols[4].trim()));
        room.setCreatedAt(cols[6].trim());
        return room;
    }

    private String toRow(Room room) {
        String extraData;
        if (room instanceof StandardRoom) {
            StandardRoom sr = (StandardRoom) room;
            extraData = sr.getAmenities() == null ? "" : sr.getAmenities().replace(", ", "|");
        } else if (room instanceof DeluxeRoom) {
            DeluxeRoom dr = (DeluxeRoom) room;
            extraData = "AC=" + dr.isHasAC() + "|Minibar=" + dr.isHasMinibar();
        } else {
            Suite s = (Suite) room;
            extraData = "Floor=" + s.getFloorNumber() + "|Jacuzzi=" + s.isHasJacuzzi() + "|MaxOcc=" + s.getMaxOccupancy();
        }
        return room.getId() + "," + room.getRoomNumber() + "," + room.getRoomType() + ","
                + room.getPricePerNight() + "," + room.isAvailable() + "," + extraData + "," + room.getCreatedAt();
    }
}
