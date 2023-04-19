package ru.practicum.shareit.user;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    User saveUser(User user);

    User updateUser(User user);

    Optional<User> findUserById(Long userId);

    List<User> findAllUsers();

    void deleteUser(Long userId);
}
