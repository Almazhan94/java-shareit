package ru.practicum.shareit.item;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemRequestRepository itemRequestRepository;

    @Test
    @DirtiesContext
    void findItemByOwnerIdTest() {
        User user = new User(1, "name", "e@mail.com");
        userRepository.save(user);
        Item item = new Item(1, "item name", "description", true, user, null);
        itemRepository.save(item);
        List<Item> itemList = itemRepository.findItemByOwnerId(item.getOwner().getId());

        assertEquals(itemList.size(), 1);
        assertEquals(itemList.get(0), item);
        assertEquals(itemList.get(0).getOwner(), user);
    }

    @Test
    @DirtiesContext
    void findItemByOwnerIdOrderByIdAscTest() {
        User user = new User(1, "name", "e@mail.com");
        userRepository.save(user);
        Item item = new Item(1, "item name", "description", true, user, null);
        Item item2 = new Item(2, "item name2", "description2", true, user, null);
        itemRepository.save(item);
        itemRepository.save(item2);
        List<Item> itemList = itemRepository.findItemByOwnerIdOrderByIdAsc(user.getId());

        assertEquals(itemList.size(), 2);
        assertEquals(itemList.get(0), item);
        assertEquals(itemList.get(1), item2);
        assertEquals(itemList.get(0).getOwner(), user);
    }

    @Test
    @DirtiesContext
    void searchTest() {
        User user = new User(1, "name", "e@mail.com");
        userRepository.save(user);
        Item item = new Item(1, "item1 name", "item1 description1", true, user, null);
        Item item2 = new Item(2, "item2 name2", "item2 description2", true, user, null);
        itemRepository.save(item);
        itemRepository.save(item2);
        List<Item> itemList = itemRepository.search("IteM1");
        assertEquals(itemList.size(), 1);
    }

    @Test
    @DirtiesContext
    void findItemByRequestIdInTest() {
        User user = new User(1, "name", "e@mail.com");
        userRepository.save(user);

        ItemRequest itemRequest = new ItemRequest(1, "asf", user, LocalDateTime.now());
        itemRequestRepository.save(itemRequest);

        ItemRequest itemRequest2 = new ItemRequest(2, "asf", user, LocalDateTime.now());
        itemRequestRepository.save(itemRequest2);

        Item item = new Item(1, "item1 name", "item1 description1", true, user, 1);
        itemRepository.save(item);

        Item item2 = new Item(2, "item2 name", "item2 description1", true, user, 2);
        itemRepository.save(item2);

        List<Item> itemList = itemRepository.findItemByRequestIdIn(Set.of(itemRequest.getId(), itemRequest2.getId()));

        assertEquals(itemList.size(), 2);
        assertEquals(itemList.get(0), item);
        assertEquals(itemList.get(1), item2);
    }

    @Test
    @DirtiesContext
    void findItemByRequestIdTest() {
        User user = new User(1, "name", "e@mail.com");
        userRepository.save(user);

        ItemRequest itemRequest = new ItemRequest(1, "asf", user, LocalDateTime.now());
        itemRequestRepository.save(itemRequest);

        Item item = new Item(1, "item1 name", "item1 description1", true, user, 1);
        itemRepository.save(item);

        List<Item> itemList = itemRepository.findItemByRequestIdIn(Set.of(itemRequest.getId()));

        assertEquals(itemList.size(), 1);
        assertEquals(itemList.get(0), item);
    }
}