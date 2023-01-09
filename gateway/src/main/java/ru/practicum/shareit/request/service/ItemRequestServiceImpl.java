package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.ArrayList;
import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {
    @Override
    public ItemRequestDto addItemRequest(final Long userId, final ItemRequestDto itemRequestDto) {
//        User requestor = userRepository.findById(userId)
//                .orElseThrow(() -> new NotFoundException("User not found"));
//        if (itemRequestDto.getDescription() == null) {
//            throw new ValidationException("No description");
//        }
//        ItemRequest itemRequestToAdd = ItemRequestMapper.toItemRequest(requestor, itemRequestDto);
//        ItemRequest createdItemRequest = itemRequestRepository.save(itemRequestToAdd);
//        return ItemRequestMapper.toItemRequestDto(createdItemRequest, new ArrayList<>());
        return null;
    }

    @Override
    public Collection<ItemRequestDto> findItemRequestsByRequestorId(Long userId) {
//        if (userRepository.findById(userId).isEmpty()) {
//            throw new NotFoundException("User not found");
//        }
//        Collection<ItemRequest> itemRequests = itemRequestRepository.findByRequestorIdOrderByCreatedDesc(userId);
//        List<ItemRequestDto> itemRequestDtos = new ArrayList<>();
//        for (ItemRequest itemRequest : itemRequests) {
//            ItemRequestDto dto = ItemRequestMapper.toItemRequestDto(itemRequest, ItemMapper.toItemDto(itemRepository.findByRequestId(itemRequest.getId())));
//            itemRequestDtos.add(dto);
//        }
//
//        return itemRequestDtos;
        return new ArrayList<>();
    }

    @Override
    public Collection<ItemRequestDto> findAll(Long userId, int from, int size) {
//        if (size == 0) {
//            throw new ValidationException("Size is zero");
//        }
//        if (from < 0) {
//            throw new ValidationException("From cannot be negative");
//        }
//        Pageable pageable = PaginationHelper.makePageable(from, size);
//        List<ItemRequest> itemRequests = itemRequestRepository.findByRequestorIdNot(userId, pageable);
//        List<ItemRequestDto> itemRequestDtos = new ArrayList<>();
//        for (ItemRequest itemRequest : itemRequests) {
//            ItemRequestDto dto = ItemRequestMapper.toItemRequestDto(itemRequest, ItemMapper.toItemDto(itemRepository.findByRequestId(itemRequest.getId())));
//            itemRequestDtos.add(dto);
//        }
//        return itemRequestDtos;
        return new ArrayList<>();
    }

    @Override
    public ItemRequestDto findItemRequestById(Long userId, Long id) {
//        if (userRepository.findById(userId).isEmpty()) {
//            throw new NotFoundException("User not found");
//        }
//        ItemRequest itemRequest = itemRequestRepository.findById(id)
//                .orElseThrow(() -> new NotFoundException("Request not found"));
//
//        ItemRequestDto dto = ItemRequestMapper.toItemRequestDto(itemRequest,
//                ItemMapper.toItemDto(itemRepository.findByRequestId(itemRequest.getId())));
//        return dto;
        return null;
    }

}
