package ru.practicum.shareit.booking.unit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.PaginationHelper;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
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
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.hasProperty;
import static org.mockito.ArgumentMatchers.*;

import static org.hamcrest.MatcherAssert.assertThat;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    LocalDateTime start = LocalDateTime.now().plusDays(1);
    LocalDateTime end = LocalDateTime.now().plusDays(2);

    Long johnId = 1L;
    Long jackId = 2L;

    Item screwdriver =
            new Item(1L, jackId, "отвертка", "nnnnnnn", 2L, true, new ArrayList<>());

    Item unavailableScrewdriver =
            new Item(1L, jackId, "отвертка", "nnnnnnn", 2L, false, new ArrayList<>());

    User john = new User(johnId, "JOHN", "john@email.com");


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

        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(screwdriver));

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(john));

        Mockito
                .when(bookingRepository.save(any()))
                .thenReturn(screwdriverBooking);

        BookingDto bookingDto = bookingService.addBooking(johnId, screwdriverBookingCreationDto);

        assertThat(bookingDto.getId(), equalTo(1L));
        assertThat(bookingDto.getStart(), equalTo(start));
        assertThat(bookingDto.getEnd(), equalTo(end));
        assertThat(bookingDto.getItem(), equalTo(screwdriver));
        assertThat(bookingDto.getBookerId(), equalTo(1L));
        assertThat(bookingDto.getBooker(), equalTo(john));
        assertThat(bookingDto.getStatus(), equalTo(BookingState.WAITING));


        Booking booking = BookingMapper.toBooking(johnId, screwdriverBookingCreationDto);
        booking.setItem(screwdriver);
        booking.setBooker(john);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .save(booking);
    }

    @Test
    void addBookingShouldThrowNotFoundExceptionWhenItemNotFound() {

        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.addBooking(johnId, screwdriverBookingCreationDto));

        Assertions.assertEquals("Item not found", exception.getMessage());
    }

    @Test
    void addBookingShouldThrowNotFoundExceptionWhenUserNotFound() {

        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(screwdriver));
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.addBooking(johnId, screwdriverBookingCreationDto));

        Assertions.assertEquals("User not found", exception.getMessage());
    }

    @Test
    void addBookingShouldThrowNotFoundExceptionWhenItemIsUnavailable() {

        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(unavailableScrewdriver));
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(john));

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.addBooking(johnId, screwdriverBookingCreationDto));

        Assertions.assertEquals("Cannot book unavailable item", exception.getMessage());
    }

    @Test
    void addBookingShouldThrowNotFoundExceptionWhenOwnerTriesToBookHisItem() {

        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(screwdriver));
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(john));

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.addBooking(jackId, screwdriverBookingCreationDto));

        Assertions.assertEquals("You can't book this item", exception.getMessage());
    }

    @Test
    void findBookingShouldReturnBookingDataForBooker() {

        Mockito
                .when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(screwdriverBooking));

        BookingDto bookingDto = bookingService.findBooking(johnId, 3L);

        assertThat(bookingDto.getId(), equalTo(1L));
        assertThat(bookingDto.getStart(), equalTo(start));
        assertThat(bookingDto.getEnd(), equalTo(end));
        assertThat(bookingDto.getItem(), equalTo(screwdriver));
        assertThat(bookingDto.getBookerId(), equalTo(1L));
        assertThat(bookingDto.getBooker(), equalTo(john));
        assertThat(bookingDto.getStatus(), equalTo(BookingState.WAITING));

        Mockito.verify(bookingRepository, Mockito.times(1))
                .findById(3L);
    }

    @Test
    void findBookingShouldReturnBookingDataForOwner() {

        Mockito
                .when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(screwdriverBooking));

        BookingDto bookingDto = bookingService.findBooking(jackId, 3L);

        assertThat(bookingDto.getId(), equalTo(1L));
        assertThat(bookingDto.getStart(), equalTo(start));
        assertThat(bookingDto.getEnd(), equalTo(end));
        assertThat(bookingDto.getItem(), equalTo(screwdriver));
        assertThat(bookingDto.getBookerId(), equalTo(1L));
        assertThat(bookingDto.getBooker(), equalTo(john));
        assertThat(bookingDto.getStatus(), equalTo(BookingState.WAITING));

        Mockito.verify(bookingRepository, Mockito.times(1))
                .findById(3L);

    }

    @Test
    void findBookingShouldThrowNotFoundExceptionWhenBookingNotFound() {

        Mockito
                .when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.findBooking(jackId, 3L));

        Assertions.assertEquals("Booking not found", exception.getMessage());
    }

    @Test
    void findBookingShouldThrowNotFoundExceptionWhenUserIsNotBookerOrOwner() {

        Mockito
                .when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(screwdriverBooking));

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.findBooking(99L, 3L));

        Assertions.assertEquals("Booking not found", exception.getMessage());
    }

    @Test
    void shouldFindAllBookingsByBookerId() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(john));
        Mockito
                .when(bookingRepository.findByBookerOrderByStartDesc(any(), any()))
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

        Mockito.verify(bookingRepository, Mockito.times(1))
                .findByBookerOrderByStartDesc(john, pageable);
    }

    @Test
    void shouldFindCurrentBookingsByBookerId() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(john));
        Mockito
                .when(bookingRepository.findByBookerAndStartIsBeforeAndEndIsAfterOrderByStartDesc(any(), any(), any(), any()))
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

        Mockito.verify(bookingRepository, Mockito.times(1))
                .findByBookerAndStartIsBeforeAndEndIsAfterOrderByStartDesc(ArgumentMatchers.any(User.class),
                        ArgumentMatchers.any(LocalDateTime.class),
                        ArgumentMatchers.any(LocalDateTime.class),
                        ArgumentMatchers.any(Pageable.class)
                );
    }

    @Test
    void shouldFindPastBookingsByBookerId() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(john));
        Mockito
                .when(bookingRepository.findByBookerAndEndIsBeforeOrderByStartDesc(any(), any(), any()))
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

        Mockito.verify(bookingRepository, Mockito.times(1))
                .findByBookerAndEndIsBeforeOrderByStartDesc(ArgumentMatchers.any(User.class),
                        ArgumentMatchers.any(LocalDateTime.class),
                        ArgumentMatchers.any(Pageable.class)
                );
    }

    @Test
    void shouldFindFutureBookingsByBookerId() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(john));
        Mockito
                .when(bookingRepository.findByBookerAndStartIsAfterOrderByStartDesc(any(), any(), any()))
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

        Mockito.verify(bookingRepository, Mockito.times(1))
                .findByBookerAndStartIsAfterOrderByStartDesc(ArgumentMatchers.any(User.class),
                        ArgumentMatchers.any(LocalDateTime.class),
                        ArgumentMatchers.any(Pageable.class)
                );
    }

    @Test
    void shouldFindWaitingBookingsByBookerId() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(john));
        Mockito
                .when(bookingRepository.findByBookerAndStatusOrderByStartDesc(any(), any(), any()))
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

        Mockito.verify(bookingRepository, Mockito.times(1))
                .findByBookerAndStatusOrderByStartDesc(john, BookingState.WAITING, pageable);
    }

    @Test
    void shouldFindRejectedBookingsByBookerId() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(john));
        Mockito
                .when(bookingRepository.findByBookerAndStatusOrderByStartDesc(any(), any(), any()))
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

        Mockito.verify(bookingRepository, Mockito.times(1))
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
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.findBookingsByBookerId(99L, "ALL", 0, 100));

        Assertions.assertEquals("User not found", exception.getMessage());
    }

    @Test
    void findBookingsByBookerIdShouldThrowValidationExceptionWhenSizeIsZero() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(john));
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.findBookingsByBookerId(johnId, "ALL", 0, 0));

        Assertions.assertEquals("Size is zero", exception.getMessage());
    }

    @Test
    void findBookingsByBookerIdShouldThrowValidationExceptionWhenFromIsNegative() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(john));
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.findBookingsByBookerId(johnId, "ALL", -1, 100));

        Assertions.assertEquals("From cannot be negative", exception.getMessage());
    }


    @Test
    void shouldFindAllBookingsByOwnerId() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(john));
        Mockito
                .when(bookingRepository.findByItemOwnerIdOrderByStartDesc(anyLong(), any()))
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

        Mockito.verify(bookingRepository, Mockito.times(1))
                .findByItemOwnerIdOrderByStartDesc(johnId, pageable);
    }

    @Test
    void shouldFindCurrentBookingsByOwnerId() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(john));
        Mockito
                .when(bookingRepository.findByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(any(), any(), any(), any()))
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

        Mockito.verify(bookingRepository, Mockito.times(1))
                .findByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(ArgumentMatchers.anyLong(),
                        ArgumentMatchers.any(LocalDateTime.class),
                        ArgumentMatchers.any(LocalDateTime.class),
                        ArgumentMatchers.any(Pageable.class)
                );
    }

    @Test
    void shouldFindPastBookingsByOwnerId() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(john));
        Mockito
                .when(bookingRepository.findByItemOwnerIdAndEndIsBeforeOrderByStartDesc(any(), any(), any()))
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

        Mockito.verify(bookingRepository, Mockito.times(1))
                .findByItemOwnerIdAndEndIsBeforeOrderByStartDesc(anyLong(),
                        ArgumentMatchers.any(LocalDateTime.class),
                        ArgumentMatchers.any(Pageable.class)
                );
    }

    @Test
    void shouldFindFutureBookingsByOwnerId() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(john));
        Mockito
                .when(bookingRepository.findByItemOwnerIdAndStartIsAfterOrderByStartDesc(any(), any(), any()))
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

        Mockito.verify(bookingRepository, Mockito.times(1))
                .findByItemOwnerIdAndStartIsAfterOrderByStartDesc(anyLong(),
                        ArgumentMatchers.any(LocalDateTime.class),
                        ArgumentMatchers.any(Pageable.class)
                );
    }

    @Test
    void shouldFindWaitingBookingsByOwnerId() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(john));
        Mockito
                .when(bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(any(), any(), any()))
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

        Mockito.verify(bookingRepository, Mockito.times(1))
                .findByItemOwnerIdAndStatusOrderByStartDesc(johnId, BookingState.WAITING, pageable);
    }

    @Test
    void shouldFindRejectedBookingsByOwnerId() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(john));
        Mockito
                .when(bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(any(), any(), any()))
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

        Mockito.verify(bookingRepository, Mockito.times(1))
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
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.findBookingsByOwnerId(99L, "ALL", 0, 100));

        Assertions.assertEquals("User not found", exception.getMessage());
    }

    @Test
    void findBookingsByOwnerIdShouldThrowValidationExceptionWhenSizeIsZero() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(john));
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.findBookingsByOwnerId(johnId, "ALL", 0, 0));

        Assertions.assertEquals("Size is zero", exception.getMessage());
    }

    @Test
    void findBookingsByOwnerIdShouldThrowValidationExceptionWhenFromIsNegative() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(john));
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.findBookingsByOwnerId(johnId, "ALL", -1, 100));

        Assertions.assertEquals("From cannot be negative", exception.getMessage());
    }

    @Test
    void updateStatusShouldApproveWaitingBooking() {
        Booking screwdriverApprovedBooking = new Booking(1L, start, end, screwdriver, john, BookingState.APPROVED);

        Mockito
                .when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(screwdriverBooking));
        Mockito
                .when(bookingRepository.save(any()))
                .thenReturn(screwdriverApprovedBooking);

        BookingDto bookingDto = bookingService.updateStatus(jackId, 1L, true);

        assertThat(bookingDto.getId(), equalTo(1L));
        assertThat(bookingDto.getStart(), equalTo(start));
        assertThat(bookingDto.getEnd(), equalTo(end));
        assertThat(bookingDto.getItem(), equalTo(screwdriver));
        assertThat(bookingDto.getBookerId(), equalTo(1L));
        assertThat(bookingDto.getBooker(), equalTo(john));
        assertThat(bookingDto.getStatus(), equalTo(BookingState.APPROVED));

        Mockito.verify(bookingRepository, Mockito.times(1))
                .save(screwdriverApprovedBooking);
    }

    @Test
    void updateStatusShouldApproveRejectedBooking() {
        Booking screwdriverRejectedBooking = new Booking(1L, start, end, screwdriver, john, BookingState.REJECTED);
        Booking screwdriverApprovedBooking = new Booking(1L, start, end, screwdriver, john, BookingState.APPROVED);

        Mockito
                .when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(screwdriverRejectedBooking));
        Mockito
                .when(bookingRepository.save(any()))
                .thenReturn(screwdriverApprovedBooking);

        BookingDto bookingDto = bookingService.updateStatus(jackId, 1L, true);

        assertThat(bookingDto.getId(), equalTo(1L));
        assertThat(bookingDto.getStart(), equalTo(start));
        assertThat(bookingDto.getEnd(), equalTo(end));
        assertThat(bookingDto.getItem(), equalTo(screwdriver));
        assertThat(bookingDto.getBookerId(), equalTo(1L));
        assertThat(bookingDto.getBooker(), equalTo(john));
        assertThat(bookingDto.getStatus(), equalTo(BookingState.APPROVED));

        Mockito.verify(bookingRepository, Mockito.times(1))
                .save(screwdriverApprovedBooking);
    }

    @Test
    void updateStatusShouldRejectWaitingBooking() {
        Booking screwdriverApprovedBooking = new Booking(1L, start, end, screwdriver, john, BookingState.APPROVED);
        Booking screwdriverRejectedBooking = new Booking(1L, start, end, screwdriver, john, BookingState.REJECTED);

        Mockito
                .when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(screwdriverApprovedBooking));
        Mockito
                .when(bookingRepository.save(any()))
                .thenReturn(screwdriverRejectedBooking);

        BookingDto bookingDto = bookingService.updateStatus(jackId, 1L, false);

        assertThat(bookingDto.getId(), equalTo(1L));
        assertThat(bookingDto.getStart(), equalTo(start));
        assertThat(bookingDto.getEnd(), equalTo(end));
        assertThat(bookingDto.getItem(), equalTo(screwdriver));
        assertThat(bookingDto.getBookerId(), equalTo(1L));
        assertThat(bookingDto.getBooker(), equalTo(john));
        assertThat(bookingDto.getStatus(), equalTo(BookingState.REJECTED));

        Mockito.verify(bookingRepository, Mockito.times(1))
                .save(screwdriverRejectedBooking);
    }

    @Test
    void updateStatusShouldRejectApprovedBooking() {
        Booking screwdriverRejectedBooking = new Booking(1L, start, end, screwdriver, john, BookingState.REJECTED);

        Mockito
                .when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(screwdriverBooking));
        Mockito
                .when(bookingRepository.save(any()))
                .thenReturn(screwdriverRejectedBooking);

        BookingDto bookingDto = bookingService.updateStatus(jackId, 1L, false);

        assertThat(bookingDto.getId(), equalTo(1L));
        assertThat(bookingDto.getStart(), equalTo(start));
        assertThat(bookingDto.getEnd(), equalTo(end));
        assertThat(bookingDto.getItem(), equalTo(screwdriver));
        assertThat(bookingDto.getBookerId(), equalTo(1L));
        assertThat(bookingDto.getBooker(), equalTo(john));
        assertThat(bookingDto.getStatus(), equalTo(BookingState.REJECTED));

        Mockito.verify(bookingRepository, Mockito.times(1))
                .save(screwdriverRejectedBooking);
    }

    @Test
    void updateStatusShouldThrowNotFoundExceptionWhenBookingNotFound() {
        Mockito
                .when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.empty());


        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.updateStatus(jackId, 1L, false));

        Assertions.assertEquals("Booking not found", exception.getMessage());
    }

    @Test
    void updateStatusShouldThrowNotFoundExceptionWhenUserIsNotTheOwner() {
        Mockito
                .when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(screwdriverBooking));

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.updateStatus(johnId, 1L, true));

        Assertions.assertEquals("Booking not found", exception.getMessage());
    }

    @Test
    void updateStatusShouldThrowValidationExceptionWhenTryingToApproveAlreadyApprovedBooking() {
        Booking screwdriverApprovedBooking = new Booking(1L, start, end, screwdriver, john, BookingState.APPROVED);

        Mockito
                .when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(screwdriverApprovedBooking));

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.updateStatus(jackId, 1L, true));

        Assertions.assertEquals("Already approved", exception.getMessage());
    }
}

