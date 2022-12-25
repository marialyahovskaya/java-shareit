package ru.practicum.shareit.user.unit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

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

    User john = new User(johnId, "JOHN", "john@email.com");
    User jack = new User(jackId, "JACK", "jack@email.com");

    UserDto johnDto = new UserDto(johnId, "JOHN", "john@email.com");

    UserDto jackDto = new UserDto(jackId, "JACK", "jack@email.com");

    UserDto johnCreationDto = new UserDto(null, "JOHN", "john@email.com");

    User johnCreationEntity = new User(null, "JOHN", "john@email.com");

    @Mock
    private UserRepository userRepository;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    void shouldAddUser() {

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

    @Test
    void shouldFindUserById() {

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(john));

        UserDto userDto = userService.findUserById(johnId);

        assertThat(userDto.getId(), notNullValue());
        assertThat(userDto.getName(), equalTo(johnDto.getName()));
        assertThat(userDto.getEmail(), equalTo(johnDto.getEmail()));

        Mockito.verify(userRepository, Mockito.times(1))
                .findById(johnId);
    }

    @Test
    void findUserByIdShouldThrowNotFoundExceptionWhenUserNotFound() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> userService.findUserById(99L));

        Assertions.assertEquals("User not found", exception.getMessage());
    }

    @Test
    void shouldPatchUser() {

        User updatedJohn = new User(
                johnId, "JOOOHN", "UPDATEDJOOOHN@EMAIL.COM");

        UserDto updatedJohnDto = new UserDto(
                johnId, "JOOOHN", "UPDATEDJOOOHN@EMAIL.COM");

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(john));

        Mockito
                .when(userRepository.findByEmailContainingIgnoreCase(anyString()))
                .thenReturn(new ArrayList<>());

        Mockito
                .when(userRepository.save(any()))
                .thenReturn(updatedJohn);

        UserDto userDto = userService.patchUser(johnId, updatedJohnDto);

        assertThat(userDto.getId(), equalTo(updatedJohnDto.getId()));
        assertThat(userDto.getName(), equalTo(updatedJohnDto.getName()));
        assertThat(userDto.getEmail(), equalTo(updatedJohnDto.getEmail()));

        Mockito.verify(userRepository, Mockito.times(1))
                .save(updatedJohn);
    }

    @Test
    void patchUserShouldThrowNotFoundExceptionWhenUserNotFound() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> userService.patchUser(99L, johnDto));

        Assertions.assertEquals("User not found", exception.getMessage());
    }

    @Test
    void patchUserShouldThrowAlreadyExistsExceptionWhenEmailAlreadyExists() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(john));

        Mockito
                .when(userRepository.findByEmailContainingIgnoreCase(anyString()))
                .thenReturn(List.of(john));

        final AlreadyExistsException exception = Assertions.assertThrows(
                AlreadyExistsException.class,
                () -> userService.patchUser(99L, johnDto));

        Assertions.assertEquals("User with provided email already exists", exception.getMessage());
    }

    @Test
    void shouldDeleteUser() {
        userService.deleteUser(1L);

        Mockito.verify(userRepository, Mockito.times(1))
                .deleteById(1L);
    }
}
