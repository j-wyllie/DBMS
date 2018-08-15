package odms.controller.database.organ;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import odms.commons.model.enums.OrganEnum;
import odms.commons.model.profile.OrganConflictException;
import odms.commons.model.profile.Profile;
import odms.controller.http.Request;
import odms.controller.http.Response;

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
    public void addDonation(Profile profile, OrganEnum organ) {
        String url = String.format("http://localhost:6969/api/v1/profiles/%s/organs", profile.getId());
        Map<String, Object> organInfo = new HashMap<>();
        System.out.println(organ.getNamePlain());
        organInfo.put("name", organ.getNamePlain());
        organInfo.put("date", organ.getDate());
        organInfo.put("donated", true);
        post(url, organInfo);
    }

    @Override
    public void addDonating(Profile profile, OrganEnum organ) {
        String url = String.format("http://localhost:6969/api/v1/profiles/%s/organs", profile.getId());
        Map<String, Object> organInfo = new HashMap<>();
        organInfo.put("name", organ.getNamePlain());
        organInfo.put("date", organ.getDate());
        organInfo.put("donating", true);
        post(url, organInfo);
    }

    @Override
    public void addRequired(Profile profile, OrganEnum organ) {
        String url = String.format("http://localhost:6969/api/v1/profiles/%s/organs", profile.getId());
        Map<String, Object> organInfo = new HashMap<>();
        organInfo.put("name", organ.getNamePlain());
        organInfo.put("date", organ.getDate());
        organInfo.put("required", true);
        post(url, organInfo);
    }

    @Override
    public void addReceived(Profile profile, OrganEnum organ) {
        String url = String.format("http://localhost:6969/api/v1/profiles/%s/organs", profile.getId());
        Map<String, Object> organInfo = new HashMap<>();
        organInfo.put("name", organ.getNamePlain());
        organInfo.put("date", organ.getDate());
        organInfo.put("received", true);
        post(url, organInfo);
    }

    private void post(String url, Map<String, Object> organInfo) {
        Gson gson = new Gson();
        String body = gson.toJson(organInfo);
        Request request = new Request(url, 0, new HashMap<>(), body);
        try {
            request.post();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeDonation(Profile profile, OrganEnum organ) {
        String url = String.format("http://localhost:6969/api/v1/profiles/%s/organs", profile.getId());
        Map<String, Object> organInfo = new HashMap<>();
        organInfo.put("name", organ.getNamePlain());
        organInfo.put("date", organ.getDate());
        organInfo.put("donated", true);
        delete(url, organInfo);
    }

    @Override
    public void removeDonating(Profile profile, OrganEnum organ) {
        String url = String.format("http://localhost:6969/api/v1/profiles/%s/organs", profile.getId());
        Map<String, Object> organInfo = new HashMap<>();
        organInfo.put("name", organ.getNamePlain());
        organInfo.put("date", organ.getDate());
        organInfo.put("donating", true);
        delete(url, organInfo);
    }

    @Override
    public void removeRequired(Profile profile, OrganEnum organ) {
        String url = String.format("http://localhost:6969/api/v1/profiles/%s/organs", profile.getId());
        Map<String, Object> organInfo = new HashMap<>();
        organInfo.put("name", organ.getNamePlain());
        organInfo.put("date", organ.getDate());
        organInfo.put("required", true);
        delete(url, organInfo);
    }

    @Override
    public void removeReceived(Profile profile, OrganEnum organ) {
        String url = String.format("http://localhost:6969/api/v1/profiles/%s/organs", profile.getId());
        Map<String, Object> organInfo = new HashMap<>();
        organInfo.put("name", organ.getNamePlain());
        organInfo.put("date", organ.getDate());
        organInfo.put("received", true);
        delete(url, organInfo);
    }

    private void delete(String url, Map<String, Object> organInfo) {
        Request request = new Request(url, 0, organInfo);
        try {
            request.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Set<OrganEnum> getSetRequest(String url, Map<String, Object> queryParams) {
        JsonParser parser = new JsonParser();
        Gson gson = new Gson();
        Response response = null;
        Request request = new Request(url, 0, queryParams);
        try {
            response = request.get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Set<OrganEnum> organs = new HashSet<>();
        if (response.getStatus() == 200) {
            JsonArray results = parser.parse(response.getBody().toString()).getAsJsonArray();
            for (JsonElement result : results) {
                organs.add(gson.fromJson(result, OrganEnum.class));
            }
        }
        return organs;
    }
}
