package OSLabClass.课程设计;


public class OutputRequest {
    private String userName;
    private String content;

    public OutputRequest(String userName, String content) {
        this.userName = userName;
        this.content = content;
    }

    public String getUserName() {
        return userName;
    }

    public String getContent() {
        return content;
    }
}