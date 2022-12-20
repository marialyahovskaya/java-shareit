package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserValidator;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor

public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto addUser(final UserDto userDto) {

        User user = UserMapper.toUser(userDto);
        UserValidator.validate(user);
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public Collection<UserDto> findAllUsers() {
        return UserMapper.toUserDto(userRepository.findAll());
    }

    @Override
    public UserDto findUserById(final Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new NotFoundException("User not found");
        }
        return UserMapper.toUserDto(user.get());
    }

    @Override
    public UserDto patchUser(final Long id, final UserDto userDto) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new NotFoundException("User not found");
        }
        User userToUpdate = user.get();
        if (userDto.getName() != null) {
            userToUpdate.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            if (!userRepository.findByEmailContainingIgnoreCase(userDto.getEmail()).isEmpty()) {
                throw new AlreadyExistsException("User with provided email already exists");
            }
            userToUpdate.setEmail(userDto.getEmail());
        }

        UserValidator.validate(userToUpdate);
        userRepository.save(userToUpdate);
        return UserMapper.toUserDto(userToUpdate);
    }


    @Override
    public void deleteUser(final Long id) {
        userRepository.deleteById(id);
    }
}
