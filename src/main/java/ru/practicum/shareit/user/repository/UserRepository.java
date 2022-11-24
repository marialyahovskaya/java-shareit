package ru.practicum.shareit.user.repository;


import ru.practicum.shareit.user.model.User;

public interface UserRepository {

 User addUser(User user);

 User findUserByEmail(String email);

}
