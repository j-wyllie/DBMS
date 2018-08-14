package odms.controller.database.profile;

import com.google.gson.Gson;
import java.io.IOException;
import java.sql.SQLException;
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
        return null;
    }

    @Override
    public Profile get(int profileId) throws SQLException {
        return null;
    }

    @Override
    public Profile get(String username) throws SQLException {
        Gson parser = new Gson();
        Response response = null;

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("username", username);
        Request request = new Request("http://localhost:6969/api/v1/profiles", 0,
                queryParams);
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

    @Override
    public void add(Profile profile) throws SQLException {

    }

    @Override
    public boolean isUniqueUsername(String username) throws SQLException {
        return false;
    }

    @Override
    public int isUniqueNHI(String nhi) throws SQLException {
        return 0;
    }

    @Override
    public void remove(Profile profile) throws SQLException {

    }

    @Override
    public void update(Profile profile) throws SQLException {

    }

    @Override
    public List<Profile> search(String searchString, int ageSearchInt, int ageRangeSearchInt,
            String region, String gender, String type, Set<OrganEnum> organs) throws SQLException {
        return null;
    }

    @Override
    public int size() throws SQLException {
        return 0;
    }

    @Override
    public List<Entry<Profile, OrganEnum>> getAllReceiving() {
        return null;
    }

    @Override
    public List<Entry<Profile, OrganEnum>> searchReceiving(String searchString) {
        return null;
    }
}
