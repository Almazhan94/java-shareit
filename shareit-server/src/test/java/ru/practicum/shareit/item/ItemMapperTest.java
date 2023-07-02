package ru.practicum.shareit.item;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.dto.AddCommentDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemMapperTest {

    private final EasyRandom easyRandom = new EasyRandom();

    @Test
    void toItemDtoTest() {
        Item item = easyRandom.nextObject(Item.class);
        ItemDto itemDto = ItemMapper.toItemDto(item);
        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getAvailable(), itemDto.getAvailable());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.getRequestId(), itemDto.getRequestId());

    }

    @Test
    void toItemTest() {
        ItemDto itemDto = easyRandom.nextObject(ItemDto.class);
        User owner = easyRandom.nextObject(User.class);
        Item item = ItemMapper.toItem(owner, itemDto);
        assertEquals(itemDto.getId(), item.getId());
        assertEquals(itemDto.getName(), item.getName());
        assertEquals(itemDto.getDescription(), item.getDescription());
        assertEquals(itemDto.getRequestId(), item.getRequestId());
        assertEquals(item.getOwner(), owner);
        assertEquals(item.getAvailable(), itemDto.getAvailable());

    }

    @Test
    void toItemDtoListTest() {
        Item item1 = easyRandom.nextObject(Item.class);
        Item item2 = easyRandom.nextObject(Item.class);
        List<Item> itemList = List.of(item1, item2);
        List<ItemDto> itemDtoList = ItemMapper.toItemDtoList(itemList);
        assertEquals(itemList.size(), itemDtoList.size());
        assertEquals(item1.getId(), itemDtoList.get(0).getId());
        assertEquals(item2.getId(), itemDtoList.get(1).getId());
    }

    @Test
    void toItemBookingDtoTest() {
        Booking lastBooking = easyRandom.nextObject(Booking.class);
        Booking nextBooking = easyRandom.nextObject(Booking.class);
        Item item = easyRandom.nextObject(Item.class);
        CommentDto commentDto1 = easyRandom.nextObject(CommentDto.class);
        CommentDto commentDto2 = easyRandom.nextObject(CommentDto.class);
        List<CommentDto> commentDtoList = List.of(commentDto1, commentDto2);
        ItemBookingDto itemBookingDto = ItemMapper.toItemBookingDto(lastBooking, nextBooking, item, commentDtoList);
        assertEquals(item.getId(), itemBookingDto.getId());
        assertEquals(item.getName(), itemBookingDto.getName());
        assertEquals(item.getDescription(), itemBookingDto.getDescription());
        assertEquals(item.getAvailable(), itemBookingDto.getAvailable());
        assertEquals(lastBooking.getId(), itemBookingDto.getLastBooking().getId());
        assertEquals(nextBooking.getId(), itemBookingDto.getNextBooking().getId());
        assertEquals(commentDtoList.size(), itemBookingDto.getComments().size());
        assertEquals(commentDtoList.get(0), itemBookingDto.getComments().get(0));
        assertEquals(commentDtoList.get(1), itemBookingDto.getComments().get(1));

    }

    @Test
    void toCommentDtoTest() {
        Comment comment = easyRandom.nextObject(Comment.class);
        CommentDto commentDto = ItemMapper.toCommentDto(comment);
        assertEquals(comment.getId(), commentDto.getId());
        assertEquals(comment.getText(), commentDto.getText());
        assertEquals(comment.getCreated(), commentDto.getCreated());
        assertEquals(comment.getAuthor().getName(), commentDto.getAuthorName());
    }

    @Test
    void toCommentTest() {
        User user = easyRandom.nextObject(User.class);
        Item item = easyRandom.nextObject(Item.class);
        AddCommentDto addCommentDto = easyRandom.nextObject(AddCommentDto.class);
        Comment comment = ItemMapper.toComment(user, item, addCommentDto);
        assertEquals(addCommentDto.getText(), comment.getText());
        assertEquals(comment.getItem(), item);
        assertEquals(comment.getAuthor(), user);
    }

    @Test
    void toCommentDtoListTest() {
        Comment comment1 = easyRandom.nextObject(Comment.class);
        Comment comment2 = easyRandom.nextObject(Comment.class);
        List<Comment> commentList = List.of(comment1, comment2);
        List<CommentDto> commentDtoList = ItemMapper.toCommentDtoList(commentList);
        assertEquals(commentList.size(), commentDtoList.size());
        assertEquals(comment1.getId(), commentList.get(0).getId());
        assertEquals(comment2.getId(), commentList.get(1).getId());
    }
}