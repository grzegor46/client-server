package message;

public class UserMessage {

    private String sender;
    private String receiver;
    private String content;

    public UserMessage(String sender, String receiver, String content) {
        this.sender = sender;
        this.receiver = receiver;

        if(content.length() > 255) {
            content = content.substring(0,255);
        }
        this.content = content;


    }

    public UserMessage() {
    }

    public void setContent(String content) {

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
