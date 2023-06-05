package ru.practicum.shareit.item;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingInItemDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.List;

public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequestId());
    }

    public static Item toItem(User owner, ItemDto itemDto) {
        return new Item(itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                owner,
                itemDto.getRequestId());
    }

    public static ItemBookingDto toItemBookingDto(Booking lastBooking, Booking nextBooking, Item item, List<CommentDto> comments) {
        //BookingInItemDto lastBookingInItemDto = null;
        // BookingInItemDto nextBookingInItemDto = null;
        BookingInItemDto lastBookingInItemDto = new BookingInItemDto();
        BookingInItemDto nextBookingInItemDto = new BookingInItemDto();
        if (lastBooking == null || lastBooking.getId() == null) {
            lastBookingInItemDto = null;
        } else {
            lastBookingInItemDto = new BookingInItemDto(lastBooking.getId(), lastBooking.getBooker().getId());
        }
        if (nextBooking == null || nextBooking.getId() == null) {
            nextBookingInItemDto = null;
        } else {
            nextBookingInItemDto = new BookingInItemDto(nextBooking.getId(), nextBooking.getBooker().getId());
        }
        return new ItemBookingDto(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                lastBookingInItemDto,
                nextBookingInItemDto,
                comments
        );
    }

    public static CommentDto toCommentDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setAuthorName(comment.getAuthor().getName());
        commentDto.setCreated(comment.getCreated());
        return commentDto;
    }


}

