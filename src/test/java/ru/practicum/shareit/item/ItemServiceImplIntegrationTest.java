package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.dto.AddCommentDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ItemServiceImplIntegrationTest {

    @Autowired
    ItemService itemService;

    @Autowired
    UserRepository userRepository;

    @Autowired
     BookingRepository bookingRepository;

    @Autowired
     CommentRepository commentRepository;

    User user;
    User user2;
    Item item;
    Item item2;
    Booking booking;
    Booking booking2;
    Comment comment;

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

        comment = new Comment(1, "text", item, user2, LocalDateTime.now());
    }


    @Test
    @DirtiesContext
    void createItem() {
        userRepository.save(user);

        ItemDto itemDto = itemService.createItem(user.getId(),
                new ItemDto(null, "item name", "description", true, null));

        assertEquals(itemDto.getId(), item.getId());
    }

    @Test
    @DirtiesContext
    void findItemById() {
        userRepository.save(user);

        itemService.createItem(user.getId(),
                new ItemDto(null, "item name", "description", true, null));

        ItemDto itemDto = itemService.findItemById(1);

        assertEquals(itemDto.getId(), item.getId());

    }

    @Test
    @DirtiesContext
    void getItemBySearch() {
        userRepository.save(user);

        itemService.createItem(user.getId(),
                new ItemDto(null, "item name", "description", true, null));

        List<ItemDto> itemDtoList = itemService.getItemBySearch("name");

        assertEquals(itemDtoList.size(), 1);
    }

    @Test
    @DirtiesContext
    void patchItem() {
        userRepository.save(user);

        itemService.createItem(user.getId(),
                new ItemDto(null, "item name", "description", true, null));

        ItemDto itemDto = itemService.patchItem(user.getId(), item.getId(),
                new ItemDto(null, "another item name", "another description", true, null));

        assertEquals(itemDto.getId(), item.getId());
        assertEquals(itemDto.getName(), "another item name");
    }

    @Test
    @DirtiesContext
    void findItemByIdWithBooking() {
        userRepository.save(user);
        userRepository.save(user2);
        itemService.createItem(user.getId(),
                new ItemDto(null, "item name", "description", true, null));

        itemService.createItem(user.getId(),
                new ItemDto(null, "item name2", "description2", true, null));

        booking.setStartTime(LocalDateTime.of(2015, 1, 1, 12, 0));
        booking.setEndTime(LocalDateTime.of(2020, 1, 1, 12, 0));

        booking2.setStartTime(LocalDateTime.of(2025, 1, 1, 12, 0));
        booking2.setEndTime(LocalDateTime.of(2030, 1, 1, 12, 0));

        bookingRepository.save(booking);
        bookingRepository.save(booking2);

        commentRepository.save(comment);

        ItemBookingDto itemBookingDto = itemService.findItemByIdWithBooking(user.getId(), item.getId());

        assertEquals(itemBookingDto.getId(), item.getId());
        assertEquals(itemBookingDto.getLastBooking().getId(), booking.getId());
        assertNull(itemBookingDto.getNextBooking());
        assertEquals(itemBookingDto.getComments().size(), 1);
    }

    @Test
    @DirtiesContext
    void findAllItemWithBooking() {
        userRepository.save(user);
        userRepository.save(user2);
        itemService.createItem(user.getId(),
                new ItemDto(null, "item name", "description", true, null));

        itemService.createItem(user.getId(),
                new ItemDto(null, "item name2", "description2", true, null));

        booking.setStartTime(LocalDateTime.of(2015, 1, 1, 12, 0));
        booking.setEndTime(LocalDateTime.of(2020, 1, 1, 12, 0));

        booking2.setStartTime(LocalDateTime.of(2025, 1, 1, 12, 0));
        booking2.setEndTime(LocalDateTime.of(2030, 1, 1, 12, 0));

        bookingRepository.save(booking);
        bookingRepository.save(booking2);

        commentRepository.save(comment);

        List<ItemBookingDto> itemBookingDtoList = itemService.findAllItemWithBooking(user.getId());

        assertEquals(itemBookingDtoList.size(), 2);
        assertEquals(itemBookingDtoList.get(0).getLastBooking().getId(), booking.getId());
        assertEquals(itemBookingDtoList.get(0).getComments().size(), 1);
        assertEquals(itemBookingDtoList.get(1).getNextBooking().getId(), booking2.getId());
    }

    @Test
    @DirtiesContext
    void addComment() {
        userRepository.save(user);
        userRepository.save(user2);
        itemService.createItem(user.getId(),
                new ItemDto(null, "item name", "description", true, null));

        itemService.createItem(user.getId(),
                new ItemDto(null, "item name2", "description2", true, null));

        booking.setStartTime(LocalDateTime.of(2015, 1, 1, 12, 0));
        booking.setEndTime(LocalDateTime.of(2020, 1, 1, 12, 0));

        booking2.setStartTime(LocalDateTime.of(2025, 1, 1, 12, 0));
        booking2.setEndTime(LocalDateTime.of(2030, 1, 1, 12, 0));

        bookingRepository.save(booking);
        bookingRepository.save(booking2);

        CommentDto commentDto = itemService.addComment(user2.getId(), item.getId(), new AddCommentDto("text"));

        assertEquals(commentDto.getId(), comment.getId());
        assertEquals(commentDto.getAuthorName(), user2.getName());
        assertEquals(commentDto.getText(), comment.getText());

    }
}