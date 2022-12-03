package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserValidator;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor

public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User addUser(final User user) {
        UserValidator.validate(user);
        return userRepository.save(user);
    }

    @Override
    public Collection<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User findUserById(final Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new NotFoundException("User not found");
        }
        return user.get();
    }

    @Override
    public User patchUser(final Long id, final User user) {
        User userToUpdate = findUserById(id);
        if (userToUpdate == null) {
            throw new NotFoundException("User not found");
        }
        if (user.getName() != null) {
            userToUpdate.setName(user.getName());
        }
        if (user.getEmail() != null) {
            if (!userRepository.findByEmailContainingIgnoreCase(user.getEmail()).isEmpty()) {
                throw new AlreadyExistsException("User with provided email already exists");
            }
            userToUpdate.setEmail(user.getEmail());
        }

        UserValidator.validate(userToUpdate);
        userRepository.save(userToUpdate);
        return userToUpdate;
    }


    @Override
    public void deleteUser(final Long id) {
        userRepository.deleteById(id);
    }
}
