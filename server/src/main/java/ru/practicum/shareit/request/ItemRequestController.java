package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ResponseEntity<ItemRequestDto> create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @RequestBody ItemRequestDto itemRequestDto) {
        return new ResponseEntity<>(itemRequestService.addItemRequest(userId, itemRequestDto), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Collection<ItemRequestDto>> findItemRequestsByRequestorId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return new ResponseEntity<Collection<ItemRequestDto>>(itemRequestService.findItemRequestsByRequestorId(userId), HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<ItemRequestDto> findById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                   @PathVariable Long id) {
        return new ResponseEntity<ItemRequestDto>(itemRequestService.findItemRequestById(userId, id), HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<Collection<ItemRequestDto>> findAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                              @RequestParam(required = false, defaultValue = "0") Integer from,
                                                              @RequestParam(required = false, defaultValue = "100") Integer size) {
        return new ResponseEntity<Collection<ItemRequestDto>>(itemRequestService.findAll(userId, from, size), HttpStatus.OK);
    }
}
