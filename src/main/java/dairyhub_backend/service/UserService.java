package dairyhub_backend.service;

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


    private final UserRepository userRepository;

    private final TokenVerifier googleTokenVerifier;


    // =========================================
    // CONSTRUCTOR
    // =========================================

    public UserService(
            UserRepository userRepository) {

        this.userRepository =
                userRepository;


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


        return PROTECTED_ADMIN_EMAIL.equalsIgnoreCase(
                user.getEmail()
        );
    }


    // =========================================
    // REGISTER CUSTOMER
    // =========================================

    public User registerUser(
            User user) {

        /*
         * Nobody can register directly as ADMIN.
         */

        user.setRole(
                "CUSTOMER"
        );


        user.setAdminManaged(
                false
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

        User user =
                userRepository
                        .findByEmail(email)
                        .orElse(null);


        if (user == null) {

            return null;
        }


        /*
         * Google users do not have a
         * normal password.
         */

        if (user.getPassword() == null) {

            return null;
        }


        if (!user.getPassword().equals(
                password
        )) {

            return null;
        }


        return user;
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


            if (token == null) {

                return null;
            }


            JsonWebSignature.Header header =
                    token.getHeader();


            if (header == null) {

                return null;
            }


            JsonWebSignature.Payload payload =
                    token.getPayload();


            if (payload == null) {

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


            if (emailObject == null) {

                return null;
            }


            String email =
                    emailObject
                            .toString()
                            .trim()
                            .toLowerCase();


            if (!email.endsWith(
                    "@gmail.com"
            )) {

                return null;
            }


            if (!Boolean.TRUE.equals(
                    emailVerifiedObject
            )) {

                return null;
            }


            String name =
                    nameObject == null
                            ? "DairyHub Customer"
                            : nameObject.toString();


            User existingUser =
                    userRepository
                            .findByEmail(email)
                            .orElse(null);


            if (existingUser != null) {

                return existingUser;
            }


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


        } catch (Exception e) {

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
    // UPDATE USER
    // =========================================

    public User updateUser(
            Long id,
            User updatedUser) {

        Optional<User> optionalUser =
                userRepository.findById(id);


        if (optionalUser.isEmpty()) {

            return null;
        }


        User user =
                optionalUser.get();


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
            // ORIGINAL PROTECTED ADMIN
            // ---------------------------------

            if (
                    isProtectedAdmin(user)
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
    // DELETE USER
    // =========================================

    public boolean deleteUser(
            Long id) {

        User user =
                userRepository
                        .findById(id)
                        .orElse(null);


        if (user == null) {

            return false;
        }


        /*
         * ONLY admin@dairyhub.com is protected.
         *
         * Other ADMIN accounts can be deleted.
         */

        if (
                isProtectedAdmin(user)
        ) {

            return false;
        }


        userRepository.deleteById(
                id
        );


        return true;
    }

}