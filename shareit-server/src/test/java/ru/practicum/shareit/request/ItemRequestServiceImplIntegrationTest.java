package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.error.UserNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItem;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ItemRequestServiceImplIntegrationTest {

    @Autowired
    ItemRequestService itemRequestService;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    UserRepository userRepository;

    User user;
    User user2;
    Item item;
    Item item2;
    Booking booking;
    Booking booking2;
    ItemRequest itemRequest;
    ItemRequest itemRequest2;

    @BeforeEach
    void setUp() {

        user = new User(1, "name", "e@mail.com");

        user2 = new User(2, "name2", "e2@mail.com");

        item = new Item(1, "item name", "description", true, user, null);

        item2 = new Item(2, "item name2", "description2", true, user, null);

        booking = new Booking(1,
                LocalDateTime.now(),
                LocalDateTime.now(),
                item,
                user2,
                Status.APPROVED);

        booking2 = new Booking(2,
                LocalDateTime.now(),
                LocalDateTime.now(),
                item2,
                user2,
                Status.APPROVED);

        itemRequest = new ItemRequest(1, "description", user2, LocalDateTime.now());
        itemRequest2 = new ItemRequest(2, "description", user2, LocalDateTime.now());
    }


    @Test
    @DirtiesContext
    void createRequest() {
        userRepository.save(user);
        userRepository.save(user2);
        itemRepository.save(item);
        itemRepository.save(item2);

        ItemRequestDto itemRequestDto = itemRequestService.createRequest(user2.getId(),
                new CreateItemRequestDto("description"));

        assertEquals(itemRequestDto.getId(), itemRequest.getId());

        assertThrows(UserNotFoundException.class, () -> itemRequestService.createRequest(100,
                new CreateItemRequestDto("description")));
    }

    @Test
    @DirtiesContext
    void findAllRequest() {
        userRepository.save(user);
        userRepository.save(user2);
        itemRepository.save(item);
        itemRepository.save(item2);

        itemRequestService.createRequest(user2.getId(),
                new CreateItemRequestDto("description"));

        item.setRequestId(1);
        itemRepository.save(item);

        List<ItemRequestDtoWithItem> itemRequestDtoWithItemList = itemRequestService.findAllRequest(user2.getId());

        assertEquals(itemRequestDtoWithItemList.size(), 1);
    }

    @Test
    @DirtiesContext
    void findRequestById() {
        userRepository.save(user);
        userRepository.save(user2);
        itemRepository.save(item);
        itemRepository.save(item2);

        itemRequestService.createRequest(user2.getId(),
                new CreateItemRequestDto("description"));

        ItemRequestDtoWithItem itemRequestDtoWithItem = itemRequestService.findRequestById(user2.getId(), 1);

        assertEquals(itemRequestDtoWithItem.getId(), 1);
        assertEquals(itemRequestDtoWithItem.getItems().size(), 0);
    }

    @Test
    @DirtiesContext
    void findAllRequestWith() {
        userRepository.save(user);
        userRepository.save(user2);
        itemRepository.save(item);
        itemRepository.save(item2);

        itemRequestService.createRequest(user2.getId(),
                new CreateItemRequestDto("description"));

        List<ItemRequestDtoWithItem> itemRequestDtoWithItemList =
                itemRequestService.findAllRequestWith(user.getId(), 0, 10);

        assertEquals(itemRequestDtoWithItemList.size(), 1);
        assertEquals(itemRequestDtoWithItemList.get(0).getItems().size(), 0);
    }
}