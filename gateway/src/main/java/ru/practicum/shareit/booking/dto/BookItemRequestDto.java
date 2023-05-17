package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookItemRequestDto {

    private long itemId;

    @FutureOrPresent(message = "The booking date cannot be in the past.")
    @NotNull(message = "The booking start date cannot be empty.")
    private LocalDateTime start;

    @Future(message = "The end date of the booking cannot be in the past.")
    @NotNull(message = "The booking end date cannot be empty.")
    private LocalDateTime end;
}
