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
    // PASSWORD RULE
    // =========================================

    /*
     * Minimum password length for a new password.
     *
     * This does not change your existing login
     * mechanism.
     */

    private static final int MIN_PASSWORD_LENGTH =
            8;


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

        if (
                user == null
        ) {

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

        if (
                user == null
        ) {

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


            newUser.setGender(
                    null
            );


            newUser.setProfilePhoto(
                    null
            );


            newUser.setAddress(
                    null
            );


            newUser.setCity(
                    null
            );


            newUser.setState(
                    null
            );


            newUser.setPincode(
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
    // GET CURRENT LOGGED-IN USER
    // =========================================

    public User getCurrentUser(
            Long userId) {

        if (
                userId == null
        ) {

            return null;
        }


        User user =
                userRepository
                        .findById(
                                userId
                        )
                        .orElse(null);


        if (
                user == null
        ) {

            return null;
        }


        // =====================================
        // DELETED USER CANNOT ACCESS PROFILE
        // =====================================

        if (
                isDeleted(
                        user
                )
        ) {

            return null;
        }


        return user;
    }


    // =========================================
    // UPDATE CURRENT USER PROFILE
    // =========================================

    public User updateCurrentUser(
            Long userId,
            User updatedUser) {

        if (
                userId == null ||
                updatedUser == null
        ) {

            return null;
        }


        User user =
                userRepository
                        .findById(
                                userId
                        )
                        .orElse(null);


        if (
                user == null
        ) {

            return null;
        }


        // =====================================
        // DELETED USER CANNOT UPDATE PROFILE
        // =====================================

        if (
                isDeleted(
                        user
                )
        ) {

            return null;
        }


        // =====================================
        // NAME
        // =====================================

        if (
                updatedUser.getName() != null
        ) {

            String name =
                    updatedUser.getName()
                            .trim();


            if (
                    !name.isEmpty()
            ) {

                user.setName(
                        name
                );
            }
        }


        // =====================================
        // GENDER
        // =====================================

        if (
                updatedUser.getGender() != null
        ) {

            String gender =
                    updatedUser.getGender()
                            .trim();


            if (
                    gender.isEmpty()
            ) {

                user.setGender(
                        null
                );

            } else {

                user.setGender(
                        gender
                );
            }
        }


        // =====================================
        // PHONE
        // =====================================

        if (
                updatedUser.getPhone() != null
        ) {

            String phone =
                    updatedUser.getPhone()
                            .trim();


            if (
                    phone.isEmpty()
            ) {

                user.setPhone(
                        null
                );

            } else {

                user.setPhone(
                        phone
                );
            }
        }


        // =====================================
        // PROFILE PHOTO
        // =====================================

        if (
                updatedUser.getProfilePhoto() != null
        ) {

            String profilePhoto =
                    updatedUser
                            .getProfilePhoto()
                            .trim();


            if (
                    profilePhoto.isEmpty()
            ) {

                user.setProfilePhoto(
                        null
                );

            } else {

                user.setProfilePhoto(
                        profilePhoto
                );
            }
        }


        // =====================================
        // ADDRESS
        // =====================================

        if (
                updatedUser.getAddress() != null
        ) {

            String address =
                    updatedUser
                            .getAddress()
                            .trim();


            if (
                    address.isEmpty()
            ) {

                user.setAddress(
                        null
                );

            } else {

                user.setAddress(
                        address
                );
            }
        }


        // =====================================
        // CITY
        // =====================================

        if (
                updatedUser.getCity() != null
        ) {

            String city =
                    updatedUser
                            .getCity()
                            .trim();


            if (
                    city.isEmpty()
            ) {

                user.setCity(
                        null
                );

            } else {

                user.setCity(
                        city
                );
            }
        }


        // =====================================
        // STATE
        // =====================================

        if (
                updatedUser.getState() != null
        ) {

            String state =
                    updatedUser
                            .getState()
                            .trim();


            if (
                    state.isEmpty()
            ) {

                user.setState(
                        null
                );

            } else {

                user.setState(
                        state
                );
            }
        }


        // =====================================
        // PINCODE
        // =====================================

        if (
                updatedUser.getPincode() != null
        ) {

            String pincode =
                    updatedUser
                            .getPincode()
                            .trim();


            if (
                    pincode.isEmpty()
            ) {

                user.setPincode(
                        null
                );

            } else {

                if (
                        !pincode.matches(
                                "\\d{6}"
                        )
                ) {

                    throw new RuntimeException(
                            "Pincode must contain exactly 6 digits."
                    );
                }


                user.setPincode(
                        pincode
                );
            }
        }


        /*
         * =====================================
         * PROTECTED FIELDS
         * =====================================
         *
         * Profile update does not modify:
         *
         * email
         * password
         * role
         * adminManaged
         * deleted
         * deletedAt
         */

        return userRepository.save(
                user
        );
    }


    // =========================================
    // CHANGE PASSWORD
    // =========================================

    /*
     * CUSTOMER + ADMIN
     *
     * Used by:
     *
     * PUT /api/users/me/password
     *
     * Existing Google accounts have no password.
     * They cannot use this endpoint until a
     * password is established through a separate
     * account-password flow.
     */

    public boolean changePassword(
            Long userId,
            String currentPassword,
            String newPassword) {

        if (
                userId == null ||
                currentPassword == null ||
                newPassword == null
        ) {

            return false;
        }


        User user =
                userRepository
                        .findById(
                                userId
                        )
                        .orElse(null);


        if (
                user == null
        ) {

            return false;
        }


        // =====================================
        // DELETED ACCOUNT
        // =====================================

        if (
                isDeleted(
                        user
                )
        ) {

            return false;
        }


        // =====================================
        // GOOGLE ACCOUNT
        // =====================================

        if (
                user.getPassword() == null
        ) {

            return false;
        }


        // =====================================
        // CHECK CURRENT PASSWORD
        // =====================================

        if (
                !user.getPassword()
                        .equals(
                                currentPassword
                        )
        ) {

            return false;
        }


        String trimmedPassword =
                newPassword.trim();


        // =====================================
        // PASSWORD LENGTH
        // =====================================

        if (
                trimmedPassword.length()
                        < MIN_PASSWORD_LENGTH
        ) {

            throw new RuntimeException(
                    "New password must contain at least "
                            + MIN_PASSWORD_LENGTH
                            + " characters."
            );
        }


        // =====================================
        // NEW PASSWORD MUST BE DIFFERENT
        // =====================================

        if (
                user.getPassword()
                        .equals(
                                trimmedPassword
                        )
        ) {

            throw new RuntimeException(
                    "New password must be different from your current password."
            );
        }


        // =====================================
        // SAVE NEW PASSWORD
        // =====================================

        /*
         * Your existing authentication system
         * currently stores and compares passwords
         * directly, so this keeps the same behavior.
         */

        user.setPassword(
                trimmedPassword
        );


        userRepository.save(
                user
        );


        return true;
    }


    // =========================================
    // DELETE CURRENT USER ACCOUNT
    // =========================================

    /*
     * CUSTOMER + protected-account protection
     *
     * This uses the existing Delete Bin system.
     */

    public boolean deleteMyAccount(
            Long userId) {

        if (
                userId == null
        ) {

            return false;
        }


        User user =
                userRepository
                        .findById(
                                userId
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
        // MOVE TO DELETE BIN
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
    // UPDATE USER
    // =========================================

    /*
     * ADMIN ONLY
     */

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


        if (
                isDeleted(
                        user
                )
        ) {

            return null;
        }


        // =====================================
        // NAME
        // =====================================

        if (
                updatedUser.getName() != null
        ) {

            user.setName(
                    updatedUser.getName()
            );
        }


        // =====================================
        // PHONE
        // =====================================

        user.setPhone(
                updatedUser.getPhone()
        );


        // =====================================
        // GENDER
        // =====================================

        if (
                updatedUser.getGender() != null
        ) {

            user.setGender(
                    updatedUser.getGender()
            );
        }


        // =====================================
        // PROFILE PHOTO
        // =====================================

        if (
                updatedUser.getProfilePhoto() != null
        ) {

            user.setProfilePhoto(
                    updatedUser.getProfilePhoto()
            );
        }


        // =====================================
        // ADDRESS
        // =====================================

        if (
                updatedUser.getAddress() != null
        ) {

            user.setAddress(
                    updatedUser.getAddress()
            );
        }


        // =====================================
        // CITY
        // =====================================

        if (
                updatedUser.getCity() != null
        ) {

            user.setCity(
                    updatedUser.getCity()
            );
        }


        // =====================================
        // STATE
        // =====================================

        if (
                updatedUser.getState() != null
        ) {

            user.setState(
                    updatedUser.getState()
            );
        }


        // =====================================
        // PINCODE
        // =====================================

        if (
                updatedUser.getPincode() != null
        ) {

            user.setPincode(
                    updatedUser.getPincode()
            );
        }


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
             * leave account untouched.
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