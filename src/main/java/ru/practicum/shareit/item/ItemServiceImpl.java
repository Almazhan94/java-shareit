package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.error.ItemNotFoundException;
import ru.practicum.shareit.error.UserNotFoundException;
import ru.practicum.shareit.error.ValidationException;
import ru.practicum.shareit.item.dto.AddCommentDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.*;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepositoryDb itemRepositoryDb;

    private final UserService userService;

    private final BookingRepository bookingRepository;

    private final CommentRepository commentRepository;

    @Autowired
    public ItemServiceImpl(ItemRepositoryDb itemRepositoryDb, UserService userService, BookingRepository bookingRepository, CommentRepository commentRepository) {
        this.itemRepositoryDb = itemRepositoryDb;
        this.userService = userService;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public ItemDto createItem(int userId, ItemDto itemDto) {
        UserDto ownerInDto = userService.findUserById(userId);
        User owner = UserMapper.toUser(ownerInDto);
        Item item = ItemMapper.toItem(owner, itemDto);
        Item createItem = itemRepositoryDb.save(item);
        return ItemMapper.toItemDto(createItem);
    }

    @Override
    public ItemDto findItemById(int itemId) {
        Optional<Item> item = itemRepositoryDb.findById(itemId);
        if (item.isPresent()) {
            return ItemMapper.toItemDto(item.get());
        } else {
            throw new ItemNotFoundException(String.format("Вещь с идентификатором %d не существует", itemId));
        }
    }

    @Override
    public List<ItemDto> findItemByUserId(int userId) {
        UserDto ownerInDto = userService.findUserById(userId);
        List<Item> itemList = itemRepositoryDb.findItemByOwnerId(userId);
        List<ItemDto> itemDtoList = new ArrayList<>();
        for (Item item : itemList) {
            itemDtoList.add(ItemMapper.toItemDto(item));
        }
        return itemDtoList;
    }

    @Override
    public List<ItemDto> getItemBySearch(String text, int ownerId) {
        List<Item> itemSearch = new ArrayList<>();
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }
        itemSearch = itemRepositoryDb.search(text, ownerId);
        List<ItemDto> itemDtoList = new ArrayList<>();
        for (Item item : itemSearch) {
            itemDtoList.add(ItemMapper.toItemDto(item));
        }
        return itemDtoList;
    }

    @Override
    public ItemDto patchItem(int userId, int itemId, ItemDto itemDto) {
        UserDto ownerInDto = userService.findUserById(userId);
        User owner = UserMapper.toUser(ownerInDto);
        ItemDto itemPatchInDto = findItemById(itemId);
        Item itemPatch = ItemMapper.toItem(owner, itemPatchInDto);
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
            itemRepositoryDb.save(itemPatch);
        } else {
            throw new UserNotFoundException(String.format("Пользователь с идентификатором %d не является владельцем вещи.", userId));
        }
        return ItemMapper.toItemDto(itemPatch);
    }

    @Override
    public Item findItemFromDb(int itemId) {
        return itemRepositoryDb.getReferenceById(itemId);
    }

    @Override
    public ItemBookingDto findItemByIdWithBooking(int userId, int itemId) {
        LocalDateTime time = LocalDateTime.now();
        Optional<Item> item = itemRepositoryDb.findById(itemId);
        userService.findUserById(userId);
        Booking lastBooking = new Booking();
        Booking nextBooking = new Booking();
        List<Comment> commentList = new ArrayList<>();
        List<CommentDto> commentDtoList = new ArrayList<>();
        if (item.isPresent()) {
            if (item.get().getOwner().getId() == userId) {
                List<Booking> lastBookingList = bookingRepository.findByItemIdAndStatusAndEndTimeIsBeforeOrderByEndTimeDesc(itemId, Status.APPROVED, time);
                if (!lastBookingList.isEmpty()) {
                    lastBooking = lastBookingList.get(0);
                }
                List<Booking> currentBookingList = bookingRepository.findByItemIdAndStartTimeIsBeforeAndEndTimeIsAfter(itemId, time, time);
                if (!currentBookingList.isEmpty()) {
                    lastBooking = currentBookingList.get(0);
                }
                List<Booking> nextBookingList = bookingRepository.findByItemIdAndStatusAndStartTimeIsAfterOrderByStartTime(itemId, Status.APPROVED, time);
                if (!nextBookingList.isEmpty()) {
                    nextBooking = nextBookingList.get(0);
                }
                commentList = commentRepository.findAllByItemId(itemId);
                if (!commentList.isEmpty()) {
                   for (Comment comment : commentList) {
                       commentDtoList.add(ItemMapper.toCommentDto(comment));
                   }
                }
                return ItemMapper.toItemBookingDto(lastBooking, nextBooking, item.get(), commentDtoList);
            } else {
                commentList = commentRepository.findAllByItemId(itemId);
                if (!commentList.isEmpty()) {
                    for (Comment comment : commentList) {
                        commentDtoList.add(ItemMapper.toCommentDto(comment));
                    }
                }
                return ItemMapper.toItemBookingDto(lastBooking, nextBooking, item.get(), commentDtoList);
            }
        } else {
            throw new ItemNotFoundException(String.format("Вещь с идентификатором %d не существует", itemId));
        }
    }

    @Override
    public List<ItemBookingDto> findAllItemWithBooking(int userId) {
        LocalDateTime time = LocalDateTime.now();
        userService.findUserById(userId);
        List<Item> itemList = itemRepositoryDb.findItemByOwnerIdOrderByIdAsc(userId);
        List<ItemBookingDto> itemBookingDtoList = new ArrayList<>();
        Booking lastBooking = new Booking();
        Booking nextBooking = new Booking();
        List<Comment> commentList = new ArrayList<>();
        List<CommentDto> commentDtoList = new ArrayList<>();
        if (!itemList.isEmpty()) {
            for (Item item : itemList) {
                List<Booking> lastBookingList = bookingRepository.findByItemIdAndEndTimeIsBeforeOrderByEndTime(item.getId(), time);
                if (!lastBookingList.isEmpty()) {
                    lastBooking = lastBookingList.get(0);
                } else {
                    lastBooking = null;
                }
                List<Booking> nextBookingList = bookingRepository.findByItemIdAndStartTimeIsAfterOrderByStartTime(item.getId(), time);
                if (!nextBookingList.isEmpty()) {
                    nextBooking = nextBookingList.get(0);
                } else {
                    nextBooking = null;
                }
                commentList = commentRepository.findAllByItemId(item.getId());
                if (!commentList.isEmpty()) {
                    for (Comment comment : commentList) {
                        commentDtoList.add(ItemMapper.toCommentDto(comment));
                    }
                }
                itemBookingDtoList.add(ItemMapper.toItemBookingDto(lastBooking, nextBooking, item, commentDtoList));
            }
        }
        return itemBookingDtoList;
    }

    @Override
    public CommentDto addComment(Integer userId, int itemId, AddCommentDto addCommentDto) {
        LocalDateTime time = LocalDateTime.now();
        userService.findUserById(userId);
        findItemById(itemId);
        List<Booking> userBookingList = bookingRepository.findByBookerIdAndItemIdAndStatusAndEndTimeIsBefore(userId, itemId, Status.APPROVED, time);
        if (!userBookingList.isEmpty()) {
            Comment comment = new Comment();
            comment.setText(addCommentDto.getText());
            comment.setAuthor(userService.findUserFromDb(userId));
            comment.setItem(findItemFromDb(itemId));
            comment.setCreated(time);
            commentRepository.save(comment);
            return ItemMapper.toCommentDto(comment);
        } else {
            throw new ValidationException("Пользователь не найден в списке бронирования вещи.");
        }
    }
}
