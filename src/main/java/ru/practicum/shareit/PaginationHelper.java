package ru.practicum.shareit;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class PaginationHelper {

    public static Pageable makePageable (int from, int size) {
        int page = from/size;
        Pageable pageable = PageRequest.of(page, size);
        return pageable;
    }
}
