package odms.controller.http;

import com.google.gson.JsonElement;
import java.util.Map;

public class Response {

    private Map<String, String> headers;
    private int token;
    private JsonElement body;
    private int status;

    public Response(Map<String, String> headers, int token, JsonElement body, int status) {
        this.headers = headers;
        this.token = token;
        this.body = body;
        this.status = status;
    }
}
