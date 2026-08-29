package dairyhub_backend;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import dairyhub_backend.entity.User;
import dairyhub_backend.repository.UserRepository;

@SpringBootApplication
public class DairyhubBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(
                DairyhubBackendApplication.class,
                args
        );
    }

    @Bean
    CommandLineRunner createAdmin(UserRepository userRepository) {

        return args -> {

            String adminEmail = "admin@dairyhub.com";

            if (userRepository.findByEmail(adminEmail).isEmpty()) {

                User admin = new User();

                admin.setName("DairyHub Admin");
                admin.setEmail(adminEmail);
                admin.setPassword("admin123");
                admin.setPhone("9999999999");
                admin.setRole("ADMIN");

                userRepository.save(admin);

                System.out.println(
                        "DairyHub Admin account created."
                );
            }
        };
    }
}