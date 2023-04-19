package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ElementAlreadyExistsException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto createUser(UserDto userDto) {
        if (userRepository.findAllUsers().stream().anyMatch(u -> u.getEmail().equals(userDto.getEmail()))) {
            throw new ElementAlreadyExistsException(
                    String.format("Пользователь с email [%s] уже существует.", userDto.getEmail()));
        }

        return UserMapper.toUserDto(userRepository.saveUser(UserMapper.toUser(userDto)));
    }

    @Override
    public UserDto updateUser(UserDto userDto, Long userId) {
        User user = userRepository.findUserById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с ID=%d не наыйден!", userId)));

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

        return UserMapper.toUserDto(userRepository.updateUser(user));
    }

    @Override
    public UserDto getUserById(Long userId) {
        return UserMapper.toUserDto(userRepository.findUserById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с ID=%d не найден!", userId))));
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAllUsers()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteUser(userId);
    }
}
