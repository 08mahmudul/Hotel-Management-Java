package com.hotel.service;

import java.time.LocalDate;
import java.util.List;
import com.hotel.exception.*;
import com.hotel.model.Room;
import com.hotel.repository.BookingRepository;
import com.hotel.repository.RoomRepository;
import com.hotel.util.IDGenerator;
import com.hotel.util.InputValidator;

public class RoomService implements Manageable<Room> {

    private RoomRepository roomRepository = new RoomRepository();
    private BookingRepository bookingRepository = new BookingRepository();

    @Override
    public void add(Room room) throws HotelException {
        if (!InputValidator.isNotEmpty(room.getRoomNumber()))
            throw new InvalidInputException("Room number is required");
        if (room.getPricePerNight() <= 0)
            throw new InvalidInputException("Price must be greater than 0");
        if (roomRepository.existsByRoomNumber(room.getRoomNumber()))
            throw new DuplicateRecordException("Room number already exists: " + room.getRoomNumber());

        room.setId(IDGenerator.generate("R", "data/rooms.csv"));
        room.setCreatedAt(LocalDate.now().toString());
        roomRepository.save(room);
    }

    @Override
    public List<Room> getAll() {
        return roomRepository.findAll();
    }

    @Override
    public Room getById(String id) throws HotelException {
        Room r = roomRepository.findById(id);
        if (r == null) throw new RecordNotFoundException("Room not found: " + id);
        return r;
    }

    @Override
    public void update(Room room) throws HotelException {
        if (room.getPricePerNight() <= 0)
            throw new InvalidInputException("Price must be greater than 0");
        roomRepository.update(room);
    }

    @Override
    public void delete(String id) throws HotelException {
        if (bookingRepository.hasActiveBookingForRoom(id))
            throw new HotelException("Cannot delete: room has an active booking");
        roomRepository.deleteById(id);
    }

    public void setAvailability(String roomId, boolean status) throws HotelException {
        Room r = getById(roomId);
        r.setAvailable(status);
        roomRepository.update(r);
    }
}
