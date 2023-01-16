package ru.practicum.shareit.item;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

public class ItemMapper {

    public static Item toItem(User owner, ItemRequest request, ItemDto itemDto) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setOwner(owner);
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setRequest(request);
        item.setAvailable(itemDto.getAvailable());
        return item;
    }

    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequest() == null ? null : item.getRequest().getId())
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

    public static Collection<ItemDto> toItemDto(Collection<Item> items) {
        return items.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toUnmodifiableList());
    }

}