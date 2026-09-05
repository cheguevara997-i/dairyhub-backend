package dairyhub_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import dairyhub_backend.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    // Active users only
    Optional<User> findByEmailAndDeletedFalse(String email);

    // Deleted users only
    Optional<User> findByEmailAndDeletedTrue(String email);

    // All users currently in Delete Bin
    List<User> findByDeletedTrue();

}