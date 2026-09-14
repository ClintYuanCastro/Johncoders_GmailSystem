package prelim.group.userHandling;

import java.util.Scanner;

/**
 * Console entry point so you can exercise UserManager (create account,
 * log in, delete account) directly from the Run window.
 */
public class UserExecutable {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        UserManager userManager = new UserManager();

        boolean running = true;

        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    handleCreateAccount(scanner, userManager);
                    break;
                case "2":
                    handleLogin(scanner, userManager);
                    break;
                case "3":
                    handleDeleteAccount(scanner, userManager);
                    break;
                case "4":
                    handleLogout(userManager);
                    break;
                case "5":
                    printStatus();
                    break;
                case "6":
                    running = false;
                    System.out.println("Exiting.");
                    break;
                default:
                    System.out.println("Invalid option. Please choose 1-6.");
            }
            System.out.println();
        }

        scanner.close();
    }

    private static void printMenu() {
        System.out.println("=========================================");
        System.out.println("  User Manager - Test Console");
        System.out.println("=========================================");
        System.out.println("1. Create account");
        System.out.println("2. Login");
        System.out.println("3. Delete account");
        System.out.println("4. Logout");
        System.out.println("5. Show current session status");
        System.out.println("6. Exit");
        System.out.print("Choose an option: ");
    }

    private static void handleCreateAccount(Scanner scanner, UserManager userManager) {
        System.out.print("Name: ");
        String name = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Password (min 8 chars, letters+numbers): ");
        String password = scanner.nextLine();

        UserManager.Result result = userManager.createAccount(name, email, password);
        System.out.println(result.isSuccess() ? "[SUCCESS] " + result.getMessage()
                : "[FAILED] " + result.getMessage());
    }

    private static void handleLogin(Scanner scanner, UserManager userManager) {
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        UserManager.Result result = userManager.login(email, password);
        System.out.println(result.isSuccess() ? "[SUCCESS] " + result.getMessage()
                : "[FAILED] " + result.getMessage());
    }

    private static void handleDeleteAccount(Scanner scanner, UserManager userManager) {
        if (!CurrentUser.getInstance().isLoggedIn()) {
            System.out.println("[FAILED] You must be logged in to delete an account.");
            return;
        }
        System.out.print("Confirm password for " + CurrentUser.getInstance().getUser().getEmail() + ": ");
        String password = scanner.nextLine();

        UserManager.Result result = userManager.deleteAccount(password);
        System.out.println(result.isSuccess() ? "[SUCCESS] " + result.getMessage()
                : "[FAILED] " + result.getMessage());
    }

    private static void handleLogout(UserManager userManager) {
        if (!CurrentUser.getInstance().isLoggedIn()) {
            System.out.println("No user is currently logged in.");
            return;
        }
        userManager.logout();
        System.out.println("Logged out.");
    }

    private static void printStatus() {
        if (CurrentUser.getInstance().isLoggedIn()) {
            System.out.println("Logged in as: " + CurrentUser.getInstance().getUser().getEmail());
        } else {
            System.out.println("No user is currently logged in.");
        }
    }
}