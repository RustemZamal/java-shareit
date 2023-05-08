package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingAllFieldsDto;
import ru.practicum.shareit.booking.dto.BookingSavingDto;

import java.util.List;

public interface BookingService {

    BookingAllFieldsDto saveBooking(Long userId, BookingSavingDto bookingSavingDto);

    BookingAllFieldsDto approve(Long ownerId, Long bookingId, boolean approved);

    BookingAllFieldsDto getBookingById(Long userId, Long bookingId);

    List<BookingAllFieldsDto> getBookingsByBookerId(Long bookerId, String state, Pageable pageable);

    List<BookingAllFieldsDto> getBookingsByOwner(Long ownerId, String state, Pageable pageable);
}
