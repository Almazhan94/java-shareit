package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;

import java.util.List;

public interface BookingService {

    BookingDto create(int bookerId,  CreateBookingDto createBookingDto);

    BookingDto findById(int userId, int bookingId);

    BookingDto update(int userId, int bookingId, String approved);

    List<BookingDto> findByState(int userId, String state);

    List<BookingDto> findOwnerBooking(int ownerId, String state);
}
