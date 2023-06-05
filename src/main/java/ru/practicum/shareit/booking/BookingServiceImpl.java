package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.error.BookingNotFoundException;
import ru.practicum.shareit.error.UserNotFoundException;
import ru.practicum.shareit.error.ValidationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    private final ItemService itemService;

    private final UserService userService;

    private LocalDateTime time = LocalDateTime.now();

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository, ItemService itemService, UserService userService) {
        this.bookingRepository = bookingRepository;
        this.itemService = itemService;
        this.userService = userService;
    }

    @Override
    public BookingDto create(int bookerId, CreateBookingDto createBookingDto) {
        validator(createBookingDto.getStart(), createBookingDto.getEnd());
        UserDto bookerDto = userService.findUserById(bookerId);
        User booker = UserMapper.toUser(bookerDto);
        ItemDto itemDto = itemService.findItemById(createBookingDto.getItemId());
        Item itemToBooking = itemService.findItemFromDb(createBookingDto.getItemId());
        if (bookerId == itemToBooking.getOwner().getId()) {
            throw new BookingNotFoundException("Владелец вещи не может бронировать свои вещи");
        }
        if (itemDto.getAvailable()) {
            Item item = itemService.findItemFromDb(createBookingDto.getItemId());
            Booking booking = new Booking();
            booking.setBooker(booker);
            booking.setItem(item);
            booking.setStatus(Status.WAITING);
            booking.setStartTime(createBookingDto.getStart());
            booking.setEndTime(createBookingDto.getEnd());
            Booking bookingCreate = bookingRepository.save(booking);
            return BookingMapper.toBookingDto(bookerDto, itemDto, bookingCreate);
        } else {
            throw new ValidationException(String.format("Вещь с идентификатором %d не доступна для бронирования", itemDto.getId()));
        }
    }

    @Override
    public BookingDto findById(int userId, int bookingId) {
        UserDto userDto = userService.findUserById(userId);
        Optional<Booking> bookingOptional = bookingRepository.findById(bookingId);
        if (bookingOptional.isPresent()) {
            Booking booking = bookingOptional.get();
            User booker = booking.getBooker();
            Item item = booking.getItem();
            if (booker.getId() == userId || item.getOwner().getId() == userId) {
                UserDto bookerDto = UserMapper.toUserDto(booker);
                ItemDto itemDto = ItemMapper.toItemDto(item);
                return BookingMapper.toBookingDto(bookerDto, itemDto, booking);
            } else {
                throw new BookingNotFoundException("Получение данных о бронировании может быть выполнено либо автором бронирования, либо владельцем вещи, к которой относится бронирование.");
            }
        } else {
            throw new BookingNotFoundException(String.format("Бронирование с идентификатором %d не существует", bookingId));
        }
    }

    @Override
    public BookingDto update(int userId, int bookingId, String approved) {
        if (approved.equals("true") || approved.equals("false")) {
            Optional<Booking> bookingOptional = bookingRepository.findById(bookingId);
            if (bookingOptional.isPresent()) {
                Booking booking = bookingOptional.get();
                if (booking.getItem().getOwner().getId() == userId) {
                    if (approved.equals("true")) {
                        if (booking.getStatus().equals(Status.APPROVED)) {
                            throw new ValidationException("Статус был подтвержден ранее");
                        }
                        booking.setStatus(Status.APPROVED);
                    } else {
                        booking.setStatus(Status.REJECTED);
                    }
                    bookingRepository.save(booking);
                    UserDto userDto = UserMapper.toUserDto(booking.getBooker());
                    ItemDto itemDto = ItemMapper.toItemDto(booking.getItem());
                    return BookingMapper.toBookingDto(userDto, itemDto, booking);
                } else {
                    throw new UserNotFoundException("Пользователь не является владельцем вещи.");
                }
            } else {
                throw new BookingNotFoundException(String.format("Бронирование с идентификатором %d не существует", bookingId));
            }
        } else {
            throw new ValidationException(String.format("Не корректный параметр запроса: %s", approved));
        }
    }

    @Override
    public List<BookingDto> findByState(int userId, String state) {
        LocalDateTime time = LocalDateTime.now();
        List<BookingDto> bookingDtoList = new ArrayList<>();
        List<Booking> bookings = new ArrayList<>();
        UserDto userDto = userService.findUserById(userId);
        if (state.equals("ALL")) {
            bookings = bookingRepository.findByBookerIdOrderByStartTimeDesc(userId);
            for (Booking booking : bookings) {
                bookingDtoList.add(BookingMapper.toBookingDto(userDto, ItemMapper.toItemDto(booking.getItem()), booking));
            }
            return bookingDtoList;
        }
        if (state.equals("CURRENT")) {
            bookings = bookingRepository.findByBookerIdAndStartTimeIsBeforeAndEndTimeIsAfter(userId, time, time);
            for (Booking booking : bookings) {
                bookingDtoList.add(BookingMapper.toBookingDto(userDto, ItemMapper.toItemDto(booking.getItem()), booking));
            }
            return bookingDtoList;
        }
        if (state.equals("PAST")) {
            bookings = bookingRepository.findByBookerIdAndEndTimeIsBeforeOrderByEndTimeDesc(userId, time);
            for (Booking booking : bookings) {
                bookingDtoList.add(BookingMapper.toBookingDto(userDto, ItemMapper.toItemDto(booking.getItem()), booking));
            }
            return bookingDtoList;
        }
        if (state.equals("FUTURE")) {
            bookings = bookingRepository.findByBookerIdAndStartTimeIsAfterOrderByStartTimeDesc(userId, time);
            for (Booking booking : bookings) {
                bookingDtoList.add(BookingMapper.toBookingDto(userDto, ItemMapper.toItemDto(booking.getItem()), booking));
            }
            return bookingDtoList;
        }
        if (state.equals("WAITING")) {
            bookings = bookingRepository.findByBookerIdAndStatus(userId, Status.WAITING);
            for (Booking booking : bookings) {
                bookingDtoList.add(BookingMapper.toBookingDto(userDto, ItemMapper.toItemDto(booking.getItem()), booking));
            }
            return bookingDtoList;
        }
        if (state.equals("REJECTED")) {
            bookings = bookingRepository.findByBookerIdAndStatus(userId, Status.REJECTED);
            for (Booking booking : bookings) {
                bookingDtoList.add(BookingMapper.toBookingDto(userDto, ItemMapper.toItemDto(booking.getItem()), booking));
            }
            return bookingDtoList;
        } else {
            throw new ValidationException(String.format("Unknown state: %s", state));
        }
    }

    @Override
    public List<BookingDto> findOwnerBooking(int ownerId, String state) {
        LocalDateTime time = LocalDateTime.now();
        List<BookingDto> bookingDtoList = new ArrayList<>();
        List<Booking> bookings = new ArrayList<>();
        UserDto userDto = userService.findUserById(ownerId);
        List<ItemDto> itemDtoList = itemService.findItemByUserId(ownerId);
        Set<Integer> itemIdSet = new HashSet<>();
        for (ItemDto itemDto : itemDtoList) {
            itemIdSet.add(itemDto.getId());
        }
        if (state.equals("ALL")) {
            bookings = bookingRepository.findAllByItemIdInOrderByStartTimeDesc(itemIdSet);
            for (Booking booking : bookings) {
                bookingDtoList.add(BookingMapper.toBookingDto(UserMapper.toUserDto(booking.getBooker()),
                        ItemMapper.toItemDto(booking.getItem()),
                        booking));
            }
            return bookingDtoList;
         }
        if (state.equals("CURRENT")) {
            bookings = bookingRepository.findByItemIdInAndStartTimeIsBeforeAndEndTimeIsAfterOrderByStartTimeDesc(itemIdSet, time, time);
            for (Booking booking : bookings) {
                bookingDtoList.add(BookingMapper.toBookingDto(UserMapper.toUserDto(booking.getBooker()),
                        ItemMapper.toItemDto(booking.getItem()),
                        booking));
            }
            return bookingDtoList;
        }
        if (state.equals("PAST")) {
            bookings = bookingRepository.findByItemIdInAndEndTimeIsBeforeOrderByStartTimeDesc(itemIdSet, time);
            for (Booking booking : bookings) {
                bookingDtoList.add(BookingMapper.toBookingDto(UserMapper.toUserDto(booking.getBooker()),
                        ItemMapper.toItemDto(booking.getItem()),
                        booking));
            }
            return bookingDtoList;
        }
        if (state.equals("FUTURE")) {
            bookings = bookingRepository.findByItemIdInAndEndTimeIsAfterOrderByStartTimeDesc(itemIdSet, time);
            for (Booking booking : bookings) {
                bookingDtoList.add(BookingMapper.toBookingDto(UserMapper.toUserDto(booking.getBooker()),
                        ItemMapper.toItemDto(booking.getItem()),
                        booking));
            }
            return bookingDtoList;
        }
        if (state.equals("WAITING")) {
            bookings = bookingRepository.findByItemIdInAndStatus(itemIdSet, Status.WAITING);
            for (Booking booking : bookings) {
                bookingDtoList.add(BookingMapper.toBookingDto(UserMapper.toUserDto(booking.getBooker()),
                        ItemMapper.toItemDto(booking.getItem()),
                        booking));
            }
            return bookingDtoList;
        }
        if (state.equals("REJECTED")) {
            bookings = bookingRepository.findByItemIdInAndStatus(itemIdSet, Status.REJECTED);
            for (Booking booking : bookings) {
                bookingDtoList.add(BookingMapper.toBookingDto(UserMapper.toUserDto(booking.getBooker()),
                        ItemMapper.toItemDto(booking.getItem()),
                        booking));
            }
            return bookingDtoList;
        } else {
            throw new ValidationException(String.format("Unknown state: %s", state));
        }
    }

    private void validator(LocalDateTime start, LocalDateTime end) {
        if (start.isBefore(time) || end.isBefore(time) || end.isBefore(start) || start.isEqual(end)) {
            throw new ValidationException("Не корректная дата бронирования");
        }
    }
}
