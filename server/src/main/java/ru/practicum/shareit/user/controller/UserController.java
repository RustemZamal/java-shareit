package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    /**
     * Эндпоинт по созданию пользователя.
     * @param userDto обьек пользовател.
     * @return Возвращает созданного польлзователя.
     */
    @PostMapping
    public UserDto creatUser(@RequestBody UserDto userDto) {
        return userService.createUser(userDto);
    }

    /**
     * Эндпонит по изменению пользователя.
     * @param userDto обьект с полями/полям, котрые будут изменены.
     * @param userId идентификатор пользователя, который будет изменет.
     * @return Возварщает измененного пользователя.
     */
    @PatchMapping("/{userId}")
    public UserDto updateUser(@RequestBody UserDto userDto, @PathVariable Long userId) {
        return userService.updateUser(userDto, userId);
    }

    /**
     * Энодпоинт по нахожднию пользователя по его идентификатору.
     * @param userId идентификатор пользователя.
     * @return Возварщает пользователя по его идентификатору
     */
    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable Long userId) {
        return userService.getUserById(userId);
    }

    /**
     * Эндпоинт по нахождению всех пользователей.
     * @return Возвращает список всех пользователей.
     */
    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    /**
     * Эндпоинт по удалению пользователя по его идентификатору.
     * @param userId идентификатор пользователя.
     */
    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }
}
