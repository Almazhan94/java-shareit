package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.error.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

@Component
public class ItemRepositoryImpl implements ItemRepository {

    private HashMap<Integer, Item> items = new HashMap<>();

    private int id = 0;

    @Override
    public List<Item> findItemByUserId(int userId) {
        List<Item> userItems = new ArrayList<>();
        for (Item item : findAll()) {
            if (item.getOwner() == userId) {
                userItems.add(item);
            }
        }
        return userItems;
    }

    @Override
    public Item findById(int itemId) {
        if (items.containsKey(itemId)) {
            return items.get(itemId);
        } else {
            throw new ItemNotFoundException(String.format("Вещь с идентификатором %d не существует", itemId));
        }
    }

    @Override
    public Item create(int userId, Item item) {
        item.setId(++id);
        item.setOwner(userId);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item item) {
        int itemId = item.getId();
        if (items.containsKey(itemId)) {
            items.put(itemId, item);
        } else {
            throw new ItemNotFoundException(String.format("Вещь с идентификатором %d не существует", itemId));
        }
        return item;
    }

    @Override
    public Collection<Item> findAll() {
        return items.values();
    }

}
