package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceTest {

    Long johnId = 1L;
    Long jackId = 2L;

    User john = new User(johnId, "JOHN", "john@email.com");

    Item screwdriver = new Item(
            1L, jackId, "отвертка", "nnnnnnn", 2L, true, new ArrayList<>());

    ItemRequestDto guitarRequestCreationDto = new ItemRequestDto(null,
            "Дайте погонять гитару",
            johnId,
            null,
            null);

    ItemRequest screwdriverRequest = new ItemRequest(1L, "Дайте погонять гитару", johnId, LocalDateTime.now());

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    ItemRequestService itemRequestService;

    @BeforeEach
    void setUp() {
        itemRequestService = new ItemRequestServiceImpl(itemRepository, userRepository, itemRequestRepository);
    }

    @Test
    void addItemRequestShouldSaveRequest() {
        ItemRequest screwdriverCreationRequest = new ItemRequest(null, "Дайте погонять гитару", johnId, null);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(john));
        when(itemRequestRepository.save(any()))
                .thenReturn(screwdriverRequest);

        ItemRequestDto requestDto = itemRequestService.addItemRequest(johnId, guitarRequestCreationDto);

        assertThat(requestDto.getId(), notNullValue());
        assertThat(requestDto.getDescription(), equalTo(guitarRequestCreationDto.getDescription()));
        assertThat(requestDto.getRequestorId(), equalTo(johnId));
        assertThat(requestDto.getCreated(), notNullValue());
        assertThat(requestDto.getItems(), hasSize(0));

        verify(itemRequestRepository, times(1))
                .save(screwdriverCreationRequest);
    }

    @Test
    void addItemRequestShouldThrowNotFoundExceptionWhenUserNotFound() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemRequestService.addItemRequest(johnId, guitarRequestCreationDto));

        Assertions.assertEquals("User not found", exception.getMessage());
    }

    @Test
    void addItemRequestShouldThrowValidationExceptionWhenNoDescriptionProvided() {
        ItemRequestDto emptyRequestCreationDto = new ItemRequestDto(null,
                null,
                johnId,
                null,
                null);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(john));

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> itemRequestService.addItemRequest(johnId, emptyRequestCreationDto));

        Assertions.assertEquals("No description", exception.getMessage());
    }

    @Test
    void shouldFindItemRequestsByUserId() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(john));
        when(itemRequestRepository.findByRequestorIdOrderByCreatedDesc(johnId))
                .thenReturn(List.of(screwdriverRequest));

        Collection<ItemRequestDto> requests = itemRequestService.findItemRequestsByRequestorId(johnId);

        assertThat(requests, hasSize(1));

        assertThat(requests, hasItem(allOf(
                hasProperty("id", notNullValue()),
                hasProperty("description", equalTo(screwdriverRequest.getDescription())),
                hasProperty("requestorId", equalTo(screwdriverRequest.getRequestorId())),
                hasProperty("created", notNullValue())
        )));
    }

    @Test
    void findItemRequestsByUserIdShouldThrowNotFoundExceptionWhenUserNotFound() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemRequestService.findItemRequestsByRequestorId(johnId));

        Assertions.assertEquals("User not found", exception.getMessage());
    }

    @Test
    void shouldFindItemRequestId() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(john));
        when(itemRequestRepository.findById(1L))
                .thenReturn(Optional.of(screwdriverRequest));
        when(itemRepository.findByRequestId(anyLong()))
                .thenReturn(List.of(screwdriver));

        ItemRequestDto requestDto = itemRequestService.findItemRequestById(johnId, 1L);

        assertThat(requestDto.getId(), notNullValue());
        assertThat(requestDto.getDescription(), equalTo(screwdriverRequest.getDescription()));
        assertThat(requestDto.getRequestorId(), equalTo(johnId));
        assertThat(requestDto.getCreated(), notNullValue());
        assertThat(requestDto.getItems(), hasSize(1));

        assertThat(requestDto.getItems(), hasItem(allOf(
                hasProperty("id", notNullValue()),
                hasProperty("name", equalTo(screwdriver.getName())),
                hasProperty("description", equalTo(screwdriver.getDescription())),
                hasProperty("requestId", equalTo(screwdriver.getRequestId())),
                hasProperty("available", equalTo(screwdriver.getAvailable())),
                hasProperty("comments", allOf(notNullValue(), hasSize(0)))
        )));
    }


    @Test
    void findByIdShouldThrowNotFoundExceptionWhenUserNotFound() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemRequestService.findItemRequestById(99L, 1L));

        Assertions.assertEquals("User not found", exception.getMessage());
    }

    @Test
    void findByIdShouldThrowNotFoundExceptionWhenRequestNotFound() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(john));
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemRequestService.findItemRequestById(johnId, 99L));

        Assertions.assertEquals("Request not found", exception.getMessage());
    }

    @Test
    void shouldFindAlLRequests() {
        when(itemRequestRepository.findByRequestorIdNot(anyLong(), any()))
                .thenReturn(List.of(screwdriverRequest));
        when(itemRepository.findByRequestId(anyLong()))
                .thenReturn(List.of(screwdriver));

        Collection<ItemRequestDto> requests = itemRequestService.findAll(johnId, 0, 100);

        assertThat(requests, hasSize(1));

        assertThat(requests, hasItem(allOf(
                hasProperty("id", notNullValue()),
                hasProperty("description", equalTo(screwdriverRequest.getDescription())),
                hasProperty("requestorId", equalTo(screwdriverRequest.getRequestorId())),
                hasProperty("created", notNullValue()),
                hasProperty("items", hasItem(allOf(
                        hasProperty("id", notNullValue()),
                        hasProperty("name", equalTo(screwdriver.getName())),
                        hasProperty("description", equalTo(screwdriver.getDescription())),
                        hasProperty("requestId", equalTo(screwdriver.getRequestId())),
                        hasProperty("available", equalTo(screwdriver.getAvailable())),
                        hasProperty("comments", allOf(notNullValue(), hasSize(0)))
                )))
        )));
    }

    @Test
    void findAlLRequestsShouldThrowValidationExceptionWhenSizeIsZero() {
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> itemRequestService.findAll(johnId, 0, 0));

        Assertions.assertEquals("Size is zero", exception.getMessage());
    }

    @Test
    void findAlLRequestsShouldThrowValidationExceptionWhenFromIsNegative() {
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> itemRequestService.findAll(johnId, -1, 20));

        Assertions.assertEquals("From cannot be negative", exception.getMessage());
    }
}
