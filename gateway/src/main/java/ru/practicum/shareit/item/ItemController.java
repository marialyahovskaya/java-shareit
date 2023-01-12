package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemClient itemClient;


    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @RequestBody @Valid ItemDto itemDto) {
        return itemClient.addItem(userId, itemDto);
    }

//    @PatchMapping("/{id}")
//    public ResponseEntity<ItemDto> updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
//                                              @PathVariable Long id,
//                                              @RequestBody ItemDto itemDto) {
//        return new ResponseEntity<>(itemService.patchItem(userId, id, itemDto), HttpStatus.OK);
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<ItemDto> findItemById(@RequestHeader("X-Sharer-User-Id") Long userId,
//                                                @PathVariable Long id) {
//        return new ResponseEntity<>(itemService.findById(userId, id), HttpStatus.OK);
//    }
//
//    @GetMapping
//    public ResponseEntity<Collection<ItemDto>> findItemsByOwnerId(@RequestHeader("X-Sharer-User-Id") Long userId) {
//        return new ResponseEntity<>(itemService.findItemsByOwnerId(userId), HttpStatus.OK);
//    }
//
//    @GetMapping("/search")
//    public ResponseEntity<Collection<ItemDto>> search(@RequestParam String text,
//                                                      @RequestHeader("X-Sharer-User-Id") Long userId) {
//        return new ResponseEntity<>(itemService.search(text), HttpStatus.OK);
//    }
//
//    @PostMapping("/{itemId}/comment")
//    public ResponseEntity<CommentDto> createComment(@RequestHeader("X-Sharer-User-Id") Long userId,
//                                                    @PathVariable Long itemId,
//                                                    @RequestBody CommentCreationDto commentCreationDto) {
//        return new ResponseEntity<>(itemService.addComment(userId, itemId, commentCreationDto), HttpStatus.OK);
//    }
}