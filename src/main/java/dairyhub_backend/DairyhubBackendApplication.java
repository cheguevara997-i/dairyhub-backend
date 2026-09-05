package dairyhub_backend;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import dairyhub_backend.entity.User;
import dairyhub_backend.repository.UserRepository;

@SpringBootApplication
@EnableScheduling
public class DairyhubBackendApplication {

    public static void main(String[] args) {

        SpringApplication.run(
                DairyhubBackendApplication.class,
                args
        );
    }


    // =========================================
    // CREATE ORIGINAL ADMIN
    // =========================================

    @Bean
    CommandLineRunner createAdmin(
            UserRepository userRepository) {

        return args -> {

            String adminEmail =
                    "admin@dairyhub.com";


            /*
             * Create the original protected
             * DairyHub admin only if it does
             * not already exist.
             */

            if (
                    userRepository
                            .findByEmail(adminEmail)
                            .isEmpty()
            ) {

                User admin =
                        new User();


                admin.setName(
                        "DairyHub Admin"
                );


                admin.setEmail(
                        adminEmail
                );


                admin.setPassword(
                        "admin123"
                );


                admin.setPhone(
                        "9999999999"
                );


                admin.setRole(
                        "ADMIN"
                );


                admin.setAdminManaged(
                        false
                );


                /*
                 * New admin is active.
                 */

                admin.setDeleted(
                        false
                );


                admin.setDeletedAt(
                        null
                );


                userRepository.save(
                        admin
                );


                System.out.println(
                        "DairyHub Admin account created."
                );
            }
        };
    }

}