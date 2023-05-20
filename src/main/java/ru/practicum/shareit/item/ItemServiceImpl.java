package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    ItemRepository itemRepository;

    UserRepository userRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ItemDto createItem(int userId, ItemDto itemDto) {
        Item item = ItemMapper.toItem(userId, itemDto);
        userRepository.findUserById(userId);
        Item createItem = itemRepository.create(userId, item);
        return ItemMapper.toItemDto(createItem);
    }

    @Override
    public ItemDto findItemById(int itemId) {
       ItemDto itemDto = ItemMapper.toItemDto(itemRepository.findById(itemId));
        return itemDto;
    }

    @Override
    public List<ItemDto> findItemByUserId(int userId) {
        userRepository.findUserById(userId);
        List<Item> itemList = itemRepository.findItemByUserId(userId);
        List<ItemDto> itemDtoList = new ArrayList<>();
        for (Item item : itemList) {
            itemDtoList.add(ItemMapper.toItemDto(item));
        }
        return itemDtoList;
    }

    @Override
    public List<ItemDto> getItemBySearch(String text) {
        List<Item> itemSearch = new ArrayList<>();
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }
        String textToLowerCase = text.toLowerCase();
        for (Item item : itemRepository.findAll()) {
            if (item.getAvailable()) {
                String nameToLowerCase = item.getName().toLowerCase();
                String descriptionToLowerCase = item.getDescription().toLowerCase();
                if (nameToLowerCase.contains(textToLowerCase) || descriptionToLowerCase.contains(textToLowerCase)) {
                    itemSearch.add(item);
                }
            }
        }
        List<ItemDto> itemDtoList = new ArrayList<>();
        for (Item item : itemSearch) {
            itemDtoList.add(ItemMapper.toItemDto(item));
        }
        return itemDtoList;
    }


    @Override
    public ItemDto patchItem(int userId, int itemId, ItemDto itemDto) {
        userRepository.findUserById(userId);
        Item itemPatch = itemRepository.findById(itemId);
        Item item = ItemMapper.toItem(userId, itemDto);
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
        return ItemMapper.toItemDto(itemPatch);
    }


}
