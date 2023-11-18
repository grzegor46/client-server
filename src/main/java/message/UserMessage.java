package message;

public class UserMessage {

    private String sender;
    private String receiver;
    private String content;

    public UserMessage(String sender, String receiver, String content) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
    }

    public UserMessage() {
    }

    public void setContent(String content) {
        if(content.length() > 255) {
            throw new RuntimeException("content of this msg is greater than 255 characters");
        }
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
}
