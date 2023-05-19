package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exceptions.ErrorResponse;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @MockBean
    private BookingClient bookingClient;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    private final String headerShareUserId = "X-Sharer-User-Id";

    private final Long userId = 1L;

    @Test
    @DisplayName("Mockmvc test for create method when date is null")
    void saveBooking_whenBookingEndStartDateIsNull_theReturnBadRequest() throws Exception {
        BookItemRequestDto invalidBooking = new BookItemRequestDto(
                1,
                null,
                null
        );

        mvc.perform(post("/bookings")
                        .header(headerShareUserId, userId)
                        .content(mapper.writeValueAsString(invalidBooking))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(result -> {
                    MethodArgumentNotValidException exception = (MethodArgumentNotValidException) result.getResolvedException();
                    BindingResult bindingResult = exception.getBindingResult();
                    List<String> errors = List.of("The booking start date cannot be empty.", "The booking end date cannot be empty.");
                    List<String> actualErrors = bindingResult.getAllErrors()
                            .stream()
                            .map(DefaultMessageSourceResolvable::getDefaultMessage)
                            .collect(Collectors.toList());
                    if (!errors.contains(actualErrors.get(0)) && errors.contains(actualErrors.get(1))) {
                        throw new AssertionError("Expected" + errors + "but got" + actualErrors);
                    }
                });

        verify(bookingClient, never()).bookItem(userId, invalidBooking);
    }

    @Test
    @DisplayName("Mockmvc test for create method when an invalid date")
    void saveBooking_whenBookingEndStartInThePast_theReturnBadRequest() throws Exception {
        BookItemRequestDto invalidBooking = new BookItemRequestDto(
                1,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusDays(1));

        mvc.perform(post("/bookings")
                        .header(headerShareUserId, userId)
                        .content(mapper.writeValueAsString(invalidBooking))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(result -> {
                    MethodArgumentNotValidException exception = (MethodArgumentNotValidException) result.getResolvedException();
                    BindingResult bindingResult = exception.getBindingResult();
                    List<String> errors = List.of("The booking start date cannot be empty.", "The booking end date cannot be empty.");
                    List<String> actualErrors = bindingResult.getAllErrors()
                            .stream()
                            .map(DefaultMessageSourceResolvable::getDefaultMessage)
                            .collect(Collectors.toList());
                    if (!errors.contains(actualErrors.get(0)) && errors.contains(actualErrors.get(1))) {
                        throw new AssertionError("Expected" + errors + "but got" + actualErrors);
                    }
                });

        verify(bookingClient, never()).bookItem(userId, invalidBooking);
    }

    @Test
    @DisplayName("MockMvc test for getBookings method with invalid param")
    void getBookings_whenParamInvalid_thenReturnBsdRequest() throws Exception {
        ErrorResponse error = new ErrorResponse(
                "from: must be greater than or equal to 0. size: must be greater than or equal to 1.");

        String response = mvc.perform(get("/bookings")
                        .header(headerShareUserId, userId)
                        .param("state", "ALL")
                        .param("from", "-1")
                        .param("size", "0"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        ErrorResponse actualError = mapper.readValue(response, ErrorResponse.class);

        Assertions.assertEquals(error.getError(), actualError.getError());
        verify(bookingClient, never()).getBookingsByOwner(userId, BookingState.CURRENT, -1, 0);
    }

    @Test
    @DisplayName("MockMvc test for getBookings method with invalid stateParam")
    void getBookings_whenInvalidStateParam_thenReturnBsdRequest() throws Exception {
        String stateParam = "UNSUPPORTED_STATUS";
        String error = "Unknown state: " + stateParam;

        String response = mvc.perform(get("/bookings/owner")
                        .header(headerShareUserId, userId)
                        .param("state", stateParam)
                        .param("from", "1")
                        .param("size", "2"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        ErrorResponse actualError = mapper.readValue(response, ErrorResponse.class);

        Assertions.assertEquals(error, actualError.getError());
        verify(bookingClient, never()).getBookingsByOwner(userId, BookingState.CURRENT, -1, 0);
    }

    @Test
    @DisplayName("MockMvc test for getBookingByOwner method with invalid param")
    void getBookingsByOwner_whenParamInvalid_thenReturnBsdRequest() throws Exception {
        ErrorResponse error = new ErrorResponse(
                "from: must be greater than or equal to 0. size: must be greater than or equal to 1.");

        String response = mvc.perform(get("/bookings/owner")
                        .header(headerShareUserId, userId)
                        .param("state", "ALL")
                        .param("from", "-1")
                        .param("size", "0"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        ErrorResponse actualError = mapper.readValue(response, ErrorResponse.class);

        Assertions.assertEquals(error.getError(), actualError.getError());
        verify(bookingClient, never()).getBookingsByOwner(userId, BookingState.CURRENT, -1, 0);
    }

    @Test
    @DisplayName("MockMvc test for getBookingByOwner method with invalid stateParam")
    void getBookingsByOwner_whenInvalidStateParam_thenReturnBsdRequest() throws Exception {
        String stateParam = "UNSUPPORTED_STATUS";
        String error = "Unknown state: " + stateParam;

        String response = mvc.perform(get("/bookings/owner")
                        .header(headerShareUserId, userId)
                        .param("state", stateParam)
                        .param("from", "1")
                        .param("size", "2"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        ErrorResponse actualError = mapper.readValue(response, ErrorResponse.class);

        Assertions.assertEquals(error, actualError.getError());
        verify(bookingClient, never()).getBookingsByOwner(userId, BookingState.CURRENT, -1, 0);
    }
}