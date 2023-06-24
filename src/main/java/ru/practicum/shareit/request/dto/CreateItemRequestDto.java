package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.error.Generated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Generated
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateItemRequestDto {

    @NotEmpty
    @NotNull
    String description;
}
