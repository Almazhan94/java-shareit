package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.error.Generated;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItem;

import javax.validation.Valid;
import java.util.List;

@Generated
@Slf4j
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @Autowired
    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @PostMapping
    public ItemRequestDto create(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                 @RequestBody @Valid CreateItemRequestDto createItemRequestDto) {
        log.info("Добавляется новый запрос вещи: {}", createItemRequestDto);
        return itemRequestService.createRequest(userId, createItemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDtoWithItem> findAll(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        List<ItemRequestDtoWithItem> allItemRequest = itemRequestService.findAllRequest(userId);
        log.info("Количество запросов в текущий момент: {}", allItemRequest.size());
        return allItemRequest;
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoWithItem findById(@RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable Integer requestId) {
        log.info("Ищется запрос по идентификатору: {}", requestId);
        return itemRequestService.findRequestById(userId, requestId);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoWithItem> findAllWith(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                    @RequestParam(value = "from", required = false, defaultValue = "0") Integer from,
                                                    @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        log.info("Ищется список запрос от пользователя с идентификатором: {}", userId);
        return itemRequestService.findAllRequestWith(userId, from, size);
    }
}
