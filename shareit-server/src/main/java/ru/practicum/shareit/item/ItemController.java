package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.error.Generated;
import ru.practicum.shareit.item.dto.AddCommentDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@Generated
@Slf4j
@RestController
@RequestMapping("/items")
@Validated
public class ItemController {

    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") Integer userId, @RequestBody ItemDto itemDto) {
        log.info("Добавляется вещь: {}", itemDto);
        return itemService.createItem(userId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemBookingDto findItemById(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable int itemId) {
        log.info("Ищется вещь по идентификатору: {}", itemId);
        return itemService.findItemByIdWithBooking(userId, itemId);
    }

    @GetMapping
    public List<ItemBookingDto> findItemByUserId(@RequestHeader("X-Sharer-User-Id") int userId,
                                                 @RequestParam(value = "from", required = false, defaultValue = "0") Integer from,
                                                 @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        log.info("Ищется вещь по пользователю: {}", userId);
        return itemService.findAllItemWithBooking(userId, from, size);
    }

    @PatchMapping("/{itemId}")
    public ItemDto patch(@RequestHeader("X-Sharer-User-Id") Integer userId,
                         @PathVariable int itemId,
                         @RequestBody ItemDto itemDto) {
        log.info("Обновляется вещь по идентификатору: {}", itemId);
        return itemService.patchItem(userId, itemId, itemDto);
    }

    @GetMapping("/search")
    public List<ItemDto> getBySearch(@RequestParam(value = "text",  required = false) String text,
                                     @RequestParam(value = "from", required = false, defaultValue = "0") Integer from,
                                     @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        log.info("Ищется вещь по параметру: {}", text);
        return itemService.getItemBySearch(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                 @PathVariable int itemId,
                                 @RequestBody AddCommentDto addCommentDto) {
        log.info("Добавляется комментарий: {}", addCommentDto);
        return itemService.addComment(userId, itemId, addCommentDto);
    }

}
