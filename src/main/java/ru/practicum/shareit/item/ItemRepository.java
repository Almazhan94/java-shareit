package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {

    List<Item> findItemByUserId(int userId);

    Item findById(int itemId);

    ItemDto create(int userId, Item item);

    Item update(Item item);

    List<Item> getBySearch(String text);
}
