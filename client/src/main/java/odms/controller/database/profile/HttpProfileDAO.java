package odms.controller.database.profile;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import odms.commons.model.enums.OrganEnum;
import odms.commons.model.profile.Profile;
import odms.controller.http.Request;
import odms.controller.http.Response;

public class HttpProfileDAO implements ProfileDAO {

    @Override
    public List<Profile> getAll() throws SQLException {
        String url = "http://localhost:6969/api/v1/profiles/all";
        Map<String, String> queryParams = new HashMap<>();
        return getArrayRequest(url, queryParams);
    }

    @Override
    public Profile get(int profileId) throws SQLException {
        String url = "http://localhost:6969/api/v1/profiles";
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("id", String.valueOf(profileId));
        return getSingleRequest(url, queryParams);
    }

    @Override
    public Profile get(String username) throws SQLException {
        String url = "http://localhost:6969/api/v1/profiles";
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("username", username);
        return getSingleRequest(url, queryParams);
    }

    @Override
    public void add(Profile profile) throws SQLException {
        Gson gson = new Gson();
        String url = "http://localhost:6969/api/v1/profiles";
        Map<String, String> queryParams = new HashMap<>();

        String body = gson.toJson(profile);
        Request request = new Request(url, 0, queryParams, body);
        try {
            request.post();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isUniqueUsername(String username) throws SQLException {
        if (get(username) != null) {
            return true;
        }
        return false;
    }

    @Override
    public int isUniqueNHI(String nhi) throws SQLException {
        // TODO: properly.
        if (get(nhi) != null) {
            return 1;
        }
        return 0;
    }

    @Override
    public void remove(Profile profile) throws SQLException {
        String url = "http://localhost:6969/api/v1/profiles";
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("id", Integer.toString(profile.getId()));

        Request request = new Request(url, 0, queryParams);
        try {
            request.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Profile profile) throws SQLException {
        Gson gson = new Gson();
        String url = "http://localhost:6969/api/v1/profiles";
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("id", Integer.toString(profile.getId()));

        String body = gson.toJson(profile);
        Request request = new Request(url, 0, queryParams, body);
        try {
            request.patch();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Profile> search(String searchString, int ageSearchInt, int ageRangeSearchInt,
            String region, String gender, String type, Set<OrganEnum> organs) throws SQLException {
        return null;
    }

    @Override
    public int size() throws SQLException {
        String url = "http://localhost:6969/api/v1/profiles/count";
        Map<String, String> queryParams = new HashMap<>();
        Request request = new Request(url, 0, queryParams);
        Response response = null;
        int count = 0;

        try {
            response = request.get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (response != null) {
            count = response.getBody().getAsInt();
        }
        return count;
    }

    @Override
    public List<Entry<Profile, OrganEnum>> getAllReceiving() {
        String url = "http://localhost:6969/api/v1/profiles/all";
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("receiving", "true");
        return null;
    }

    @Override
    public List<Entry<Profile, OrganEnum>> searchReceiving(String searchString) {
        return null;
    }

    private Profile getSingleRequest(String url, Map<String, String> queryParams) {
        Gson parser = new Gson();
        Response response = null;
        Request request = new Request(url, 0, queryParams);
        try {
            response = request.get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (response.getStatus() == 200) {
            Profile profile = parser.fromJson(response.getBody(), Profile.class);
            return profile;
        }
        return null;
    }

    private List<Profile> getArrayRequest(String url, Map<String, String> queryParams) {
        JsonParser parser = new JsonParser();
        Gson gson = new Gson();
        Response response = null;
        Request request = new Request(url, 0, queryParams);
        try {
            response = request.get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<Profile> profiles = new ArrayList<>();
        if (response.getStatus() == 200) {
            JsonArray results = parser.parse(response.getBody().toString()).getAsJsonArray();
            for (JsonElement result : results) {
                profiles.add(gson.fromJson(result, Profile.class));
            }
        }
        return profiles;
    }
}
