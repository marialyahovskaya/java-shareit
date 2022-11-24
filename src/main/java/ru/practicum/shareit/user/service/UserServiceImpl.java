package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.user.UserValidator;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@Service
@RequiredArgsConstructor

public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public User addUser(final User user) {
        UserValidator.validate(user);
        if (userRepository.findUserByEmail(user.getEmail()) != null) {
            throw new AlreadyExistsException("User already exists");
        }
        return userRepository.addUser(user);
    }
}
