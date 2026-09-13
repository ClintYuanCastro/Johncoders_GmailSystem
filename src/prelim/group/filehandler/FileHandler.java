package prelim.group.filehandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import prelim.group.model.Email;
import prelim.group.userHandling.CurrentUser;

public class FileHandler {
    ObjectMapper objectMapper;
    private CurrentUser currentUser;
    private final File mailFolder = new File("mailFolder");
    private final File userDetailsFolder = new File("userDetailsFolder");

    public FileHandler(CurrentUser user) {
        this.objectMapper = new ObjectMapper();
        this.currentUser = user;
        if (!mailFolder.exists()){
            mailFolder.mkdirs();
        }
        if (!userDetailsFolder.exists()){
            userDetailsFolder.mkdirs();
        }
    }

    /*
    ================================ Handles Mail ===========================================
     */
    public void saveMail(Email email) throws IOException {
        File fileToBeSaved = new File(mailFolder, currentUser.toString());
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(fileToBeSaved, email);
    }

    public void deleteMail(Email emailToDelete) throws IOException {
        File userFile = new File(mailFolder, currentUser.toString());

        if (!userFile.exists()) {
            System.out.println("User file does not exist.");
            return;
        }

        // 1. Read existing emails into a List
        List<Email> emailList = objectMapper.readValue(
                userFile,
                objectMapper.getTypeFactory().constructCollectionType(List.class, Email.class)
        );

        // 2. Iterate through the list using an Iterator to find and remove the matching ID safely
        boolean removed = false;
        Iterator<Email> iterator = emailList.iterator();

        while (iterator.hasNext()) {
            Email email = iterator.next();
            if (email.getId() == emailToDelete.getId()) {
                iterator.remove();
                removed = true;
                break; // Stop loop once target is found and deleted
            }
        }

        // 3. Write back to file if removed
        if (removed) {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(userFile, emailList);
            System.out.println("Email deleted successfully.");
        } else {
            System.out.println("Email not found in user file.");
        }
    }

    public List<Email> loadMails() throws IOException {
        File mailFile = new File(mailFolder, currentUser.toString());

        // Return an empty list if the file doesn't exist yet
        if (!mailFile.exists() || mailFile.length() == 0) {
            return new ArrayList<>();
        }

        return objectMapper.readValue(
                mailFile,
                objectMapper.getTypeFactory().constructCollectionType(List.class, Email.class)
        );
    }

    public void updateMail(Email updatedEmail) throws IOException {
        List<Email> emailList = loadMails();
        boolean updated = false;

        for (int i = 0; i < emailList.size(); i++) {
            if (emailList.get(i).getId() == updatedEmail.getId()) {
                emailList.set(i, updatedEmail);
                updated = true;
                break;
            }
        }

        if (updated) {
            File mailFile = new File(mailFolder, currentUser.toString());
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(mailFile, emailList);
            System.out.println("Email updated successfully.");
        } else {
            System.out.println("Email not found to update.");
        }
    }

    /*
    =================================Handle Users============================================
     */

    public void saveUser() throws IOException {
        File fileToBeSaved = new File(userDetailsFolder, currentUser.toString());
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(fileToBeSaved, currentUser);
    }
    public void deleteUser(){

    }

    public CurrentUser loadUser(String username) throws IOException {
        File userFile = new File(userDetailsFolder, username);

        if (!userFile.exists()) {
            System.out.println("User does not exist.");
            return null;
        }

        return objectMapper.readValue(userFile, CurrentUser.class);
    }

    public List<CurrentUser> loadAllUsers() throws IOException {
        List<CurrentUser> users = new ArrayList<>();

        // 1. Check if the folder exists and contains files
        File[] userFiles = userDetailsFolder.listFiles();

        if (userFiles != null) {
            // 2. Loop through every file in the directory
            for (File file : userFiles) {
                if (file.isFile()) {
                    try {
                        // Deserialize each JSON file into a CurrentUser object
                        CurrentUser user = objectMapper.readValue(file, CurrentUser.class);
                        users.add(user);
                    } catch (IOException e) {
                        System.err.println("Failed to parse user file: " + file.getName());
                    }
                }
            }
        }

        return users;
    }

}
