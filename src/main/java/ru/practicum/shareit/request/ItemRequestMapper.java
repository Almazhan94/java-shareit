package ru.practicum.shareit.request;

import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItem;

import java.util.ArrayList;
import java.util.List;

public class ItemRequestMapper {

    public static ItemRequestDto itemRequestDto(ItemRequest itemRequest) {
        return  new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated()
        );
    }

    public static List<ItemRequestDtoWithItem> toItemRequestDtoWithItemList(List<ItemRequest> itemRequestList, List<Item> itemList){
        List<ItemRequestDtoWithItem> itemRequestDtoList = new ArrayList<>();
        List<ItemDto> itemDtoList = new ArrayList<>();
        if (itemList.size() == 0) {
            for (ItemRequest itemRequest : itemRequestList) {                            // мапим без списка Item для каждого Request
                ItemRequestDtoWithItem itemRequestDtoWithItem = new ItemRequestDtoWithItem();
                itemRequestDtoWithItem.setId(itemRequest.getId());
                itemRequestDtoWithItem.setDescription(itemRequest.getDescription());
                itemRequestDtoWithItem.setCreated(itemRequest.getCreated());
                itemRequestDtoWithItem.setItems(itemDtoList);
                itemRequestDtoList.add(itemRequestDtoWithItem);
            }
            return itemRequestDtoList;
        }
        for (ItemRequest itemRequest : itemRequestList) {                                    // иначе заполняем список Item в Request
            ItemRequestDtoWithItem itemRequestDtoWithItem = new ItemRequestDtoWithItem();
            itemRequestDtoWithItem.setId(itemRequest.getId());
            itemRequestDtoWithItem.setDescription(itemRequest.getDescription());
            itemRequestDtoWithItem.setCreated(itemRequest.getCreated());
            itemDtoList.clear();
            for (Item item : itemList) {
                if (item.getRequestId().equals(itemRequest.getId())) {
                    ItemDto itemDto = ItemMapper.toItemDto(item);
                    itemDtoList.add(itemDto);
                }
            }
            itemRequestDtoWithItem.setItems(itemDtoList);
            itemRequestDtoList.add(itemRequestDtoWithItem);
        }
        return itemRequestDtoList;
    }

    public static ItemRequestDtoWithItem toItemRequestDtoWithItem (ItemRequest itemRequest, List<Item> itemList) {
        ItemRequestDtoWithItem itemRequestDtoWithItem = new ItemRequestDtoWithItem();
        itemRequestDtoWithItem.setId(itemRequest.getId());
        itemRequestDtoWithItem.setDescription(itemRequest.getDescription());
        itemRequestDtoWithItem.setCreated(itemRequest.getCreated());
        List<ItemDto> itemDtoList = new ArrayList<>();
        if (!itemList.isEmpty()) {
            itemDtoList = ItemMapper.toItemDtoList(itemList);
        }
        itemRequestDtoWithItem.setItems(itemDtoList);
        return itemRequestDtoWithItem;
    }
}
