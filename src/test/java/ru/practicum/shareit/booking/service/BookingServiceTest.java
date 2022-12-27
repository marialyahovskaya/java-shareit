package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.PaginationHelper;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
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
public class BookingServiceTest {

    LocalDateTime start = LocalDateTime.now().plusDays(1);
    LocalDateTime end = LocalDateTime.now().plusDays(2);

    Long johnId = 1L;
    Long jackId = 2L;

    User john = new User(johnId, "JOHN", "john@email.com");
    User jack = new User(jackId, "JACK", "jack@email.com");

    ItemRequest request = new ItemRequest(2L, "Дайте дрель", john, LocalDateTime.now());

    Item screwdriver =
            new Item(1L, jack, "отвертка", "nnnnnnn", request, true, new ArrayList<>());

    Item unavailableScrewdriver =
            new Item(1L, jack, "отвертка", "nnnnnnn", request, false, new ArrayList<>());


    Booking screwdriverBooking = new Booking(1L, start, end, screwdriver, john, BookingState.WAITING);

    BookingCreationDto screwdriverBookingCreationDto = new BookingCreationDto(1L, start, end);

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;

    private BookingService bookingService;

    @BeforeEach
    void setUp() {
        bookingService = new BookingServiceImpl(bookingRepository, itemRepository, userRepository);
    }

