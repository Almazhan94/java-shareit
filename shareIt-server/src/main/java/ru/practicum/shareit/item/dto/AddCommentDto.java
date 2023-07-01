package ru.practicum.shareit.item.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.error.Generated;

import javax.validation.constraints.NotEmpty;

@Generated
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddCommentDto {

    @NotEmpty
    String text;

}
