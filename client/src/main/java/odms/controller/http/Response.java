package odms.controller.http;

import com.google.gson.JsonElement;
import java.util.Map;

public class Response {

    private int token;
    private JsonElement body;
    private int status;

    public Response(int token, JsonElement body, int status) {
        this.token = token;
        this.body = body;
        this.status = status;
    }

    public int getToken() { return token; }

    public JsonElement getBody() { return body; }

    public int getStatus() { return status; }
}
