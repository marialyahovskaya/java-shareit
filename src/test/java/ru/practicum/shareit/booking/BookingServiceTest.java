package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
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
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    LocalDateTime start = LocalDateTime.now().plusDays(1);
    LocalDateTime end = LocalDateTime.now().plusDays(2);

    Long johnId = 1L;
    Long jackId = 2L;

    Item screwdriver = new Item(
            1L, jackId, "отвертка", "nnnnnnn", 2L, true, new ArrayList<>());

    Item unavailableScrewdriver = new Item(
            1L, jackId, "отвертка", "nnnnnnn", 2L, false, new ArrayList<>());
    User john = new User(
            johnId, "JOHN", "john@email.com");

    Booking screwdriverBooking = new Booking(
            1L, start, end, screwdriver, john, BookingState.WAITING);

    BookingCreationDto screwdriverBookingCreationDto = new BookingCreationDto(
            1L, start, end);

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

}

