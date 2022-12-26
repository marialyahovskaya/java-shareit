package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.CommentCreationDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
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
public class ItemServiceTest {

    Long johnId = 1L;
    Long jackId = 2L;

    User john = new User(johnId, "JOHN", "john@email.com");
    User jack = new User(jackId, "JACK", "jack@email.com");

    Item screwdriver = new Item(
            1L, jackId, "отвертка", "nnnnnnn", 2L, true, new ArrayList<>());
    Item screwdriverCreationEntity = new Item(
            null, jackId, "отвертка", "nnnnnnn", 2L, true, new ArrayList<>());

    ItemDto screwdriverDto = new ItemDto(
            1L, "отвертка", "nnnnnnn", 2L, true, null, null, new ArrayList<>());
    ItemDto screwdriverCreationDto = new ItemDto(
            null, "отвертка", "nnnnnnn", 2L, true, null, null, new ArrayList<>());
    ItemDto unavailableScrewdriverDto = new ItemDto(
            null, "отвертка", "nnnnnnn", 2L, null, null, null, new ArrayList<>());

    CommentCreationDto commentCreationDto = new CommentCreationDto("Замечательная отвёртка");

    LocalDateTime prevStart = LocalDateTime.now().minusDays(2);
    LocalDateTime prevEnd = LocalDateTime.now().minusDays(1);
    Booking screwdriverLastBooking = new Booking(1L, prevStart, prevEnd, screwdriver, john, BookingState.APPROVED);
    BookingDto screwdriverLastBookingDto = new BookingDto(1L, prevStart, prevEnd, screwdriver, johnId, john, BookingState.APPROVED);

