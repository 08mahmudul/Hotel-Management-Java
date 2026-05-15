package com.hotel.service;

import java.time.LocalDate;
import java.util.List;
import com.hotel.exception.*;
import com.hotel.model.Guest;
import com.hotel.repository.BookingRepository;
import com.hotel.repository.GuestRepository;
import com.hotel.util.IDGenerator;
import com.hotel.util.InputValidator;

public class GuestService implements Manageable<Guest> {

    private GuestRepository guestRepository = new GuestRepository();
    private BookingRepository bookingRepository = new BookingRepository();

    @Override
    public void add(Guest guest) throws HotelException {
        if (!InputValidator.isNotEmpty(guest.getFullName()) || !guest.getFullName().matches("[a-zA-Z ]+"))
            throw new InvalidInputException("Full name must contain only letters and spaces");
        if (guest.getFullName().trim().length() < 3)
            throw new InvalidInputException("Full name must be at least 3 characters");
        if (!InputValidator.isValidPhone(guest.getPhone()))
            throw new InvalidInputException("Phone must be 11 digits starting with 01");
        if (!InputValidator.isNotEmpty(guest.getNidOrPassport()))
            throw new InvalidInputException("NID/Passport is required");
        if (guestRepository.existsByNid(guest.getNidOrPassport()))
            throw new DuplicateRecordException("NID already registered: " + guest.getNidOrPassport());

        guest.setId(IDGenerator.generate("G", "data/guests.csv"));
        guest.setCreatedAt(LocalDate.now().toString());
        guestRepository.save(guest);
    }

    @Override
    public List<Guest> getAll() {
        return guestRepository.findAll();
    }

    @Override
    public Guest getById(String id) throws HotelException {
        Guest g = guestRepository.findById(id);
        if (g == null) throw new RecordNotFoundException("Guest not found: " + id);
        return g;
    }

    @Override
    public void update(Guest guest) throws HotelException {
        guestRepository.update(guest);
    }

    @Override
    public void delete(String id) throws HotelException {
        if (bookingRepository.hasActiveBookingForGuest(id))
            throw new HotelException("Cannot delete: guest has an active booking");
        guestRepository.deleteById(id);
    }
}
