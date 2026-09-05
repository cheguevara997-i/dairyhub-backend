package dairyhub_backend.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.google.api.client.json.webtoken.JsonWebSignature;
import com.google.auth.oauth2.TokenVerifier;

import dairyhub_backend.entity.User;
import dairyhub_backend.repository.UserRepository;

@Service
public class UserService {

    // =========================================
    // GOOGLE CONFIGURATION
    // =========================================

    private static final String GOOGLE_CLIENT_ID =
            "687009414509-5aft1ji4r2b9o8hfadsnfavn1nk6cs4c.apps.googleusercontent.com";

    private static final String GOOGLE_ISSUER =
            "https://accounts.google.com";


    // =========================================
    // ORIGINAL PROTECTED ADMIN
    // =========================================

    private static final String PROTECTED_ADMIN_EMAIL =
            "admin@dairyhub.com";


    // =========================================
    // DELETE BIN RETENTION
    // =========================================

    private static final long DELETE_BIN_DAYS =
            30;


    // =========================================
    // SERVICES
    // =========================================

    private final UserRepository userRepository;

    private final JwtService jwtService;

    private final TokenVerifier googleTokenVerifier;


    // =========================================
    // CONSTRUCTOR
    // =========================================

    public UserService(
            UserRepository userRepository,
            JwtService jwtService) {

        this.userRepository =
                userRepository;

        this.jwtService =
                jwtService;


        this.googleTokenVerifier =
                TokenVerifier
                        .newBuilder()
                        .setAudience(
                                GOOGLE_CLIENT_ID
                        )
                        .setIssuer(
                                GOOGLE_ISSUER
                        )
                        .build();
    }


    // =========================================
    // CHECK PROTECTED ADMIN
    // =========================================

    private boolean isProtectedAdmin(
            User user) {

        if (user == null) {

            return false;
        }


        return PROTECTED_ADMIN_EMAIL
                .equalsIgnoreCase(
                        user.getEmail()
                );
    }


    // =========================================
    // CHECK DELETED USER
    // =========================================

    private boolean isDeleted(
            User user) {

        return user != null
                && Boolean.TRUE.equals(
                        user.getDeleted()
                );
    }


    // =========================================
    // REGISTER CUSTOMER
    // =========================================

    public User registerUser(
            User user) {

        if (user == null) {

            throw new RuntimeException(
                    "User data is required."
            );
        }


        if (
                user.getEmail() == null ||
                user.getEmail()
                        .trim()
                        .isEmpty()
        ) {

            throw new RuntimeException(
                    "Email is required."
            );
        }


        String email =
                user.getEmail()
                        .trim()
                        .toLowerCase();


        // =====================================
        // CHECK EXISTING EMAIL
        // =====================================

        User existingUser =
                userRepository
                        .findByEmail(
                                email
                        )
                        .orElse(null);


        if (
                existingUser != null
        ) {

            // ---------------------------------
            // DELETED ACCOUNT
            // ---------------------------------

            if (
                    isDeleted(
                            existingUser
                    )
            ) {

                throw new RuntimeException(
                        "This email belongs to a deleted account. Please contact DairyHub support."
                );
            }


            // ---------------------------------
            // ACTIVE ACCOUNT
            // ---------------------------------

            throw new RuntimeException(
                    "An account with this email already exists."
            );
        }


        user.setEmail(
                email
        );


        // =====================================
        // FORCE CUSTOMER ROLE
        // =====================================

        user.setRole(
                "CUSTOMER"
        );


        user.setAdminManaged(
                false
        );


        // =====================================
        // NEW ACCOUNT IS ACTIVE
        // =====================================

        user.setDeleted(
                false
        );


        user.setDeletedAt(
                null
        );


        return userRepository.save(
                user
        );
    }


    // =========================================
    // NORMAL LOGIN
    // =========================================

    public User loginUser(
            String email,
            String password) {

        if (
                email == null ||
                password == null
        ) {

            return null;
        }


        String normalizedEmail =
                email
                        .trim()
                        .toLowerCase();


        User user =
                userRepository
                        .findByEmail(
                                normalizedEmail
                        )
                        .orElse(null);


        if (
                user == null
        ) {

            return null;
        }


        // =====================================
        // DELETED / LOCKED ACCOUNT
        // =====================================

        if (
                isDeleted(
                        user
                )
        ) {

            return null;
        }


        // =====================================
        // GOOGLE ACCOUNT
        // =====================================

        if (
                user.getPassword() == null
        ) {

            return null;
        }


        // =====================================
        // CHECK PASSWORD
        // =====================================

        if (
                !user.getPassword()
                        .equals(
                                password
                        )
        ) {

            return null;
        }


        return user;
    }


