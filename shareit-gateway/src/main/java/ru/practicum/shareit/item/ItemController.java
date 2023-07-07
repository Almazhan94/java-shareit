package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import ru.practicum.shareit.error.Generated;
import ru.practicum.shareit.item.dto.AddCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Generated
@Slf4j
@RestController
@RequestMapping("/items")
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @Autowired
    public ItemController(ItemClient itemClient) {
        this.itemClient = itemClient;
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @RequestBody @Valid ItemDto itemDto) {
        log.info("Добавляется вещь: {}", itemDto);
        return itemClient.createItem(userId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findItemById(@RequestHeader("X-Sharer-User-Id") long userId,
                                               @PathVariable Long itemId) {
        log.info("Ищется вещь по идентификатору: {}", itemId);
        return itemClient.findItemById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> findItemByUserId(@RequestHeader("X-Sharer-User-Id") long userId,
                                                   @PositiveOrZero
                                                   @RequestParam(value = "from", required = false, defaultValue = "0")
                                                   Integer from,
                                                   @Positive
                                                   @RequestParam(value = "size", required = false, defaultValue = "10")
                                                   Integer size) {
        log.info("Ищется вещь по пользователю: {}", userId);
        return itemClient.findItemByUserId(userId, from, size);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> patch(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable Long itemId,
                                        @RequestBody ItemDto itemDto) {
        log.info("Обновляется вещь по идентификатору: {}", itemId);
        return itemClient.patchItem(userId, itemId, itemDto);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getBySearch(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @RequestParam(value = "text", required = false) String text,
                                              @PositiveOrZero
                                              @RequestParam(value = "from", required = false, defaultValue = "0")
                                              Integer from,
                                              @Positive
                                              @RequestParam(value = "size", required = false, defaultValue = "10")
                                              Integer size) {
        log.info("Ищется вещь по параметру: {}", text);
        return itemClient.getItemBySearch(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable Long itemId,
                                             @RequestBody @Valid AddCommentDto addCommentDto) {
        log.info("Добавляется комментарий: {}", addCommentDto);
        return itemClient.addComment(userId, itemId, addCommentDto);
    }

}
