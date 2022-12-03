package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserService {

   User addUser(final User user);

   Collection<User> findAllUsers();

   User findUserById(final Long id);

   User patchUser(final Long id, final User user);

   void deleteUser(final Long id);
}
