package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.error.Generated;

@Generated
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {

    private Integer id;

    private String name;

    private String description;

    private Boolean available;

    Integer requestId;
}
