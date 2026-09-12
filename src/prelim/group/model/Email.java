package prelim.group.model;

import prelim.exercises.MyDoublyLinkedList;
import prelim.exercises.MyList;

public class Email {
    private String id;
    private String sender;
    private String recipient;
    private String subject;
    private String snippet;
    private String body;
    private String date;
    private boolean isStarred;
    private boolean isRead;
    private String category; // Primary, Promotions, Social, etc.

    // Inner-most nested lists as required by List of Lists spec!
    private MyList<String> attachments;
    private MyList<EmailReply> replies;

    public Email(String id, String sender, String recipient, String subject, String snippet, String body, String date, String category) {
        this.id = id;
        this.sender = sender;
        this.recipient = recipient;
        this.subject = subject;
        this.snippet = snippet;
        this.body = body;
        this.date = date;
        this.isStarred = false;
        this.isRead = false;
        this.category = category;
        this.attachments = new MyDoublyLinkedList<>();
        this.replies = new MyDoublyLinkedList<>();
    }

    public String getId() {
        return id;
    }

    public String getSender() {
        return sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getSubject() {
        return subject;
    }

    public String getSnippet() {
        return snippet;
    }

    public String getBody() {
        return body;
    }

    public String getDate() {
        return date;
    }

    public boolean isStarred() {
        return isStarred;
    }

    public void setStarred(boolean starred) {
        isStarred = starred;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public MyList<String> getAttachments() {
        return attachments;
    }

    public MyList<EmailReply> getReplies() {
        return replies;
    }

    public void addAttachment(String filename) {
        try {
            attachments.insert(filename);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addReply(EmailReply reply) {
        try {
            replies.insert(reply);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return sender + " - " + subject + " (" + date + ")";
    }
}
