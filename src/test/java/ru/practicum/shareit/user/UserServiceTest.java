package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.Collection;
import java.util.List;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    Long johnId = 1L;

    Long jackId = 1L;

    User john = new User(
            johnId, "JOHN", "john@email.com");


    User jack = new User(
            jackId, "JACK", "jack@email.com");
    UserDto johnDto = new UserDto(
            johnId, "JOHN", "john@email.com");

    UserDto jackDto = new UserDto(
            jackId, "JACK", "jack@email.com");

    UserDto johnCreationDto = new UserDto(
            null, "JOHN", "john@email.com");

    User johnCreationEntity = new User(
            null, "JOHN", "john@email.com");

    @Mock
    private UserRepository userRepository;

    @Test
    void shouldAddUser() {

        UserService userService = new UserServiceImpl(userRepository);

        Mockito
                .when(userRepository.save(any()))
                .thenReturn(john);

        UserDto userDto = userService.addUser(johnCreationDto);

        assertThat(userDto.getId(), equalTo(johnDto.getId()));
        assertThat(userDto.getName(), equalTo(johnDto.getName()));
        assertThat(userDto.getEmail(), equalTo(johnDto.getEmail()));

        Mockito.verify(userRepository, Mockito.times(1))
                .save(johnCreationEntity);

    }

    @Test
    void shouldFindAllUsers() {
        UserService userService = new UserServiceImpl(userRepository);

        Mockito
                .when(userRepository.findAll())
                .thenReturn(List.of(john, jack));

        Collection<UserDto> users = userService.findAllUsers();

        assertThat(users, hasSize(2));
        for (UserDto sourceUser : List.of(johnDto, jackDto)) {
            assertThat(users, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(sourceUser.getName())),
                    hasProperty("email", equalTo(sourceUser.getEmail()))
            )));
        }
    }
}
