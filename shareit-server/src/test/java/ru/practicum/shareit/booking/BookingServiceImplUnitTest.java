package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplUnitTest {

    BookingService bookingService;

    @Mock
    ItemRepository itemRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    BookingRepository bookingRepository;

    User user;
    User user2;
    Item item;
    Item item2;
    Booking booking;
    Booking booking2;

    @BeforeEach
    void setUp() {

        bookingService = new BookingServiceImpl(bookingRepository, itemRepository, userRepository);

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
                LocalDateTime.of(2000, 1, 1, 12, 0),
                LocalDateTime.now(),
                item2,
                user2,
                Status.APPROVED);
    }


    @Test
    void create() {
        LocalDateTime start = LocalDateTime.of(2025, 1, 1, 12, 0);
        LocalDateTime end = LocalDateTime.of(2030, 1, 1, 12, 0);
        CreateBookingDto createBookingDto = new CreateBookingDto(item.getId(), start, end);

        when(userRepository.findById(user2.getId()))
                .thenReturn(Optional.of(user2));

        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));

        when(bookingRepository.save(Mockito.any()))
                .thenReturn(booking);

        BookingDto bookingDto = bookingService.create(user2.getId(), createBookingDto);

        assertEquals(bookingDto.getId(), booking.getId());

    }

    @Test
    void findById() {
        when(userRepository.findById(user2.getId()))
                .thenReturn(Optional.of(user2));

        when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));

        BookingDto bookingDto = bookingService.findById(user2.getId(), booking.getId());

        assertEquals(bookingDto.getId(), booking.getId());
    }

    @Test
    void update() {
        booking.setStatus(Status.WAITING);
        when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));

        when(bookingRepository.save(booking))
                .thenReturn(booking);

        BookingDto bookingDto = bookingService.update(user.getId(), booking.getId(), "true");

        assertEquals(bookingDto.getId(), booking.getId());
    }

    @Test
    void findByState() {
        when(userRepository.findById(user2.getId()))
                .thenReturn(Optional.of(user2));

        when(bookingRepository.findByBookerId(user2.getId(),
                PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "startTime"))))
                .thenReturn(List.of(booking, booking2));

        List<BookingDto> bookingDtoList = bookingService.findByState(user2.getId(), "ALL", 0, 10);

        assertEquals(bookingDtoList.size(), 2);
    }

    @Test
    void findOwnerBooking() {
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        when(itemRepository.findItemByOwnerId(user.getId()))
                .thenReturn(List.of(item, item2));

        when(bookingRepository.findAllByItemIdIn(Set.of(item.getId(), item2.getId()),
                PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "startTime"))))
                .thenReturn(List.of(booking, booking2));

        List<BookingDto> bookingDtoList = bookingService.findOwnerBooking(user.getId(), "ALL", 0, 10);

        assertEquals(bookingDtoList.size(), 2);
    }
}