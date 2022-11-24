package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryUserRepository implements UserRepository {

    private Map<Long, User> users = new HashMap<>();

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
    public User findUserByEmail(String email) {
        for (User user : users.values()) {
            if (user.getEmail().equals(email)) {
                return user;
            }
        }
        return null;
    }
}
