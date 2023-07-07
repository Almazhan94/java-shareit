package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    private final LocalDateTime time = LocalDateTime.now();

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository, ItemRepository itemRepository, UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public BookingDto create(int bookerId, CreateBookingDto createBookingDto) {
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
            throw new ValidationException(String.format("Вещь с идентификатором %d не доступна для бронирования", itemId));
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
    public List<BookingDto> findByState(int userId, String state, Integer from, Integer size) {
        LocalDateTime time = LocalDateTime.now();
        List<BookingDto> bookingDtoList = new ArrayList<>();
        List<Booking> bookings = new ArrayList<>();
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new ItemNotFoundException(String.format("Пользователь с идентификатором %d не существует", userId));
        }
        UserDto userDto = UserMapper.toUserDto(userOptional.get());
        State enumState = toEnum(state);
        if (enumState == State.ALL) {
            Pageable pageable = pageRequest(from, size, Sort.by(Sort.Direction.DESC, "startTime"));
            bookings = bookingRepository.findByBookerId(userId, pageable);
            bookingDtoList = BookingMapper.toBookingDtoList(bookings, userDto);
            return bookingDtoList;
        }
        if (enumState == State.CURRENT) {
            Pageable pageable = pageRequest(from, size, Sort.by(Sort.Direction.ASC, "startTime")); //DCS
            bookings = bookingRepository.findByBookerIdAndStartTimeIsBeforeAndEndTimeIsAfter(userId, time, time, pageable);
            bookingDtoList = BookingMapper.toBookingDtoList(bookings, userDto);
            return bookingDtoList;
        }
        if (enumState == State.PAST) {
            Pageable pageable = pageRequest(from, size, Sort.by(Sort.Direction.DESC, "endTime"));
            bookings = bookingRepository.findByBookerIdAndEndTimeIsBefore(userId, time, pageable);
            bookingDtoList = BookingMapper.toBookingDtoList(bookings, userDto);
            return bookingDtoList;
        }
        if (enumState == State.FUTURE) {
            Pageable pageable = pageRequest(from, size, Sort.by(Sort.Direction.DESC, "startTime"));
            bookings = bookingRepository.findByBookerIdAndStartTimeIsAfter(userId, time, pageable);
            bookingDtoList = BookingMapper.toBookingDtoList(bookings, userDto);
            return bookingDtoList;
        }
        if (enumState == State.WAITING) {
            Pageable pageable = pageRequest(from, size, Sort.by(Sort.Direction.DESC, "startTime"));
            bookings = bookingRepository.findByBookerIdAndStatus(userId, Status.WAITING, pageable);
            bookingDtoList = BookingMapper.toBookingDtoList(bookings, userDto);
            return bookingDtoList;
        }
        if (enumState == State.REJECTED) {
            Pageable pageable = pageRequest(from, size, Sort.by(Sort.Direction.DESC, "startTime"));
            bookings = bookingRepository.findByBookerIdAndStatus(userId, Status.REJECTED, pageable);
            bookingDtoList = BookingMapper.toBookingDtoList(bookings, userDto);
            return bookingDtoList;
        } else {
            throw new ValidationException(String.format("Unknown state: %s", state));
        }
    }

    @Override
    public List<BookingDto> findOwnerBooking(int ownerId, String state, Integer from, Integer size) {
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
        State enumState = toEnum(state);
        if (enumState == State.ALL) {
            Pageable pageable = pageRequest(from, size, Sort.by(Sort.Direction.DESC, "startTime"));
            bookings = bookingRepository.findAllByItemIdIn(itemIdSet, pageable);
            bookingDtoList = BookingMapper.toOwnerBookingDtoList(bookings);
        }
        if (enumState == State.CURRENT) {
            Pageable pageable = pageRequest(from, size, Sort.by(Sort.Direction.DESC, "startTime"));
            bookings = bookingRepository.findByItemIdInAndStartTimeIsBeforeAndEndTimeIsAfter(itemIdSet, time, time,
                pageable);
            bookingDtoList = BookingMapper.toOwnerBookingDtoList(bookings);
        }
        if (enumState == State.PAST) {
            Pageable pageable = pageRequest(from, size, Sort.by(Sort.Direction.DESC, "startTime"));
            bookings = bookingRepository.findByItemIdInAndEndTimeIsBefore(itemIdSet, time, pageable);
            bookingDtoList = BookingMapper.toOwnerBookingDtoList(bookings);
        }
        if (enumState == State.FUTURE) {
            Pageable pageable = pageRequest(from, size, Sort.by(Sort.Direction.DESC, "startTime"));
            bookings = bookingRepository.findByItemIdInAndEndTimeIsAfter(itemIdSet, time, pageable);
            bookingDtoList = BookingMapper.toOwnerBookingDtoList(bookings);
        }
        if (enumState == State.WAITING) {
            Pageable pageable = pageRequest(from, size, Sort.by(Sort.Direction.DESC, "startTime"));
            bookings = bookingRepository.findByItemIdInAndStatus(itemIdSet, Status.WAITING, pageable);
            bookingDtoList = BookingMapper.toOwnerBookingDtoList(bookings);
        }
        if (enumState == State.REJECTED) {
            Pageable pageable = pageRequest(from, size, Sort.by(Sort.Direction.DESC, "startTime"));
            bookings = bookingRepository.findByItemIdInAndStatus(itemIdSet, Status.REJECTED, pageable);
            bookingDtoList = BookingMapper.toOwnerBookingDtoList(bookings);
        }
        return bookingDtoList;
    }

    private State toEnum(String state) {
        try {
            return Enum.valueOf(State.class, state);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(String.format("Unknown state: %s", state));
        }
    }

    private Pageable pageRequest(Integer from, Integer size, Sort sort) {
        int page = from / size;
        return PageRequest.of(page, size, sort);
    }
}
