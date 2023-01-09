package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserValidator;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.Collection;

@Service
@RequiredArgsConstructor

public class UserServiceImpl implements UserService {


    @Override
    public UserDto addUser(final UserDto userDto) {
//
//        User user = UserMapper.toUser(userDto);
//        UserValidator.validate(user);
//        return UserMapper.toUserDto(userRepository.save(user));
        return null;
    }

    @Override
    public Collection<UserDto> findAllUsers() {
//        return UserMapper.toUserDto(userRepository.findAll());
        return new ArrayList<>();
    }

    @Override
    public UserDto findUserById(final Long id) {
//        User user = userRepository.findById(id)
//                .orElseThrow(() -> new NotFoundException("User not found"));
//        return UserMapper.toUserDto(user);
        return null;
    }

    @Override
    public UserDto patchUser(final Long id, final UserDto userDto) {
//        User user = userRepository.findById(id)
//                .orElseThrow(() -> new NotFoundException("User not found"));
//        User userToUpdate = user;
//        if (userDto.getName() != null) {
//            userToUpdate.setName(userDto.getName());
//        }
//        if (userDto.getEmail() != null) {
//            if (!userRepository.findByEmailContainingIgnoreCase(userDto.getEmail()).isEmpty()) {
//                throw new AlreadyExistsException("User with provided email already exists");
//            }
//            userToUpdate.setEmail(userDto.getEmail());
//        }
//
//       UserValidator.validate(userToUpdate);
//        userRepository.save(userToUpdate);
//        return UserMapper.toUserDto(userToUpdate);
    return null;
    }


    @Override
    public void deleteUser(final Long id) {
//        userRepository.deleteById(id);
    }
}
