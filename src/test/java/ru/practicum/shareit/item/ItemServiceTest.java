package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {

    Long jackId = 2L;

    User jack = new User(
            jackId, "JACK", "jack@email.com");

    Item screwdriver = new Item(
            1L, jackId, "отвертка", "nnnnnnn", 2L, true, new ArrayList<>());


    ItemDto screwdriverDto = new ItemDto(
            1L, "отвертка", "nnnnnnn", 2L, true, null, null, new ArrayList<>());

    ItemDto screwdriverCreationDto = new ItemDto(
            null, "отвертка", "nnnnnnn", 2L, true, null, null, new ArrayList<>());

    Item screwdriverCreationEntity = new Item(
            null, jackId, "отвертка", "nnnnnnn", 2L, true, new ArrayList<>());


    Item unavailableScrewdriver = new Item(
            1L, jackId, "отвертка", "nnnnnnn", 2L, false, new ArrayList<>());

    ItemDto unavailableScrewdriverDto = new ItemDto(
            null, "отвертка", "nnnnnnn", 2L, null, null, null, new ArrayList<>());
    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    private ItemService itemService;

    @BeforeEach
    void setUp() {
        itemService = new ItemServiceImpl(itemRepository, userRepository, bookingRepository, commentRepository);
    }

    @Test
    void shouldAddItem() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(jack));

        Mockito
                .when(itemRepository.save(any()))
                .thenReturn(screwdriver);

        ItemDto itemDto = itemService.addItem(jackId, screwdriverCreationDto);

        assertThat(itemDto.getId(), equalTo(screwdriverDto.getId()));
        assertThat(itemDto.getName(), equalTo(screwdriverDto.getName()));
        assertThat(itemDto.getDescription(), equalTo(screwdriverDto.getDescription()));
        assertThat(itemDto.getRequestId(), equalTo(screwdriverDto.getRequestId()));
        assertThat(itemDto.getAvailable(), equalTo(screwdriverDto.getAvailable()));
        assertThat(itemDto.getLastBooking(), equalTo(screwdriverDto.getLastBooking()));
        assertThat(itemDto.getNextBooking(), equalTo(screwdriverDto.getNextBooking()));
        assertThat(itemDto.getComments(), equalTo(screwdriverDto.getComments()));

        Mockito.verify(itemRepository, Mockito.times(1))
                .save(screwdriverCreationEntity);

    }

    @Test
    void addItemShouldThrowNotFoundExceptionWhenUserNotFound() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.addItem(99L, screwdriverCreationDto));

        Assertions.assertEquals("User not found", exception.getMessage());
    }

    @Test
    void addItemShouldThrowNotFoundExceptionWhenItemIsUnavailable() {

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(jack));

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> itemService.addItem(jackId, unavailableScrewdriverDto));

        Assertions.assertEquals("Item availability is undefined", exception.getMessage());
    }
}
