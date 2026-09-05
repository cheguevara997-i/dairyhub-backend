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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dairyhub_backend.entity.User;
import dairyhub_backend.service.JwtService;
import dairyhub_backend.service.UserService;


@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "https://dairyhub-five.vercel.app"
})
public class UserController {


    private final UserService userService;

    private final JwtService jwtService;


    // =========================================
    // CONSTRUCTOR
    // =========================================

    public UserController(
            UserService userService,
            JwtService jwtService) {

        this.userService =
                userService;

        this.jwtService =
                jwtService;
    }


    // =========================================
    // SAFE USER RESPONSE
    // =========================================

    /*
     * IMPORTANT:
     *
     * Never return the User entity directly
     * from login/register APIs because the User
     * entity contains the password field.
     *
     * This method creates a response containing
     * only information the frontend actually needs.
     */

    private Map<String, Object> createSafeUserResponse(
            User user,
            String token) {

        Map<String, Object> response =
                new HashMap<>();


        response.put(
                "id",
                user.getId()
        );


        response.put(
                "name",
                user.getName()
        );


        response.put(
                "email",
                user.getEmail()
        );


        response.put(
                "phone",
                user.getPhone()
        );


        response.put(
                "role",
                user.getRole()
        );


        response.put(
                "adminManaged",
                user.getAdminManaged()
        );


        response.put(
                "deleted",
                user.getDeleted()
        );


        /*
         * Token is included for authenticated
         * frontend requests.
         */

        if (
                token != null &&
                !token.trim().isEmpty()
        ) {

            response.put(
                    "token",
                    token
            );
        }


        return response;
    }


    // =========================================
    // EXTRACT BEARER TOKEN
    // =========================================

    private String extractToken(
            String authorizationHeader) {

        if (
                authorizationHeader == null ||
                authorizationHeader.trim().isEmpty()
        ) {

            return null;
        }


        if (
                !authorizationHeader
                        .startsWith("Bearer ")
        ) {

            return null;
        }


        String token =
                authorizationHeader
                        .substring(7)
                        .trim();


        if (
                token.isEmpty()
        ) {

            return null;
        }


        return token;
    }


    // =========================================
    // CHECK ADMIN AUTHORIZATION
    // =========================================

