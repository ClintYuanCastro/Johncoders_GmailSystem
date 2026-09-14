package prelim.group.userHandling;

import prelim.group.filehandler.FileHandler;
import prelim.group.model.Email;

import java.util.List;
import java.util.regex.Pattern;

/**
 * UserManager owns the business logic for the User Requirements:
 *  - create an account
 *  - log in
 *  - delete an account
 *  - only allow a user to access their own emails
 *
 * Persistence is delegated to FileHandler (a singleton - see getInstance()).
 * Session state is delegated to CurrentUser (also a singleton - see
 * CurrentUser.getInstance()).
 *
 * NOTE: User no longer has a separate userID field - email is the account
 * identifier directly.
 */
public class UserManager {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private static final int MIN_PASSWORD_LENGTH = 8;

    private final FileHandler fileHandler;

    public UserManager() {
        this.fileHandler = FileHandler.getInstance();
    }

    /**
     * Result wrapper so callers (e.g. Main) can distinguish *why* an
     * operation failed instead of just getting a boolean or an exception.
     */
    public static class Result {
        private final boolean success;
        private final String message;

        private Result(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public static Result ok(String message) {
            return new Result(true, message);
        }

        public static Result fail(String message) {
            return new Result(false, message);
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }
    }

    /*
    ================================ Account Creation ===========================================
     */
    public Result createAccount(String name, String email, String password) {
        // --- Validation ---
        if (isBlank(name)) {
            return Result.fail("Name cannot be empty.");
        }
        if (isBlank(email) || !EMAIL_PATTERN.matcher(email).matches()) {
            return Result.fail("Email address is not valid.");
        }
        if (password == null || password.length() < MIN_PASSWORD_LENGTH) {
            return Result.fail("Password must be at least " + MIN_PASSWORD_LENGTH + " characters long.");
        }
        if (!containsLetterAndDigit(password)) {
            return Result.fail("Password must contain at least one letter and one number.");
        }

        if (fileHandler.userExists(email)) {
            return Result.fail("An account with that email already exists.");
        }

        User newUser = new User(email, password, name);
        fileHandler.registerUser(newUser);
        return Result.ok("Account created successfully.");
    }

    /*
    ================================ Login ===========================================
     */
    public Result login(String email, String password) {
        if (isBlank(email) || isBlank(password)) {
            return Result.fail("Email and password are required.");
        }

        User user = fileHandler.authenticate(email, password);
        if (user == null) {
            // Deliberately vague: don't reveal whether the email exists or
            // the password was wrong, to avoid leaking which accounts exist.
            return Result.fail("Invalid email or password.");
        }

        CurrentUser.getInstance().setUser(user);
        return Result.ok("Logged in as " + user.getEmail() + ".");
    }

    public void logout() {
        CurrentUser.getInstance().logout();
    }

    /*
    ================================ Account Deletion ===========================================
     */
    /**
     * Deletes the currently logged-in user's account. Requires re-entering
     * the password as confirmation, since account deletion is destructive.
     */
    public Result deleteAccount(String password) {
        CurrentUser currentUser = CurrentUser.getInstance();
        if (!currentUser.isLoggedIn()) {
            return Result.fail("You must be logged in to delete your account.");
        }

        User user = currentUser.getUser();
        if (!user.getPassword().equals(password)) {
            return Result.fail("Incorrect password. Account was not deleted.");
        }

        boolean deleted = fileHandler.removeUser(user.getEmail());
        if (deleted) {
            currentUser.logout();
            return Result.ok("Account deleted successfully.");
        } else {
            return Result.fail("Failed to delete account.");
        }
    }

    /*
    ================================ Access Control Helper ===========================================
     */
    /**
     * Enforces "a user shall only access emails associated with their own
     * account." Any feature that touches a specific user's mail should
     * check this first.
     */
    public boolean canAccess(String email) {
        CurrentUser currentUser = CurrentUser.getInstance();
        return currentUser.isLoggedIn()
                && currentUser.getUser().getEmail().equals(email);
    }

    /**
     * Enforces the requirement directly against an Email object: the
     * logged-in user may only view an email if they are its sender or
     * its recipient.
     */
    public boolean canAccessEmail(Email email) {
        CurrentUser currentUser = CurrentUser.getInstance();
        if (!currentUser.isLoggedIn() || email == null) {
            return false;
        }
        String me = currentUser.getUser().getEmail();
        return me.equals(email.getSender())
                || me.equals(email.getRecipient());
    }

    /*
    ================================ Helpers ===========================================
     */
    public List<User> listAllUsers() {
        return fileHandler.getRegisteredUsers();
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private boolean containsLetterAndDigit(String s) {
        boolean hasLetter = false;
        boolean hasDigit = false;
        for (char c : s.toCharArray()) {
            if (Character.isLetter(c)) hasLetter = true;
            if (Character.isDigit(c)) hasDigit = true;
        }
        return hasLetter && hasDigit;
    }
}