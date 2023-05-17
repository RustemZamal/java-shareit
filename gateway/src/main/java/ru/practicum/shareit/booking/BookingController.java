package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exceptions.InvalidDataException;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingClient bookingClient;

    /**
     * Эндпоинт по получению списка всех бронирования пользователя, кто забронировал товар.
     * @param userId id пользователя
     * @param stateParam параметр может принимать заначения: ALL, CURRENT, PAST, FUTURE, WAITING, REJECTED
     * @return Возвралщает список бронирования.
     */
    @GetMapping
    public ResponseEntity<Object> getBookings(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                              @RequestParam(name = "size", defaultValue = "10") @Min(1) Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new InvalidDataException("Unknown state: " + stateParam));
        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getBookings(userId, state, from, size);
    }

    /**
     * Эндпонит по добовлению бронированию.
     * @param userId id пользователя бронирующий продукт.
     * @param requestDto данные о вбронирования.
     * @return Возвращает объект бронировния.
     */
    @PostMapping
    public ResponseEntity<Object> bookItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @RequestBody @Valid BookItemRequestDto requestDto) {
        log.info("Creating booking {}, userId={}", requestDto, userId);
        return bookingClient.bookItem(userId, requestDto);
    }

    /**
     * Эндпоинт по получению данных о конкретном бронирование.
     * @param userId id пользователя.
     * @param bookingId id бронирования.
     * @return Возвращает объект бронирования.
     */
    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    /**
     * Эндпоинт по потверждению или отклонинию запроса на бронирование.
     * @param ownerId id владельца товара
     * @param bookingId id брони
     * @param approved параметр принимает значение true или false.
     * @return Возвращает объект бронирования.
     */
    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approve(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @PathVariable Long bookingId,
            @RequestParam boolean approved) {
        log.info("Get a booking approval request with parameters approved={}, ownerId={}, bookingId={}",
                approved,
                ownerId,
                bookingId);
        return bookingClient.approve(ownerId, bookingId, approved);
    }

    /**
     * Эндпоинт по получению списка всех бронирования владельца товара.
     * @param ownerId id пользователя
     * @param stateParam параметр может принимать заначения: ALL, CURRENT, PAST, FUTURE, WAITING, REJECTED
     * @return Возвралщает список бронирования.
     */
    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsByOwner(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @RequestParam(name = "state", defaultValue = "all") String stateParam,
            @RequestParam(defaultValue = "0") @Min(0) Integer from,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new InvalidDataException("Unknown state: " + stateParam));
        log.info("Get booking by Owner with state {}, ownerId={}, from={}, size={}", stateParam, ownerId, from, size);
        return bookingClient.getBookingsByOwner(ownerId, state, from, size);
    }
}
