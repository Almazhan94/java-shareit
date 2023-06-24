package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRequestRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemRequestRepository itemRequestRepository;

    User user;
    User user2;
    ItemRequest itemRequest;
    ItemRequest itemRequest2;
    Item item;
    Item item2;

    @BeforeEach
    void setUp() {
        user = new User(1, "name", "e@mail.com");
        userRepository.save(user);

        user2 = new User(2, "name2", "e2@mail.com");
        userRepository.save(user2);

        itemRequest = new ItemRequest(1, "asf", user2, LocalDateTime.now());
        itemRequestRepository.save(itemRequest);

        itemRequest2 = new ItemRequest(2, "asf", user2, LocalDateTime.now());
        itemRequestRepository.save(itemRequest2);

        item = new Item(1, "item name", "description", true, user, 1);
        itemRepository.save(item);

        item2 = new Item(2, "item name2", "description2", true, user, 2);
        itemRepository.save(item2);
    }

    @Test
    @DirtiesContext
    void findAllByRequestorIdTest() {

        List<ItemRequest> itemRequestList = itemRequestRepository.findAllByRequestorId(user2.getId());

        assertEquals(itemRequestList.size(), 2);
        assertEquals(itemRequestList.get(0).getId(), itemRequest.getId());
        assertEquals(itemRequestList.get(1).getId(), itemRequest2.getId());
    }

    @Test
    @DirtiesContext
    void findByRequestorIdNotTest() {

        List<ItemRequest> itemRequestList = itemRequestRepository.findByRequestorIdNot(user.getId(),
                PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "created")));

        assertEquals(itemRequestList.size(), 2);
        assertEquals(itemRequestList.get(0).getId(), itemRequest.getId());
        assertEquals(itemRequestList.get(1).getId(), itemRequest2.getId());
    }
}