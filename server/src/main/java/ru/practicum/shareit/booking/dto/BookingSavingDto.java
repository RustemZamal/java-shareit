package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingSavingDto {

    private Long id;

    @FutureOrPresent(message = "The booking date cannot be in the past.")
    @NotNull(message = "The booking start date cannot be empty.")
    private LocalDateTime start;

    @Future(message = "The end date of the booking cannot be in the past.")
    @NotNull(message = "The booking end date cannot be empty.")
    private LocalDateTime end;

    private Long itemId;

    private Long bookerId;

    private String status;
}