    LocalDateTime nextStart = LocalDateTime.now().plusDays(2);
    LocalDateTime nextEnd = LocalDateTime.now().plusDays(1);
    Booking screwdriverNextBooking = new Booking(1L, nextStart, nextEnd, screwdriver, john, BookingState.APPROVED);
    BookingDto screwdriverNextBookingDto = new BookingDto(1L, nextStart, nextEnd, screwdriver, johnId, john, BookingState.APPROVED);

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
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(jack));
        when(itemRepository.save(any()))
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

        verify(itemRepository, times(1))
                .save(screwdriverCreationEntity);
    }

    @Test
    void addItemShouldThrowNotFoundExceptionWhenUserNotFound() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.addItem(99L, screwdriverCreationDto));

        Assertions.assertEquals("User not found", exception.getMessage());
    }

    @Test
    void addItemShouldThrowNotFoundExceptionWhenItemIsUnavailable() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(jack));

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> itemService.addItem(jackId, unavailableScrewdriverDto));

        Assertions.assertEquals("Item availability is undefined", exception.getMessage());
    }

    @Test
    void shouldAddComment() {
        LocalDateTime start = LocalDateTime.now().minusDays(2);
        LocalDateTime end = LocalDateTime.now().minusDays(1);
        Booking screwdriverBooking = new Booking(1L, start, end, screwdriver, john, BookingState.APPROVED);
        Comment comment = new Comment(1L, "Замечательная отвёртка", john, LocalDateTime.now(), screwdriver.getId());

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(screwdriver));
        when(userRepository.findById(johnId))
                .thenReturn(Optional.of(john));
        when(bookingRepository.findByItem_IdAndEndIsBefore(anyLong(), any()))
                .thenReturn(List.of(screwdriverBooking));
        when(commentRepository.save(any()))
                .thenReturn(comment);

        CommentDto commentDto = itemService.addComment(johnId, screwdriver.getId(), commentCreationDto);
        assertThat(commentDto.getId(), notNullValue());
        assertThat(commentDto.getText(), equalTo(commentCreationDto.getText()));
        assertThat(commentDto.getAuthorName(), equalTo(john.getName()));
        assertThat(commentDto.getCreated(), notNullValue());
    }

    @Test
    void addCommentShouldThrowNotFoundExceptionWhenUserNotFound() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(screwdriver));
        when(userRepository.findById(johnId))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.addComment(johnId, 99L, commentCreationDto));

        Assertions.assertEquals("User not found", exception.getMessage());
    }

    @Test
    void addCommentShouldThrowNotFoundExceptionWhenItemNotFound() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.addComment(99L, screwdriver.getId(), commentCreationDto));

        Assertions.assertEquals("Item not found", exception.getMessage());
    }

    @Test
    void addCommentShouldThrowValidationExceptionWhenUserCommentsWithoutPreviousBooking() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(screwdriver));
        when(userRepository.findById(johnId))
                .thenReturn(Optional.of(john));
        when(bookingRepository.findByItem_IdAndEndIsBefore(anyLong(), any()))
                .thenReturn(new ArrayList<>());

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> itemService.addComment(johnId, screwdriver.getId(), commentCreationDto));

        Assertions.assertEquals("Cannot create comment", exception.getMessage());
    }

    @Test
    void shouldFindItemByIdAndFindNextAndPrevBookingsWhenRequestingUserIsOwner() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(screwdriver));
        when(bookingRepository.findFirstByItem_IdAndEndIsBeforeOrderByEndDesc(anyLong(), any()))
                .thenReturn(Optional.of(screwdriverLastBooking));
        when(bookingRepository.findFirstByItem_IdAndStartIsAfterOrderByStartAsc(anyLong(), any()))
                .thenReturn(Optional.of(screwdriverNextBooking));

        ItemDto itemDto = itemService.findById(jackId, screwdriver.getId());

        assertThat(itemDto.getId(), equalTo(screwdriverDto.getId()));
        assertThat(itemDto.getName(), equalTo(screwdriverDto.getName()));
        assertThat(itemDto.getDescription(), equalTo(screwdriverDto.getDescription()));
        assertThat(itemDto.getRequestId(), equalTo(screwdriverDto.getRequestId()));
        assertThat(itemDto.getAvailable(), equalTo(screwdriverDto.getAvailable()));
        assertThat(itemDto.getLastBooking(), equalTo(screwdriverLastBookingDto));
        assertThat(itemDto.getNextBooking(), equalTo(screwdriverNextBookingDto));
        assertThat(itemDto.getComments(), equalTo(screwdriverDto.getComments()));

        verify(itemRepository, times(1))
                .findById(screwdriver.getId());
        verify(bookingRepository, times(1))
                .findFirstByItem_IdAndEndIsBeforeOrderByEndDesc(anyLong(), any());
        verify(bookingRepository, times(1))
                .findFirstByItem_IdAndStartIsAfterOrderByStartAsc(anyLong(), any());
    }

    @Test
    void shouldFindItemByIdWithoutLastAndNextBookingsWhenRequestingUserIsNotOwner() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(screwdriver));

        ItemDto itemDto = itemService.findById(johnId, screwdriver.getId());

        assertThat(itemDto.getId(), equalTo(screwdriverDto.getId()));
        assertThat(itemDto.getName(), equalTo(screwdriverDto.getName()));
        assertThat(itemDto.getDescription(), equalTo(screwdriverDto.getDescription()));
        assertThat(itemDto.getRequestId(), equalTo(screwdriverDto.getRequestId()));
        assertThat(itemDto.getAvailable(), equalTo(screwdriverDto.getAvailable()));
        assertThat(itemDto.getLastBooking(), equalTo(null));
        assertThat(itemDto.getNextBooking(), equalTo(null));
        assertThat(itemDto.getComments(), equalTo(screwdriverDto.getComments()));

        verify(itemRepository, times(1))
                .findById(screwdriver.getId());
        verify(bookingRepository, times(0))
                .findFirstByItem_IdAndEndIsBeforeOrderByEndDesc(anyLong(), any());
        verify(bookingRepository, times(0))
                .findFirstByItem_IdAndStartIsAfterOrderByStartAsc(anyLong(), any());
    }

    @Test
    void findItemByIdShouldThrowNotFoundExceptionWhenItemNotFound() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.findById(johnId, 99L));

        Assertions.assertEquals("Item not found", exception.getMessage());
    }

    @Test
    void shouldFindItemsByOwnerIdAndFindLastAndNextBookings() {
        when(itemRepository.findByOwnerIdOrderByIdAsc(anyLong()))
                .thenReturn(List.of(screwdriver));
        when(bookingRepository.findFirstByItem_IdAndEndIsBeforeOrderByEndDesc(anyLong(), any()))
                .thenReturn(Optional.of(screwdriverLastBooking));
        when(bookingRepository.findFirstByItem_IdAndStartIsAfterOrderByStartAsc(anyLong(), any()))
                .thenReturn(Optional.of(screwdriverNextBooking));

        Collection<ItemDto> items = itemService.findItemsByOwnerId(jackId);

        assertThat(items, hasSize(1));
        assertThat(items, hasItem(allOf(
                hasProperty("id", notNullValue()),
                hasProperty("name", equalTo(screwdriverDto.getName())),
                hasProperty("description", equalTo(screwdriverDto.getDescription())),
                hasProperty("requestId", equalTo(screwdriverDto.getRequestId())),
                hasProperty("available", equalTo(screwdriverDto.getAvailable())),
                hasProperty("lastBooking", equalTo(screwdriverLastBookingDto)),
                hasProperty("nextBooking", equalTo(screwdriverNextBookingDto)),
                hasProperty("comments", equalTo(screwdriverDto.getComments()))
        )));

        verify(itemRepository, times(1))
                .findByOwnerIdOrderByIdAsc(jackId);
        verify(bookingRepository, times(1))
                .findFirstByItem_IdAndEndIsBeforeOrderByEndDesc(anyLong(), any());
        verify(bookingRepository, times(1))
                .findFirstByItem_IdAndStartIsAfterOrderByStartAsc(anyLong(), any());
    }

    @Test
    void searchShouldFindItemsByTextInName() {
        Item drill = new Item(
                2L, jackId, "Дрель", "ударная дрель", 2L, true, new ArrayList<>());
        when(itemRepository.findAll())
                .thenReturn(List.of(screwdriver, drill));

        Collection<ItemDto> items = itemService.search("твертк");

        assertThat(items, hasSize(1));
        assertThat(items, hasItem(allOf(
                hasProperty("id", notNullValue()),
                hasProperty("name", equalTo(screwdriverDto.getName())),
                hasProperty("description", equalTo(screwdriverDto.getDescription())),
                hasProperty("requestId", equalTo(screwdriverDto.getRequestId())),
                hasProperty("available", equalTo(screwdriverDto.getAvailable())),
                hasProperty("lastBooking", equalTo(null)),
                hasProperty("nextBooking", equalTo(null)),
                hasProperty("comments", equalTo(screwdriverDto.getComments()))
        )));
    }

    @Test
    void searchShouldFindItemsByTextInDescription() {
        Item drill = new Item(
                2L, jackId, "Дрель", "ударная дрель", 2L, true, new ArrayList<>());
        when(itemRepository.findAll())
                .thenReturn(List.of(screwdriver, drill));

        Collection<ItemDto> items = itemService.search("nnnnn");

        assertThat(items, hasSize(1));
        assertThat(items, hasItem(allOf(
                hasProperty("id", notNullValue()),
                hasProperty("name", equalTo(screwdriverDto.getName())),
                hasProperty("description", equalTo(screwdriverDto.getDescription())),
                hasProperty("requestId", equalTo(screwdriverDto.getRequestId())),
                hasProperty("available", equalTo(screwdriverDto.getAvailable())),
                hasProperty("lastBooking", equalTo(null)),
                hasProperty("nextBooking", equalTo(null)),
                hasProperty("comments", equalTo(screwdriverDto.getComments()))
        )));
    }

    @Test
    void searchShouldBeCaseInsensitive() {
        Item drill = new Item(
                2L, jackId, "Дрель", "ударная дрель", 2L, true, new ArrayList<>());
        when(itemRepository.findAll())
                .thenReturn(List.of(screwdriver, drill));

        Collection<ItemDto> items = itemService.search("ОтвЕР");

        assertThat(items, hasSize(1));
        assertThat(items, hasItem(allOf(
                hasProperty("id", notNullValue()),
                hasProperty("name", equalTo(screwdriverDto.getName())),
                hasProperty("description", equalTo(screwdriverDto.getDescription())),
                hasProperty("requestId", equalTo(screwdriverDto.getRequestId())),
                hasProperty("available", equalTo(screwdriverDto.getAvailable())),
                hasProperty("lastBooking", equalTo(null)),
                hasProperty("nextBooking", equalTo(null)),
                hasProperty("comments", equalTo(screwdriverDto.getComments()))
        )));
    }

    @Test
    void searchShouldReturnOnlyAvailableItems() {
        Item screwdriver2 = new Item(
                2L, jackId, "отвертка крестовая", "Большая отвертка", 2L, false, new ArrayList<>());
        when(itemRepository.findAll())
                .thenReturn(List.of(screwdriver, screwdriver2));

        Collection<ItemDto> items = itemService.search("отвер");

        assertThat(items, hasSize(1));
        assertThat(items, hasItem(allOf(
                hasProperty("id", notNullValue()),
                hasProperty("name", equalTo(screwdriverDto.getName())),
                hasProperty("description", equalTo(screwdriverDto.getDescription())),
                hasProperty("requestId", equalTo(screwdriverDto.getRequestId())),
                hasProperty("available", equalTo(screwdriverDto.getAvailable())),
                hasProperty("lastBooking", equalTo(null)),
                hasProperty("nextBooking", equalTo(null)),
                hasProperty("comments", equalTo(screwdriverDto.getComments()))
        )));
    }


    @Test
    void searchShouldFindNothingWhenSearchIsEmptyString() {
        Collection<ItemDto> items = itemService.search("");

        assertThat(items, hasSize(0));
    }

    @Test
    void shouldPatchItem() {
        ItemDto screwdriverPatchDto = new ItemDto(
                1L,
                "Отвертка upd",
                "Крестовая",
                2L,
                false,
                null,
                null,
                null);

        Item updatedScrewDriver = new Item(
                1L,
                jackId,
                "Отвертка upd",
                "Крестовая",
                2L,
                false,
                List.of());


        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(screwdriver));
        when(itemRepository.save(any()))
                .thenReturn(updatedScrewDriver);

        ItemDto updatedItemDto = itemService.patchItem(jackId, 1L, screwdriverPatchDto);

        assertThat(updatedItemDto.getId(), equalTo(screwdriverPatchDto.getId()));
        assertThat(updatedItemDto.getName(), equalTo(screwdriverPatchDto.getName()));
        assertThat(updatedItemDto.getDescription(), equalTo(screwdriverPatchDto.getDescription()));
        assertThat(updatedItemDto.getAvailable(), equalTo(screwdriverPatchDto.getAvailable()));
        assertThat(updatedItemDto.getLastBooking(), equalTo(null));
        assertThat(updatedItemDto.getNextBooking(), equalTo(null));
        assertThat(updatedItemDto.getComments(), equalTo(List.of()));

    }

    @Test
    void shouldThrowValidationExceptionWhenNoUserIdProvided() {
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> itemService.patchItem(null, 1L, screwdriverDto));

        Assertions.assertEquals("UserId not provided", exception.getMessage());
    }

    @Test
    void shouldThrowNotFoundExceptionWhenItemNotFound() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.patchItem(jackId, 99L, screwdriverDto));

        Assertions.assertEquals("Item not found", exception.getMessage());
    }

    @Test
    void shouldThrowNotFoundExceptionWhenRequesterIsNotTheOwner() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(screwdriver));

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.patchItem(johnId, 1L, screwdriverDto));

        Assertions.assertEquals("Item not found", exception.getMessage());
    }
}
