package ru.practicum.shareit.user;

import java.util.List;

public interface UserRepository {

    User saveUser(User user);

    User updateUser(User user);

    User findUserById(Long userId);

    List<User> findAllUsers();

    void deleteUser(Long userId);
}
