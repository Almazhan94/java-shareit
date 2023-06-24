package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.error.UserNotFoundException;
import ru.practicum.shareit.error.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BookingServiceImplIntegrationTest {

    @Autowired
    BookingService bookingService;

    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRepository itemRepository;

    User user;
    User user2;
    Item item;
    Item item2;
    Booking booking;
    Booking booking2;

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
                LocalDateTime.of(2000, 1, 1, 12, 0),
                LocalDateTime.now(),
                item2,
                user2,
                Status.APPROVED);
    }

    @Test
    @DirtiesContext
    void create() {
        userRepository.save(user);
        userRepository.save(user2);
        itemRepository.save(item);
        itemRepository.save(item2);

        BookingDto bookingDto = bookingService.create(user2.getId(),
                new CreateBookingDto(item.getId(),
                        LocalDateTime.of(2025, 1, 1, 12, 0),
                        LocalDateTime.of(2030, 1, 1, 12, 0)));

        assertEquals(bookingDto.getId(), 1);
        assertEquals(bookingDto.getBooker().getId(), user2.getId());
        assertEquals(bookingDto.getStatus(), Status.WAITING);

        /*assertThrows(ValidationException.class, () -> bookingService.create(user2.getId(),
                new CreateBookingDto(item.getId(),
                        LocalDateTime.now(),
                        LocalDateTime.now())));*/
    }

    @Test
    @DirtiesContext
    void findById() {
        userRepository.save(user);
        userRepository.save(user2);
        itemRepository.save(item);
        itemRepository.save(item2);

        bookingService.create(user2.getId(),
                new CreateBookingDto(item.getId(),
                        LocalDateTime.of(2025, 1, 1, 12, 0),
                        LocalDateTime.of(2030, 1, 1, 12, 0)));

        BookingDto bookingDto = bookingService.findById(user2.getId(), 1);

        assertEquals(bookingDto.getId(), 1);
        assertEquals(bookingDto.getBooker().getId(), user2.getId());
        assertEquals(bookingDto.getStatus(), Status.WAITING);
    }

    @Test
    @DirtiesContext
    void update() {
        userRepository.save(user);
        userRepository.save(user2);
        itemRepository.save(item);
        itemRepository.save(item2);

        bookingService.create(user2.getId(),
                new CreateBookingDto(item.getId(),
                        LocalDateTime.of(2025, 1, 1, 12, 0),
                        LocalDateTime.of(2030, 1, 1, 12, 0)));

        BookingDto bookingDto = bookingService.update(user.getId(), 1, "true");

        assertEquals(bookingDto.getId(), 1);
        assertEquals(bookingDto.getStatus(), Status.APPROVED);

        assertThrows(ValidationException.class, () -> bookingService.update(user.getId(), 1, "true"));
        assertThrows(UserNotFoundException.class, () -> bookingService.update(user2.getId(), 1, "true"));
    }

    @Test
    @DirtiesContext
    void findByState() {
        userRepository.save(user);
        userRepository.save(user2);
        itemRepository.save(item);
        itemRepository.save(item2);

        bookingService.create(user2.getId(),
                new CreateBookingDto(item.getId(),
                        LocalDateTime.of(2025, 1, 1, 12, 0),
                        LocalDateTime.of(2030, 1, 1, 12, 0)));

        bookingService.create(user2.getId(),
                new CreateBookingDto(item2.getId(),
                        LocalDateTime.of(2035, 1, 1, 12, 0),
                        LocalDateTime.of(2040, 1, 1, 12, 0)));

        List<BookingDto> bookingDtoList = bookingService.findByState(user2.getId(), "ALL", 0, 10);

        assertEquals(bookingDtoList.size(), 2);
        assertEquals(bookingDtoList.get(0).getStatus(), Status.WAITING);
        assertEquals(bookingDtoList.get(1).getStatus(), Status.WAITING);

        bookingService.update(user.getId(), 1, "true");

        List<BookingDto> bookingDtoList2 = bookingService.findByState(user2.getId(), "WAITING", 0, 10);

        assertEquals(bookingDtoList2.size(), 1);
        assertEquals(bookingDtoList2.get(0).getStatus(), Status.WAITING);
    }

    @Test
    @DirtiesContext
    void findOwnerBooking() {
        userRepository.save(user);
        userRepository.save(user2);
        itemRepository.save(item);
        itemRepository.save(item2);

        bookingService.create(user2.getId(),
                new CreateBookingDto(item.getId(),
                        LocalDateTime.of(2025, 1, 1, 12, 0),
                        LocalDateTime.of(2030, 1, 1, 12, 0)));

        bookingService.create(user2.getId(),
                new CreateBookingDto(item2.getId(),
                        LocalDateTime.of(2035, 1, 1, 12, 0),
                        LocalDateTime.of(2040, 1, 1, 12, 0)));

        List<BookingDto> bookingDtoList = bookingService.findOwnerBooking(user.getId(), "WAITING", 0, 10);

        assertEquals(bookingDtoList.size(), 2);
        assertEquals(bookingDtoList.get(0).getStatus(), Status.WAITING);
        assertEquals(bookingDtoList.get(1).getStatus(), Status.WAITING);

        List<BookingDto> bookingDtoListWAITING =
                bookingService.findOwnerBooking(user.getId(), "WAITING", 0, 10);

        assertEquals(bookingDtoList.size(), 2);
        assertEquals(bookingDtoList.get(0).getStatus(), Status.WAITING);
        assertEquals(bookingDtoList.get(1).getStatus(), Status.WAITING);
    }
}