package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.PatchUserDto;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import javax.validation.ValidationException;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserClient userClient;

    @GetMapping("/{id}")
    public ResponseEntity<Object> findUserById(@PathVariable Long id) {
        return userClient.getUser(id);
    }

    @GetMapping
    public ResponseEntity<Object> findAllUsers() {
        return userClient.getUsers();
    }

    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody @Valid UserDto userDto) throws ValidationException {
        return userClient.addUser(userDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateUser(@PathVariable Long id, @RequestBody @Valid PatchUserDto userDto) {
        return userClient.patchUser(id, userDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long id) {
        return userClient.deleteUser(id);
    }
}
