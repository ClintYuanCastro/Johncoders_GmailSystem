package prelim.group.filehandler;

import prelim.group.model.Email;
import prelim.group.userHandling.User;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {
    private static FileHandler instance;
    private static final String DATA_DIR = "mailData";
    private static final String EMAILS_FILE = DATA_DIR + File.separator + "emails.json";
    private static final String USERS_FILE = DATA_DIR + File.separator + "users.json";

    private final ObjectMapper objectMapper;
    private List<User> registeredUsers;
    private List<Email> emails;

    private FileHandler() {
        this.objectMapper = new ObjectMapper();
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        loadUsers();
        loadEmails();
        initDefaultUsersIfEmpty();
    }

    public static synchronized FileHandler getInstance() {
        if (instance == null) {
            instance = new FileHandler();
        }
        return instance;
    }

    private void initDefaultUsersIfEmpty() {
        if (registeredUsers.isEmpty()) {
            registeredUsers.add(new User("user@mail.com", "pass", "John Doe"));
            registeredUsers.add(new User("admin@mail.com", "pass", "System Admin"));
            saveUsers();
        }
    }

    public boolean userExists(String email) {
        if (email == null) return false;
        String target = email.trim();
        for (User u : registeredUsers) {
            if (u.getEmail().equals(target)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Validates login credentials against the registered users list.
     *
     * @param email    the email entered on the login screen
     * @param password the plaintext password entered on the login screen
     * @return the matching {@link User} if the email and password both match
     *         a registered account, otherwise {@code null}
     */
    public User authenticate(String email, String password) {
        if (email == null || password == null) return null;
        String target = email.trim();
        for (User u : registeredUsers) {
            if (u.getEmail().equals(target) && u.getPassword().equals(password)) {
                return u;
            }
        }
        return null;
    }

    private void loadUsers() {
        File file = new File(USERS_FILE);
        if (!file.exists() || file.length() == 0) {
            registeredUsers = new ArrayList<>();
            return;
        }
        try {
            registeredUsers = objectMapper.readValue(
                    file,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, User.class)
            );
        } catch (IOException e) {
            System.err.println("Failed to load users.json, starting with an empty list: " + e.getMessage());
            registeredUsers = new ArrayList<>();
        }
    }

    public void saveUsers() {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(USERS_FILE), registeredUsers);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<User> getRegisteredUsers() {
        return registeredUsers;
    }

    public void registerUser(User user) {
        registeredUsers.add(user);
        saveUsers();
    }

    /**
     * Removes a user account by email. Required for the "delete account"
     * requirement - this method was missing from the incoming FileHandler.
     *
     * @param email the email of the account to remove
     * @return true if a matching account was found and removed
     */
    public boolean removeUser(String email) {
        if (email == null) return false;
        String target = email.trim();
        boolean removed = registeredUsers.removeIf(u -> u.getEmail().equals(target));
        if (removed) {
            saveUsers();
        }
        return removed;
    }

    public void loadEmails() {
        File file = new File(EMAILS_FILE);
        if (!file.exists() || file.length() == 0) {
            emails = new ArrayList<>();
            return;
        }
        try {
            emails = objectMapper.readValue(
                    file,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Email.class)
            );
        } catch (IOException e) {
            System.err.println("Failed to load emails.json, starting with an empty list: " + e.getMessage());
            emails = new ArrayList<>();
        }
    }

    public void saveEmails() {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(EMAILS_FILE), emails);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Email> getEmails() {
        return emails;
    }

    public void addEmail(Email email) {
        emails.add(0, email);
        saveEmails();
    }

    public void updateEmail(Email updatedEmail) {
        for (int i = 0; i < emails.size(); i++) {
            if (emails.get(i).getId().equals(updatedEmail.getId())) {
                emails.set(i, updatedEmail);
                break;
            }
        }
        saveEmails();
    }

    public void deleteEmail(String emailId) {
        emails.removeIf(e -> e.getId().equals(emailId));
        saveEmails();
    }

    public static void saveEmailToUserFolder(String userEmail, Email email) {
        File userFolder = new File("mailFolder/" + userEmail);
        if (!userFolder.exists()) {
            userFolder.mkdirs(); // Create the folder if it doesn't exist
        }

        File emailFile = new File(userFolder, email.getId() + ".json");
        try {
            new ObjectMapper().writerWithDefaultPrettyPrinter().writeValue(emailFile, email);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}