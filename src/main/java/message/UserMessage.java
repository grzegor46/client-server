package message;

public class UserMessage {

    private String sender;
    private String receiver;
    private String content;
    private boolean isRead;

    public UserMessage(String sender, String receiver, String content) {
        this.sender = sender;
        this.receiver = receiver;

        if(content.length() > 255) {
            content = content.substring(0,255);
        }
        this.content = content;

        this.isRead = false;
    }
    // this constructor should be because during deserialization (reading from file) error appears
    public UserMessage() {
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getContent() {
        return content;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }
}
