package dairyhub_backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import dairyhub_backend.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

}