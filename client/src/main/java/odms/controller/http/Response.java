package odms.controller.http;

public class Response {

    private String body;
    private int status;

    public Response(String body, int status) {
        this.body = body;
        this.status = status;
    }

    public String getBody() {
        return body;
    }

    public int getStatus() {
        return status;
    }
}
