package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    List<Booking> findByBookerId(int bookerId, Pageable pageable);

    List<Booking> findByBookerIdAndStartTimeIsBeforeAndEndTimeIsAfter(int bookerId, LocalDateTime start, LocalDateTime end,
                                                                      Pageable pageable);

    List<Booking> findByBookerIdAndEndTimeIsBefore(int bookerId, LocalDateTime end, Pageable sort);

    List<Booking> findByBookerIdAndStartTimeIsAfter(int bookerId, LocalDateTime startTime, Pageable pageable);

    List<Booking> findByBookerIdAndStatus(int bookerId, Status status, Pageable pageable);

    List<Booking> findAllByItemIdIn(Set<Integer> itemIdSet, Pageable pageable);

    List<Booking> findByItemIdInAndStartTimeIsBeforeAndEndTimeIsAfter(Set<Integer> itemIdSet, LocalDateTime end, LocalDateTime start, Pageable pageable);

    List<Booking> findByItemIdInAndEndTimeIsAfter(Set<Integer> itemIdSet, LocalDateTime startTime, Pageable pageable);

    List<Booking> findByItemIdInAndEndTimeIsBefore(Set<Integer> itemIdSet, LocalDateTime end, Pageable pageable);

    List<Booking> findByItemIdInAndStatus(Set<Integer> itemIdSet, Status status, Pageable pageable);

    List<Booking> findByItemIdAndEndTimeIsBefore(int itemId, LocalDateTime endTime, Sort sort);

    List<Booking> findByItemIdAndStartTimeIsAfter(int itemId, LocalDateTime time, Sort sort);

    List<Booking> findByBookerIdAndItemIdAndStatusAndEndTimeIsBefore(int bookerId, int itemId, Status status, LocalDateTime endTime);

    List<Booking> findByItemIdAndStatusAndStartTimeIsAfter(int itemId, Status status, LocalDateTime startTime, Sort sort);

    List<Booking> findByItemIdAndStatusAndEndTimeIsBefore(int itemId, Status status, LocalDateTime endTime, Sort sort);

    List<Booking> findByItemIdAndStartTimeIsBeforeAndEndTimeIsAfter(int itemId, LocalDateTime end, LocalDateTime start);
}
