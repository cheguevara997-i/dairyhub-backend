package dairyhub_backend.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;


@Service
public class JwtService {


    // =========================================
    // JWT SECRET
    // =========================================

    /*
     * The JWT secret is read from the environment
     * variable:
     *
     * DAIRYHUB_JWT_SECRET
     *
     * This keeps the secret out of GitHub/source code.
     */

    private static final String SECRET =
            System.getenv("DAIRYHUB_JWT_SECRET");


    // =========================================
    // TOKEN VALIDITY
    // =========================================

    /*
     * Token validity:
     *
     * 24 hours
     */

    private static final long TOKEN_EXPIRATION =
            24 * 60 * 60 * 1000L;


    // =========================================
    // CREATE SIGNING KEY
    // =========================================

    private SecretKey getSigningKey() {

        /*
         * Make sure the environment variable
         * exists before creating the key.
         */

        if (
                SECRET == null ||
                SECRET.trim().isEmpty()
        ) {

            throw new IllegalStateException(
                    "DAIRYHUB_JWT_SECRET environment variable is not configured."
            );

        }


        try {

            /*
             * Convert the secret text into a SHA-256
             * byte array.
             */

            byte[] keyBytes =
                    MessageDigest
                            .getInstance("SHA-256")
                            .digest(
                                    SECRET.getBytes(
                                            StandardCharsets.UTF_8
                                    )
                            );


            /*
             * Create a secure HMAC signing key.
             */

            return Keys.hmacShaKeyFor(
                    keyBytes
            );


        } catch (Exception e) {

            /*
             * If the signing key cannot be created,
             * stop the authentication process.
             */

            throw new RuntimeException(
                    "Unable to create JWT signing key.",
                    e
            );

        }

    }


    // =========================================
    // GENERATE TOKEN
    // =========================================

    public String generateToken(
            Long userId,
            String email,
            String role) {


        Date now =
                new Date();


        Date expiration =
                new Date(
                        now.getTime()
                                + TOKEN_EXPIRATION
                );


        /*
         * Create JWT token.
         *
         * subject = user ID
         *
         * email   = user's email
         *
         * role    = ADMIN / CUSTOMER
         */

        return Jwts.builder()


                // -----------------------------
                // USER ID
                // -----------------------------

                .subject(
                        String.valueOf(
                                userId
                        )
                )


                // -----------------------------
                // EMAIL
                // -----------------------------

                .claim(
                        "email",
                        email
                )


                // -----------------------------
                // ROLE
                // -----------------------------

                .claim(
                        "role",
                        role
                )


                // -----------------------------
                // TOKEN CREATED TIME
                // -----------------------------

                .issuedAt(
                        now
                )


                // -----------------------------
                // TOKEN EXPIRATION
                // -----------------------------

                .expiration(
                        expiration
                )


                // -----------------------------
                // SIGN TOKEN
                // -----------------------------

                .signWith(
                        getSigningKey()
                )


                // -----------------------------
                // BUILD TOKEN
                // -----------------------------

                .compact();

    }


    // =========================================
    // VALIDATE TOKEN
    // =========================================

    public boolean isValidToken(
            String token) {


        /*
         * Make sure a token was actually supplied.
         */

        if (
                token == null ||
                token.trim().isEmpty()
        ) {

            return false;

        }


        try {

            /*
             * Parse and verify the token.
             *
             * This checks:
             *
             * 1. Signature
             * 2. Token format
             * 3. Expiration
             */

            Jwts.parser()


                    .verifyWith(
                            getSigningKey()
                    )


                    .build()


                    .parseSignedClaims(
                            token
                    );


            return true;


        } catch (
                Exception e
        ) {

            /*
             * Invalid, expired, or tampered token.
             */

            return false;

        }

    }


    // =========================================
    // GET TOKEN CLAIMS
    // =========================================

    public Claims getClaims(
            String token) {


        if (
                token == null ||
                token.trim().isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "JWT token is required."
            );

        }


        return Jwts.parser()


                .verifyWith(
                        getSigningKey()
                )


                .build()


                .parseSignedClaims(
                        token
                )


                .getPayload();

    }


    // =========================================
    // GET USER ID
    // =========================================

    public Long getUserId(
            String token) {

        try {

            Claims claims =
                    getClaims(
                            token
                    );


            String subject =
                    claims.getSubject();


            if (
                    subject == null ||
                    subject.trim().isEmpty()
            ) {

                return null;

            }


            return Long.valueOf(
                    subject
            );


        } catch (
                Exception e
        ) {

            return null;

        }

    }


    // =========================================
    // GET EMAIL
    // =========================================

    public String getEmail(
            String token) {

        try {

            Claims claims =
                    getClaims(
                            token
                    );


            return claims.get(
                    "email",
                    String.class
            );


        } catch (
                Exception e
        ) {

            return null;

        }

    }


    // =========================================
    // GET ROLE
    // =========================================

    public String getRole(
            String token) {

        try {

            Claims claims =
                    getClaims(
                            token
                    );


            return claims.get(
                    "role",
                    String.class
            );


        } catch (
                Exception e
        ) {

            return null;

        }

    }


    // =========================================
    // CHECK ADMIN
    // =========================================

    public boolean isAdmin(
            String token) {


        try {

            /*
             * First verify that the token itself
             * is valid.
             */

            if (
                    !isValidToken(
                            token
                    )
            ) {

                return false;

            }


            String role =
                    getRole(
                            token
                    );


            return "ADMIN".equalsIgnoreCase(
                    role
            );


        } catch (
                Exception e
        ) {

            return false;

        }

    }

}