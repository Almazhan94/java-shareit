package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.error.BookingNotFoundException;
import ru.practicum.shareit.error.ItemNotFoundException;
import ru.practicum.shareit.error.UserNotFoundException;
import ru.practicum.shareit.error.ValidationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    private final ItemService itemService;

    private final ItemRepository itemRepository;

    private final UserService userService;

    private final UserRepository userRepository;

    private LocalDateTime time = LocalDateTime.now();

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository, ItemService itemService, ItemRepository itemRepository, UserService userService, UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.itemService = itemService;
        this.itemRepository = itemRepository;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @Override
    public BookingDto create(int bookerId, CreateBookingDto createBookingDto) {
        validator(createBookingDto.getStart(), createBookingDto.getEnd());
        Optional<User> bookerOptional = userRepository.findById(bookerId);
        if (bookerOptional.isEmpty()) {
            throw new ItemNotFoundException(String.format("Пользователь с идентификатором %d не существует", bookerId));
        }
        User booker = bookerOptional.get();
        int itemId = createBookingDto.getItemId();
        Optional<Item> itemOptional = itemRepository.findById(itemId);
        if (itemOptional.isEmpty()) {
            throw new ItemNotFoundException(String.format("Вещь с идентификатором %d не существует", itemId));
        }
        Item itemToBooking = itemOptional.get();
        if (bookerId == itemToBooking.getOwner().getId()) {
            throw new BookingNotFoundException("Владелец вещи не может бронировать свои вещи");
        }
        if (itemToBooking.getAvailable()) {
            Booking booking = new Booking();
            booking.setBooker(booker);
            booking.setItem(itemToBooking);
            booking.setStatus(Status.WAITING);
            booking.setStartTime(createBookingDto.getStart());
            booking.setEndTime(createBookingDto.getEnd());
            Booking bookingCreate = bookingRepository.save(booking);
            return BookingMapper.toBookingDto(UserMapper.toUserDto(booker), ItemMapper.toItemDto(itemToBooking), bookingCreate);
        } else {
            throw new ValidationException(String.format("Вещь с идентификатором %d не доступна для бронирования",itemId));
        }
    }

    @Override
    public BookingDto findById(int userId, int bookingId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new ItemNotFoundException(String.format("Пользователь с идентификатором %d не существует", userId));
        }
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
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new ItemNotFoundException(String.format("Пользователь с идентификатором %d не существует", userId));
        }
        UserDto userDto = UserMapper.toUserDto(userOptional.get());
        if (state.equals(State.ALL.toString())) {
            bookings = bookingRepository.findByBookerId(userId, Sort.by(Sort.Direction.DESC, "startTime"));
            for (Booking booking : bookings) {
                bookingDtoList.add(BookingMapper.toBookingDto(userDto, ItemMapper.toItemDto(booking.getItem()), booking));
            }
            return bookingDtoList;
        }
        if (state.equals(State.CURRENT.toString())) {
            bookings = bookingRepository.findByBookerIdAndStartTimeIsBeforeAndEndTimeIsAfter(userId, time, time);
            for (Booking booking : bookings) {
                bookingDtoList.add(BookingMapper.toBookingDto(userDto, ItemMapper.toItemDto(booking.getItem()), booking));
            }
            return bookingDtoList;
        }
        if (state.equals(State.PAST.toString())) {
            bookings = bookingRepository.findByBookerIdAndEndTimeIsBefore(userId, time, Sort.by(Sort.Direction.DESC, "endTime"));
            for (Booking booking : bookings) {
                bookingDtoList.add(BookingMapper.toBookingDto(userDto, ItemMapper.toItemDto(booking.getItem()), booking));
            }
            return bookingDtoList;
        }
        if (state.equals(State.FUTURE.toString())) {
            bookings = bookingRepository.findByBookerIdAndStartTimeIsAfter(userId, time, Sort.by(Sort.Direction.DESC, "startTime"));
            for (Booking booking : bookings) {
                bookingDtoList.add(BookingMapper.toBookingDto(userDto, ItemMapper.toItemDto(booking.getItem()), booking));
            }
            return bookingDtoList;
        }
        if (state.equals(Status.WAITING.toString())) {
            bookings = bookingRepository.findByBookerIdAndStatus(userId, Status.WAITING);
            for (Booking booking : bookings) {
                bookingDtoList.add(BookingMapper.toBookingDto(userDto, ItemMapper.toItemDto(booking.getItem()), booking));
            }
            return bookingDtoList;
        }
        if (state.equals(Status.REJECTED.toString())) {
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
        Optional<User> userOptional = userRepository.findById(ownerId);
        if (userOptional.isEmpty()) {
            throw new ItemNotFoundException(String.format("Пользователь с идентификатором %d не существует", ownerId));
        }
        List<Item> items = itemRepository.findItemByOwnerId(ownerId);
        Set<Integer> itemIdSet = new HashSet<>();
        for (Item item : items) {
            itemIdSet.add(item.getId());
        }
        if (state.equals(State.ALL.toString())) {
            bookings = bookingRepository.findAllByItemIdIn(itemIdSet, Sort.by(Sort.Direction.DESC, "startTime"));
            for (Booking booking : bookings) {
                bookingDtoList.add(BookingMapper.toBookingDto(UserMapper.toUserDto(booking.getBooker()),
                        ItemMapper.toItemDto(booking.getItem()),
                        booking));
            }
            return bookingDtoList;
        }
        if (state.equals(State.CURRENT.toString())) {
            bookings = bookingRepository.findByItemIdInAndStartTimeIsBeforeAndEndTimeIsAfter(itemIdSet, time, time, Sort.by(Sort.Direction.DESC, "startTime"));
            for (Booking booking : bookings) {
                bookingDtoList.add(BookingMapper.toBookingDto(UserMapper.toUserDto(booking.getBooker()),
                        ItemMapper.toItemDto(booking.getItem()),
                        booking));
            }
            return bookingDtoList;
        }
        if (state.equals(State.PAST.toString())) {
            bookings = bookingRepository.findByItemIdInAndEndTimeIsBefore(itemIdSet, time, Sort.by(Sort.Direction.DESC, "startTime"));
            for (Booking booking : bookings) {
                bookingDtoList.add(BookingMapper.toBookingDto(UserMapper.toUserDto(booking.getBooker()),
                        ItemMapper.toItemDto(booking.getItem()),
                        booking));
            }
            return bookingDtoList;
        }
        if (state.equals(State.FUTURE.toString())) {
            bookings = bookingRepository.findByItemIdInAndEndTimeIsAfter(itemIdSet, time, Sort.by(Sort.Direction.DESC, "startTime"));
            for (Booking booking : bookings) {
                bookingDtoList.add(BookingMapper.toBookingDto(UserMapper.toUserDto(booking.getBooker()),
                        ItemMapper.toItemDto(booking.getItem()),
                        booking));
            }
            return bookingDtoList;
        }
        if (state.equals(Status.WAITING.toString())) {
            bookings = bookingRepository.findByItemIdInAndStatus(itemIdSet, Status.WAITING);
            for (Booking booking : bookings) {
                bookingDtoList.add(BookingMapper.toBookingDto(UserMapper.toUserDto(booking.getBooker()),
                        ItemMapper.toItemDto(booking.getItem()),
                        booking));
            }
            return bookingDtoList;
        }
        if (state.equals(Status.REJECTED.toString())) {
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
