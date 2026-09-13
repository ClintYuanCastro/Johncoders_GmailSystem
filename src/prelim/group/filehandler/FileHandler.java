package prelim.group.filehandler;


import prelim.group.model.Email;
import prelim.group.userHandling.User;


import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class FileHandler {
    private static FileHandler instance;
    private static final String DATA_DIR = "mailData";
    private static final String EMAILS_FILE = DATA_DIR + File.separator + "emails.dat";
    private static final String USERS_FILE = DATA_DIR + File.separator + "users.dat";


    private List<User> registeredUsers;
    private List<Email> emails;


    private FileHandler() {
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
        String target = email.trim().toLowerCase();
        for (User u : registeredUsers) {
            if (u.getEmail().toLowerCase().equals(target)) {
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
        String target = email.trim().toLowerCase();
        for (User u : registeredUsers) {
            if (u.getEmail().toLowerCase().equals(target) && u.getPassword().equals(password)) {
                return u;
            }
        }
        return null;
    }


    @SuppressWarnings("unchecked")
    private void loadUsers() {
        File file = new File(USERS_FILE);
        if (!file.exists()) {
            registeredUsers = new ArrayList<>();
            return;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            registeredUsers = (List<User>) ois.readObject();
        } catch (Exception e) {
            registeredUsers = new ArrayList<>();
        }
    }


    public void saveUsers() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USERS_FILE))) {
            oos.writeObject(registeredUsers);
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


    @SuppressWarnings("unchecked")
    public void loadEmails() {
        File file = new File(EMAILS_FILE);
        if (!file.exists()) {
            emails = new ArrayList<>();
            return;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            emails = (List<Email>) ois.readObject();
        } catch (Exception e) {
            emails = new ArrayList<>();
        }
    }


    public void saveEmails() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(EMAILS_FILE))) {
            oos.writeObject(emails);
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


        File emailFile = new File(userFolder, email.getId() + ".dat");
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(emailFile))) {
            oos.writeObject(email);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

