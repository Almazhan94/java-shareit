package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItem;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto createRequest(Integer userId, CreateItemRequestDto createItemRequestDto);

    List<ItemRequestDtoWithItem> findAllRequest(Integer userId);

    ItemRequestDtoWithItem findRequestById(Integer userId, Integer requestId);

    List<ItemRequestDtoWithItem> findAllRequestWith(Integer userId, Integer from, Integer size);
}
