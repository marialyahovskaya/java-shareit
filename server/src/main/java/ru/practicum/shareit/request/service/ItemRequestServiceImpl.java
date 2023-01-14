package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.PaginationHelper;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    private final ItemRequestRepository itemRequestRepository;

    @Override
    public ItemRequestDto addItemRequest(final Long userId, final ItemRequestDto itemRequestDto) {
        User requestor = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        ItemRequest itemRequestToAdd = ItemRequestMapper.toItemRequest(requestor, itemRequestDto);
        ItemRequest createdItemRequest = itemRequestRepository.save(itemRequestToAdd);
        return ItemRequestMapper.toItemRequestDto(createdItemRequest, new ArrayList<>());
    }

    @Override
    public Collection<ItemRequestDto> findItemRequestsByRequestorId(Long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("User not found");
        }
        Collection<ItemRequest> itemRequests = itemRequestRepository.findByRequestorIdOrderByCreatedDesc(userId);
        List<ItemRequestDto> itemRequestDtos = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequests) {
            ItemRequestDto dto = ItemRequestMapper.toItemRequestDto(itemRequest, ItemMapper.toItemDto(itemRepository.findByRequestId(itemRequest.getId())));
            itemRequestDtos.add(dto);
        }

        return itemRequestDtos;
    }

    @Override
    public Collection<ItemRequestDto> findAll(Long userId, int from, int size) {
        Pageable pageable = PaginationHelper.makePageable(from, size);
        List<ItemRequest> itemRequests = itemRequestRepository.findByRequestorIdNot(userId, pageable);
        List<ItemRequestDto> itemRequestDtos = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequests) {
            ItemRequestDto dto = ItemRequestMapper.toItemRequestDto(itemRequest, ItemMapper.toItemDto(itemRepository.findByRequestId(itemRequest.getId())));
            itemRequestDtos.add(dto);
        }
        return itemRequestDtos;
    }

    @Override
    public ItemRequestDto findItemRequestById(Long userId, Long id) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("User not found");
        }
        ItemRequest itemRequest = itemRequestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Request not found"));

        ItemRequestDto dto = ItemRequestMapper.toItemRequestDto(itemRequest,
                ItemMapper.toItemDto(itemRepository.findByRequestId(itemRequest.getId())));
        return dto;
    }

}
