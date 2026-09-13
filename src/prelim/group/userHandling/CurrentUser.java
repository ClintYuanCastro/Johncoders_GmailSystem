package prelim.group.userHandling;

public class CurrentUser {
    private static User currentUser;

    // Private constructor prevents direct instantiation
    public CurrentUser() {}

    public static void set(User user) {
        currentUser = user;
    }

    public static User get() {
        return currentUser;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static void logout() {
        currentUser = null;
    }

    @Override
    public String toString() {
        return currentUser != null ? currentUser.getUserID() : "";
    }
}
