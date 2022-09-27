package ua.com.zmike.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.com.zmike.userservice.model.User;

import java.time.LocalDate;
import java.util.Set;

public interface UserRepository extends JpaRepository<User, Long> {

    Set<User> findAllByBirthDateBetween(LocalDate from, LocalDate to);

    boolean existsByEmail(String email);

    Set<User> findAllByBirthDate(LocalDate date);

}