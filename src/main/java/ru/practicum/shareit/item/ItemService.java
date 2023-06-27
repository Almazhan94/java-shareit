package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.AddCommentDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto findItemById(int itemId);

    ItemDto createItem(int userId, ItemDto itemDto);

    List<ItemDto> getItemBySearch(String text, Integer from, Integer size);

    ItemDto patchItem(int userId, int itemId, ItemDto itemDto);

    ItemBookingDto findItemByIdWithBooking(int userId, int itemId);

    List<ItemBookingDto> findAllItemWithBooking(int userId, Integer from, Integer size);

    CommentDto addComment(Integer userId, int itemId, AddCommentDto addCommentDto);

}
