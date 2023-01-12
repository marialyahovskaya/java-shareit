package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.UserClient;
import ru.practicum.shareit.user.UserValidator;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.ArrayList;
import java.util.Collection;

@Service
@RequiredArgsConstructor

public class UserServiceImpl implements UserService {


    private final UserClient userClient;
    @Override
    public ResponseEntity<Object> addUser(final UserDto userDto) {
        UserValidator.validate(userDto);
        return userClient.addUser(userDto);
    }

    @Override
    public Collection<UserDto> findAllUsers() {
//        return UserMapper.toUserDto(userRepository.findAll());
        return new ArrayList<>();
    }

    @Override
    public ResponseEntity<Object> findUserById(final Long id) {
//        User user = userRepository.findById(id)
//                .orElseThrow(() -> new NotFoundException("User not found"));
//        return UserMapper.toUserDto(user);
        return userClient.getUser(id);
    }

    @Override
    public ResponseEntity<Object> patchUser(final Long id, final UserDto userDto) {
        UserValidator.validatePatch(userDto);
        return userClient.patchUser(id, userDto);
    }


    @Override
    public ResponseEntity<Object> deleteUser(final Long id) {
        return userClient.deleteUser(id);
    }
}
