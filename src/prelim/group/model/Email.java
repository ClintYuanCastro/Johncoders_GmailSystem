package prelim.group.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Email implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String sender;
    private String recipient;
    private String subject;
    private String body;
    private String timestamp;
    private boolean isRead;
    private boolean isImportant;
    private boolean isArchived;
    private boolean isTrashed;
    private boolean isDraft;
    private List<String> attachments;

    public Email(String id, String sender, String recipient, String subject, String body, String timestamp) {
        this.id = id;
        this.sender = sender;
        this.recipient = recipient;
        this.subject = subject;
        this.body = body;
        this.timestamp = timestamp;
        this.isRead = false;
        this.isImportant = false;
        this.isArchived = false;
        this.isTrashed = false;
        this.isDraft = false;
        this.attachments = new ArrayList<>();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }

    public String getRecipient() { return recipient; }
    public void setRecipient(String recipient) { this.recipient = recipient; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }

    public boolean isImportant() { return isImportant; }
    public void setImportant(boolean important) { isImportant = important; }

    public boolean isArchived() { return isArchived; }
    public void setArchived(boolean archived) { isArchived = archived; }

    public boolean isTrashed() { return isTrashed; }
    public void setTrashed(boolean trashed) { isTrashed = trashed; }

    public boolean isDraft() { return isDraft; }
    public void setDraft(boolean draft) { isDraft = draft; }

    public List<String> getAttachments() { return attachments; }
    public void setAttachments(List<String> attachments) { this.attachments = attachments; }
    public void addAttachment(String path) { this.attachments.add(path); }
}