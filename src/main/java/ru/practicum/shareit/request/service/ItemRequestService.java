package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.Collection;

public interface ItemRequestService {

    ItemRequestDto addItemRequest(final Long userId, final ItemRequestDto itemRequest);

    Collection<ItemRequestDto> findItemRequestsByUserId(Long userId);

    Collection<ItemRequestDto> findAll(Long userId, int from, int size);

    ItemRequestDto findById(Long userId, Long id);
}
