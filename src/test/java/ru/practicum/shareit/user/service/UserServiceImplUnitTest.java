package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
class UserServiceImplUnitTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private UserDto userDto = new UserDto(1L, "Mike", "mike@gmail.com");

    private User user = new User(1L, "Mike", "mike@gmail.com");


    private final Long userId = 1L;


    @BeforeEach
    void setUp() {
    }

    /**
     * Method under test: {@link UserServiceImpl#createUser(UserDto)}
     */
    @Test
    void createUser() {
        Mockito.when(userRepository.save(any())).thenReturn(user);

        UserDto actualUser = userService.createUser(userDto);

        Assertions.assertEquals(userDto, actualUser);
    }

    /**
     * Method under test: {@link UserServiceImpl#updateUser(UserDto, Long)}
     */
    @Test
    void updateUser() {
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        Mockito.when(userRepository.save(any())).thenReturn(user);

        UserDto actualUser = userService.updateUser(userDto, userId);

        Assertions.assertEquals(userDto, actualUser);
    }

    /**
     * Method under test: {@link UserServiceImpl#updateUser(UserDto, Long)}
     */
    @Test
    void updateUser_whenUserNotFound_thenNotFoundException() {
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> userService.updateUser(userDto, userId));
        Assertions.assertEquals("User with ID=1 was not found!", exception.getMessage());
        Mockito.verify(userRepository, Mockito.never()).save(user);
    }

    /**
     * Method under test: {@link UserServiceImpl#getUserById(Long)}
     */
    @Test
    void getUserById() {
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        UserDto actualUser = userService.getUserById(userId);

        Assertions.assertEquals(userDto, actualUser);
    }

    /**
     * Method under test: {@link UserServiceImpl#getAllUsers()}
     */
    @Test
    void getAllUsers() {
        Mockito.when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserDto> actualUsers = userService.getAllUsers();

        Assertions.assertEquals(List.of(userDto), actualUsers);
    }

    /**
     * Method under test: {@link UserServiceImpl#deleteUser(Long)}
     */
    @Test
    void deleteUser() {
        userService.deleteUser(userId);
        Mockito.verify(userRepository, Mockito.times(1)).deleteById(userId);
    }
}