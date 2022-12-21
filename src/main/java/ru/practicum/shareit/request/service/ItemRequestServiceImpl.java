package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserRepository;

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
       // ItemValidator.validate(itemRequestDto);
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("User not found");
        }
        if (itemRequestDto.getDescription() == null){
            throw new ValidationException("No description");
        }
        ItemRequest itemRequestToAdd = ItemRequestMapper.toItemRequest(userId, itemRequestDto);
        ItemRequest createdItemRequest = itemRequestRepository.save(itemRequestToAdd);
        return ItemRequestMapper.toItemRequestDto(createdItemRequest, new ArrayList<>());
    }

    @Override
    public Collection<ItemRequestDto> findItemRequestByUserId(Long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("User not found");
        }
        Collection<ItemRequest> itemRequests = itemRequestRepository.findByRequestorId(userId);
        List<ItemRequestDto> itemRequestDtos = new ArrayList<>();
        for(ItemRequest itemRequest: itemRequests){
            ItemRequestDto dto = ItemRequestMapper.toItemRequestDto(itemRequest, ItemMapper.toItemDto(itemRepository.findByRequestId(itemRequest.getId())));
            itemRequestDtos.add(dto);
        }

        return itemRequestDtos;
    }

    @Override
    public Collection<ItemRequestDto> findAll() {
        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.findAll());
    }

}