    private boolean isAuthorizedAdmin(
            String authorizationHeader) {

        String token =
                extractToken(
                        authorizationHeader
                );


        if (
                token == null
        ) {

            return false;
        }


        /*
         * First verify that the token itself
         * is valid.
         */

        if (
                !jwtService.isValidToken(
                        token
                )
        ) {

            return false;
        }


        /*
         * Then verify that the authenticated
         * user has ADMIN role.
         */

        return jwtService.isAdmin(
                token
        );
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


            /*
             * Do not send the password back.
             *
             * Registration does not need to
             * automatically log the user in,
             * so no JWT token is generated here.
             */

            return ResponseEntity.ok(
                    createSafeUserResponse(
                            savedUser,
                            null
                    )
            );


        } catch (Exception e) {

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
                    e.getMessage() != null
                            ? e.getMessage()
                            : "Registration failed on the backend."
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
                            HttpStatus.BAD_REQUEST
                    )
                    .body(error);
        }
    }


    // =========================================
    // NORMAL EMAIL + PASSWORD LOGIN
    // =========================================

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(
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


        if (
                user == null
        ) {

            return ResponseEntity
                    .status(
                            HttpStatus.UNAUTHORIZED
                    )
                    .body(
                            Map.of(
                                    "success",
                                    false,

                                    "message",
                                    "Invalid email or password, or the account is locked."
                            )
                    );
        }


        /*
         * Generate JWT after successful login.
         */

        String token =
                userService.generateLoginToken(
                        user
                );


        if (
                token == null
        ) {

            return ResponseEntity
                    .status(
                            HttpStatus.UNAUTHORIZED
                    )
                    .body(
                            Map.of(
                                    "success",
                                    false,

                                    "message",
                                    "Unable to create authentication token."
                            )
                    );
        }


        /*
         * Return safe user information.
         *
         * Password is NOT included.
         */

        return ResponseEntity.ok(
                createSafeUserResponse(
                        user,
                        token
                )
        );
    }


    // =========================================
    // GOOGLE LOGIN
    // =========================================

    @PostMapping("/google")
    public ResponseEntity<?> googleLogin(
            @RequestBody Map<String, String> request) {

        String credential =
                request.get("credential");


        if (
                credential == null ||
                credential.trim().isEmpty()
        ) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "success",
                                    false,

                                    "message",
                                    "Google credential is required."
                            )
                    );
        }


        User user =
                userService.loginWithGoogle(
                        credential
                );


        if (
                user == null
        ) {

            return ResponseEntity
                    .status(
                            HttpStatus.UNAUTHORIZED
                    )
                    .body(
                            Map.of(
                                    "success",
                                    false,

                                    "message",
                                    "Google login failed or the account is locked."
                            )
                    );
        }


        /*
         * Generate JWT for Google login too.
         */

        String token =
                userService.generateLoginToken(
                        user
                );


        if (
                token == null
        ) {

            return ResponseEntity
                    .status(
                            HttpStatus.UNAUTHORIZED
                    )
                    .body(
                            Map.of(
                                    "success",
                                    false,

                                    "message",
                                    "Unable to create authentication token."
                            )
                    );
        }


        return ResponseEntity.ok(
                createSafeUserResponse(
                        user,
                        token
                )
        );
    }


    // =========================================
    // GET ALL USERS
    // =========================================

    /*
     * ADMIN ONLY
     */

    @GetMapping
    public ResponseEntity<?> getAllUsers(
            @RequestHeader(
                    value = "Authorization",
                    required = false
            )
            String authorizationHeader) {


        if (
                !isAuthorizedAdmin(
                        authorizationHeader
                )
        ) {

            return ResponseEntity
                    .status(
                            HttpStatus.FORBIDDEN
                    )
                    .body(
                            Map.of(
                                    "success",
                                    false,

                                    "message",
                                    "Admin authorization required."
                            )
                    );
        }


        return ResponseEntity.ok(
                userService.getAllUsers()
        );
    }


    // =========================================
    // GET ACTIVE USERS
    // =========================================

    /*
     * ADMIN ONLY
     */

    @GetMapping("/active")
    public ResponseEntity<?> getActiveUsers(
            @RequestHeader(
                    value = "Authorization",
                    required = false
            )
            String authorizationHeader) {


        if (
                !isAuthorizedAdmin(
                        authorizationHeader
                )
        ) {

            return ResponseEntity
                    .status(
                            HttpStatus.FORBIDDEN
                    )
                    .body(
                            Map.of(
                                    "success",
                                    false,

                                    "message",
                                    "Admin authorization required."
                            )
                    );
        }


        return ResponseEntity.ok(
                userService.getActiveUsers()
        );
    }


    // =========================================
    // GET DELETED USERS
    // =========================================

    /*
     * ADMIN ONLY
     */

    @GetMapping("/deleted")
    public ResponseEntity<?> getDeletedUsers(
            @RequestHeader(
                    value = "Authorization",
                    required = false
            )
            String authorizationHeader) {


        if (
                !isAuthorizedAdmin(
                        authorizationHeader
                )
        ) {

            return ResponseEntity
                    .status(
                            HttpStatus.FORBIDDEN
                    )
                    .body(
                            Map.of(
                                    "success",
                                    false,

                                    "message",
                                    "Admin authorization required."
                            )
                    );
        }


        return ResponseEntity.ok(
                userService.getDeletedUsers()
        );
    }


    // =========================================
    // UPDATE USER
    // =========================================

    /*
     * ADMIN ONLY
     */

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable Long id,
            @RequestBody User updatedUser,
            @RequestHeader(
                    value = "Authorization",
                    required = false
            )
            String authorizationHeader) {


        if (
                !isAuthorizedAdmin(
                        authorizationHeader
                )
        ) {

            return ResponseEntity
                    .status(
                            HttpStatus.FORBIDDEN
                    )
                    .body(
                            Map.of(
                                    "success",
                                    false,

                                    "message",
                                    "Admin authorization required."
                            )
                    );
        }


        User user =
                userService.updateUser(
                        id,
                        updatedUser
                );


        if (
                user == null
        ) {

            return ResponseEntity
                    .notFound()
                    .build();
        }


        /*
         * Do not return password.
         */

        return ResponseEntity.ok(
                createSafeUserResponse(
                        user,
                        null
                )
        );
    }


    // =========================================
    // MOVE USER TO DELETE BIN
    // =========================================

    /*
     * ADMIN ONLY
     *
     * DELETE /api/users/{id}
     *
     * This is now a SOFT DELETE.
     */

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(
            @PathVariable Long id,
            @RequestHeader(
                    value = "Authorization",
                    required = false
            )
            String authorizationHeader) {


        if (
                !isAuthorizedAdmin(
                        authorizationHeader
                )
        ) {

            return ResponseEntity
                    .status(
                            HttpStatus.FORBIDDEN
                    )
                    .body(
                            "Admin authorization required."
                    );
        }


        boolean deleted =
                userService.deleteUser(
                        id
                );


        if (
                !deleted
        ) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            "User cannot be deleted or is already in the Delete Bin."
                    );
        }


        return ResponseEntity.ok(
                "User moved to Delete Bin successfully. Account is locked."
        );
    }


    // =========================================
    // RESTORE USER
    // =========================================

    /*
     * ADMIN ONLY
     */

    @PostMapping("/{id}/restore")
    public ResponseEntity<String> restoreUser(
            @PathVariable Long id,
            @RequestHeader(
                    value = "Authorization",
                    required = false
            )
            String authorizationHeader) {


        if (
                !isAuthorizedAdmin(
                        authorizationHeader
                )
        ) {

            return ResponseEntity
                    .status(
                            HttpStatus.FORBIDDEN
                    )
                    .body(
                            "Admin authorization required."
                    );
        }


        boolean restored =
                userService.restoreUser(
                        id
                );


        if (
                !restored
        ) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            "User cannot be restored."
                    );
        }


        return ResponseEntity.ok(
                "User restored successfully."
        );
    }


    // =========================================
    // PERMANENT DELETE
    // =========================================

    /*
     * ADMIN ONLY
     */

    @DeleteMapping("/{id}/permanent")
    public ResponseEntity<String> permanentlyDeleteUser(
            @PathVariable Long id,
            @RequestHeader(
                    value = "Authorization",
                    required = false
            )
            String authorizationHeader) {


        if (
                !isAuthorizedAdmin(
                        authorizationHeader
                )
        ) {

            return ResponseEntity
                    .status(
                            HttpStatus.FORBIDDEN
                    )
                    .body(
                            "Admin authorization required."
                    );
        }


        boolean deleted =
                userService.permanentlyDeleteUser(
                        id
                );


        if (
                !deleted
        ) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            "User cannot be permanently deleted."
                    );
        }


        return ResponseEntity.ok(
                "User permanently deleted."
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

    @ExceptionHandler(
            Exception.class
    )
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