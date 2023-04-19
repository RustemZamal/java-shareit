package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingAllFieldsDto;
import ru.practicum.shareit.booking.dto.BookingSavingDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    /**
     * Эндпонит по добовлению бронированию.
     * @param userId id пользователя бронирующий продукт.
     * @param bookingSavingDto данные о вбронирования.
     * @return Возвращает объект бронировния.
     */
    @PostMapping
    public BookingAllFieldsDto saveBooking(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestBody @Valid BookingSavingDto bookingSavingDto) {
        return bookingService.saveBooking(userId, bookingSavingDto);
    }

    /**
     * Эндпоинт по потверждению или отклонинию запроса на бронирование.
     * @param ownerId id владельца товара
     * @param bookingId id брони
     * @param approved параметр принимает значение true или false.
     * @return Возвращает объект бронирования.
     */
    @PatchMapping("/{bookingId}")
    public BookingAllFieldsDto approve(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @PathVariable Long bookingId,
            @RequestParam boolean approved) {
        return bookingService.approve(ownerId, bookingId, approved);
    }

    /**
     * Эндпоинт по получению данных о конкретном бронирование.
     * @param userId id пользователя.
     * @param bookingId id бронирования.
     * @return Возвращает объект бронирования.
     */
    @GetMapping("/{bookingId}")
    public BookingAllFieldsDto getBookingById(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long bookingId) {
        return bookingService.getBookingById(userId, bookingId);
    }

    /**
     * Эндпоинт по получению списка всех бронирования пользователя, кто забронировал товар.
     * @param bookerId id пользователя
     * @param state параметр может принимать заначения: ALL, CURRENT, PAST, FUTURE, WAITING, REJECTED
     * @return Возвралщает список бронирования.
     */
    @GetMapping
    public List<BookingAllFieldsDto> getBookingsByUBooker(
            @RequestHeader("X-Sharer-User-Id") Long bookerId,
            @RequestParam(required = false, defaultValue = "ALL") String state) {
        return bookingService.getBookingsByBookerId(bookerId, state);
    }

    /**
     * Эндпоинт по получению списка всех бронирования владельца товара.
     * @param ownerId id пользователя
     * @param state параметр может принимать заначения: ALL, CURRENT, PAST, FUTURE, WAITING, REJECTED
     * @return Возвралщает список бронирования.
     */
    @GetMapping("/owner")
    public List<BookingAllFieldsDto> getBookingsByOwner(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @RequestParam(required = false, defaultValue = "ALL") String state) {
        return bookingService.getBookingsByOwner(ownerId, state);
    }
}
