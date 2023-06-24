package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @MockBean
    BookingService bookingService;

    @Autowired
    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    MockMvc mockMvc;

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
    void findBookingById()throws Exception {
        BookingDto bookingDto = BookingMapper.toBookingDto(UserMapper.toUserDto(user2),
                ItemMapper.toItemDto(item), booking);

        when(bookingService.findById(user2.getId(), 1))
                .thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/1")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2))
                .andExpect(status().isOk());
    }

    @Test
    void findByState() throws Exception {
        BookingDto bookingDto = BookingMapper.toBookingDto(UserMapper.toUserDto(user2),
                ItemMapper.toItemDto(item), booking);


        when(bookingService.findByState(user2.getId(), "ALL", 0, 10))
                .thenReturn(BookingMapper.toBookingDtoList(List.of(booking), UserMapper.toUserDto(user2)));

        mockMvc.perform(get("/bookings/owner")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2))
                .andExpect(status().isOk());
    }

    @Test
    void findOwnerBooking() throws Exception {
        BookingDto bookingDto = BookingMapper.toBookingDto(UserMapper.toUserDto(user2),
                ItemMapper.toItemDto(item), booking);


        when(bookingService.findOwnerBooking(user2.getId(), "ALL", 0, 10))
                .thenReturn(BookingMapper.toBookingDtoList(List.of(booking), UserMapper.toUserDto(user2)));

        mockMvc.perform(get("/bookings/owner")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2))
                .andExpect(status().isOk());
    }
}