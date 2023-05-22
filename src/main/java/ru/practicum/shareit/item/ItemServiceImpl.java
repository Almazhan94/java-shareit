package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ItemDto createItem(int userId, ItemDto itemDto) {
        User owner = userRepository.findUserById(userId);
        Item item = ItemMapper.toItem(owner, itemDto);
        Item createItem = itemRepository.create(owner, item);
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
                if (item.getName().toLowerCase().contains(textToLowerCase) || item.getDescription().toLowerCase().contains(textToLowerCase)) {
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
        User owner = userRepository.findUserById(userId);
        Item itemPatch = itemRepository.findById(itemId);
        Item item = ItemMapper.toItem(owner, itemDto);
        if (itemPatch.getOwner().getId() == owner.getId()) {
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
