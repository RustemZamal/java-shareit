package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingAllFieldsDto;
import ru.practicum.shareit.booking.dto.BookingSavingDto;

import java.util.List;

public interface BookingService {

    BookingAllFieldsDto saveBooking(Long userId, BookingSavingDto bookingSavingDto);

    BookingAllFieldsDto approve(Long ownerId, Long bookingId, boolean approved);

    BookingAllFieldsDto getBookingById(Long userID, Long bookingId);

    List<BookingAllFieldsDto> getBookingsByBookerId(Long bookerId, String state);

    List<BookingAllFieldsDto> getBookingsByOwner(Long ownerId, String state);
}
