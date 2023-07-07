package ru.practicum.shareit.item;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingInItemDto;
import ru.practicum.shareit.item.dto.AddCommentDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
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

    public static List<ItemDto> toItemDtoList(List<Item> itemList) {
        List<ItemDto> itemDtoList = new ArrayList<>();
        for (Item item : itemList) {
            itemDtoList.add(ItemMapper.toItemDto(item));
        }
        return itemDtoList;
    }

    public static ItemBookingDto toItemBookingDto(Booking lastBooking, Booking nextBooking, Item item, List<CommentDto> comments) {
        BookingInItemDto lastBookingInItemDto;
        BookingInItemDto nextBookingInItemDto;
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

    public static Comment toComment(User user, Item item,  AddCommentDto addCommentDto) {
        Comment comment = new Comment();
        comment.setText(addCommentDto.getText());
        comment.setAuthor(user);
        comment.setItem(item);
        return comment;
    }

    public static List<CommentDto> toCommentDtoList(List<Comment> commentList) {
        List<CommentDto> commentDtoList = new ArrayList<>();
        for (Comment comment : commentList) {
            commentDtoList.add(ItemMapper.toCommentDto(comment));
        }
        return commentDtoList;
    }
}

