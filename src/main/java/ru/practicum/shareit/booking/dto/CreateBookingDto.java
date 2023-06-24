package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.error.Generated;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Generated
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateBookingDto {

    @NotNull
    Integer itemId;

    @NotNull
    LocalDateTime start;

    @NotNull
    LocalDateTime end;
}
