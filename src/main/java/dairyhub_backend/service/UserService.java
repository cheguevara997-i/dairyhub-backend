package dairyhub_backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.google.api.client.json.webtoken.JsonWebSignature;
import com.google.auth.oauth2.TokenVerifier;

import dairyhub_backend.entity.User;
import dairyhub_backend.repository.UserRepository;

@Service
public class UserService {

    private static final String GOOGLE_CLIENT_ID =
            "687009414509-5aft1ji4r2b9o8hfadsnfavn1nk6cs4c.apps.googleusercontent.com";

    private static final String GOOGLE_ISSUER =
            "https://accounts.google.com";

    private final UserRepository userRepository;

    private final TokenVerifier googleTokenVerifier;

    public UserService(UserRepository userRepository) {

        this.userRepository = userRepository;

        this.googleTokenVerifier =
                TokenVerifier
                        .newBuilder()
                        .setAudience(GOOGLE_CLIENT_ID)
                        .setIssuer(GOOGLE_ISSUER)
                        .build();
    }

    // =========================================
    // REGISTER CUSTOMER
    // =========================================

    public User registerUser(User user) {

        user.setRole("CUSTOMER");

        return userRepository.save(user);
    }

    // =========================================
    // NORMAL EMAIL + PASSWORD LOGIN
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
         * Google-created users do not have a normal password.
         */
        if (user.getPassword() == null) {
            return null;
        }

        if (!user.getPassword().equals(password)) {
            return null;
        }

        return user;
    }

    // =========================================
    // GOOGLE LOGIN
    // =========================================

    public User loginWithGoogle(String credential) {

        try {

            JsonWebSignature token =
                    googleTokenVerifier.verify(credential);

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
                    payload.get("email");

            Object emailVerifiedObject =
                    payload.get("email_verified");

            Object nameObject =
                    payload.get("name");

            if (emailObject == null) {
                return null;
            }

            String email =
                    emailObject.toString()
                            .trim()
                            .toLowerCase();

            /*
             * Must be a real Gmail address.
             * Google token itself is verified above.
             */
            if (!email.endsWith("@gmail.com")) {
                return null;
            }

            /*
             * Google must confirm that the email is verified.
             */
            if (!Boolean.TRUE.equals(
                    emailVerifiedObject)) {

                return null;
            }

            String name =
                    nameObject == null
                            ? "DairyHub Customer"
                            : nameObject.toString();

            /*
             * Check whether this Gmail already exists.
             */
            User existingUser =
                    userRepository
                            .findByEmail(email)
                            .orElse(null);

            if (existingUser != null) {

                return existingUser;
            }

            /*
             * Create a new DairyHub customer.
             */
            User newUser =
                    new User();

            newUser.setName(name);
            newUser.setEmail(email);

            /*
             * Google users do not use the normal
             * email/password login.
             */
            newUser.setPassword(null);

            newUser.setPhone(null);
            newUser.setRole("CUSTOMER");

            return userRepository.save(newUser);

        } catch (TokenVerifier.VerificationException e) {

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
    // DELETE USER
    // =========================================

    public boolean deleteUser(Long id) {

        User user =
                userRepository
                        .findById(id)
                        .orElse(null);

        if (user == null) {
            return false;
        }

        /*
         * Do not allow Admin account deletion.
         */
        if ("ADMIN".equalsIgnoreCase(
                user.getRole())) {

            return false;
        }

        userRepository.deleteById(id);

        return true;
    }
}