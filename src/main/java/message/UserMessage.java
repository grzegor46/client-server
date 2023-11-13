package message;

public class UserMessage {

    private String sender;
    private String receiver;
    private String content;

    public void setContent(String content) {
        if(content.length() > 255) {
            throw new RuntimeException("content of this msg is greater than 255 characters");
        }
        this.content = content;
    }

}
