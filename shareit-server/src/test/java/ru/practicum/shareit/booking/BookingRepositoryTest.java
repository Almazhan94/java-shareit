package ru.practicum.shareit.booking;

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
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    BookingRepository bookingRepository;

    User user;
    User user2;
    Item item;
    Item item2;
    Booking booking;
    Booking booking2;

    @BeforeEach
    void setUp() {
         user = new User(1, "name", "e@mail.com");
        userRepository.save(user);

         user2 = new User(2, "name2", "e2@mail.com");
        userRepository.save(user2);

         item = new Item(1, "item name", "description", true, user, null);
        itemRepository.save(item);

         item2 = new Item(2, "item name2", "description2", true, user, null);
        itemRepository.save(item2);

         booking = new Booking(1,
                LocalDateTime.now(),
                LocalDateTime.now(),
                item,
                user2,
                Status.APPROVED);
        bookingRepository.save(booking);

         booking2 = new Booking(2,
                LocalDateTime.of(2000, 1, 1, 12, 0),
                LocalDateTime.now(),
                item2,
                user2,
                Status.APPROVED);
        bookingRepository.save(booking2);

    }

    @Test
    @DirtiesContext
    void findByBookerIdTest() {
        List<Booking> bookingList = bookingRepository.findByBookerId(user2.getId(),
                PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "startTime")));

        assertEquals(bookingList.size(), 2);
        assertEquals(bookingList.get(0).getId(), booking.getId());
        assertEquals(bookingList.get(1).getId(), booking2.getId());
    }

    @Test
    @DirtiesContext
    void findAllByItemIdInTest() {

        List<Booking> bookingList = bookingRepository.findAllByItemIdIn(Set.of(item.getId(), item2.getId()),
                PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "startTime")));

        assertEquals(bookingList.size(), 2);
        assertEquals(bookingList.get(0), booking);
        assertEquals(bookingList.get(1), booking2);
    }

    @Test
    @DirtiesContext
    void findByBookerIdAndStatusTest() {
        booking2.setStatus(Status.REJECTED);
        bookingRepository.save(booking2);
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "startTime"));
        List<Booking> bookingList = bookingRepository.findByBookerIdAndStatus(user2.getId(), Status.APPROVED, pageable);
        List<Booking> bookingListREJECTED = bookingRepository.findByBookerIdAndStatus(user2.getId(), Status.REJECTED,
            pageable);

        assertEquals(bookingList.size(), 1);
        assertEquals(bookingListREJECTED.size(), 1);
        assertEquals(bookingList.get(0), booking);
        assertEquals(bookingListREJECTED.get(0), booking2);
    }

    @Test
    @DirtiesContext
    void findByItemIdInAndStatusTest() {
        List<Booking> bookingList = bookingRepository.findByItemIdInAndStatus(Set.of(item.getId(), item2.getId()),
            Status.APPROVED, PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "startTime")));

        assertEquals(bookingList.size(), 2);
        assertEquals(bookingList.get(0), booking);
        assertEquals(bookingList.get(1), booking2);
    }

    @Test
    @DirtiesContext
    void findByBookerIdAndStartTimeIsBeforeAndEndTimeIsAfterTest() {

        booking.setStartTime(LocalDateTime.of(2000, 1, 1, 12, 0));
        booking.setEndTime(LocalDateTime.now());
        bookingRepository.save(booking);

        booking2.setStartTime(LocalDateTime.of(2000, 1, 1, 12, 0));
        booking2.setEndTime(LocalDateTime.of(2030, 1, 1, 12, 0));
        bookingRepository.save(booking2);

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "startTime"));

        List<Booking> bookingList = bookingRepository.findByBookerIdAndStartTimeIsBeforeAndEndTimeIsAfter(user2.getId(),
                LocalDateTime.now(),
                LocalDateTime.now(), pageable);
        assertEquals(bookingList.size(), 1);
        assertEquals(bookingList.get(0), booking2);
    }

    @Test
    @DirtiesContext
    void findByBookerIdAndEndTimeIsBeforeTest() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "endTime"));
        List<Booking> bookingList = bookingRepository.findByBookerIdAndEndTimeIsBefore(user2.getId(),
                LocalDateTime.now(), pageable);
        assertEquals(bookingList.size(), 2);
        assertEquals(bookingList.get(0), booking2);
        assertEquals(bookingList.get(1), booking);
    }

    @Test
    @DirtiesContext
    void findByBookerIdAndStartTimeIsAfterTest() {
        booking.setStartTime(LocalDateTime.of(2024, 1, 1, 12, 0));
        booking.setEndTime(LocalDateTime.of(2024, 6, 1, 12, 0));
        bookingRepository.save(booking);

        booking2.setStartTime(LocalDateTime.of(2025, 1, 1, 12, 0));
        booking2.setEndTime(LocalDateTime.of(2030, 1, 1, 12, 0));
        bookingRepository.save(booking2);

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "startTime"));

        List<Booking> bookingList = bookingRepository.findByBookerIdAndStartTimeIsAfter(user2.getId(), LocalDateTime.now(),
                pageable);

        assertEquals(bookingList.size(), 2);
        assertEquals(bookingList.get(0), booking2);
        assertEquals(bookingList.get(1), booking);
    }


    @Test
    @DirtiesContext
    void findByItemIdInAndStartTimeIsBeforeAndEndTimeIsAfterTest() {
        booking.setStartTime(LocalDateTime.of(2010, 1, 1, 12, 0));
        booking.setEndTime(LocalDateTime.of(2024, 6, 1, 12, 0));
        bookingRepository.save(booking);

        booking2.setStartTime(LocalDateTime.of(2015, 1, 1, 12, 0));
        booking2.setEndTime(LocalDateTime.of(2030, 1, 1, 12, 0));
        bookingRepository.save(booking2);

        List<Booking> bookingList = bookingRepository.findByItemIdInAndStartTimeIsBeforeAndEndTimeIsAfter(Set.of(item.getId(), item2.getId()),
                LocalDateTime.now(), LocalDateTime.now(), PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "startTime")));

        assertEquals(bookingList.size(), 2);
        assertEquals(bookingList.get(0), booking2);
        assertEquals(bookingList.get(1), booking);

    }

    @Test
    @DirtiesContext
    void findByItemIdInAndEndTimeIsAfterTest() {
        booking.setStartTime(LocalDateTime.of(2024, 1, 1, 12, 0));
        booking.setEndTime(LocalDateTime.of(2024, 6, 1, 12, 0));
        bookingRepository.save(booking);

        booking2.setStartTime(LocalDateTime.of(2025, 1, 1, 12, 0));
        booking2.setEndTime(LocalDateTime.of(2030, 1, 1, 12, 0));
        bookingRepository.save(booking2);

        List<Booking> bookingList = bookingRepository.findByItemIdInAndEndTimeIsAfter(Set.of(item.getId(), item2.getId()),
                LocalDateTime.now(), PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "startTime")));

        assertEquals(bookingList.size(), 2);
        assertEquals(bookingList.get(0), booking2);
        assertEquals(bookingList.get(1), booking);
    }

    @Test
    @DirtiesContext
    void findByItemIdInAndEndTimeIsBeforeTest() {

        List<Booking> bookingList = bookingRepository.findByItemIdInAndEndTimeIsBefore(Set.of(item.getId(), item2.getId()),
                LocalDateTime.now(), PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "startTime")));

        assertEquals(bookingList.size(), 2);
        assertEquals(bookingList.get(0), booking);
        assertEquals(bookingList.get(1), booking2);
    }

    @Test
    @DirtiesContext
    void findByItemIdAndEndTimeIsBeforeTest() {
        booking2.setItem(item);
        bookingRepository.save(booking2);

        List<Booking> bookingList = bookingRepository.findByItemIdAndEndTimeIsBefore(item.getId(), LocalDateTime.now(),
                Sort.by(Sort.Direction.DESC, "endTime"));

        assertEquals(bookingList.size(), 2);
        assertEquals(bookingList.get(0), booking2);
        assertEquals(bookingList.get(1), booking);
    }

    @Test
    @DirtiesContext
    void findByItemIdAndStartTimeIsAfterTest() {

        booking.setStartTime(LocalDateTime.of(2024, 1, 1, 12, 0));
        booking.setEndTime(LocalDateTime.of(2024, 6, 1, 12, 0));
        bookingRepository.save(booking);

        booking2.setStartTime(LocalDateTime.of(2025, 1, 1, 12, 0));
        booking2.setEndTime(LocalDateTime.of(2030, 1, 1, 12, 0));
        booking2.setItem(item);
        bookingRepository.save(booking2);

        List<Booking> bookingList = bookingRepository.findByItemIdAndStartTimeIsAfter(item.getId(), LocalDateTime.now(),
                Sort.by("startTime"));

        assertEquals(bookingList.size(), 2);
        assertEquals(bookingList.get(0), booking);
        assertEquals(bookingList.get(1), booking2);
    }

    @Test
    @DirtiesContext
    void findByBookerIdAndItemIdAndStatusAndEndTimeIsBeforeTest() {

        booking2.setItem(item);
        bookingRepository.save(booking2);

        List<Booking> bookingList = bookingRepository.findByBookerIdAndItemIdAndStatusAndEndTimeIsBefore(user2.getId(),
                item.getId(), Status.APPROVED, LocalDateTime.now());

        assertEquals(bookingList.size(), 2);
        assertEquals(bookingList.get(0), booking);
        assertEquals(bookingList.get(1), booking2);
    }

    @Test
    @DirtiesContext
    void findByItemIdAndStatusAndStartTimeIsAfterTest() {
        booking.setStartTime(LocalDateTime.of(2024, 1, 1, 12, 0));
        booking.setEndTime(LocalDateTime.of(2024, 6, 1, 12, 0));
        bookingRepository.save(booking);

        booking2.setStartTime(LocalDateTime.of(2025, 1, 1, 12, 0));
        booking2.setEndTime(LocalDateTime.of(2030, 1, 1, 12, 0));
        booking2.setItem(item);
        bookingRepository.save(booking2);

        List<Booking> bookingList = bookingRepository.findByItemIdAndStatusAndStartTimeIsAfter(
                item.getId(), Status.APPROVED, LocalDateTime.now(), Sort.by("startTime"));

        assertEquals(bookingList.size(), 2);
        assertEquals(bookingList.get(0), booking);
        assertEquals(bookingList.get(1), booking2);
    }

    @Test
    @DirtiesContext
    void findByItemIdAndStatusAndEndTimeIsBeforeTest() {
        booking2.setItem(item);
        bookingRepository.save(booking2);

        List<Booking> bookingList = bookingRepository.findByItemIdAndStatusAndEndTimeIsBefore(
                item.getId(),Status.APPROVED,  LocalDateTime.now(),Sort.by(Sort.Direction.DESC, "endTime"));

        assertEquals(bookingList.size(), 2);
        assertEquals(bookingList.get(0), booking2);
        assertEquals(bookingList.get(1), booking);
    }

    @Test
    @DirtiesContext
    void findByItemIdAndStartTimeIsBeforeAndEndTimeIsAfterTest() {
        booking.setEndTime(LocalDateTime.of(2024, 6, 1, 12, 0));
        bookingRepository.save(booking);

        booking2.setEndTime(LocalDateTime.of(2030, 1, 1, 12, 0));
        booking2.setItem(item);
        bookingRepository.save(booking2);

        List<Booking> bookingList = bookingRepository.findByItemIdAndStartTimeIsBeforeAndEndTimeIsAfter(item.getId(),
                LocalDateTime.now(), LocalDateTime.now());

        assertEquals(bookingList.size(), 2);
        assertEquals(bookingList.get(0), booking);
        assertEquals(bookingList.get(1), booking2);
    }
}