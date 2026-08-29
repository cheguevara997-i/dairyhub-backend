package dairyhub_backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dairyhub_backend.entity.User;
import dairyhub_backend.service.UserService;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Register customer
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(
            @RequestBody User user) {

        User savedUser =
                userService.registerUser(user);

        return ResponseEntity.ok(savedUser);
    }

    // Login
    @PostMapping("/login")
    public ResponseEntity<User> loginUser(
            @RequestBody User loginRequest) {

        User user =
                userService.loginUser(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                );

        if (user == null) {
            return ResponseEntity
                    .status(401)
                    .build();
        }

        return ResponseEntity.ok(user);
    }

    // Get all users
    @GetMapping
    public List<User> getAllUsers() {

        return userService.getAllUsers();
    }

    // Delete user
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(
            @PathVariable Long id) {

        boolean deleted =
                userService.deleteUser(id);

        if (!deleted) {
            return ResponseEntity
                    .badRequest()
                    .body("User cannot be deleted");
        }

        return ResponseEntity.ok(
                "User deleted successfully"
        );
    }
}