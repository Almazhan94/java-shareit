package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.error.Generated;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Generated
@Slf4j
@RestController
@RequestMapping(path = "/requests")
@Validated
public class ItemRequestController {

    private final RequestClient requestClient;

    @Autowired
    public ItemRequestController(RequestClient requestClient) {
        this.requestClient = requestClient;
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @RequestBody @Valid CreateItemRequestDto createItemRequestDto) {
        log.info("Добавляется новый запрос вещи: {}", createItemRequestDto);
        return requestClient.createRequest(userId, createItemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> findAll(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Ищутся все request");
        return requestClient.findAllRequest(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findById(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @PathVariable Long requestId) {
        log.info("Ищется запрос по идентификатору: {}", requestId);
        return requestClient.findRequestById(userId, requestId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAllWith(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @PositiveOrZero
                                              @RequestParam(value = "from", required = false, defaultValue = "0")
                                              Integer from,
                                              @Positive
                                              @RequestParam(value = "size", required = false, defaultValue = "10")
                                              Integer size) {
        log.info("Ищется список запрос от пользователя с идентификатором: {}", userId);
        return requestClient.findAllRequestWithPagination(userId, from, size);
    }
}
