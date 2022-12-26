package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerIT {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @SneakyThrows
    @Test
    void createUser() {
        UserDto userToCreate = new UserDto(null, "John", "john.doe@mail.com");
        UserDto createdUser = new UserDto(1L, "John", "john.doe@mail.com");
        when(userService.addUser(userToCreate)).thenReturn(createdUser);

        String result = mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userToCreate)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(createdUser), result);
    }

    @SneakyThrows
    @Test
    void findUserById() {
        long userId = 0L;
        mockMvc.perform(get("/users/{id}", userId)).andExpect(status().isOk());
        verify(userService).findUserById(userId);
    }

    @SneakyThrows
    @Test
    void findAllUsers() {
        mockMvc.perform(get("/users")).andExpect(status().isOk());
        verify(userService).findAllUsers();
    }

    @SneakyThrows
    @Test
    void updateUser() {
        UserDto userToUpdate = new UserDto(1L, "Henry", "henry.obriain@mail.com");
        long sharerUserId = 1L;
        when(userService.patchUser(sharerUserId, userToUpdate)).thenReturn(userToUpdate);

        String result = mockMvc.perform(patch("/users/{id}", userToUpdate.getId())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userToUpdate)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(userToUpdate), result);
    }

    @SneakyThrows
    @Test
    void deleteUser() {
        long userId = 1L;
        mockMvc.perform(delete("/users/{id}", userId)).andExpect(status().isOk());
        verify(userService).deleteUser(userId);
    }
}
