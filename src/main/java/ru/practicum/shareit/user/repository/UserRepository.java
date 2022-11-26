package ru.practicum.shareit.user.repository;


import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserRepository {

 User addUser(final User user);

 Collection<User> findAllUsers();

 User findUserById(final Long id);

 User findUserByEmail(final String email);

 User updateUser(final User user);

 void deleteUser(final Long id);

}
