package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class ItemRepositoryImpl implements ItemRepository {

    private HashMap<Integer, Item> items = new HashMap<>();

    private int id = 0;

    @Override
    public List<Item> findItemByUserId(int userId) {
        List<Item> userItems = new ArrayList<>();
        for (Item item : items.values()) {
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
            throw new RuntimeException(String.format("Вещь с идентификатором %d не существует", itemId));
        }
    }

    @Override
    public ItemDto create(int userId, Item item) {
        item.setId(++id);
        item.setOwner(userId);
        items.put(item.getId(), item);
        ItemDto itemDto = ItemMapper.toItemDto(item);
        return itemDto;
    }

    @Override
    public Item update(Item item) {
        int itemId = item.getId();
        if (items.containsKey(itemId)) {
            items.put(itemId, item);
        } else {
            throw new RuntimeException(String.format("Вещь с идентификатором %d не существует", itemId));
        }
        return item;
    }

    @Override
    public List<Item> getBySearch(String text) {
        List<Item> itemSearch = new ArrayList<>();
        if (text == null || text.isBlank()) {
            return itemSearch;
        }
        for (Item item : items.values()) {
            if (item.getAvailable()) {
                String textToLowerCase = text.toLowerCase();
                String nameToLowerCase = item.getName().toLowerCase();
                String descriptionToLowerCase = item.getDescription().toLowerCase();
                if (nameToLowerCase.contains(textToLowerCase) || descriptionToLowerCase.contains(textToLowerCase)) {
                    itemSearch.add(item);
                }
            }
        }
        return itemSearch;
    }
}
