package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.practicum.shareit.exceptions.ErrorHandler;
import ru.practicum.shareit.exceptions.ErrorResponse;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemAllFieldsDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.util.OffsetPageRequest;

import javax.validation.ConstraintViolationException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @MockBean
    private ItemService itemService;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ErrorHandler errorHandler;

    private final String headerShareUserId = "X-Sharer-User-Id";

    private final Long userId = 1L;

    private final Long itemId = 1L;

    private final ItemDto itemDto = new ItemDto(
            1L,
            "Аккумуляторная дрель",
            "Аккумуляторная дрель + аккумулятор",
            true,
            2L);

    private final ItemDto itemError = new ItemDto(
            1L,
            null,
            null,
            true,
            2L);

    private final CommentDto commentDto = new CommentDto(
            1L,
            "Add comment from user1",
            "Mike",
            LocalDateTime.of(2023, 5, 5, 17, 11, 30));

    private final ItemAllFieldsDto itemAllFields = new ItemAllFieldsDto(
       1L,
            "Аккумуляторная дрель",
            "Аккумуляторная дрель + аккумулятор",
            true,
            new ItemAllFieldsDto.BookingDto(1L, 2L),
            new ItemAllFieldsDto.BookingDto(2L, 3L),
            List.of(commentDto));

    private final CommentDto commentDtoError = new CommentDto(
            1L,
            null,
            "Mike",
            LocalDateTime.now());

    /**
     * Method under test: {@link ItemController#addNewItem(Long, ItemDto)}
     */
    @Test
    void addNewItem() throws Exception {
        when(itemService.addNewItem(anyLong(), any())).thenReturn(itemDto);

        mvc.perform(MockMvcRequestBuilders.post("/items")
                        .header(headerShareUserId, userId)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
        verify(itemService, times(1)).addNewItem(userId, itemDto);
    }

    /**
     * Method under test: {@link ItemController#addNewItem(Long, ItemDto)}
     */
    @Test
    void addNewItem_invalidData_returnsBadRequest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/items/")
                        .header(headerShareUserId, userId)
                        .content(mapper.writeValueAsString(itemError))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(result -> {
                    MethodArgumentNotValidException exception = (MethodArgumentNotValidException) result.getResolvedException();
                    BindingResult bindingResult = exception.getBindingResult();
                    List<String> errors = List.of("The name cannot be empty!", "The description cannot be empty!");
                    List<String> actualErrors = bindingResult.getAllErrors().stream()
                            .map(DefaultMessageSourceResolvable::getDefaultMessage)
                            .collect(Collectors.toList());
                    if (!errors.contains(actualErrors.get(0)) && !errors.contains(actualErrors.get(1))) {
                        throw new AssertionError("Expected " + errors + ", but got " + actualErrors);
                    }
                })
                .andExpect(status().isBadRequest());
        verify(itemService, never()).addNewComment(userId, itemId, commentDtoError);
    }

    /**
     * Method under test: {@link ItemController#addNewComment(Long, Long, CommentDto)}
     */
    @Test
    void addNewComment() throws Exception {
        when(itemService.addNewComment(anyLong(), anyLong(), any())).thenReturn(commentDto);

        mvc.perform(MockMvcRequestBuilders.post("/items/{itemId}/comment", 1)
                        .header(headerShareUserId, userId)
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is((commentDto.getText()))))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())))
                .andExpect(jsonPath("$.created", is(commentDto.getCreated().toString())));
        verify(itemService, times(1)).addNewComment(userId, itemId, commentDto);
    }

    @Test
    void addNewComment_invalidInput_returnsBadRequest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/items/{itemId}/comment", 1)
                        .header(headerShareUserId, userId)
                        .content(mapper.writeValueAsString(commentDtoError))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(result -> {
                    MethodArgumentNotValidException exception = (MethodArgumentNotValidException) result.getResolvedException();
                    BindingResult bindingResult = exception.getBindingResult();
                    List<String> errors = Collections.singletonList("The text comment should not be empty.");
                    List<String> actualErrors = bindingResult.getAllErrors().stream()
                            .map(DefaultMessageSourceResolvable::getDefaultMessage)
                            .collect(Collectors.toList());
                    if (!errors.equals(actualErrors)) {
                        throw new AssertionError("Expected " + errors + ", but got " + actualErrors);
                    }
                })
                .andExpect(status().isBadRequest());
        verify(itemService, never()).addNewComment(userId, itemId, commentDtoError);
    }

    /**
     * Method under test: {@link ItemController#updateItem(Long, Long, ItemDto)}
     */
    @Test
    void updateItem() throws Exception {
        when(itemService.updateItem(anyLong(), anyLong(), any())).thenReturn(itemDto);

        mvc.perform(MockMvcRequestBuilders.patch("/items/{itemId}", itemId)
                        .header(headerShareUserId, userId)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
        verify(itemService, times(1)).updateItem(userId, itemId, itemDto);
        verifyNoMoreInteractions(itemService);

    }

    /**
     * Method under test: {@link ItemController#getItemById(Long, Long)}
     */
    @Test
    void getItemById() throws Exception {
        when(itemService.getItemById(anyLong(), anyLong())).thenReturn(itemAllFields);

        MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/items/{itemId}", itemId)
                        .header(headerShareUserId, userId)
                        .content(mapper.writeValueAsString(itemAllFields))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemAllFields.getId()), Long.class))
                .andExpect(jsonPath("$.comments", hasSize(1))).andReturn();

        String responseBody = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ItemAllFieldsDto responseItem = mapper.readValue(responseBody, ItemAllFieldsDto.class);

        assertThat(itemAllFields, equalTo(responseItem));
        verify(itemService, times(1)).getItemById(anyLong(), anyLong());
    }

    /**
     * Method under test: {@link ItemController#getItemsBySearch(String, int, int)}
     */
    @Test
    void getItemsBySearch() throws Exception {
        when(itemService.getItemBySearch(anyString(), any(OffsetPageRequest.class))).thenReturn(List.of(itemDto));

        mvc.perform(MockMvcRequestBuilders.get("/items/search")
                        .param("text", "дрель")
                        .param("from", "0")
                        .param("size", "2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable())));
        verify(itemService, times(1)).getItemBySearch("дрель", new OffsetPageRequest(0, 2));
    }

    /**
     * Method under test: {@link ItemController#getItemByUserId(Long, int, int)}
     */
    @Test
    void getItemByUserId() throws Exception {
        when(itemService.getItemByUserId(anyLong(), any(OffsetPageRequest.class))).thenReturn(List.of(itemAllFields));

        mvc.perform(MockMvcRequestBuilders.get("/items")
                        .header(headerShareUserId, userId)
                        .param("text", "дрель")
                        .param("from", "0")
                        .param("size", "2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemAllFields.getName())))
                .andExpect(jsonPath("$[0].description", is(itemAllFields.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemAllFields.getAvailable())));
        verify(itemService, times(1)).getItemByUserId(userId, new OffsetPageRequest(0, 2));
    }

    /**
     * Method under test: {@link ItemController#getItemByUserId(Long, int, int)}
     */
    @Test
    void getItemByUserId_invalidParamFrom_returnBadRequest() throws Exception {
        ErrorResponse error = new ErrorResponse("size: must be greater than or equal to 0. from: must be greater than or equal to 1.");
        when(errorHandler.handleConstraintViolationException(any(ConstraintViolationException.class))).thenReturn(error);
        mvc.perform(MockMvcRequestBuilders.get("/items")
                        .header(headerShareUserId, userId)
                        .param("text", "дрель")
                        .param("from", "-1")
                        .param("size", "1"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ConstraintViolationException))
                .andExpect(jsonPath("$.error", is(error.getError())));

        verify(itemService, never()).getItemByUserId(userId, new OffsetPageRequest(1, 2));
        verify(errorHandler, times(1)).handleConstraintViolationException(any());
    }
}