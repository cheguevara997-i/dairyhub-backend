package dairyhub_backend.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dairyhub_backend.entity.User;
import dairyhub_backend.service.UserService;


@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "https://dairyhub-five.vercel.app"
})
public class UserController {


    private final UserService userService;


    // =========================================
    // CONSTRUCTOR
    // =========================================

    public UserController(
            UserService userService) {

        this.userService =
                userService;
    }


    // =========================================
    // REGISTER CUSTOMER
    // =========================================

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(
            @RequestBody User user) {

        System.out.println(
                "========================================="
        );

        System.out.println(
                "REGISTER REQUEST RECEIVED"
        );

        System.out.println(
                "Name: " +
                user.getName()
        );

        System.out.println(
                "Email: " +
                user.getEmail()
        );

        System.out.println(
                "Phone: " +
                user.getPhone()
        );

        /*
         * IMPORTANT:
         * Password is intentionally NOT printed.
         */

        System.out.println(
                "Role before service: " +
                user.getRole()
        );

        System.out.println(
                "========================================="
        );


        try {

            User savedUser =
                    userService.registerUser(
                            user
                    );


            System.out.println(
                    "REGISTRATION SUCCESS"
            );

            System.out.println(
                    "Saved User ID: " +
                    savedUser.getId()
            );

            System.out.println(
                    "Saved User Email: " +
                    savedUser.getEmail()
            );

            System.out.println(
                    "Saved User Role: " +
                    savedUser.getRole()
            );


            return ResponseEntity.ok(
                    savedUser
            );


        } catch (Exception e) {

            /*
             * Print the REAL backend exception
             * into Render Logs.
             */

            System.err.println(
                    "========================================="
            );

            System.err.println(
                    "REGISTRATION ERROR"
            );

            System.err.println(
                    "Exception Type: " +
                    e.getClass().getName()
            );

            System.err.println(
                    "Exception Message: " +
                    e.getMessage()
            );

            System.err.println(
                    "========================================="
            );

            e.printStackTrace();


            Map<String, Object> error =
                    new HashMap<>();

            error.put(
                    "success",
                    false
            );

            error.put(
                    "message",
                    "Registration failed on the backend."
            );

            error.put(
                    "errorType",
                    e.getClass().getSimpleName()
            );

            error.put(
                    "errorDetail",
                    e.getMessage()
            );


            return ResponseEntity
                    .status(
                            HttpStatus.INTERNAL_SERVER_ERROR
                    )
                    .body(error);
        }
    }


    // =========================================
    // NORMAL EMAIL + PASSWORD LOGIN
    // =========================================

    @PostMapping("/login")
    public ResponseEntity<User> loginUser(
            @RequestBody User loginRequest) {


        System.out.println(
                "LOGIN REQUEST: " +
                loginRequest.getEmail()
        );


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


        return ResponseEntity.ok(
                user
        );
    }


    // =========================================
    // GOOGLE LOGIN
    // =========================================

    @PostMapping("/google")
    public ResponseEntity<User> googleLogin(
            @RequestBody Map<String, String> request) {


        String credential =
                request.get("credential");


        if (
                credential == null ||
                credential.trim().isEmpty()
        ) {

            return ResponseEntity
                    .badRequest()
                    .build();
        }


        User user =
                userService.loginWithGoogle(
                        credential
                );


        if (user == null) {

            return ResponseEntity
                    .status(401)
                    .build();
        }


        return ResponseEntity.ok(
                user
        );
    }


    // =========================================
    // GET ALL USERS
    // =========================================

    @GetMapping
    public List<User> getAllUsers() {

        return userService.getAllUsers();
    }


    // =========================================
    // UPDATE USER
    // =========================================

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(
            @PathVariable Long id,
            @RequestBody User updatedUser) {


        User user =
                userService.updateUser(
                        id,
                        updatedUser
                );


        if (user == null) {

            return ResponseEntity
                    .notFound()
                    .build();
        }


        return ResponseEntity.ok(
                user
        );
    }


    // =========================================
    // DELETE USER
    // =========================================

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(
            @PathVariable Long id) {


        boolean deleted =
                userService.deleteUser(
                        id
                );


        if (!deleted) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            "User cannot be deleted"
                    );
        }


        return ResponseEntity.ok(
                "User deleted successfully"
        );
    }


    // =========================================
    // INVALID JSON / REQUEST BODY
    // =========================================

    @ExceptionHandler(
            HttpMessageNotReadableException.class
    )
    public ResponseEntity<Map<String, Object>>
    handleInvalidRequestBody(
            HttpMessageNotReadableException e) {


        System.err.println(
                "========================================="
        );

        System.err.println(
                "INVALID REQUEST BODY"
        );

        System.err.println(
                "Exception Type: " +
                e.getClass().getName()
        );

        System.err.println(
                "Exception Message: " +
                e.getMessage()
        );

        System.err.println(
                "========================================="
        );

        e.printStackTrace();


        Map<String, Object> error =
                new HashMap<>();

        error.put(
                "success",
                false
        );

        error.put(
                "message",
                "Invalid JSON request body."
        );

        error.put(
                "errorType",
                e.getClass().getSimpleName()
        );

        error.put(
                "errorDetail",
                e.getMessage()
        );


        return ResponseEntity
                .badRequest()
                .body(error);
    }


    // =========================================
    // GENERAL CONTROLLER ERROR
    // =========================================

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>>
    handleGeneralException(
            Exception e) {


        System.err.println(
                "========================================="
        );

        System.err.println(
                "CONTROLLER ERROR"
        );

        System.err.println(
                "Exception Type: " +
                e.getClass().getName()
        );

        System.err.println(
                "Exception Message: " +
                e.getMessage()
        );

        System.err.println(
                "========================================="
        );

        e.printStackTrace();


        Map<String, Object> error =
                new HashMap<>();

        error.put(
                "success",
                false
        );

        error.put(
                "message",
                "Backend request failed."
        );

        error.put(
                "errorType",
                e.getClass().getSimpleName()
        );

        error.put(
                "errorDetail",
                e.getMessage()
        );


        return ResponseEntity
                .status(
                        HttpStatus.INTERNAL_SERVER_ERROR
                )
                .body(error);
    }
}