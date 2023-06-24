package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.error.Generated;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

@Generated
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestDtoWithItem {

    Integer id;

    String description;

    LocalDateTime created;

    List<ItemDto> items;
}
