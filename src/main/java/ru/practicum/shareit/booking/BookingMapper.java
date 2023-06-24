package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.ArrayList;
import java.util.List;

public class BookingMapper {

    public static BookingDto toBookingDto(UserDto userDto, ItemDto itemDto, Booking booking) {
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

    public static List<BookingDto> toBookingDtoList(List<Booking> bookingList, UserDto userDto) {
        List<BookingDto> bookingDtoList = new ArrayList<>();
        for (Booking booking : bookingList) {
            bookingDtoList.add(BookingMapper.toBookingDto(userDto, ItemMapper.toItemDto(booking.getItem()), booking));
        }
        return bookingDtoList;
    }

    public static List<BookingDto> toOwnerBookingDtoList(List<Booking> bookingList) {
        List<BookingDto> bookingDtoList = new ArrayList<>();
        for (Booking booking : bookingList) {
            bookingDtoList.add(BookingMapper.toBookingDto(UserMapper.toUserDto(booking.getBooker()),
                    ItemMapper.toItemDto(booking.getItem()),
                    booking));
        }
        return bookingDtoList;
    }
}
