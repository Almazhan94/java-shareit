package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.error.Generated;

import java.util.List;

@Generated
@Slf4j
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDto create(@RequestHeader("X-Sharer-User-Id") Integer bookerId,
                             @RequestBody CreateBookingDto createBookingDto) {
        log.info("Добавляется бронирование: {}", createBookingDto);
        return bookingService.create(bookerId, createBookingDto);
    }

    @GetMapping("/{bookingId}")
    public BookingDto findBookingById(@RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable int bookingId) {
        log.info("Ищется бронирование по идентификатору: {}", bookingId);
        return bookingService.findById(userId, bookingId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto patch(@RequestHeader("X-Sharer-User-Id") Integer userId,
                         @PathVariable int bookingId,
                         @RequestParam(value = "approved") String approved) {
        log.info("Обновляется бронирование по идентификатору: {}", bookingId);
        return bookingService.update(userId, bookingId, approved);
    }

    @GetMapping
    public List<BookingDto> findByState(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                        @RequestParam(value = "state", required = false, defaultValue = "ALL") String state,
                                        @RequestParam(value = "from", required = false, defaultValue = "0") Integer from,
                                        @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        log.info("Ищется бронирования по статусу: {}", state);
        return bookingService.findByState(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> findOwnerBooking(@RequestHeader("X-Sharer-User-Id") Integer ownerId,
                                             @RequestParam(value = "state", required = false, defaultValue = "ALL") String state,
                                             @RequestParam(value = "from", required = false, defaultValue = "0") Integer from,
                                             @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        log.info("Ищется бронирования по пользователю: {}", ownerId);
        return bookingService.findOwnerBooking(ownerId, state, from, size);
    }
}
