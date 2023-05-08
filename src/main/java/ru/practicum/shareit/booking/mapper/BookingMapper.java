package ru.practicum.shareit.booking.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingAllFieldsDto;
import ru.practicum.shareit.booking.dto.BookingSavingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {
    public static Booking mapToBooking(BookingSavingDto bookingSavingDto) {
        Booking booking = new Booking();
        booking.setStart(bookingSavingDto.getStart());
        booking.setEnd((bookingSavingDto.getEnd()));
        return booking;
    }

    public static BookingAllFieldsDto mapToBookingAllFieldsDto(Booking booking) {
        return BookingAllFieldsDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus().name())
                .booker(UserMapper.mapToUserDto(booking.getBooker()))
                .item(ItemMapper.mapToItemDto(booking.getItem()))
                .build();
    }

    public static List<BookingAllFieldsDto> mapToBookingAllFieldsDto(Iterable<Booking> bookings) {
        List<BookingAllFieldsDto> bookingsDto = new ArrayList<>();

        for (Booking booking : bookings) {
            bookingsDto.add(mapToBookingAllFieldsDto(booking));
        }
        return bookingsDto;
    }
}