    // =========================================
    // GENERATE LOGIN TOKEN
    // =========================================

    public String generateLoginToken(
            User user) {

        if (
                user == null
        ) {

            return null;
        }


        /*
         * Never generate a token for an account
         * currently inside the Delete Bin.
         */

        if (
                Boolean.TRUE.equals(
                        user.getDeleted()
                )
        ) {

            return null;
        }


        return jwtService.generateToken(
                user.getId(),
                user.getEmail(),
                user.getRole()
        );
    }


    // =========================================
    // GOOGLE LOGIN
    // =========================================

    public User loginWithGoogle(
            String credential) {

        try {

            JsonWebSignature token =
                    googleTokenVerifier.verify(
                            credential
                    );


            if (
                    token == null
            ) {

                return null;
            }


            JsonWebSignature.Header header =
                    token.getHeader();


            if (
                    header == null
            ) {

                return null;
            }


            JsonWebSignature.Payload payload =
                    token.getPayload();


            if (
                    payload == null
            ) {

                return null;
            }


            Object emailObject =
                    payload.get(
                            "email"
                    );


            Object emailVerifiedObject =
                    payload.get(
                            "email_verified"
                    );


            Object nameObject =
                    payload.get(
                            "name"
                    );


            if (
                    emailObject == null
            ) {

                return null;
            }


            String email =
                    emailObject
                            .toString()
                            .trim()
                            .toLowerCase();


            if (
                    !email.endsWith(
                            "@gmail.com"
                    )
            ) {

                return null;
            }


            if (
                    !Boolean.TRUE.equals(
                            emailVerifiedObject
                    )
            ) {

                return null;
            }


            String name =
                    nameObject == null
                            ? "DairyHub Customer"
                            : nameObject.toString();


            User existingUser =
                    userRepository
                            .findByEmail(
                                    email
                            )
                            .orElse(null);


            if (
                    existingUser != null
            ) {

                /*
                 * Deleted Google accounts cannot
                 * login through Google.
                 */

                if (
                        isDeleted(
                                existingUser
                        )
                ) {

                    return null;
                }


                return existingUser;
            }


            // =================================
            // CREATE GOOGLE CUSTOMER
            // =================================

            User newUser =
                    new User();


            newUser.setName(
                    name
            );


            newUser.setEmail(
                    email
            );


            newUser.setPassword(
                    null
            );


            newUser.setPhone(
                    null
            );


            newUser.setRole(
                    "CUSTOMER"
            );


            newUser.setAdminManaged(
                    false
            );


            newUser.setDeleted(
                    false
            );


            newUser.setDeletedAt(
                    null
            );


            return userRepository.save(
                    newUser
            );


        } catch (
                TokenVerifier.VerificationException e
        ) {

            System.out.println(
                    "Google token verification failed: "
                            + e.getMessage()
            );


            return null;


        } catch (
                Exception e
        ) {

            System.out.println(
                    "Google login error: "
                            + e.getMessage()
            );


            return null;
        }
    }


    // =========================================
    // GET ALL USERS
    // =========================================

    public List<User> getAllUsers() {

        return userRepository.findAll();
    }


    // =========================================
    // GET ACTIVE USERS
    // =========================================

    public List<User> getActiveUsers() {

        return userRepository
                .findAll()
                .stream()
                .filter(
                        user ->
                                !isDeleted(
                                        user
                                )
                )
                .toList();
    }


    // =========================================
    // GET DELETED USERS
    // =========================================

    public List<User> getDeletedUsers() {

        return userRepository
                .findByDeletedTrue();
    }


    // =========================================
    // UPDATE USER
    // =========================================

