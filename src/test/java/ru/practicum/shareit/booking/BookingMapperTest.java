package ru.practicum.shareit.booking;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BookingMapperTest {

    private final EasyRandom easyRandom = new EasyRandom();

    @Test
    void toBookingDtoTest() {
        Booking booking = easyRandom.nextObject(Booking.class);
        BookingDto bookingDto = BookingMapper.toBookingDto(UserMapper.toUserDto(booking.getBooker()), ItemMapper.toItemDto(booking.getItem()), booking);
        assertEquals(booking.getId(), bookingDto.getId());
        assertEquals(booking.getStartTime(), bookingDto.getStart());
        assertEquals(booking.getEndTime(), bookingDto.getEnd());
        assertEquals(booking.getStatus(), bookingDto.getStatus());
        assertEquals(booking.getBooker().getId(), bookingDto.getBooker().getId());
        assertEquals(booking.getItem().getId(), bookingDto.getItem().getId());
    }

    @Test
    void toBookingTest() {
        User user = easyRandom.nextObject(User.class);
        Item item = easyRandom.nextObject(Item.class);
        BookingDto bookingDto = easyRandom.nextObject(BookingDto.class);
        Booking booking = BookingMapper.toBooking(user, item, bookingDto);
        assertEquals(booking.getId(), bookingDto.getId());
        assertEquals(booking.getStartTime(), bookingDto.getStart());
        assertEquals(booking.getEndTime(), bookingDto.getEnd());
        assertEquals(booking.getStatus(), bookingDto.getStatus());
        assertEquals(user.getId(), booking.getBooker().getId());
        assertEquals(item.getId(), booking.getItem().getId());
    }

    @Test
    void toBookingDtoListTest() {
        UserDto userDto = easyRandom.nextObject(UserDto.class);
        Booking booking1 = easyRandom.nextObject(Booking.class);
        Booking booking2 = easyRandom.nextObject(Booking.class);
        List<Booking> bookingList = List.of(booking1, booking2);
        List<BookingDto> bookingDtoList = BookingMapper.toBookingDtoList(bookingList, userDto);
        assertEquals(bookingList.size(), bookingDtoList.size());
        assertEquals(bookingList.get(0).getId(), bookingDtoList.get(0).getId());
        assertEquals(bookingList.get(1).getId(), bookingDtoList.get(1).getId());
    }

    @Test
    void toOwnerBookingDtoListTest() {
        Booking booking1 = easyRandom.nextObject(Booking.class);
        Booking booking2 = easyRandom.nextObject(Booking.class);
        List<Booking> bookingList = List.of(booking1, booking2);
        List<BookingDto> bookingDtoList = BookingMapper.toOwnerBookingDtoList(bookingList);
        assertEquals(bookingList.size(), bookingDtoList.size());
        assertEquals(bookingList.get(0).getId(), bookingDtoList.get(0).getId());
        assertEquals(bookingList.get(1).getId(), bookingDtoList.get(1).getId());
    }
}