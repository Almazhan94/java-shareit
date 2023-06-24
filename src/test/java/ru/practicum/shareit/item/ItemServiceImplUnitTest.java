package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplUnitTest {

    ItemService itemService;

    @Mock
    ItemRepository itemRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    BookingRepository bookingRepository;

    @Mock
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

        itemService = new ItemServiceImpl(itemRepository, userRepository, bookingRepository, commentRepository);

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
    void createItem() {
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        when(itemRepository.save(item))
                .thenReturn(item);

        ItemDto itemDto = itemService.createItem(user.getId(), ItemMapper.toItemDto(item));

        assertEquals(itemDto.getId(), item.getId());

    }

    @Test
    void findItemById() {

        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));

        ItemDto itemDto = itemService.findItemById(item.getId());

        assertEquals(itemDto.getId(), item.getId());
    }

    @Test
    void getItemBySearch() {

        when(itemRepository.search("name"))
                .thenReturn(List.of(item, item2));

        List<ItemDto> itemDtoList = itemService.getItemBySearch("name");

        assertEquals(itemDtoList.size(), 2);

        List<ItemDto> itemDtoList2 = itemService.getItemBySearch("");

        assertEquals(itemDtoList2.size(), 0);
    }

    @Test
    void patchItem() {
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));

        when(itemRepository.save(item))
                .thenReturn(item);
        
        ItemDto itemDto = itemService.patchItem(user.getId(), item.getId(), ItemMapper.toItemDto(item));

        assertEquals(itemDto.getId(), item.getId());
    }

    @Test
    void findItemByIdWithBooking() {
        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        when(bookingRepository.findByItemIdAndStatusAndEndTimeIsBefore(Mockito.anyInt(), Mockito.any(), Mockito.any(),
                Mockito.any()))
                .thenReturn(List.of(booking));

        when(bookingRepository.findByItemIdAndStartTimeIsBeforeAndEndTimeIsAfter(Mockito.anyInt(),
                Mockito.any(),  Mockito.any()))
                .thenReturn(new ArrayList<>());

        when(bookingRepository.findByItemIdAndStatusAndStartTimeIsAfter(Mockito.anyInt(), Mockito.any(), Mockito.any(),
                Mockito.any()))
                .thenReturn(List.of(booking2));

        when(commentRepository.findAllByItemId(item.getId()))
                .thenReturn(List.of(comment));

        ItemBookingDto itemBookingDto = itemService.findItemByIdWithBooking(user.getId(), item.getId());

        assertEquals(itemBookingDto.getId(), item.getId());
        assertEquals(itemBookingDto.getLastBooking().getId(), booking.getId());
        assertEquals(itemBookingDto.getNextBooking().getId(), booking2.getId());
        assertEquals(itemBookingDto.getComments().size(), 1);
    }

    @Test
    void findAllItemWithBooking() {
        when(itemRepository.findItemByOwnerIdOrderByIdAsc(user.getId()))
                .thenReturn(List.of(item));

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        when(bookingRepository.findByItemIdAndEndTimeIsBefore(Mockito.anyInt(), Mockito.any(), Mockito.any()))
                .thenReturn(List.of(booking));

        when(bookingRepository.findByItemIdAndStartTimeIsAfter(Mockito.anyInt(), Mockito.any(), Mockito.any()))
                .thenReturn(List.of(booking2));

        when(commentRepository.findAllByItemId(item.getId()))
                .thenReturn(List.of(comment));

        List<ItemBookingDto> itemBookingDtoList = itemService.findAllItemWithBooking(user.getId());

        assertEquals(itemBookingDtoList.size(), 1);
        assertEquals(itemBookingDtoList.get(0).getLastBooking().getId(), booking.getId());
        assertEquals(itemBookingDtoList.get(0).getNextBooking().getId(), booking2.getId());
        assertEquals(itemBookingDtoList.get(0).getComments().size(), 1);

    }

    @Test
    void addComment() {
        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        when(bookingRepository.findByBookerIdAndItemIdAndStatusAndEndTimeIsBefore(Mockito.anyInt(), Mockito.anyInt(),
                Mockito.any(), Mockito.any()))
                .thenReturn(List.of(booking));

        when(commentRepository.save(Mockito.any()))
                .thenReturn(comment);

        CommentDto commentDto = itemService.addComment(user.getId(), item.getId(), new AddCommentDto("text"));

        assertEquals(commentDto.getText(), comment.getText());
    }
}