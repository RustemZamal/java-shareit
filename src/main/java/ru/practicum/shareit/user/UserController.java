package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    /**
     * Эндпоинт по созданию пользователя.
     * @param user обьек пользовател.
     * @return Возварещает созданаго польлзователя.
     */
    @PostMapping
    public User creatUser(@Valid @RequestBody User user) {
        return userService.createUser(user);
    }

    /**
     * Эндпонит по изменению пользователя.
     * @param userDto обьект с полями/полям, котрые будут изменены.
     * @param userId идентификатор пользователя, который будет изменет.
     * @return Возварщает изменного пользователя.
     */
    @PatchMapping("/{userId}")
    public User updateUser(@Valid @RequestBody UserDto userDto, @PathVariable Long userId) {
        return userService.updateUser(userDto, userId);
    }

    /**
     * Энодпоинт по нахожднию пользователя по его идентификатору.
     * @param userId идентификатор пользователя.
     * @return Возварщает пользователя по его идентификатор
     */
    @GetMapping("/{userId}")
    public User getUserById(@PathVariable Long userId) {
        return userService.getUserById(userId);
    }

    /**
     * Эндпоинт возвращает всех пользователей.
     * @return Возвращает список всех пользователей.
     */
    @GetMapping
    public List<User> getAllUsers() {
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
