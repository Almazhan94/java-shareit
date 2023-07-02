package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.error.Generated;

import java.time.LocalDateTime;

@Generated
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateBookingDto {

    Integer itemId;

    LocalDateTime start;

    LocalDateTime end;
}
