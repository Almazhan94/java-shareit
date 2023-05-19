package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepositoryImpl;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    ItemRepositoryImpl itemRepository;

    UserRepositoryImpl userRepository;

    @Autowired
    public ItemServiceImpl(ItemRepositoryImpl itemRepository, UserRepositoryImpl userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ItemDto createItem(int userId, Item item) {
        userRepository.findUserById(userId);
        return itemRepository.create(userId, item);
    }

    @Override
    public List<Item> findItemByUserId(int userId) {
        try {
            userRepository.findUserById(userId);
        } catch (RuntimeException e) {
            throw new RuntimeException(String.format("Пользователь с идентификатором %d не существует.", userId));
        }
        return itemRepository.findItemByUserId(userId);
    }

    @Override
    public Item findItemById(int itemId) {
        return itemRepository.findById(itemId);
    }

    @Override
    public List<Item> getItemBySearch(String text) {
        return itemRepository.getBySearch(text);
    }

    @Override
    public Item patchItem(int userId, int itemId, Item item) {
        userRepository.findUserById(userId);
        Item itemPatch = itemRepository.findById(itemId);
        if (itemPatch.getOwner() == userId) {
            if (item.getName() != null) {
                itemPatch.setName(item.getName());
            }
            if (item.getDescription() != null) {
                itemPatch.setDescription(item.getDescription());
            }
            if (item.getAvailable() != null) {
                itemPatch.setAvailable(item.getAvailable());
            }
            itemRepository.update(itemPatch);
        } else {
            throw new UserNotFoundException(String.format("Пользователь с идентификатором %d не является владельцем вещи.", userId));
        }
        return itemPatch;
    }


}
