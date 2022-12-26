package ru.practicum.shareit.user;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

@DataJpaTest
class UserRepositoryIT {

    @BeforeEach
    void addUsers() {
        userRepository.save(new User(null, "John", "john.doe@email.com"));
        userRepository.save(new User(null, "Jack", "john.foe@email.com"));
    }

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByEmailContainingIgnoreCase() {
        List<User> actualUsers = userRepository.findByEmailContainingIgnoreCase("doe");
        assertThat(actualUsers, hasSize(1));
    }

    @AfterEach
    void deleteUsers() {
        userRepository.deleteAll();
    }
}