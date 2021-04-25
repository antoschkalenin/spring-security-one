package ru.demo.springsecurityone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.demo.springsecurityone.model.User;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}