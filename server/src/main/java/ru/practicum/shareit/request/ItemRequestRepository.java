package ru.practicum.shareit.request;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRequestRepository extends PagingAndSortingRepository<ItemRequest, Long> {

    List<ItemRequest> findByRequestorIdOrderByCreatedDesc(Long id);

    List<ItemRequest> findByRequestorIdNot(Long id, Pageable pageable);
}
