package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.ValidationException;
import java.util.Collection;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    @Autowired
    private final UserService userService;

    @GetMapping("/{id}")
    public User findUserById(@PathVariable Long id) {
        return userService.findUserById(id);
    }

    @GetMapping
    public Collection<User> findAll() {
        return userService.findAllUsers();
    }

    @PostMapping
    public User create(@RequestBody User user) throws ValidationException {
        return userService.addUser(user);
    }

    @PatchMapping("/{id}")
    public User update(@PathVariable Long id, @RequestBody User user) {
        return userService.patchUser(id, user);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }


}
