package ru.practicum.shareit.request;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItem;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemRequestMapperTest {

    private final EasyRandom easyRandom = new EasyRandom();

    @Test
    void itemRequestDtoTest() {
        ItemRequest itemRequest = easyRandom.nextObject(ItemRequest.class);
        ItemRequestDto itemRequestDto = ItemRequestMapper.itemRequestDto(itemRequest);
        assertEquals(itemRequest.getId(), itemRequestDto.getId());
        assertEquals(itemRequest.getDescription(), itemRequestDto.getDescription());
        assertEquals(itemRequest.getCreated(), itemRequestDto.getCreated());
    }

    @Test
    void toItemRequestDtoWithItemListTest() {
        ItemRequest itemRequest1 = easyRandom.nextObject(ItemRequest.class);
        ItemRequest itemRequest2 = easyRandom.nextObject(ItemRequest.class);
        List<ItemRequest> itemRequestList = List.of(itemRequest1, itemRequest2);
        Item item1 = easyRandom.nextObject(Item.class);
        Item item2 = easyRandom.nextObject(Item.class);
        item1.setRequestId(itemRequest1.getId());
        item2.setRequestId(itemRequest1.getId());
        List<Item> itemList =  List.of(item1, item2);
        List<ItemRequestDtoWithItem> itemRequestDtoList = ItemRequestMapper.toItemRequestDtoWithItemList(itemRequestList, itemList);
        assertEquals(itemRequestList.size(), itemRequestDtoList.size());
        assertEquals(itemRequestList.get(0).getId(), itemRequestDtoList.get(0).getId());
        assertEquals(itemRequestList.get(1).getId(), itemRequestDtoList.get(1).getId());
    }

    @Test
    void toItemRequestDtoWithItemTest() {
        ItemRequest itemRequest = easyRandom.nextObject(ItemRequest.class);
        Item item1 = easyRandom.nextObject(Item.class);
        Item item2 = easyRandom.nextObject(Item.class);
        item1.setRequestId(itemRequest.getId());
        item2.setRequestId(itemRequest.getId());
        List<Item> itemList =  List.of(item1, item2);
        ItemRequestDtoWithItem itemRequestDto = ItemRequestMapper.toItemRequestDtoWithItem(itemRequest, itemList);
        assertEquals(itemRequest.getId(), itemRequestDto.getId());
        assertEquals(itemRequestDto.getItems().size(), 2);
        assertEquals(itemRequestDto.getItems().get(0).getId(), item1.getId());
        assertEquals(itemRequestDto.getItems().get(1).getId(), item2.getId());
    }
}