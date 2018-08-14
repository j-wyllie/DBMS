package odms.controller.database.profile;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.sql.SQLException;
import java.util.AbstractMap.SimpleEntry;
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
import odms.data.NHIConflictException;

public class HttpProfileDAO implements ProfileDAO {

    @Override
    public List<Profile> getAll() {
        String url = "http://localhost:6969/api/v1/profiles/all";
        Map<String, String> queryParams = new HashMap<>();
        return getArrayRequest(url, queryParams);
    }

    @Override
    public Profile get(int profileId) {
        String url = "http://localhost:6969/api/v1/profiles";
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("id", String.valueOf(profileId));
        return getSingleRequest(url, queryParams);
    }

    @Override
    public Profile get(String username) {
        String url = "http://localhost:6969/api/v1/profiles";
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("username", username);
        return getSingleRequest(url, queryParams);
    }

    @Override
    public void add(Profile profile) throws NHIConflictException, SQLException {
        Gson gson = new Gson();
        String url = "http://localhost:6969/api/v1/profiles";
        Map<String, String> queryParams = new HashMap<>();
        Response response = null;

        String body = gson.toJson(profile);
        Request request = new Request(url, 0, queryParams, body);
        try {
            response = request.post();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (response.getStatus() == 400 ) {
            throw new NHIConflictException("NHI in use.", profile.getNhi());
        }
        else if (response.getStatus() == 500) {
            throw new SQLException(response.getBody());
        }
    }

    @Override
    public boolean isUniqueUsername(String username) { throw new UnsupportedOperationException(); }

    @Override
    public int isUniqueNHI(String nhi) { throw new UnsupportedOperationException(); }

    @Override
    public void remove(Profile profile) {
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
    public void update(Profile profile) {
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
            String region, String gender, String type, Set<OrganEnum> organs) {
        return null;
    }

    @Override
    public int size() {
        JsonParser parser = new JsonParser();
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
            count = parser.parse(response.getBody()).getAsInt();
        }
        return count;
    }

    @Override
    public List<Entry<Profile, OrganEnum>> getAllReceiving() {
        String url = "http://localhost:6969/api/v1/profiles/all";
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("receiving", "true");
        return getEntryArrayRequest(url, queryParams);
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

    private List<Entry<Profile, OrganEnum>> getEntryArrayRequest(String url, Map<String, String> queryParams) {
        JsonParser parser = new JsonParser();
        Gson gson = new Gson();

        Response response = null;
        Request request = new Request(url, 0, queryParams);
        try {
            response = request.get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<Entry<Profile, OrganEnum>> profiles = new ArrayList<>();
        if (response.getStatus() == 200) {
            JsonArray results = parser.parse(response.getBody().toString()).getAsJsonArray();
            for (JsonElement result : results) {
                Profile profile = gson.fromJson(result.getAsJsonObject().get("key"), Profile.class);
                OrganEnum organ = gson.fromJson(result.getAsJsonObject().get("value"), OrganEnum.class);
                profiles.add(new SimpleEntry<>(profile, organ));
            }
        }
        return profiles;
    }
}