package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ResponseEntity<ItemRequestDto> create(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody ItemRequestDto itemRequestDto) {
        return new ResponseEntity<>(itemRequestService.addItemRequest(userId, itemRequestDto), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Collection<ItemRequestDto>> findItemRequestsByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return new ResponseEntity<Collection<ItemRequestDto>>(itemRequestService.findItemRequestByUserId(userId), HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<Collection<ItemRequestDto>> findAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return new ResponseEntity<Collection<ItemRequestDto>>(itemRequestService.findAll(), HttpStatus.OK);
    }
}
