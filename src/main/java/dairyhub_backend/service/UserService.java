package dairyhub_backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import dairyhub_backend.entity.User;
import dairyhub_backend.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Register customer
    public User registerUser(User user) {

        user.setRole("CUSTOMER");

        return userRepository.save(user);
    }

    // Login
    public User loginUser(
            String email,
            String password) {

        User user =
                userRepository
                        .findByEmail(email)
                        .orElse(null);

        if (user == null) {
            return null;
        }

        if (!user.getPassword().equals(password)) {
            return null;
        }

        return user;
    }

    // Get all users
    public List<User> getAllUsers() {

        return userRepository.findAll();
    }

    // Delete user
    public boolean deleteUser(Long id) {

        User user =
                userRepository
                        .findById(id)
                        .orElse(null);

        if (user == null) {
            return false;
        }

        // Do not allow Admin account deletion
        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            return false;
        }

        userRepository.deleteById(id);

        return true;
    }
}