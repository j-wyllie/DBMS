package odms.controller.database.user;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import odms.commons.model.user.User;
import odms.controller.http.Request;
import odms.controller.http.Response;
import odms.controller.user.UserNotFoundException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contains all of the methods to do with the user endpoint.
 */
public class HttpUserDAO implements UserDAO {

    private static final String USERS_ALL = "http://localhost:6969/api/v1/users/all";
    private static final String USERS = "http://localhost:6969/api/v1/users";
    private static final String USERS_LOGIN = "http://localhost:6969/api/v1/users/login";

    @Override
    public List<User> getAll() {
        Map<String, Object> queryParams = new HashMap<>();
        return getArrayRequest(USERS_ALL, queryParams);
    }

    @Override
    public User get(int userId) throws UserNotFoundException {
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("id", String.valueOf(userId));
        return getSingleRequest(USERS, queryParams);
    }

    @Override
    public User get(String username) throws UserNotFoundException {
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("username", username);
        return getSingleRequest(USERS, queryParams);
    }

    @Override
    public void add(User user) throws IllegalArgumentException {
        Gson gson = new Gson();
        Map<String, Object> queryParams = new HashMap<>();

        String body = gson.toJson(user);
        Request request = new Request(USERS, 0, queryParams, body);
        Response response = null;
        try {
            response = request.post();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (response != null) {
            if (response.getStatus() == 400) {
                throw new IllegalArgumentException("Invalid details.");
            }
            if (response.getStatus() == 403) {
                throw new IllegalArgumentException("Username already exists.");
            }
        }
    }

    @Override
    public void remove(User user) {
        String url = USERS + '/' + user.getStaffID();
        Request request = new Request(url, 0, new HashMap<>());
        try {
            request.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(User user) throws IllegalArgumentException {
        Gson gson = new Gson();
        String url = USERS + '/' + user.getStaffID();
        String body = gson.toJson(user);
        Request request = new Request(url, 0, new HashMap<>(), body);
        try {
            Response response = request.patch();
            if (response.getStatus() == 403) {
                throw new IllegalArgumentException("Username already exists.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<User> search(String name) {
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("name", name);
        return getArrayRequest(USERS_ALL, queryParams);
    }

    @Override
    public List<User> search(int id) {
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("id", id);
        return getArrayRequest(USERS_ALL, queryParams);
    }

    @Override
    public Boolean checkCredentials(String username, String password) {
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("username", username);
        queryParams.put("password", password);

        Request request = new Request(USERS_LOGIN, 0, queryParams, "{}");
        Response response;
        try {
            response = request.post();

            if (response.getStatus() == 200) {
                return true;
            }

            if (response.getStatus() == 400) {
                throw new IllegalArgumentException("Invalid details.");
            }

            if (response.getStatus() == 403) {
                throw new IllegalArgumentException("Username already exists.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private User getSingleRequest(String url, Map<String, Object> queryParams)
            throws UserNotFoundException {
        Gson parser = new Gson();
        Response response = null;
        Request request = new Request(url, 0, queryParams);
        try {
            response = request.get();

            if (response.getStatus() == 200) {
                return parser.fromJson(response.getBody(), User.class);
            } else if (response.getStatus() == 400) {
                if (queryParams.keySet().contains("id")) {
                    throw new UserNotFoundException("user not found",
                            Integer.valueOf(queryParams.get("id").toString()));
                } else if (queryParams.keySet().contains("username")) {
                    throw new UserNotFoundException("user not found",
                            queryParams.get("username").toString());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private List<User> getArrayRequest(String url, Map<String, Object> queryParams) {
        JsonParser parser = new JsonParser();
        Gson gson = new Gson();
        Response response;
        Request request = new Request(url, 0, queryParams);
        List<User> users = new ArrayList<>();

        try {
            response = request.get();
            if (response.getStatus() == 200) {
                JsonArray results = parser.parse(response.getBody()).getAsJsonArray();
                for (JsonElement result : results) {
                    users.add(gson.fromJson(result, User.class));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return users;
    }
}
