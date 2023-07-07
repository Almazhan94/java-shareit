package ru.practicum.shareit.booking;

import ru.practicum.shareit.error.Generated;

@Generated
public enum Status {
    WAITING, /** новое бронирование, ожидает одобрения */

    APPROVED,  /** бронирование подтверждено владельцем */

    REJECTED, /** бронирование отклонено владельцем */

    CANCELED /** бронирование отменено создателем */
}