    @Test
    void shouldAddBooking() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(screwdriver));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(john));
        when(bookingRepository.save(any()))
                .thenReturn(screwdriverBooking);
        Booking booking = BookingMapper.toBooking(johnId, screwdriverBookingCreationDto);
        booking.setItem(screwdriver);
        booking.setBooker(john);

        BookingDto bookingDto = bookingService.addBooking(johnId, screwdriverBookingCreationDto);

        assertThat(bookingDto.getId(), equalTo(1L));
        assertThat(bookingDto.getStart(), equalTo(start));
        assertThat(bookingDto.getEnd(), equalTo(end));
        assertThat(bookingDto.getItem(), equalTo(screwdriver));
        assertThat(bookingDto.getBookerId(), equalTo(1L));
        assertThat(bookingDto.getBooker(), equalTo(john));
        assertThat(bookingDto.getStatus(), equalTo(BookingState.WAITING));
        verify(bookingRepository, times(1)).save(booking);
    }

    @Test
    void addBookingShouldThrowNotFoundExceptionWhenItemNotFound() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.addBooking(johnId, screwdriverBookingCreationDto));

        Assertions.assertEquals("Item not found", exception.getMessage());
    }

    @Test
    void addBookingShouldThrowNotFoundExceptionWhenUserNotFound() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(screwdriver));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.addBooking(johnId, screwdriverBookingCreationDto));

        Assertions.assertEquals("User not found", exception.getMessage());
    }

    @Test
    void addBookingShouldThrowNotFoundExceptionWhenItemIsUnavailable() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(unavailableScrewdriver));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(john));

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.addBooking(johnId, screwdriverBookingCreationDto));

        Assertions.assertEquals("Cannot book unavailable item", exception.getMessage());
    }

    @Test
    void addBookingShouldThrowNotFoundExceptionWhenOwnerTriesToBookHisItem() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(screwdriver));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(john));

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.addBooking(jackId, screwdriverBookingCreationDto));

        Assertions.assertEquals("You can't book this item", exception.getMessage());
    }

    @Test
    void findBookingShouldReturnBookingDataForBooker() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(screwdriverBooking));

        BookingDto bookingDto = bookingService.findBooking(johnId, 3L);

        assertThat(bookingDto.getId(), equalTo(1L));
        assertThat(bookingDto.getStart(), equalTo(start));
        assertThat(bookingDto.getEnd(), equalTo(end));
        assertThat(bookingDto.getItem(), equalTo(screwdriver));
        assertThat(bookingDto.getBookerId(), equalTo(1L));
        assertThat(bookingDto.getBooker(), equalTo(john));
        assertThat(bookingDto.getStatus(), equalTo(BookingState.WAITING));

        verify(bookingRepository, times(1))
                .findById(3L);
    }

    @Test
    void findBookingShouldReturnBookingDataForOwner() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(screwdriverBooking));

        BookingDto bookingDto = bookingService.findBooking(jackId, 3L);

        assertThat(bookingDto.getId(), equalTo(1L));
        assertThat(bookingDto.getStart(), equalTo(start));
        assertThat(bookingDto.getEnd(), equalTo(end));
        assertThat(bookingDto.getItem(), equalTo(screwdriver));
        assertThat(bookingDto.getBookerId(), equalTo(1L));
        assertThat(bookingDto.getBooker(), equalTo(john));
        assertThat(bookingDto.getStatus(), equalTo(BookingState.WAITING));

        verify(bookingRepository, times(1))
                .findById(3L);
    }

    @Test
    void findBookingShouldThrowNotFoundExceptionWhenBookingNotFound() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.findBooking(jackId, 3L));

        Assertions.assertEquals("Booking not found", exception.getMessage());
    }

    @Test
    void findBookingShouldThrowNotFoundExceptionWhenUserIsNotBookerOrOwner() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(screwdriverBooking));

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.findBooking(99L, 3L));

        Assertions.assertEquals("Booking not found", exception.getMessage());
    }

    @Test
    void shouldFindAllBookingsByBookerId() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(john));
        when(bookingRepository.findByBookerOrderByStartDesc(any(), any()))
                .thenReturn(List.of(screwdriverBooking));

        Collection<BookingDto> bookings = bookingService.findBookingsByBookerId(johnId, "ALL", 0, 100);
        Pageable pageable = PaginationHelper.makePageable(0, 100);

        assertThat(bookings, hasSize(1));
        assertThat(bookings, hasItem(allOf(
                hasProperty("id", notNullValue()),
                hasProperty("start", equalTo(screwdriverBooking.getStart())),
                hasProperty("end", equalTo(screwdriverBooking.getEnd())),
                hasProperty("item", equalTo(screwdriverBooking.getItem())),
                hasProperty("bookerId", equalTo(screwdriverBooking.getBooker().getId())),
                hasProperty("booker", equalTo(screwdriverBooking.getBooker())),
                hasProperty("status", equalTo(screwdriverBooking.getStatus()))
        )));

        verify(bookingRepository, times(1))
                .findByBookerOrderByStartDesc(john, pageable);
    }

    @Test
    void shouldFindCurrentBookingsByBookerId() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(john));
        when(bookingRepository.findByBookerAndStartIsBeforeAndEndIsAfterOrderByStartDesc(any(), any(), any(), any()))
                .thenReturn(List.of(screwdriverBooking));

        Collection<BookingDto> bookings = bookingService.findBookingsByBookerId(johnId, "CURRENT", 0, 100);

        assertThat(bookings, hasSize(1));
        assertThat(bookings, hasItem(allOf(
                hasProperty("id", notNullValue()),
                hasProperty("start", equalTo(screwdriverBooking.getStart())),
                hasProperty("end", equalTo(screwdriverBooking.getEnd())),
                hasProperty("item", equalTo(screwdriverBooking.getItem())),
                hasProperty("bookerId", equalTo(screwdriverBooking.getBooker().getId())),
                hasProperty("booker", equalTo(screwdriverBooking.getBooker())),
                hasProperty("status", equalTo(screwdriverBooking.getStatus()))
        )));
        verify(bookingRepository, times(1))
                .findByBookerAndStartIsBeforeAndEndIsAfterOrderByStartDesc(ArgumentMatchers.any(User.class),
                        ArgumentMatchers.any(LocalDateTime.class),
                        ArgumentMatchers.any(LocalDateTime.class),
                        ArgumentMatchers.any(Pageable.class)
                );
    }

    @Test
    void shouldFindPastBookingsByBookerId() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(john));
        when(bookingRepository.findByBookerAndEndIsBeforeOrderByStartDesc(any(), any(), any()))
                .thenReturn(List.of(screwdriverBooking));

        Collection<BookingDto> bookings = bookingService.findBookingsByBookerId(johnId, "PAST", 0, 100);

        assertThat(bookings, hasSize(1));
        assertThat(bookings, hasItem(allOf(
                hasProperty("id", notNullValue()),
                hasProperty("start", equalTo(screwdriverBooking.getStart())),
                hasProperty("end", equalTo(screwdriverBooking.getEnd())),
                hasProperty("item", equalTo(screwdriverBooking.getItem())),
                hasProperty("bookerId", equalTo(screwdriverBooking.getBooker().getId())),
                hasProperty("booker", equalTo(screwdriverBooking.getBooker())),
                hasProperty("status", equalTo(screwdriverBooking.getStatus()))
        )));
        verify(bookingRepository, times(1))
                .findByBookerAndEndIsBeforeOrderByStartDesc(ArgumentMatchers.any(User.class),
                        ArgumentMatchers.any(LocalDateTime.class),
                        ArgumentMatchers.any(Pageable.class)
                );
    }

    @Test
    void shouldFindFutureBookingsByBookerId() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(john));
        when(bookingRepository.findByBookerAndStartIsAfterOrderByStartDesc(any(), any(), any()))
                .thenReturn(List.of(screwdriverBooking));

        Collection<BookingDto> bookings = bookingService.findBookingsByBookerId(johnId, "FUTURE", 0, 100);

        assertThat(bookings, hasSize(1));
        assertThat(bookings, hasItem(allOf(
                hasProperty("id", notNullValue()),
                hasProperty("start", equalTo(screwdriverBooking.getStart())),
                hasProperty("end", equalTo(screwdriverBooking.getEnd())),
                hasProperty("item", equalTo(screwdriverBooking.getItem())),
                hasProperty("bookerId", equalTo(screwdriverBooking.getBooker().getId())),
                hasProperty("booker", equalTo(screwdriverBooking.getBooker())),
                hasProperty("status", equalTo(screwdriverBooking.getStatus()))
        )));
        verify(bookingRepository, times(1))
                .findByBookerAndStartIsAfterOrderByStartDesc(ArgumentMatchers.any(User.class),
                        ArgumentMatchers.any(LocalDateTime.class),
                        ArgumentMatchers.any(Pageable.class)
                );
    }

    @Test
    void shouldFindWaitingBookingsByBookerId() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(john));
        when(bookingRepository.findByBookerAndStatusOrderByStartDesc(any(), any(), any()))
                .thenReturn(List.of(screwdriverBooking));

        Collection<BookingDto> bookings = bookingService.findBookingsByBookerId(johnId, "WAITING", 0, 100);
        Pageable pageable = PaginationHelper.makePageable(0, 100);

        assertThat(bookings, hasSize(1));
        assertThat(bookings, hasItem(allOf(
                hasProperty("id", notNullValue()),
                hasProperty("start", equalTo(screwdriverBooking.getStart())),
                hasProperty("end", equalTo(screwdriverBooking.getEnd())),
                hasProperty("item", equalTo(screwdriverBooking.getItem())),
                hasProperty("bookerId", equalTo(screwdriverBooking.getBooker().getId())),
                hasProperty("booker", equalTo(screwdriverBooking.getBooker())),
                hasProperty("status", equalTo(screwdriverBooking.getStatus()))
        )));
        verify(bookingRepository, times(1))
                .findByBookerAndStatusOrderByStartDesc(john, BookingState.WAITING, pageable);
    }

    @Test
    void shouldFindRejectedBookingsByBookerId() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(john));
        when(bookingRepository.findByBookerAndStatusOrderByStartDesc(any(), any(), any()))
                .thenReturn(List.of(screwdriverBooking));

        Collection<BookingDto> bookings = bookingService.findBookingsByBookerId(johnId, "REJECTED", 0, 100);
        Pageable pageable = PaginationHelper.makePageable(0, 100);

        assertThat(bookings, hasSize(1));
        assertThat(bookings, hasItem(allOf(
                hasProperty("id", notNullValue()),
                hasProperty("start", equalTo(screwdriverBooking.getStart())),
                hasProperty("end", equalTo(screwdriverBooking.getEnd())),
                hasProperty("item", equalTo(screwdriverBooking.getItem())),
                hasProperty("bookerId", equalTo(screwdriverBooking.getBooker().getId())),
                hasProperty("booker", equalTo(screwdriverBooking.getBooker())),
                hasProperty("status", equalTo(screwdriverBooking.getStatus()))
        )));
        verify(bookingRepository, times(1))
                .findByBookerAndStatusOrderByStartDesc(john, BookingState.REJECTED, pageable);
    }

    @Test
    void findBookingsByBookerIdShouldThrowValidationExceptionWhenWrongStatePassed() {
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.findBookingsByBookerId(johnId, "SOME_GARBAGE", 0, 100));

        Assertions.assertEquals("Unknown state: SOME_GARBAGE", exception.getMessage());
    }


    @Test
    void findBookingsByBookerIdShouldThrowNotFoundExceptionWhenUserNotFound() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.findBookingsByBookerId(99L, "ALL", 0, 100));

        Assertions.assertEquals("User not found", exception.getMessage());
    }

    @Test
    void findBookingsByBookerIdShouldThrowValidationExceptionWhenSizeIsZero() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(john));
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.findBookingsByBookerId(johnId, "ALL", 0, 0));

        Assertions.assertEquals("Size is zero", exception.getMessage());
    }

    @Test
    void findBookingsByBookerIdShouldThrowValidationExceptionWhenFromIsNegative() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(john));
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.findBookingsByBookerId(johnId, "ALL", -1, 100));

        Assertions.assertEquals("From cannot be negative", exception.getMessage());
    }


    @Test
    void shouldFindAllBookingsByOwnerId() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(john));
        when(bookingRepository.findByItemOwnerIdOrderByStartDesc(anyLong(), any()))
                .thenReturn(List.of(screwdriverBooking));

        Collection<BookingDto> bookings = bookingService.findBookingsByOwnerId(johnId, "ALL", 0, 100);
        Pageable pageable = PaginationHelper.makePageable(0, 100);

        assertThat(bookings, hasSize(1));
        assertThat(bookings, hasItem(allOf(
                hasProperty("id", notNullValue()),
                hasProperty("start", equalTo(screwdriverBooking.getStart())),
                hasProperty("end", equalTo(screwdriverBooking.getEnd())),
                hasProperty("item", equalTo(screwdriverBooking.getItem())),
                hasProperty("bookerId", equalTo(screwdriverBooking.getBooker().getId())),
                hasProperty("booker", equalTo(screwdriverBooking.getBooker())),
                hasProperty("status", equalTo(screwdriverBooking.getStatus()))
        )));
        verify(bookingRepository, times(1))
                .findByItemOwnerIdOrderByStartDesc(johnId, pageable);
    }

    @Test
    void shouldFindCurrentBookingsByOwnerId() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(john));
        when(bookingRepository.findByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(any(), any(), any(), any()))
                .thenReturn(List.of(screwdriverBooking));

        Collection<BookingDto> bookings = bookingService.findBookingsByOwnerId(johnId, "CURRENT", 0, 100);

        assertThat(bookings, hasSize(1));
        assertThat(bookings, hasItem(allOf(
                hasProperty("id", notNullValue()),
                hasProperty("start", equalTo(screwdriverBooking.getStart())),
                hasProperty("end", equalTo(screwdriverBooking.getEnd())),
                hasProperty("item", equalTo(screwdriverBooking.getItem())),
                hasProperty("bookerId", equalTo(screwdriverBooking.getBooker().getId())),
                hasProperty("booker", equalTo(screwdriverBooking.getBooker())),
                hasProperty("status", equalTo(screwdriverBooking.getStatus()))
        )));
        verify(bookingRepository, times(1))
                .findByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(ArgumentMatchers.anyLong(),
                        ArgumentMatchers.any(LocalDateTime.class),
                        ArgumentMatchers.any(LocalDateTime.class),
                        ArgumentMatchers.any(Pageable.class)
                );
    }

    @Test
    void shouldFindPastBookingsByOwnerId() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(john));
        when(bookingRepository.findByItemOwnerIdAndEndIsBeforeOrderByStartDesc(any(), any(), any()))
                .thenReturn(List.of(screwdriverBooking));

        Collection<BookingDto> bookings = bookingService.findBookingsByOwnerId(johnId, "PAST", 0, 100);

        assertThat(bookings, hasSize(1));
        assertThat(bookings, hasItem(allOf(
                hasProperty("id", notNullValue()),
                hasProperty("start", equalTo(screwdriverBooking.getStart())),
                hasProperty("end", equalTo(screwdriverBooking.getEnd())),
                hasProperty("item", equalTo(screwdriverBooking.getItem())),
                hasProperty("bookerId", equalTo(screwdriverBooking.getBooker().getId())),
                hasProperty("booker", equalTo(screwdriverBooking.getBooker())),
                hasProperty("status", equalTo(screwdriverBooking.getStatus()))
        )));
        verify(bookingRepository, times(1))
                .findByItemOwnerIdAndEndIsBeforeOrderByStartDesc(anyLong(),
                        ArgumentMatchers.any(LocalDateTime.class),
                        ArgumentMatchers.any(Pageable.class)
                );
    }

    @Test
    void shouldFindFutureBookingsByOwnerId() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(john));
        when(bookingRepository.findByItemOwnerIdAndStartIsAfterOrderByStartDesc(any(), any(), any()))
                .thenReturn(List.of(screwdriverBooking));

        Collection<BookingDto> bookings = bookingService.findBookingsByOwnerId(johnId, "FUTURE", 0, 100);

        assertThat(bookings, hasSize(1));
        assertThat(bookings, hasItem(allOf(
                hasProperty("id", notNullValue()),
                hasProperty("start", equalTo(screwdriverBooking.getStart())),
                hasProperty("end", equalTo(screwdriverBooking.getEnd())),
                hasProperty("item", equalTo(screwdriverBooking.getItem())),
                hasProperty("bookerId", equalTo(screwdriverBooking.getBooker().getId())),
                hasProperty("booker", equalTo(screwdriverBooking.getBooker())),
                hasProperty("status", equalTo(screwdriverBooking.getStatus()))
        )));
        verify(bookingRepository, times(1))
                .findByItemOwnerIdAndStartIsAfterOrderByStartDesc(anyLong(),
                        ArgumentMatchers.any(LocalDateTime.class),
                        ArgumentMatchers.any(Pageable.class)
                );
    }

    @Test
    void shouldFindWaitingBookingsByOwnerId() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(john));
        when(bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(any(), any(), any()))
                .thenReturn(List.of(screwdriverBooking));

        Collection<BookingDto> bookings = bookingService.findBookingsByOwnerId(johnId, "WAITING", 0, 100);
        Pageable pageable = PaginationHelper.makePageable(0, 100);

        assertThat(bookings, hasSize(1));
        assertThat(bookings, hasItem(allOf(
                hasProperty("id", notNullValue()),
                hasProperty("start", equalTo(screwdriverBooking.getStart())),
                hasProperty("end", equalTo(screwdriverBooking.getEnd())),
                hasProperty("item", equalTo(screwdriverBooking.getItem())),
                hasProperty("bookerId", equalTo(screwdriverBooking.getBooker().getId())),
                hasProperty("booker", equalTo(screwdriverBooking.getBooker())),
                hasProperty("status", equalTo(screwdriverBooking.getStatus()))
        )));
        verify(bookingRepository, times(1))
                .findByItemOwnerIdAndStatusOrderByStartDesc(johnId, BookingState.WAITING, pageable);
    }

    @Test
    void shouldFindRejectedBookingsByOwnerId() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(john));
        when(bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(any(), any(), any()))
                .thenReturn(List.of(screwdriverBooking));

        Collection<BookingDto> bookings = bookingService.findBookingsByOwnerId(johnId, "REJECTED", 0, 100);
        Pageable pageable = PaginationHelper.makePageable(0, 100);

        assertThat(bookings, hasSize(1));
        assertThat(bookings, hasItem(allOf(
                hasProperty("id", notNullValue()),
                hasProperty("start", equalTo(screwdriverBooking.getStart())),
                hasProperty("end", equalTo(screwdriverBooking.getEnd())),
                hasProperty("item", equalTo(screwdriverBooking.getItem())),
                hasProperty("bookerId", equalTo(screwdriverBooking.getBooker().getId())),
                hasProperty("booker", equalTo(screwdriverBooking.getBooker())),
                hasProperty("status", equalTo(screwdriverBooking.getStatus()))
        )));
        verify(bookingRepository, times(1))
                .findByItemOwnerIdAndStatusOrderByStartDesc(johnId, BookingState.REJECTED, pageable);
    }

    @Test
    void findBookingsByOwnerIdShouldThrowValidationExceptionWhenWrongStatePassed() {
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.findBookingsByOwnerId(johnId, "SOME_GARBAGE", 0, 100));

        Assertions.assertEquals("Unknown state: SOME_GARBAGE", exception.getMessage());
    }


    @Test
    void findBookingsByOwnerIdShouldThrowNotFoundExceptionWhenUserNotFound() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.findBookingsByOwnerId(99L, "ALL", 0, 100));

        Assertions.assertEquals("User not found", exception.getMessage());
    }

    @Test
    void findBookingsByOwnerIdShouldThrowValidationExceptionWhenSizeIsZero() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(john));
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.findBookingsByOwnerId(johnId, "ALL", 0, 0));

        Assertions.assertEquals("Size is zero", exception.getMessage());
    }

    @Test
    void findBookingsByOwnerIdShouldThrowValidationExceptionWhenFromIsNegative() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(john));
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.findBookingsByOwnerId(johnId, "ALL", -1, 100));

        Assertions.assertEquals("From cannot be negative", exception.getMessage());
    }

    @Test
    void updateStatusShouldApproveWaitingBooking() {
        Booking screwdriverApprovedBooking = new Booking(1L, start, end, screwdriver, john, BookingState.APPROVED);

        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(screwdriverBooking));
        when(bookingRepository.save(any()))
                .thenReturn(screwdriverApprovedBooking);

        BookingDto bookingDto = bookingService.updateStatus(jackId, 1L, true);

        assertThat(bookingDto.getId(), equalTo(1L));
        assertThat(bookingDto.getStart(), equalTo(start));
        assertThat(bookingDto.getEnd(), equalTo(end));
        assertThat(bookingDto.getItem(), equalTo(screwdriver));
        assertThat(bookingDto.getBookerId(), equalTo(1L));
        assertThat(bookingDto.getBooker(), equalTo(john));
        assertThat(bookingDto.getStatus(), equalTo(BookingState.APPROVED));
        verify(bookingRepository, times(1))
                .save(screwdriverApprovedBooking);
    }

    @Test
    void updateStatusShouldApproveRejectedBooking() {
        Booking screwdriverRejectedBooking = new Booking(1L, start, end, screwdriver, john, BookingState.REJECTED);
        Booking screwdriverApprovedBooking = new Booking(1L, start, end, screwdriver, john, BookingState.APPROVED);

        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(screwdriverRejectedBooking));
        when(bookingRepository.save(any()))
                .thenReturn(screwdriverApprovedBooking);

        BookingDto bookingDto = bookingService.updateStatus(jackId, 1L, true);

        assertThat(bookingDto.getId(), equalTo(1L));
        assertThat(bookingDto.getStart(), equalTo(start));
        assertThat(bookingDto.getEnd(), equalTo(end));
        assertThat(bookingDto.getItem(), equalTo(screwdriver));
        assertThat(bookingDto.getBookerId(), equalTo(1L));
        assertThat(bookingDto.getBooker(), equalTo(john));
        assertThat(bookingDto.getStatus(), equalTo(BookingState.APPROVED));
        verify(bookingRepository, times(1))
                .save(screwdriverApprovedBooking);
    }

    @Test
    void updateStatusShouldRejectWaitingBooking() {
        Booking screwdriverApprovedBooking = new Booking(1L, start, end, screwdriver, john, BookingState.APPROVED);
        Booking screwdriverRejectedBooking = new Booking(1L, start, end, screwdriver, john, BookingState.REJECTED);

        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(screwdriverApprovedBooking));
        when(bookingRepository.save(any()))
                .thenReturn(screwdriverRejectedBooking);

        BookingDto bookingDto = bookingService.updateStatus(jackId, 1L, false);

        assertThat(bookingDto.getId(), equalTo(1L));
        assertThat(bookingDto.getStart(), equalTo(start));
        assertThat(bookingDto.getEnd(), equalTo(end));
        assertThat(bookingDto.getItem(), equalTo(screwdriver));
        assertThat(bookingDto.getBookerId(), equalTo(1L));
        assertThat(bookingDto.getBooker(), equalTo(john));
        assertThat(bookingDto.getStatus(), equalTo(BookingState.REJECTED));
        verify(bookingRepository, times(1))
                .save(screwdriverRejectedBooking);
    }

    @Test
    void updateStatusShouldRejectApprovedBooking() {
        Booking screwdriverRejectedBooking = new Booking(1L, start, end, screwdriver, john, BookingState.REJECTED);

        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(screwdriverBooking));
        when(bookingRepository.save(any()))
                .thenReturn(screwdriverRejectedBooking);

        BookingDto bookingDto = bookingService.updateStatus(jackId, 1L, false);

        assertThat(bookingDto.getId(), equalTo(1L));
        assertThat(bookingDto.getStart(), equalTo(start));
        assertThat(bookingDto.getEnd(), equalTo(end));
        assertThat(bookingDto.getItem(), equalTo(screwdriver));
        assertThat(bookingDto.getBookerId(), equalTo(1L));
        assertThat(bookingDto.getBooker(), equalTo(john));
        assertThat(bookingDto.getStatus(), equalTo(BookingState.REJECTED));

        verify(bookingRepository, times(1))
                .save(screwdriverRejectedBooking);
    }

    @Test
    void updateStatusShouldThrowNotFoundExceptionWhenBookingNotFound() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.updateStatus(jackId, 1L, false));

        Assertions.assertEquals("Booking not found", exception.getMessage());
    }

    @Test
    void updateStatusShouldThrowNotFoundExceptionWhenUserIsNotTheOwner() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(screwdriverBooking));

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.updateStatus(johnId, 1L, true));

        Assertions.assertEquals("Booking not found", exception.getMessage());
    }

    @Test
    void updateStatusShouldThrowValidationExceptionWhenTryingToApproveAlreadyApprovedBooking() {
        Booking screwdriverApprovedBooking = new Booking(1L, start, end, screwdriver, john, BookingState.APPROVED);

        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(screwdriverApprovedBooking));

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.updateStatus(jackId, 1L, true));

        Assertions.assertEquals("Already approved", exception.getMessage());
    }
}

