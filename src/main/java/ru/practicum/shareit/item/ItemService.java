package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.AddCommentDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto findItemById(int itemId);

    ItemDto createItem(int userId, ItemDto itemDto);

    List<ItemDto> getItemBySearch(String text);

    ItemDto patchItem(int userId, int itemId, ItemDto itemDto);

    ItemBookingDto findItemByIdWithBooking(int userId, int itemId);

    List<ItemBookingDto> findAllItemWithBooking(int userId);

    CommentDto addComment(Integer userId, int itemId, AddCommentDto addCommentDto);

}
