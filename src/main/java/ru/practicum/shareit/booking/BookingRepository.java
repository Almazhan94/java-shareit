package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    List<Booking> findByBookerIdOrderByStartTimeDesc(int bookerId);

    List<Booking> findByBookerIdAndStartTimeIsBeforeAndEndTimeIsAfter(int bookerId, LocalDateTime start, LocalDateTime end);

    List<Booking> findByBookerIdAndEndTimeIsBeforeOrderByEndTimeDesc(int bookerId, LocalDateTime end);

    List<Booking> findByBookerIdAndStartTimeIsAfterOrderByStartTimeDesc(int bookerId, LocalDateTime end);

    List<Booking> findByBookerIdAndStatus(int bookerId, Status status);

    List<Booking> findAllByItemIdInOrderByStartTimeDesc(Set<Integer> itemIdSet);

    List<Booking> findByItemIdInAndStartTimeIsBeforeAndEndTimeIsAfterOrderByStartTimeDesc(Set<Integer> itemIdSet, LocalDateTime end, LocalDateTime start);

    List<Booking> findByItemIdInAndEndTimeIsAfterOrderByStartTimeDesc(Set<Integer> itemIdSet, LocalDateTime end);

    List<Booking> findByItemIdInAndEndTimeIsBeforeOrderByStartTimeDesc(Set<Integer> itemIdSet, LocalDateTime end);

    List<Booking> findByItemIdInAndStatus(Set<Integer> itemIdSet, Status status);

    List<Booking> findByItemIdAndEndTimeIsBeforeOrderByEndTime(int itemId, LocalDateTime end);

    List<Booking> findByItemIdAndStartTimeIsAfterOrderByStartTime(int itemId, LocalDateTime time);

    List<Booking> findByBookerIdAndItemIdAndStatusAndEndTimeIsBefore(int bookerId, int itemId, Status status, LocalDateTime time);

    List<Booking> findByItemIdAndStatusAndStartTimeIsAfterOrderByStartTime(int itemId, Status status, LocalDateTime time);

    List<Booking> findByItemIdAndStatusAndEndTimeIsBeforeOrderByEndTimeDesc(int itemId, Status status, LocalDateTime time);

    List<Booking> findByItemIdAndStartTimeIsBeforeAndEndTimeIsAfter(int itemId, LocalDateTime end, LocalDateTime start);
}
