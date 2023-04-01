package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ElementAlreadyExistsException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User createUser(User user) {
        if (userRepository.findAllUsers().stream().anyMatch(u -> u.getEmail().equals(user.getEmail()))) {
            throw new ElementAlreadyExistsException(
                    String.format("Пользователь с email [%s] уже существует.", user.getEmail()));
        }

        return userRepository.saveUser(user);
    }

    @Override
    public User updateUser(UserDto userDto, Long userId) {
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new NotFoundException(String.format("Пользователь с ID=%d не наыйдет", userId));
        }

        boolean isEmailDuplicated = userRepository.findAllUsers()
                .stream().anyMatch(u -> u.getEmail().equals(userDto.getEmail()) && !Objects.equals(u.getId(), userId));
        if (isEmailDuplicated) {
            throw new ElementAlreadyExistsException(
                    String.format("Не возможно обновить email. Пользователь с email [%s] уже существует.", userDto.getEmail()));
        }

        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }

        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }

        return userRepository.updateUser(user);
    }

    @Override
    public User getUserById(Long userId) {
        return userRepository.findUserById(userId);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAllUsers();
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteUser(userId);
    }
}
