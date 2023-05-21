package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.Collection;
import java.util.List;

public interface ItemRepository {

    List<Item> findItemByUserId(int userId);

    Item findById(int itemId);

    Item create(User owner, Item item);

    Item update(Item item);

    Collection<Item> findAll();
}
