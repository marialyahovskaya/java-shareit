package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {

    UserDto addUser(final UserDto user);

    Collection<UserDto> findAllUsers();

    UserDto findUserById(final Long id);

    UserDto patchUser(final Long id, final UserDto userDto);

    void deleteUser(final Long id);
}
