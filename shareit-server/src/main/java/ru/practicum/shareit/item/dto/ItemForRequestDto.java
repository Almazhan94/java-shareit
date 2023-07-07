package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.error.Generated;

@Generated
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemForRequestDto {

    Integer id;

    String name;

    Integer ownerId;
}
