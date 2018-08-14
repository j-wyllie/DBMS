package odms.controller.database.user;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.sql.SQLException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import odms.commons.model.user.User;
import odms.controller.http.Request;
import odms.controller.http.Response;
import odms.controller.user.UserNotFoundException;
import odms.data.NHIConflictException;

public class HttpUserDAO implements UserDAO {

    @Override
    public List<User> getAll() {
        String url = "http://localhost:6969/api/v1/users/all";
        Map<String, Object> queryParams = new HashMap<>();
        return getArrayRequest(url, queryParams);
    }

    @Override
    public User get(int userId) throws UserNotFoundException {
        String url = "http://localhost:6969/api/v1/users";
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("id", String.valueOf(userId));
        return getSingleRequest(url, queryParams);
    }

    @Override
    public User get(String username) throws UserNotFoundException {
        String url = "http://localhost:6969/api/v1/users";
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("username", username);
        return getSingleRequest(url, queryParams);
    }

    @Override
    public void add(User user) throws IllegalArgumentException {
        Gson gson = new Gson();
        String url = "http://localhost:6969/api/v1/users";
        Map<String, Object> queryParams = new HashMap<>();

        String body = gson.toJson(user);
        Request request = new Request(url, 0, queryParams, body);
        Response response = null;
        try {
            response = request.post();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (response.getStatus() == 400) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public boolean isUniqueUsername(String username) { throw new UnsupportedOperationException(); }

    @Override
    public void remove(User user) {
        String url = "http://localhost:6969/api/v1/users/" + user.getStaffID();
        Request request = new Request(url, 0, new HashMap<>());
        try {
            request.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(User user) {
        Gson gson = new Gson();
        String url = "http://localhost:6969/api/v1/users/" + user.getStaffID();
        String body = gson.toJson(user);
        Request request = new Request(url, 0, new HashMap<>(), body);
        try {
            request.patch();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<User> search(String name) throws SQLException {
        String url = "http://localhost:6969/api/v1/users/all";
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("name", name);
        return getArrayRequest(url, queryParams);
    }

    @Override
    public List<User> search(int id) throws SQLException {
        String url = "http://localhost:6969/api/v1/users/all";
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("id", id);
        return getArrayRequest(url, queryParams);
    }

    private User getSingleRequest(String url, Map<String, Object> queryParams)
            throws UserNotFoundException {
        Gson parser = new Gson();
        Response response = null;
        Request request = new Request(url, 0, queryParams);
        try {
            response = request.get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (response.getStatus() == 200) {
            User user = parser.fromJson(response.getBody(), User.class);
            return user;
        }
        else if (response.getStatus() == 400) {
            if (queryParams.keySet().contains("id")) {
                throw new UserNotFoundException("User not found",
                        Integer.valueOf(queryParams.get("id").toString()));
            }
            else if (queryParams.keySet().contains("username")) {
                throw new UserNotFoundException("User not found",
                        queryParams.get("username").toString());
            }
        }
        return null;
    }

    private List<User> getArrayRequest(String url, Map<String, Object> queryParams) {
        JsonParser parser = new JsonParser();
        Gson gson = new Gson();
        Response response = null;
        Request request = new Request(url, 0, queryParams);
        try {
            response = request.get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<User> users = new ArrayList<>();
        if (response.getStatus() == 200) {
            JsonArray results = parser.parse(response.getBody().toString()).getAsJsonArray();
            for (JsonElement result : results) {
                users.add(gson.fromJson(result, User.class));
            }
        }
        return users;
    }
}
