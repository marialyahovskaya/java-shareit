package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemValidator;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    private final BookingRepository bookingRepository;

    @Override
    public ItemDto addItem(final Long userId, final ItemDto itemDto) {
        ItemValidator.validate(itemDto);
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("User not found");
        }
        Item itemToAdd = ItemMapper.toItem(userId, itemDto);
        if (itemToAdd.getAvailable() == null) {
            throw new ValidationException("Item is not available");
        }
        Item createdItem = itemRepository.save(itemToAdd);
        return ItemMapper.toItemDto(createdItem);
    }

    @Override
    public ItemDto findItemById(final Long id) {
        Optional<Item> item = itemRepository.findById(id);
        if (item.isEmpty()) {
            throw new NotFoundException("Item not found");
        }
        Optional<Booking> lastBooking = bookingRepository.findFirstByItem_IdAndEndIsBeforeOrderByEndDesc(id, LocalDateTime.now());
        Optional<Booking> nextBooking = bookingRepository.findFirstByItem_IdAndStartIsAfterOrderByStartAsc(id, LocalDateTime.now());
        return ItemMapper.toItemDto(item.get(), lastBooking, nextBooking);
    }

    @Override
    public Collection<ItemDto> findItemsByUserId(final Long userId) {
        log.info("trying to find items of user with id " + userId);
        return itemRepository.findByUserId(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public Collection<ItemDto> search(final String text) {
        if (text.equals("")) {
            return new ArrayList<>();
        }
        return itemRepository.findAll().stream()
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase()) ||
                        item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .filter(Item::getAvailable)
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public ItemDto patchItem(final Long userId, final Long id, final ItemDto itemDto) {
        if (userId == null) {
            throw new ValidationException("UserId not provided");
        }
        Optional<Item> loadedItem = itemRepository.findById(id);
        if (loadedItem.isEmpty()) {
            throw new NotFoundException("Item not found");
        }
        Item itemToUpdate = loadedItem.get();
        if (!userId.equals(itemToUpdate.getUserId())) {
            throw new NotFoundException("Item not found");
        }
        if (itemDto.getName() != null) {
            itemToUpdate.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            itemToUpdate.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            itemToUpdate.setAvailable(itemDto.getAvailable());
        }

        ItemValidator.validate(ItemMapper.toItemDto(itemToUpdate));
        Item updatedItem = itemRepository.save(itemToUpdate);
        return ItemMapper.toItemDto(updatedItem);
    }
}