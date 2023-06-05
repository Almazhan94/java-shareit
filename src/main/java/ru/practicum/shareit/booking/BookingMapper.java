package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

public class BookingMapper {

    public static BookingDto toBookingDto(UserDto userDto, ItemDto itemDto,Booking booking) {
        return new BookingDto(booking.getId(),
                booking.getStartTime(),
                booking.getEndTime(),
                itemDto,
                userDto,
                booking.getStatus()
        );
    }

public static Booking toBooking(User user, Item item, BookingDto bookingDto) {
        return new Booking(bookingDto.getId(),
                bookingDto.getStart(),
                bookingDto.getEnd(),
                item,
                user,
                bookingDto.getStatus()
        );
}
}
