package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByItemIdAndItemOwnerIdAndStatusNotOrderByStartDesc(Long itemId, Long ownerId, BookingStatus status);

    @Query(value = "select distinct on(item_id, booker_id, status) b.* " +
            "from bookings b where b.booker_id = :bookerId and b.item_id = :itemId",
    nativeQuery = true)
    List<Booking> findDistinctBookingByBookerIdAndItemId(@Param("bookerId") Long bookerId, @Param("itemId") Long itemId);

    List<Booking> findByBookerIdOrderByStartDesc(Long bookerId);

    List<Booking> findByBookerIdAndEndIsBeforeOrderByStartDesc(Long bookerId, LocalDateTime end);

    List<Booking> findAllByBookerIdAndStartIsAfterAndEndIsAfterOrderByStartDesc(Long bookerId, LocalDateTime start, LocalDateTime end);

    List<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(Long bookerId, LocalDateTime start, LocalDateTime end);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus bookingStatus);

   List<Booking> findAllByItemOwnerIdOrderByStartDesc(Long ownerId);

   List<Booking> findAllByItemOwnerIdAndStatusNotOrderByStartDesc(Long ownerId, BookingStatus status);

   List<Booking> findByItemOwnerIdAndStartIsAfterAndEndIsAfterOrderByStartDesc(Long ownerId , LocalDateTime start, LocalDateTime end);

   List<Booking> findByItemOwnerIdAndEndIsBeforeOrderByStartDesc(Long ownerId, LocalDateTime end);

   List<Booking> findByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(Long owner, LocalDateTime start, LocalDateTime end);

   List<Booking> findByItemOwnerIdAndStatusOrderByStartDesc(Long ownerId, BookingStatus status);

}