package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByItemIdAndItemOwnerIdAndStatusNotOrderByStartDesc(Long itemId, Long ownerId, BookingStatus status);

    @Query(value = "select distinct on(item_id, booker_id, status) b.* "
            + "from bookings b where b.booker_id = :bookerId and b.item_id = :itemId and b.status = :status",
            nativeQuery = true)
    Optional<Booking> findDistinctBookingByBooker(
            @Param("bookerId") Long bookerId,
            @Param("itemId") Long itemId,
            @Param("status") String status
    );

    List<Booking> findByBookerIdOrderByStartDesc(Long bookerId, Pageable pageable);

    List<Booking> findByBookerIdAndEndIsBeforeOrderByStartDesc(Long bookerId, LocalDateTime end, Pageable pageable);

    List<Booking> findAllByBookerIdAndStartIsAfterAndEndIsAfterOrderByStartDesc(Long bookerId,
                                                                                LocalDateTime start,
                                                                                LocalDateTime end,
                                                                                Pageable pageable);

    List<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(Long bookerId,
                                                                              LocalDateTime start,
                                                                              LocalDateTime end,
                                                                              Pageable pageable);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(Long bookerId,
                                                          BookingStatus bookingStatus,
                                                          Pageable pageable);

   List<Booking> findAllByItemOwnerIdOrderByStartDesc(Long ownerId, Pageable pageable);

   List<Booking> findAllByItemOwnerIdAndStatusNotOrderByStartDesc(Long ownerId, BookingStatus status);

   List<Booking> findByItemOwnerIdAndStartIsAfterAndEndIsAfterOrderByStartDesc(Long ownerId,
                                                                               LocalDateTime start,
                                                                               LocalDateTime end,
                                                                               Pageable pageable);

   List<Booking> findByItemOwnerIdAndEndIsBeforeOrderByStartDesc(Long ownerId,
                                                                 LocalDateTime end,
                                                                 Pageable pageable);

   List<Booking> findByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(Long owner,
                                                                                LocalDateTime start,
                                                                                LocalDateTime end,
                                                                                Pageable pageable);

   List<Booking> findByItemOwnerIdAndStatusOrderByStartDesc(Long ownerId, BookingStatus status, Pageable pageable);

}
