package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    User createUser(User user);

    User updateUser(UserDto userDto, Long userId);

    User getUserById(Long userId);

    List<User> getAllUsers();

    void deleteUser(Long userId);
}
