package com.hotel.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import com.hotel.exception.*;
import com.hotel.model.Booking;
import com.hotel.model.Room;
import com.hotel.repository.BookingRepository;
import com.hotel.util.IDGenerator;
import com.hotel.util.InputValidator;

public class BookingService implements Manageable<Booking> {

    private BookingRepository bookingRepository = new BookingRepository();
    private RoomService roomService = new RoomService();

    @Override
    public void add(Booking booking) throws HotelException {
        if (!InputValidator.isValidDate(booking.getCheckInDate()))
            throw new InvalidInputException("Invalid check-in date");
        if (!InputValidator.isValidDate(booking.getCheckOutDate()))
            throw new InvalidInputException("Invalid check-out date");
        if (!InputValidator.isTodayOrFuture(booking.getCheckInDate()))
            throw new InvalidInputException("Check-in cannot be in the past");
        if (!InputValidator.isDateAfter(booking.getCheckOutDate(), booking.getCheckInDate()))
            throw new InvalidInputException("Check-out must be after check-in");

        Room room = roomService.getById(booking.getRoomId());
        if (!room.isAvailable())
            throw new RoomNotAvailableException("Room is not available");

        long nights = calculateNights(booking.getCheckInDate(), booking.getCheckOutDate());
        booking.setTotalBill(nights * room.getPricePerNight());
        booking.setStatus("ACTIVE");
        booking.setId(IDGenerator.generate("B", "data/bookings.csv"));
        booking.setCreatedAt(LocalDate.now().toString());
        bookingRepository.save(booking);
        roomService.setAvailability(booking.getRoomId(), false);
    }

    @Override
    public List<Booking> getAll() {
        return bookingRepository.findAll();
    }

    @Override
    public Booking getById(String id) throws HotelException {
        Booking b = bookingRepository.findById(id);
        if (b == null) throw new RecordNotFoundException("Booking not found: " + id);
        return b;
    }

    @Override
    public void update(Booking booking) throws HotelException {
        bookingRepository.update(booking);
    }

    @Override
    public void delete(String id) throws HotelException {
        bookingRepository.deleteById(id);
    }

    public void cancelBooking(String bookingId) throws HotelException {
        Booking b = getById(bookingId);
        if ("CANCELLED".equals(b.getStatus()))
            throw new HotelException("Booking already cancelled");
        b.setStatus("CANCELLED");
        bookingRepository.update(b);
        roomService.setAvailability(b.getRoomId(), true);
    }

    private long calculateNights(String checkIn, String checkOut) {
        return ChronoUnit.DAYS.between(LocalDate.parse(checkIn), LocalDate.parse(checkOut));
    }
}
