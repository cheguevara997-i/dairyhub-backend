package dairyhub_backend.controller;

import java.util.HashMap;
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
     * because it contains the password.
     */

    private Map<String, Object> createSafeUserResponse(
            User user,
            String token) {

        Map<String, Object> response =
                new HashMap<>();


        // =====================================
        // BASIC INFORMATION
        // =====================================

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
                "gender",
                user.getGender()
        );


        // =====================================
        // PROFILE PHOTO
        // =====================================

        response.put(
                "profilePhoto",
                user.getProfilePhoto()
        );


        // =====================================
        // DELIVERY ADDRESS
        // =====================================

        response.put(
                "address",
                user.getAddress()
        );


        response.put(
                "city",
                user.getCity()
        );


        response.put(
                "state",
                user.getState()
        );


        response.put(
                "pincode",
                user.getPincode()
        );


        // =====================================
        // ROLE
        // =====================================

        response.put(
                "role",
                user.getRole()
        );


        // =====================================
        // ADMIN MANAGEMENT
        // =====================================

        response.put(
                "adminManaged",
                user.getAdminManaged()
        );


        // =====================================
        // DELETE STATUS
        // =====================================

        response.put(
                "deleted",
                user.getDeleted()
        );


        // =====================================
        // JWT TOKEN
        // =====================================

        /*
         * Token is included only when required,
         * mainly after login / Google login.
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
    // GET AUTHENTICATED USER ID
    // =========================================

    private Long getAuthenticatedUserId(
            String authorizationHeader) {

        String token =
                extractToken(
                        authorizationHeader
                );


        if (
                token == null
        ) {

            return null;
        }


        if (
                !jwtService.isValidToken(
                        token
                )
        ) {

            return null;
        }


        return jwtService.getUserId(
                token
        );
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


        if (
                !jwtService.isValidToken(
                        token
                )
        ) {

            return false;
        }


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

        System.out.println(
                "Gender: " +
                user.getGender()
        );

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
    // NORMAL LOGIN
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


        // =====================================
        // GENERATE JWT
        // =====================================

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


        // =====================================
        // SAFE RESPONSE
        // =====================================

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


        // =====================================
        // GENERATE JWT
        // =====================================

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
    // GET CURRENT USER PROFILE
    // =========================================

    /*
     * CUSTOMER + ADMIN
     *
     * GET /api/users/me
     */

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(
            @RequestHeader(
                    value = "Authorization",
                    required = false
            )
            String authorizationHeader) {


        Long userId =
                getAuthenticatedUserId(
                        authorizationHeader
                );


        if (
                userId == null
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
                                    "Authentication required."
                            )
                    );
        }


        User user =
                userService.getCurrentUser(
                        userId
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
                                    "User account is unavailable or locked."
                            )
                    );
        }


        return ResponseEntity.ok(
                createSafeUserResponse(
                        user,
                        null
                )
        );
    }


    // =========================================
    // UPDATE CURRENT USER PROFILE
    // =========================================

    /*
     * CUSTOMER + ADMIN
     *
     * PUT /api/users/me
     *
     * Editable profile fields:
     *
     * name
     * phone
     * gender
     * profilePhoto
     * address
     * city
     * state
     * pincode
     *
     * Protected fields:
     *
     * email
     * password
     * role
     * adminManaged
     * deleted
     * deletedAt
     */

    @PutMapping("/me")
    public ResponseEntity<?> updateCurrentUser(
            @RequestBody User updatedUser,
            @RequestHeader(
                    value = "Authorization",
                    required = false
            )
            String authorizationHeader) {


        Long userId =
                getAuthenticatedUserId(
                        authorizationHeader
                );


        if (
                userId == null
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
                                    "Authentication required."
                            )
                    );
        }


        try {

            User user =
                    userService.updateCurrentUser(
                            userId,
                            updatedUser
                    );


            if (
                    user == null
            ) {

                return ResponseEntity
                        .status(
                                HttpStatus.NOT_FOUND
                        )
                        .body(
                                Map.of(
                                        "success",
                                        false,

                                        "message",
                                        "Unable to update profile."
                                )
                        );
            }


            return ResponseEntity.ok(
                    createSafeUserResponse(
                            user,
                            null
                    )
            );


        } catch (Exception e) {

            e.printStackTrace();


            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "success",
                                    false,

                                    "message",
                                    e.getMessage() != null
                                            ? e.getMessage()
                                            : "Unable to update profile."
                            )
                    );
        }
    }


    // =========================================
    // CHANGE PASSWORD
    // =========================================

    /*
     * CUSTOMER + ADMIN
     *
     * PUT /api/users/me/password
     */

    @PutMapping("/me/password")
    public ResponseEntity<?> changePassword(
            @RequestBody Map<String, String> request,
            @RequestHeader(
                    value = "Authorization",
                    required = false
            )
            String authorizationHeader) {


        Long userId =
                getAuthenticatedUserId(
                        authorizationHeader
                );


        if (
                userId == null
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
                                    "Authentication required."
                            )
                    );
        }


        String currentPassword =
                request.get(
                        "currentPassword"
                );


        String newPassword =
                request.get(
                        "newPassword"
                );


        if (
                currentPassword == null ||
                currentPassword.isBlank() ||
                newPassword == null ||
                newPassword.isBlank()
        ) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "success",
                                    false,

                                    "message",
                                    "Current password and new password are required."
                            )
                    );
        }


        try {

            boolean changed =
                    userService.changePassword(
                            userId,
                            currentPassword,
                            newPassword
                    );


            if (
                    !changed
            ) {

                return ResponseEntity
                        .badRequest()
                        .body(
                                Map.of(
                                        "success",
                                        false,

                                        "message",
                                        "Current password is incorrect or the password could not be changed."
                                )
                        );
            }


            return ResponseEntity.ok(
                    Map.of(
                            "success",
                            true,

                            "message",
                            "Password changed successfully."
                    )
            );


        } catch (Exception e) {

            e.printStackTrace();


            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "success",
                                    false,

                                    "message",
                                    e.getMessage() != null
                                            ? e.getMessage()
                                            : "Unable to change password."
                            )
                    );
        }
    }


    // =========================================
    // DELETE MY ACCOUNT
    // =========================================

    /*
     * CUSTOMER ONLY / ADMIN ACCOUNT PROTECTED
     *
     * DELETE /api/users/me
     *
     * Moves the current account into the
     * existing Delete Bin system.
     */

    @DeleteMapping("/me")
    public ResponseEntity<?> deleteMyAccount(
            @RequestHeader(
                    value = "Authorization",
                    required = false
            )
            String authorizationHeader) {


        Long userId =
                getAuthenticatedUserId(
                        authorizationHeader
                );


        if (
                userId == null
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
                                    "Authentication required."
                            )
                    );
        }


        try {

            boolean deleted =
                    userService.deleteMyAccount(
                            userId
                    );


            if (
                    !deleted
            ) {

                return ResponseEntity
                        .badRequest()
                        .body(
                                Map.of(
                                        "success",
                                        false,

                                        "message",
                                        "This account cannot be deleted."
                                )
                        );
            }


            return ResponseEntity.ok(
                    Map.of(
                            "success",
                            true,

                            "message",
                            "Your account has been moved to the Delete Bin."
                    )
            );


        } catch (Exception e) {

            e.printStackTrace();


            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "success",
                                    false,

                                    "message",
                                    e.getMessage() != null
                                            ? e.getMessage()
                                            : "Unable to delete your account."
                            )
                    );
        }
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
    // INVALID JSON
    // =========================================

    @ExceptionHandler(
            HttpMessageNotReadableException.class
    )
    public ResponseEntity<Map<String, Object>>
    handleInvalidRequestBody(
            HttpMessageNotReadableException e) {

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