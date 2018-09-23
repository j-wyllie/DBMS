package odms.controller.database.organ;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import odms.commons.model.enums.OrganEnum;
import odms.commons.model.profile.ExpiredOrgan;
import odms.commons.model.profile.Profile;
import odms.controller.http.Request;
import odms.controller.http.Response;

@Slf4j
public class HttpOrganDAO implements OrganDAO {

    @Override
    public Set<OrganEnum> getDonations(Profile profile) {
        String url = String.format("http://localhost:6969/api/v1/profiles/%s/organs", profile.getId());
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("donated", true);
        return getSetRequest(url, queryParams);
    }

    @Override
    public Set<OrganEnum> getDonating(Profile profile) {
        String url = String.format("http://localhost:6969/api/v1/profiles/%s/organs", profile.getId());
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("donating", true);
        return getSetRequest(url, queryParams);
    }

    @Override
    public Set<OrganEnum> getRequired(Profile profile) {
        String url = String.format("http://localhost:6969/api/v1/profiles/%s/organs", profile.getId());
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("required", true);
        return getSetRequest(url, queryParams);
    }

    @Override
    public Set<OrganEnum> getReceived(Profile profile) {
        String url = String.format("http://localhost:6969/api/v1/profiles/%s/organs", profile.getId());
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("received", true);
        return getSetRequest(url, queryParams);
    }

    @Override
    public List<ExpiredOrgan> getExpired(Profile profile) throws SQLException {
        String url = String.format("http://localhost:6969/api/v1/profiles/%s/organs/expired", profile.getId());
        Map<String, Object> queryParams = new HashMap<>();
        return getArrayRequest(url, queryParams);
    }

    @Override
    public void addDonation(Profile profile, OrganEnum organ) {
        String url = String.format("http://localhost:6969/api/v1/profiles/%s/organs", profile.getId());
        Map<String, Object> organInfo = new HashMap<>();
        organInfo.put("name", organ);
        organInfo.put("date", organ.getDate(profile));
        organInfo.put("donated", true);
        post(url, organInfo);
    }

    @Override
    public void addDonating(Profile profile, OrganEnum organ) {
        String url = String.format("http://localhost:6969/api/v1/profiles/%s/organs", profile.getId());
        Map<String, Object> organInfo = new HashMap<>();
        organInfo.put("name", organ);
        organInfo.put("date", organ.getDate(profile));
        organInfo.put("donating", true);
        post(url, organInfo);
    }

    @Override
    public void addRequired(Profile profile, OrganEnum organ) {
        String url = String.format("http://localhost:6969/api/v1/profiles/%s/organs", profile.getId());
        Map<String, Object> organInfo = new HashMap<>();
        organInfo.put("name", organ);
        organInfo.put("date", organ.getDate(profile));
        organInfo.put("required", true);
        post(url, organInfo);
    }

    @Override
    public void addReceived(Profile profile, OrganEnum organ) {
        String url = String.format("http://localhost:6969/api/v1/profiles/%s/organs", profile.getId());
        Map<String, Object> organInfo = new HashMap<>();
        organInfo.put("name", organ);
        organInfo.put("date", organ.getDate(profile));
        organInfo.put("received", true);
        post(url, organInfo);
    }

    private void post(String url, Map<String, Object> organInfo) {
        Gson gson = new Gson();
        String body = gson.toJson(organInfo);
        Request request = new Request(url, new HashMap<>(), body);
        try {
            request.post();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void removeDonation(Profile profile, OrganEnum organ) {
        String url = String.format("http://localhost:6969/api/v1/profiles/%s/organs", profile.getId());
        Map<String, Object> organInfo = new HashMap<>();
        organInfo.put("name", organ);
        organInfo.put("date", organ.getDate(profile));
        organInfo.put("donated", true);
        delete(url, organInfo);
    }

    @Override
    public void removeDonating(Profile profile, OrganEnum organ) {
        String url = String.format("http://localhost:6969/api/v1/profiles/%s/organs", profile.getId());
        Map<String, Object> organInfo = new HashMap<>();
        organInfo.put("name", organ);
        organInfo.put("date", organ.getDate(profile));
        organInfo.put("donating", true);
        delete(url, organInfo);
    }

    @Override
    public void removeRequired(Profile profile, OrganEnum organ) {
        String url = String.format("http://localhost:6969/api/v1/profiles/%s/organs", profile.getId());
        Map<String, Object> organInfo = new HashMap<>();
        organInfo.put("name", organ);
        organInfo.put("date", organ.getDate(profile));
        organInfo.put("required", true);
        delete(url, organInfo);
    }

    @Override
    public void removeReceived(Profile profile, OrganEnum organ) {
        String url = String.format("http://localhost:6969/api/v1/profiles/%s/organs", profile.getId());
        Map<String, Object> organInfo = new HashMap<>();
        organInfo.put("name", organ);
        organInfo.put("date", organ.getDate(profile));
        organInfo.put("received", true);
        delete(url, organInfo);
    }

    @Override
    public void setExpired(Profile profile, String organ, Integer expired, String note,
            Integer userId) throws SQLException {
        String url = String.format("http://localhost:6969/api/v1/profiles/%s/organs/expired", profile.getId());
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("organ", organ);
        queryParams.put("expired", expired);
        queryParams.put("note", note);
        queryParams.put("userId", userId);
        Request request = new Request(url, queryParams,"");
        try {
            request.post();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void revertExpired(Integer profileId, String organ) {
        String url = String.format("http://localhost:6969/api/v1/profiles/%s/organs/expired", profileId);
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("organ", organ);
        queryParams.put("expired", 0);
        Request request = new Request(url, queryParams, "");
        try {
            request.post();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void delete(String url, Map<String, Object> organInfo) {
        Request request = new Request(url, organInfo);
        try {
            request.delete();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Set<OrganEnum> getSetRequest(String url, Map<String, Object> queryParams) {
        JsonParser parser = new JsonParser();
        Gson gson = new Gson();
        Response response = null;
        Request request = new Request(url, queryParams);
        try {
            response = request.get();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        Set<OrganEnum> organs = new HashSet<>();
        if (response != null && response.getStatus() == 200) {
            JsonArray results = parser.parse(response.getBody()).getAsJsonArray();
            for (JsonElement result : results) {
                organs.add(gson.fromJson(result, OrganEnum.class));
            }
        }
        return organs;
    }

    private List<ExpiredOrgan> getArrayRequest(String url, Map<String, Object> queryParams) {
        JsonParser parser = new JsonParser();
        Gson gson = new Gson();
        Response response = null;
        Request request = new Request(url, queryParams);
        try {
            response = request.get();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        List<ExpiredOrgan> organs = new ArrayList<>();
        if (response != null && response.getStatus() == 200) {
            JsonArray results = parser.parse(response.getBody()).getAsJsonArray();
            for (JsonElement result : results) {
                organs.add(gson.fromJson(result, ExpiredOrgan.class));
            }
        }
        return organs;
    }
}
