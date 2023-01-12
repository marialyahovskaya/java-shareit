package ru.practicum.shareit.user.service;

import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {

    ResponseEntity<Object> addUser(final UserDto user);

    Collection<UserDto> findAllUsers();

    UserDto findUserById(final Long id);

    ResponseEntity<Object> patchUser(final Long id, final UserDto userDto);

    void deleteUser(final Long id);
}
