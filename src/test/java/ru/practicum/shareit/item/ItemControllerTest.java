package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.dto.AddCommentDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    ObjectMapper mapper = new ObjectMapper();

    @MockBean
    ItemService itemService;

    @Autowired
    MockMvc mockMvc;

    User user;
    User user2;
    ItemRequest itemRequest;
    ItemRequest itemRequest2;
    Item item;
    Item item2;

    @BeforeEach
    void setUp() {
        user = new User(1, "name", "e@mail.com");

        user2 = new User(2, "name2", "e2@mail.com");
        itemRequest = new ItemRequest(1, "description", user2, LocalDateTime.now());
        itemRequest2 = new ItemRequest(2, "description", user2, LocalDateTime.now());

        item = new Item(1, "item name", "description", true, user, 1);

        item2 = new Item(2, "item name2", "description2", true, user, 2);

    }

    @Test
    void create() throws Exception {

        ItemDto itemDto = ItemMapper.toItemDto(item);

        when(itemService.createItem(user.getId(),itemDto))
                .thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Integer.class))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.name", is(itemDto.getName())));

    }

    @Test
    void findItemById() throws Exception {
        ItemBookingDto itemDto = ItemMapper.toItemBookingDto(new Booking(), new Booking(), item, new ArrayList<>());

        when(itemService.findItemByIdWithBooking(user.getId(), itemDto.getId()))
                .thenReturn(itemDto);

        mockMvc.perform(get("/items/1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Integer.class))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.name", is(itemDto.getName())));
    }

        @Test
    void findItemByUserId() throws Exception {
            ItemBookingDto itemDto = ItemMapper.toItemBookingDto(new Booking(), new Booking(), item, new ArrayList<>());

            when(itemService.findAllItemWithBooking(user.getId()))
                    .thenReturn(List.of(itemDto));

            mockMvc.perform(get("/items")
                            .content(mapper.writeValueAsString(itemDto))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .header("X-Sharer-User-Id", 1))
                    .andExpect(status().isOk());
    }

    @Test
    void getBySearch() throws Exception {
        ItemDto itemDto = ItemMapper.toItemDto(item);

        when(itemService.getItemBySearch(Mockito.any()))
                .thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items/search")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());
    }

    @Test
    void addComment() throws Exception {
        Comment comment = new Comment(1, "text", item, user2, LocalDateTime.now());
        CommentDto commentDto = ItemMapper.toCommentDto(comment);
        ItemDto itemDto = ItemMapper.toItemDto(item);

        when(itemService.addComment(user.getId(),itemDto.getId(), new AddCommentDto("text")))
                .thenReturn(commentDto);

        mockMvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Integer.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())));
    }
}