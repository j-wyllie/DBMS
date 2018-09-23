package odms.controller.database.common;

import lombok.extern.slf4j.Slf4j;
import odms.Session;
import odms.commons.model.InitializationException;
import odms.commons.model.enums.UserType;
import odms.controller.http.Request;
import odms.controller.http.Response;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class HttpCommonDAO implements CommonDAO {

    /**
     * Interface for the querying of databases across
     * platforms.
     * @param query to execute.
     */
    @Override
    public void queryDatabase(String query) {

    }

    /**
     * Sends request to set up default user accounts on the server.
     * @throws InitializationException error.
     */
    @Override
    public void setup() throws InitializationException {
        Response response = null;
        String url = "http://localhost:6969/api/v1/setup";

        Request request = new Request(url, new HashMap<>());
        try {
            response = request.post();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        if (response.getStatus() != 201) {
            throw new InitializationException(response.getBody());
        }
    }

    /**
     * Logs a user out from their session on the server.
     */
    @Override
    public void logout() {
        Response response = null;
        int id = Session.getCurrentId();
        UserType userType = Session.getCurrentUser().getValue();

        String url = "http://localhost:6969/api/v1/setup";
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("id", id);
        queryParams.put("UserType", userType);

        Request request = new Request(url, queryParams);
        try {
            response = request.post();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        if (response.getStatus() != 200) {
            throw new InternalError(response.getBody());
        }
    }
}
