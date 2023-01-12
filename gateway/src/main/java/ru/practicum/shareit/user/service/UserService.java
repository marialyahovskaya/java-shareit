package ru.practicum.shareit.user.service;

import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.user.dto.UserDto;

public interface UserService {

    ResponseEntity<Object> addUser(final UserDto user);

    ResponseEntity<Object> findAllUsers();

    ResponseEntity<Object> findUserById(final Long id);

    ResponseEntity<Object> patchUser(final Long id, final UserDto userDto);

    ResponseEntity<Object> deleteUser(final Long id);
}
