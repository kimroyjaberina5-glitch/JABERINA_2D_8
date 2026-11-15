
package ui;

import dao.UserDao;
import model.User;

import java.util.List;
import java.util.Scanner;

public class AdminMenu {

    private final Scanner scanner;
    private final UserDao userDao;
    private final User currentAdmin;

    public AdminMenu(Scanner scanner, User currentAdmin) {
        this.scanner = scanner;
        this.currentAdmin = currentAdmin;
        this.userDao = new UserDao();
    }

    // ADMIN MENU
    public void showMenu() {

        if (!currentAdmin.roles().equalsIgnoreCase("ADMIN")) {
            System.out.println("Access Denied! Admin only.");
            return;
        }

        boolean running = true;

        while (running) {

            System.out.println("\n================================");
            System.out.println("       ADMIN DASHBOARD");
            System.out.println("================================");
            System.out.println("Logged in as: " + currentAdmin.username());
            System.out.println("[1] View All Users");
            System.out.println("[2] Update User Role and Status");
            System.out.println("[0] Logout");
            System.out.println("================================");

            int choice = readIntInput("Enter choice: ");

            switch (choice) {

                case 1:
                    displayAllUsers();
                    break;

                case 2:
                    updateUser();
                    break;

                case 0:
                    System.out.println("Admin logged out successfully!");
                    running = false;
                    break;

                default:
                    System.out.println("Invalid choice! Try again.");
            }
        }
    }

    // DISPLAY ALL USERS
    private void displayAllUsers() {

        List<User> users = userDao.getAllUsers();

        System.out.println("\n================ ALL USERS ================");

        System.out.printf("%-8s %-18s %-18s %-12s%n",
                "ID", "USERNAME", "ROLE", "STATUS");

        System.out.println("-------------------------------------------");

        for (User user : users) {

            System.out.printf("%-8d %-18s %-18s %-12s%n",
                    user.id(),
                    user.username(),
                    user.roles(),
                    user.status());
        }

        System.out.println("===========================================");
    }

    // UPDATE USER
    private void updateUser() {

        System.out.println("\n===== UPDATE USER ROLE AND STATUS =====");

        // Display all users first
        displayAllUsers();

        // Get target user ID
        int targetUserId = readIntInput(
                "Enter User ID to update (0 to cancel): ");

        if (targetUserId == 0) {
            System.out.println("Update cancelled.");
            return;
        }

        // SELF-EDIT RESTRICTION
        if (targetUserId == currentAdmin.id()) {

            System.out.println(
                    "WARNING: Modifying the currently active admin account is restricted.");

            return;
        }

        // Check if user exists
        User targetUser = userDao.getUserById(targetUserId);

        if (targetUser == null) {
            System.out.println("User ID not found!");
            return;
        }

        System.out.println("\nSelected User:");
        System.out.println("ID: " + targetUser.id());
        System.out.println("Username: " + targetUser.username());
        System.out.println("Current Role: " + targetUser.roles());
        System.out.println("Current Status: " + targetUser.status());

        // Select new role
        System.out.println("\nAvailable Roles:");
        System.out.println("[1] ADMIN");
        System.out.println("[2] RECEPTIONIST");
        System.out.println("[3] DOCTOR");
        System.out.println("[0] Cancel");

        String newRole = readValidRole();

        if (newRole == null) {
            System.out.println("Update cancelled.");
            return;
        }

        // Select new status
        System.out.println("\nAvailable Statuses:");
        System.out.println("[1] APPROVED");
        System.out.println("[2] PENDING");
        System.out.println("[0] Cancel");

        String newStatus = readValidStatus();

        if (newStatus == null) {
            System.out.println("Update cancelled.");
            return;
        }

        // Confirm update
        System.out.println("\n===== UPDATE CONFIRMATION =====");
        System.out.println("Username: " + targetUser.username());
        System.out.println("New Role: " + newRole);
        System.out.println("New Status: " + newStatus);

        int confirm = readIntInput(
                "Enter 1 to confirm or 0 to cancel: ");

        if (confirm != 1) {
            System.out.println("Update cancelled.");
            return;
        }

        // SAVE TO DATABASE
        boolean updated = userDao.updateUserRoleAndStatus(
                targetUserId,
                newRole,
                newStatus
        );

        if (updated) {
            System.out.println("\nSUCCESS!");
            System.out.println("User role and status updated successfully!");
        } else {
            System.out.println("Failed to update user.");
        }
    }

    // INPUT VALIDATION: INTEGER
    private int readIntInput(String prompt) {

        while (true) {

            System.out.print(prompt);

            String input = scanner.nextLine().trim();

            try {
                return Integer.parseInt(input);

            } catch (NumberFormatException e) {

                System.out.println(
                        "Invalid input! Please enter a valid number.");
            }
        }
    }

    // INPUT VALIDATION: ROLE
    private String readValidRole() {

        while (true) {

            System.out.print("Enter new role (0 to cancel): ");

            String role = scanner.nextLine().trim().toUpperCase();

            if (role.equals("0")) {
                return null;
            }

            if (role.equals("ADMIN")
                    || role.equals("RECEPTIONIST")
                    || role.equals("DOCTOR")) {

                return role;
            }

            System.out.println(
                    "Invalid role! Choose ADMIN, RECEPTIONIST, or DOCTOR.");
        }
    }

    // INPUT VALIDATION: STATUS
    private String readValidStatus() {

        while (true) {

            System.out.print("Enter new status (0 to cancel): ");

            String status = scanner.nextLine().trim().toUpperCase();

            if (status.equals("0")) {
                return null;
            }

            if (status.equals("APPROVED")
                    || status.equals("PENDING")) {

                return status;
            }

            System.out.println(
                    "Invalid status! Choose APPROVED or PENDING.");
        }
    }
}
