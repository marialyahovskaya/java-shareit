package ru.practicum.shareit.item;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Optional;
import java.util.stream.Collectors;

public class ItemMapper {

    public static Item toItem(Long userId, ItemDto itemDto) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setUserId(userId);
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        return item;
    }

    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .comments(item.getComments().stream()
                        .map(CommentMapper::commentToDto)
                        .collect(Collectors.toUnmodifiableList()))
                .build();
    }

    public static ItemDto toItemDto(Item item, Optional<Booking> lastBooking, Optional<Booking> nextBooking) {
        ItemDto itemDto = toItemDto(item);
        lastBooking.ifPresent(booking -> itemDto.setLastBooking(BookingMapper.toBookingDto(booking)));
        nextBooking.ifPresent(booking -> itemDto.setNextBooking(BookingMapper.toBookingDto(booking)));
        return itemDto;
    }

}