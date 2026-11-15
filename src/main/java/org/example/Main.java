
import dao.DatabaseConnection;
import dao.UserDao;
import model.User;
import ui.AdminMenu;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        // Initialize database
        DatabaseConnection.initializeDatabase();

        UserDao userDao = new UserDao();

        // Create default testing accounts
        userDao.createDefaultUsers();

        boolean running = true;

        while (running) {

            System.out.println("\n====================================");
            System.out.println("     CLINIC APPOINTMENT SYSTEM");
            System.out.println("====================================");
            System.out.println("[1] Login");
            System.out.println("[0] Exit");
            System.out.println("====================================");

            System.out.print("Enter choice: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {

                case "1":

                    System.out.println("\n========== LOGIN ==========");

                    System.out.print("Username: ");
                    String username = scanner.nextLine().trim();

                    System.out.print("Password: ");
                    String password = scanner.nextLine();

                    User loggedInUser = userDao.login(
                            username, password);

                    if (loggedInUser == null) {

                        System.out.println(
                                "Invalid credentials or account is pending.");

                    } else {

                        System.out.println("\nLogin successful!");
                        System.out.println(
                                "Welcome, " + loggedInUser.username());

                        if (loggedInUser.roles()
                                .equalsIgnoreCase("ADMIN")) {

                            AdminMenu adminMenu =
                                    new AdminMenu(scanner, loggedInUser);

                            adminMenu.showMenu();

                        } else {

                            System.out.println(
                                    "This account does not have Admin access.");

                            System.out.println(
                                    "Receptionist and Doctor menus can be connected here.");
                        }
                    }

                    break;

                case "0":

                    System.out.println(
                            "Thank you for using Clinic Appointment System!");

                    running = false;
                    break;

                default:

                    System.out.println(
                            "Invalid choice! Please try again.");
            }
        }

        scanner.close();
    }
}
