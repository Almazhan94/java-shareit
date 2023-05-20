package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;

public interface ItemRepository {

    List<Item> findItemByUserId(int userId);

    Item findById(int itemId);

    Item create(int userId, Item item);

    Item update(Item item);

    Collection<Item> findAll();
}
