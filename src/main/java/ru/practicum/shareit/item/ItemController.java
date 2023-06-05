package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.AddCommentDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import java.util.List;

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
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") Integer userId, @RequestBody @Valid ItemDto itemDto) {
        log.info("Добавляется вещь: {}", itemDto);
        return itemService.createItem(userId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemBookingDto findItemById(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable int itemId) {
        log.info("Ищется вещь по идентификатору: {}", itemId);
        return itemService.findItemByIdWithBooking(userId, itemId);
    }

    @GetMapping
    public List<ItemBookingDto> findItemByUserId(@RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("Ищется вещь по пользователю: {}", userId);
        return itemService.findAllItemWithBooking(userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto patch(@RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable int itemId, @RequestBody ItemDto itemDto) {
        log.info("Обновляется вещь по идентификатору: {}", itemId);
        return itemService.patchItem(userId, itemId, itemDto);
    }

    @GetMapping("/search")
    public List<ItemDto> getBySearch(@RequestHeader("X-Sharer-User-Id") Integer ownerId, @RequestParam(value = "text",  required = false) String text) {
        log.info("Ищется вещь по параметру: {}", text);
        return itemService.getItemBySearch(text, ownerId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                 @PathVariable int itemId,
                                 @RequestBody @Valid AddCommentDto addCommentDto) {
        log.info("Добавляется комментарий: {}", addCommentDto);
        return itemService.addComment(userId, itemId, addCommentDto);
    }

}
