package ru.practicum.shareit.booking.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.practicum.shareit.booking.dto.BookingAllFieldsDto;
import ru.practicum.shareit.booking.dto.BookingSavingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.OffsetPageRequest;


@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @MockBean
    private BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    private final String headerShareUserId = "X-Sharer-User-Id";

    private final Long userId = 1L;

    private static User user;
    private static ItemRequest request;

    private static Item item;

    private static BookingSavingDto bookingSaving;

    private static BookingAllFieldsDto bookingAllFields;

    private static ItemDto itemDto;

    @BeforeAll
    static void setUp() {

        user = new User();
        user.setEmail("jane.doe@example.org");
        user.setId(1L);
        user.setName("Name");

        request = new ItemRequest();
        request.setCreated(LocalDate.of(1970, 1, 1).atStartOfDay());
        request.setDescription("The characteristics of someone or something");
        request.setId(1L);
        request.setRequester(user);

        item = new Item();
        item.setAvailable(true);
        item.setDescription("The characteristics of someone or something");
        item.setId(1L);
        item.setName("Name");
        item.setOwner(user);
        item.setRequest(request);

        itemDto = new ItemDto(
                1L,
                "Аккумуляторная дрель",
                "Аккумуляторная дрель + аккумулятор",
                true,
                request.getId());

        bookingSaving = new BookingSavingDto(
                null,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                item.getId(),
                user.getId(),
                null);

        bookingAllFields = new BookingAllFieldsDto(
                1L,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(2),
                "APPROVED",
                new UserDto(1L, "Mike", "mike@mail.gcom"),
                itemDto);
    }

    /**
     * Method under test: {@link BookingController#saveBooking(Long, BookingSavingDto)}.
     */
    @Test
    void saveBooking() throws Exception {
        when(bookingService.saveBooking(anyLong(), any())).thenReturn(bookingAllFields);

        String result = mvc.perform(post("/bookings")
                        .header(headerShareUserId, userId)
                        .content(mapper.writeValueAsString(bookingSaving))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertEquals(bookingAllFields, mapper.readValue(result, BookingAllFieldsDto.class));
    }

    /**
     * Method under test: {@link BookingController#saveBooking(Long, BookingSavingDto)}
     */
    @Test
    void saveBooking_whenBookingInvalid_theReturnBadRequest() throws Exception {
        BookingSavingDto invalidBooking = new BookingSavingDto(
                null,
                null,
                null,
                item.getId(),
                user.getId(),
                "WAITING");

        mvc.perform(post("/bookings")
                        .header(headerShareUserId, userId)
                        .content(mapper.writeValueAsString(invalidBooking))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
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

        verify(bookingService, never()).saveBooking(anyLong(), any());
    }


    /**
     * Method under test: {@link BookingController#approve(Long, Long, boolean)}.
     */
    @Test
    void approve() throws Exception {
        when(bookingService.approve(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingAllFields);

        mvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header(headerShareUserId, userId)
                        .param("approved", "true")
                        .content(mapper.writeValueAsString(bookingAllFields))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    /**
     * Method under test: {@link BookingController#approve(Long, Long, boolean)}
     */
    @Test
    void approve_whenParamMissing_thenReturnBadRequest() throws Exception {
        mvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header(headerShareUserId, userId))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    /**
     * Method under test: {@link BookingController#getBookingById(Long, Long)}
     */
    @Test
    void getBookingById() throws Exception {
        when(bookingService.getBookingById(anyLong(), anyLong())).thenReturn(bookingAllFields);

        String result = mvc.perform(get("/bookings/{bookingId}", 1L)
                        .header(headerShareUserId, userId))
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertEquals(bookingAllFields, mapper.readValue(result, BookingAllFieldsDto.class));
    }

    /**
     * Method under test: {@link BookingController#getBookingsByOwner(Long, String, int, int)}
     */
    @Test
    void getBookingsByOwner() throws Exception {
        when(bookingService.getBookingsByOwner(anyLong(), anyString(), any(OffsetPageRequest.class)))
                .thenReturn(List.of(bookingAllFields));

        String result = mvc.perform(get("/bookings/owner")
                        .header(headerShareUserId, userId)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        List<BookingAllFieldsDto> actualResult = mapper.readValue(result, new TypeReference<>() {
        });

        assertEquals(List.of(bookingAllFields), actualResult);
    }

    /**
     * Method under test: {@link BookingController#getBookingsByBooker(Long, String, int, int)}
     */
    @Test
    void testGetBookingsByBooker() throws Exception {
        when(bookingService.getBookingsByBookerId(anyLong(), anyString(), any(OffsetPageRequest.class)))
                .thenReturn(List.of(bookingAllFields));

        String result = mvc.perform(get("/bookings/")
                        .header(headerShareUserId, userId)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        List<BookingAllFieldsDto> actualResult = mapper.readValue(result, new TypeReference<>() {});

        assertEquals(List.of(bookingAllFields), actualResult);
    }
}