    public User updateUser(
            Long id,
            User updatedUser) {

        Optional<User> optionalUser =
                userRepository.findById(
                        id
                );


        if (
                optionalUser.isEmpty()
        ) {

            return null;
        }


        User user =
                optionalUser.get();


        // =====================================
        // DELETED USERS CANNOT BE EDITED
        // =====================================

        if (
                isDeleted(
                        user
                )
        ) {

            return null;
        }


        // =====================================
        // UPDATE NAME
        // =====================================

        if (
                updatedUser.getName() != null
        ) {

            user.setName(
                    updatedUser.getName()
            );
        }


        // =====================================
        // UPDATE PHONE
        // =====================================

        user.setPhone(
                updatedUser.getPhone()
        );


        // =====================================
        // UPDATE ROLE
        // =====================================

        if (
                updatedUser.getRole() != null
        ) {

            String requestedRole =
                    updatedUser.getRole()
                            .trim()
                            .toUpperCase();


            // ---------------------------------
            // PROTECTED ADMIN
            // ---------------------------------

            if (
                    isProtectedAdmin(
                            user
                    )
            ) {

                user.setRole(
                        "ADMIN"
                );


                user.setAdminManaged(
                        false
                );
            }


            // ---------------------------------
            // OTHER USER → ADMIN
            // ---------------------------------

            else if (
                    "ADMIN".equals(
                            requestedRole
                    )
            ) {

                user.setRole(
                        "ADMIN"
                );


                user.setAdminManaged(
                        true
                );
            }


            // ---------------------------------
            // OTHER USER → CUSTOMER
            // ---------------------------------

            else {

                user.setRole(
                        "CUSTOMER"
                );


                user.setAdminManaged(
                        false
                );
            }
        }


        return userRepository.save(
                user
        );
    }


    // =========================================
    // MOVE USER TO DELETE BIN
    // =========================================

    public boolean deleteUser(
            Long id) {

        User user =
                userRepository
                        .findById(
                                id
                        )
                        .orElse(null);


        if (
                user == null
        ) {

            return false;
        }


        // =====================================
        // PROTECTED ADMIN
        // =====================================

        if (
                isProtectedAdmin(
                        user
                )
        ) {

            return false;
        }


        // =====================================
        // ALREADY DELETED
        // =====================================

        if (
                isDeleted(
                        user
                )
        ) {

            return false;
        }


        // =====================================
        // SOFT DELETE
        // =====================================

        user.setDeleted(
                true
        );


        user.setDeletedAt(
                LocalDateTime.now()
        );


        userRepository.save(
                user
        );


        return true;
    }


    // =========================================
    // RESTORE USER
    // =========================================

    public boolean restoreUser(
            Long id) {

        User user =
                userRepository
                        .findById(
                                id
                        )
                        .orElse(null);


        if (
                user == null
        ) {

            return false;
        }


        // =====================================
        // MUST BE DELETED
        // =====================================

        if (
                !isDeleted(
                        user
                )
        ) {

            return false;
        }


        user.setDeleted(
                false
        );


        user.setDeletedAt(
                null
        );


        userRepository.save(
                user
        );


        return true;
    }


    // =========================================
    // PERMANENT DELETE
    // =========================================

    public boolean permanentlyDeleteUser(
            Long id) {

        User user =
                userRepository
                        .findById(
                                id
                        )
                        .orElse(null);


        if (
                user == null
        ) {

            return false;
        }


        // =====================================
        // PROTECTED ADMIN
        // =====================================

        if (
                isProtectedAdmin(
                        user
                )
        ) {

            return false;
        }


        // =====================================
        // ONLY DELETE USERS IN BIN
        // =====================================

        if (
                !isDeleted(
                        user
                )
        ) {

            return false;
        }


        userRepository.delete(
                user
        );


        return true;
    }


    // =========================================
    // PERMANENT DELETE AFTER 30 DAYS
    // =========================================

    public int permanentlyDeleteExpiredUsers() {

        List<User> deletedUsers =
                userRepository
                        .findByDeletedTrue();


        if (
                deletedUsers.isEmpty()
        ) {

            return 0;
        }


        LocalDateTime expiryTime =
                LocalDateTime.now()
                        .minusDays(
                                DELETE_BIN_DAYS
                        );


        int deletedCount =
                0;


        for (
                User user :
                deletedUsers
        ) {

            // ---------------------------------
            // NEVER DELETE PROTECTED ADMIN
            // ---------------------------------

            if (
                    isProtectedAdmin(
                            user
                    )
            ) {

                continue;
            }


            LocalDateTime deletedAt =
                    user.getDeletedAt();


            /*
             * If deletedAt is missing,
             * leave the account untouched.
             */

            if (
                    deletedAt == null
            ) {

                continue;
            }


            // ---------------------------------
            // EXPIRED
            // ---------------------------------

            if (
                    deletedAt.isBefore(
                            expiryTime
                    )
                    ||
                    deletedAt.isEqual(
                            expiryTime
                    )
            ) {

                userRepository.delete(
                        user
                );


                deletedCount++;
            }
        }


        return deletedCount;
    }

}