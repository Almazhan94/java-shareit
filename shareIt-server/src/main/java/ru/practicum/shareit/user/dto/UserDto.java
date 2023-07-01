package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.error.Generated;
import ru.practicum.shareit.user.CreateStep;
import ru.practicum.shareit.user.UpdateStep;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Generated
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private Integer id;

    @NotBlank(groups = CreateStep.class)
    private String name;

    @NotBlank(groups = CreateStep.class)
    @Email(groups = {CreateStep.class, UpdateStep.class})
    private String email;

    public UserDto(Integer id) {

    }
}
