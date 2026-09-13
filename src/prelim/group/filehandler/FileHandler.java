package prelim.group.filehandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

import prelim.group.model.Email;
import prelim.group.userHandling.CurrentUser;

public class FileHandler {
    ObjectMapper objectMapper;
    private CurrentUser currentUser;
    private final File userFolder = new File("userFolder");

    public FileHandler(CurrentUser user) {
        this.objectMapper = new ObjectMapper();
        this.currentUser = user;
        if (!userFolder.exists()){
            userFolder.mkdirs();
        }
    }

    public void saveMail(Email email) throws IOException {
        File fileToBeSaved = new File(userFolder, currentUser.toString());
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(fileToBeSaved, email);
    }
}
