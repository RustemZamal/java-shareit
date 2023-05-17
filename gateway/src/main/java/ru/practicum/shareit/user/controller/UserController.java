package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.client.UserClient;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserClient userClient;

    /**
     * Эндпоинт по созданию пользователя.
     * @param userDto обьек пользовател.
     * @return Возвращает созданного польлзователя.
     */
    @PostMapping
    public ResponseEntity<Object> creatUser(@Valid @RequestBody UserDto userDto) {
        log.info("Creating user={}", userDto);
        return userClient.createUser(userDto);
    }

    /**
     * Эндпонит по изменению пользователя.
     * @param userDto обьект с полями/полям, котрые будут изменены.
     * @param userId идентификатор пользователя, который будет изменет.
     * @return Возварщает измененного пользователя.
     */
    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@RequestBody UserDto userDto, @PathVariable Long userId) {
        log.info("Update user={}, userId={}", userDto, userId);
        return userClient.updateUser(userDto, userId);
    }

    /**
     * Энодпоинт по нахожднию пользователя по его идентификатору.
     * @param userId идентификатор пользователя.
     * @return Возварщает пользователя по его идентификатору
     */
    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable Long userId) {
        log.info("Get user by userId={}", userId);
        return userClient.getUserById(userId);
    }

    /**
     * Эндпоинт по нахождению всех пользователей.
     * @return Возвращает список всех пользователей.
     */
    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("Get all users.");
        return userClient.getAllUsers();
    }

    /**
     * Эндпоинт по удалению пользователя по его идентификатору.
     * @param userId идентификатор пользователя.
     */
    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable Long userId) {
        log.info("Delete user bu userId={}", userId);
        userClient.deleteUser(userId);
    }
}
