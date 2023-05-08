package ru.practicum.shareit.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Constructor;
import java.util.HashSet;
import javax.validation.ConstraintViolationException;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

class ErrorHandlerTest {
    /**
     * Method under test: {@link ErrorHandler#handleMethodArgumentNotValidException(MethodArgumentNotValidException)}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testHandleMethodArgumentNotValidException() {
        // TODO: Complete this test.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.IllegalArgumentException: Constructor must not be null
        //   See https://diff.blue/R013 to resolve this issue.

        ErrorHandler errorHandler = new ErrorHandler();
        MethodParameter parameter = new MethodParameter((Constructor<?>) null, 1);

        errorHandler.handleMethodArgumentNotValidException(
                new MethodArgumentNotValidException(parameter, new BindException("Target", "Object Name")));
    }

    /**
     * Method under test: {@link ErrorHandler#handleMethodArgumentNotValidException(MethodArgumentNotValidException)}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testHandleMethodArgumentNotValidException2() {
        // TODO: Complete this test.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Objects.requireNonNull(Objects.java:221)
        //       at ru.practicum.shareit.exceptions.ErrorHandler.handleMethodArgumentNotValidException(ErrorHandler.java:24)
        //   See https://diff.blue/R013 to resolve this issue.

        ErrorHandler errorHandler = new ErrorHandler();
        errorHandler.handleMethodArgumentNotValidException(
                new MethodArgumentNotValidException(null, new BindException("Target", "Object Name")));
    }

    /**
     * Method under test: {@link ErrorHandler#handleMethodArgumentNotValidException(MethodArgumentNotValidException)}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testHandleMethodArgumentNotValidException3() {
        // TODO: Complete this test.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Objects.requireNonNull(Objects.java:221)
        //       at ru.practicum.shareit.exceptions.ErrorHandler.handleMethodArgumentNotValidException(ErrorHandler.java:24)
        //   See https://diff.blue/R013 to resolve this issue.

        ErrorHandler errorHandler = new ErrorHandler();
        MethodParameter parameter = mock(MethodParameter.class);
        errorHandler.handleMethodArgumentNotValidException(
                new MethodArgumentNotValidException(parameter, new BindException("Target", "Object Name")));
    }

    /**
     * Method under test: {@link ErrorHandler#handleMethodArgumentNotValidException(MethodArgumentNotValidException)}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testHandleMethodArgumentNotValidException4() {
        // TODO: Complete this test.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at ru.practicum.shareit.exceptions.ErrorHandler.handleMethodArgumentNotValidException(ErrorHandler.java:24)
        //   See https://diff.blue/R013 to resolve this issue.

        (new ErrorHandler()).handleMethodArgumentNotValidException(null);
    }

    /**
     * Method under test: {@link ErrorHandler#handleMethodArgumentNotValidException(MethodArgumentNotValidException)}
     */
    @Test
    void testHandleMethodArgumentNotValidException5() {
        ErrorHandler errorHandler = new ErrorHandler();
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getFieldError()).thenReturn(new FieldError("Object Name", "Field", "Default Message"));
        assertEquals("Default Message", errorHandler.handleMethodArgumentNotValidException(ex).getError());
        verify(ex, atLeast(1)).getFieldError();
    }

    /**
     * Method under test: {@link ErrorHandler#handleMethodArgumentNotValidException(MethodArgumentNotValidException)}
     */
    @Test
    void testHandleMethodArgumentNotValidException6() {
        ErrorHandler errorHandler = new ErrorHandler();
        FieldError fieldError = mock(FieldError.class);
        when(fieldError.getDefaultMessage()).thenThrow(new ConstraintViolationException(new HashSet<>()));
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getFieldError()).thenReturn(fieldError);
        assertThrows(ConstraintViolationException.class, () -> errorHandler.handleMethodArgumentNotValidException(ex));
        verify(ex).getFieldError();
        verify(fieldError).getDefaultMessage();
    }
}

