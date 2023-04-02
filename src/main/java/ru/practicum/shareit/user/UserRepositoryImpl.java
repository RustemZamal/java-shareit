package ru.practicum.shareit.user;


import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Repository
public class UserRepositoryImpl implements UserRepository {

    private final Map<Long, User> users = new HashMap<>();

    private Long id = 1L;

    @Override
    public User saveUser(User user) {
        user.setId(id++);
        users.put(user.getId(),user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        users.put(user.getId(),user);
        return users.get(user.getId());
    }

    @Override
    public Optional<User> findUserById(Long userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public List<User> findAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void deleteUser(Long id) {
        users.remove(id);
    }
}
