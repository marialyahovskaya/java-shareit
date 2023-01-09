package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.*;
import ru.practicum.shareit.item.dto.CommentCreationDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    private final BookingRepository bookingRepository;

    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public ItemDto addItem(final Long userId, final ItemDto itemDto) {
        ItemValidator.validate(itemDto);
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        ItemRequest request = null;
        if (itemDto.getRequestId() != null) {
            request = itemRequestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Request not found"));
        }
        Item itemToAdd = ItemMapper.toItem(owner, request, itemDto);
        if (itemToAdd.getAvailable() == null) {
            throw new ValidationException("Item availability is undefined");
        }
        Item createdItem = itemRepository.save(itemToAdd);
        return ItemMapper.toItemDto(createdItem);
    }

    @Override
    public CommentDto addComment(final Long userId, final Long itemId, final CommentCreationDto commentCreationDto) {
        CommentValidator.validate(commentCreationDto);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        List<Booking> bookings = bookingRepository.findByItem_IdAndEndIsBefore(itemId, LocalDateTime.now());
        boolean hasEverBooked = false;
        for (Booking booking : bookings) {
            if (booking.getBooker().getId().equals(userId) && booking.getStatus() == BookingState.APPROVED) {
                hasEverBooked = true;
                break;
            }
        }
        if (!hasEverBooked) {
            throw new ValidationException("Cannot create comment");
        }
        Comment comment = CommentMapper.toComment(userId, itemId, commentCreationDto);
        comment.setAuthor(user);
        Comment savedComment = commentRepository.save(comment);
        return CommentMapper.commentToDto(savedComment);
    }

    @Override
    public ItemDto findById(final Long userId, final Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item not found"));
        Optional<Booking> lastBooking = Optional.empty();
        Optional<Booking> nextBooking = Optional.empty();
        if (userId.equals(item.getOwner().getId())) {
            lastBooking = bookingRepository.findFirstByItem_IdAndEndIsBeforeOrderByEndDesc(id, LocalDateTime.now());
            nextBooking = bookingRepository.findFirstByItem_IdAndStartIsAfterOrderByStartAsc(id, LocalDateTime.now());
        }
        return ItemMapper.toItemDto(item, lastBooking, nextBooking);
    }

    @Override
    public Collection<ItemDto> findItemsByOwnerId(final Long userId) {
        return itemRepository.findByOwnerIdOrderByIdAsc(userId).stream()
                .map(item -> ItemMapper.toItemDto(item,
                        bookingRepository.findFirstByItem_IdAndEndIsBeforeOrderByEndDesc(item.getId(), LocalDateTime.now()),
                        bookingRepository.findFirstByItem_IdAndStartIsAfterOrderByStartAsc(item.getId(), LocalDateTime.now())))
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
        Item itemToUpdate = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item not found"));
        if (!userId.equals(itemToUpdate.getOwner().getId())) {
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