package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryUserRepository implements UserRepository {

    private final Map<Long, User> users = new HashMap<>();

    private Long nextId = 1L;

    private Long generateUserId() {
        return nextId++;
    }

    @Override
    public User addUser(final User user) {
        Long userId = generateUserId();
        user.setId(userId);
        users.put(userId, user);

        return user;
    }

    @Override
    public Collection<User> findAllUsers() {
        return users.values();
    }

    @Override
    public User findUserById(final Long id) {
        return users.get(id);
    }

    @Override
    public User findUserByEmail(String email) {
        for (User user : users.values()) {
            if (user.getEmail().equals(email)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public User updateUser(final User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void deleteUser(final Long id) {
        users.remove(id);
    }
}
