package odms.controller.http;

public class Response {

    private int token;
    private String body;
    private int status;

    public Response(int token, String body, int status) {
        this.token = token;
        this.body = body;
        this.status = status;
    }

    public int getToken() { return token; }

    public String getBody() { return body; }

    public int getStatus() { return status; }
}
