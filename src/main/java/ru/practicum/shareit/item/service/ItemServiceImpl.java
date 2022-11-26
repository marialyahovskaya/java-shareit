package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemValidator;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    @Override
    public ItemDto addItem(final Long userId, final ItemDto itemDto) {
        ItemValidator.validate(itemDto);
        if (userRepository.findUserById(userId) == null) {
            throw new NotFoundException("User not found");
        }
        Item itemToAdd = ItemMapper.toItem(userId, itemDto);
        if (itemToAdd.getAvailable() == null) {
            throw new ValidationException("Item is not available");
        }
        Item createdItem = itemRepository.addItem(itemToAdd);
        return ItemMapper.toItemDto(createdItem);
    }

    @Override
    public ItemDto findItemById(final Long id) {
        Item item = itemRepository.findItemById(id);
        if (item == null) {
            throw new NotFoundException("Item not found");
        }
        return ItemMapper.toItemDto(item);
    }

    @Override
    public Collection<ItemDto> findItemsByUserId(final Long userId) {
        return itemRepository.findItemsByUserId(userId).stream()
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
                .filter(item -> item.getAvailable())
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public ItemDto patchItem(final Long userId, final Long id, final ItemDto itemDto) {
        if (userId == null) {
            throw new ValidationException("UserId not provided");
        }
        Item itemToUpdate = itemRepository.findItemById(id);
        if (itemToUpdate == null) {
            throw new NotFoundException("Item not found");
        }
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
        Item updatedItem = itemRepository.updateItem(itemToUpdate);
        return ItemMapper.toItemDto(updatedItem);
    }
}