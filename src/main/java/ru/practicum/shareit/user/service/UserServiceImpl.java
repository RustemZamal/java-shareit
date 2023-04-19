package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.ElementAlreadyExistsException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        try {
            return UserMapper.mapToUserDto(userRepository.save(UserMapper.mapToUser(userDto)));
        } catch (DataIntegrityViolationException ex) {
                throw new ElementAlreadyExistsException(String.format(
                        "User with email [%s] already exists", userDto.getEmail()));
        }
    }

    @Override
    @Transactional
    public UserDto updateUser(UserDto userDto, Long userId) {
       User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с ID=%d не наыйден!", userId)));

       if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
       }

       if (userDto.getName() != null) {
            user.setName(userDto.getName());
       }

       try {
            return UserMapper.mapToUserDto(userRepository.save(user));
       } catch (DataIntegrityViolationException ex) {
                throw new ElementAlreadyExistsException(String.format(
                        "User with email [%s] already exists", userDto.getEmail()));
       }
    }

    @Override
    public UserDto getUserById(Long userId) {
        return UserMapper.mapToUserDto(userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with ID=%d was not found!", userId))));
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
}
