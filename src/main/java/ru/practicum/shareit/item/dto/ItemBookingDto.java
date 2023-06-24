package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingInItemDto;
import ru.practicum.shareit.error.Generated;

import java.util.List;

@Generated
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemBookingDto {

    private Integer id;

    private String name;

    private String description;

    private Boolean available;

    private BookingInItemDto lastBooking;

    private BookingInItemDto nextBooking;

    private List<CommentDto> comments;

}
