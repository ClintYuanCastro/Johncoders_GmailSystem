package prelim.group.userHandling;

public class CurrentUser {
    private static CurrentUser instance;
    private User user;

    private CurrentUser() {
        this.user = new User("user@mail.com", "password", "Default User");
    }

    public static synchronized CurrentUser getInstance() {
        if (instance == null) {
            instance = new CurrentUser();
        }
        return instance;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void logout() {
        this.user = null;
    }

    public boolean isLoggedIn() {
        return this.user != null;
    }
}