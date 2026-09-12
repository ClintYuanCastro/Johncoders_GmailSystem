package prelim.group.model;

public class EmailReply {
    private String sender;
    private String date;
    private String content;

    public EmailReply(String sender, String date, String content) {
        this.sender = sender;
        this.date = date;
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public String getDate() {
        return date;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return sender + " (" + date + "): " + content;
    }
}
