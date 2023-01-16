package ru.practicum.shareit.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    List<User> findByEmailContainingIgnoreCase(String emailSearch);
}