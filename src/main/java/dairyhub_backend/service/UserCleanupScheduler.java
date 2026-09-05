package dairyhub_backend.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class UserCleanupScheduler {

    private final UserService userService;


    // =========================================
    // CONSTRUCTOR
    // =========================================

    public UserCleanupScheduler(
            UserService userService) {

        this.userService =
                userService;
    }


    // =========================================
    // DELETE EXPIRED USERS
    // =========================================

    /*
     * Runs once every day.
     *
     * 86,400,000 milliseconds = 24 hours.
     *
     * The UserService checks which deleted
     * accounts have been in the Delete Bin
     * for 30 days or longer.
     */

    @Scheduled(
            fixedRate = 86_400_000
    )
    public void cleanupDeletedUsers() {

        try {

            int deletedCount =
                    userService
                            .permanentlyDeleteExpiredUsers();


            if (
                    deletedCount > 0
            ) {

                System.out.println(
                        "Delete Bin cleanup: "
                                + deletedCount
                                + " expired account(s) permanently deleted."
                );

            } else {

                System.out.println(
                        "Delete Bin cleanup: no expired accounts found."
                );

            }

        } catch (Exception e) {

            System.err.println(
                    "Delete Bin cleanup failed: "
                            + e.getMessage()
            );

            e.printStackTrace();
        }
    }

}