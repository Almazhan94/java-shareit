package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    List<ItemDto> findItemByUserId(int userId);

    ItemDto findItemById(int itemId);

    ItemDto createItem(int userId, ItemDto itemDto);

    List<ItemDto> getItemBySearch(String text);

    ItemDto patchItem(int userId, int itemId, ItemDto itemDto);

}
