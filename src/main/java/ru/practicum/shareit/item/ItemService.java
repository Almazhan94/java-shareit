package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.AddCommentDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    List<ItemDto> findItemByUserId(int userId);

    ItemDto findItemById(int itemId);

    ItemDto createItem(int userId, ItemDto itemDto);

    List<ItemDto> getItemBySearch(String text, int ownerId);

    ItemDto patchItem(int userId, int itemId, ItemDto itemDto);

    Item findItemFromDb(int itemId);

    ItemBookingDto findItemByIdWithBooking(int userId, int itemId);

    List<ItemBookingDto> findAllItemWithBooking(int userId);

    CommentDto addComment(Integer userId, int itemId, AddCommentDto addCommentDto);

}
