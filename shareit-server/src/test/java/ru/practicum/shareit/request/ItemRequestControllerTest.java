package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItem;
import ru.practicum.shareit.user.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    ObjectMapper mapper = new ObjectMapper();

    @MockBean
    ItemRequestService itemRequestService;

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
        ItemRequestDto itemRequestDto = ItemRequestMapper.itemRequestDto(itemRequest);
        when(itemRequestService.createRequest(user.getId(),
                new CreateItemRequestDto("description")))
                .thenReturn(itemRequestDto);

        mockMvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Integer.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.created", is(any(String.class))));

    }

    @Test
    void findAll() throws Exception {
        ItemRequestDtoWithItem itemRequestDtoWithItem =
                new ItemRequestDtoWithItem(1, "description", LocalDateTime.now(), List.of());

        when(itemRequestService.findAllRequest(user.getId()))
                .thenReturn(List.of(itemRequestDtoWithItem));

        mockMvc.perform(get("/requests")
                        .content(mapper.writeValueAsString(itemRequestDtoWithItem))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());
    }

    @Test
    void findById() throws Exception {
        ItemRequestDtoWithItem itemRequestDto = ItemRequestMapper.toItemRequestDtoWithItem(itemRequest, List.of());
        when(itemRequestService.findRequestById(user.getId(),1))
                .thenReturn(itemRequestDto);

        mockMvc.perform(get("/requests/1")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Integer.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.created", is(any(String.class))));
    }

    @Test
    void findAllWith() throws Exception {
        ItemRequestDtoWithItem itemRequestDto = ItemRequestMapper.toItemRequestDtoWithItem(itemRequest, List.of());
        when(itemRequestService.findAllRequestWith(user.getId(),0, 10))
                .thenReturn(List.of(itemRequestDto));

        mockMvc.perform(get("/requests/all")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());
    }
}