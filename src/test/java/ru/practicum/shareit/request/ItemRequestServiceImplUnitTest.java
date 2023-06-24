package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingServiceImpl;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItem;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplUnitTest {

    ItemRequestService itemRequestService;

    @Mock
    ItemRequestRepository itemRequestRepository;

    @Mock
    ItemRepository itemRepository;

    @Mock
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

        itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, userRepository, itemRepository);

        user = new User(1, "name", "e@mail.com");

        user2 = new User(2, "name2", "e2@mail.com");

        item = new Item(1, "item name", "description", true, user, 1);

        item2 = new Item(2, "item name2", "description2", true, user, 2);

        booking = new Booking(1,
                LocalDateTime.now(),
                LocalDateTime.now(),
                item,
                user2,
                Status.APPROVED);

        booking2 = new Booking(2,
                LocalDateTime.of(2000, 1, 1, 12, 0),
                LocalDateTime.now(),
                item2,
                user2,
                Status.APPROVED);

        itemRequest = new ItemRequest(1, "description", user2, LocalDateTime.now());
        itemRequest2 = new ItemRequest(2, "description", user2, LocalDateTime.now());
    }

    @Test
    void createRequest() {
        CreateItemRequestDto createItemRequestDto = new CreateItemRequestDto("description");
        when(userRepository.findById(user2.getId()))
                .thenReturn(Optional.of(user2));

        when(itemRequestRepository.save(Mockito.any()))
                .thenReturn(itemRequest);

        ItemRequestDto itemRequestDto = itemRequestService.createRequest(user2.getId(), createItemRequestDto);

        assertEquals(itemRequestDto.getId(), itemRequest.getId());
    }

    @Test
    void findAllRequest() {

        when(userRepository.findById(user2.getId()))
                .thenReturn(Optional.of(user2));

        when(itemRequestRepository.findAllByRequestorId(user2.getId()))
                .thenReturn((List.of(itemRequest, itemRequest2)));

        when(itemRepository.findItemByRequestIdIn(Set.of(itemRequest.getId(), itemRequest2.getId())))
                .thenReturn(List.of(item, item2));

        List<ItemRequestDtoWithItem> itemRequestDtoWithItemList = itemRequestService.findAllRequest(user2.getId());

        assertEquals(itemRequestDtoWithItemList.size(), 2);
    }

    @Test
    void findRequestById() {

        when(userRepository.findById(user2.getId()))
                .thenReturn(Optional.of(user2));

        when(itemRequestRepository.findById(itemRequest.getId()))
                .thenReturn(Optional.of(itemRequest));

        when(itemRepository.findItemByRequestId(itemRequest.getId()))
                .thenReturn(List.of(item));

        ItemRequestDtoWithItem itemRequestDtoWithItem = itemRequestService.findRequestById(user2.getId(), itemRequest.getId());

        assertEquals(itemRequestDtoWithItem.getId(), itemRequest.getId());
    }

    @Test
    void findAllRequestWith() {
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        when(itemRequestRepository.findByRequestorIdNot(user.getId(),
                PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "created"))))
                .thenReturn(List.of(itemRequest, itemRequest2));

        when(itemRepository.findItemByRequestIdIn(Set.of(itemRequest.getId(), itemRequest2.getId())))
                .thenReturn(List.of(item, item2));

        List<ItemRequestDtoWithItem> itemRequestDtoWithItemList = itemRequestService.findAllRequestWith(user.getId(), 0, 10);

        assertEquals(itemRequestDtoWithItemList.size(), 2);
    }
}