package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequestMapping("/items")
@Validated
public class ItemController {

    private ItemServiceImpl itemService;

    @Autowired
    public ItemController(ItemServiceImpl itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") @NotNull Integer userId, @RequestBody @Valid Item item) {
        log.info("Добавляется вещь: {}", item);
        return itemService.createItem(userId, item);
    }

    @GetMapping("/{itemId}")
    public Item findItemById(@PathVariable int itemId) {
        log.info("Ищется вещь по идентификатору: {}", itemService.findItemById(itemId));
        return itemService.findItemById(itemId);
    }

    @GetMapping
    public List<Item> findItemByUserId(@RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("Ищется вещь по пользователю: {}", itemService.findItemByUserId(userId));
        return itemService.findItemByUserId(userId);
    }

    @PatchMapping("/{itemId}")
    public Item patch(@RequestHeader("X-Sharer-User-Id") @NotNull Integer userId, @PathVariable int itemId, @RequestBody Item item) {
        log.info("Обновляется пользователь: {}", item);
        return itemService.patchItem(userId, itemId, item);
    }

    @GetMapping("/search")
    public List<Item> getBySearch(@RequestParam(value = "text",  required = false) String text) {
        log.info("Ищется вещь по параметру: {}", text);
        return itemService.getItemBySearch(text);
    }

}
