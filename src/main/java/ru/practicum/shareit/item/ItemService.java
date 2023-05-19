package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    List<Item> findItemByUserId(int userId);

    Item findItemById(int itemId);

    ItemDto createItem(int userId, Item item);

    List<Item> getItemBySearch(String text);

    Item patchItem(int userId, int itemId, Item item);

}
