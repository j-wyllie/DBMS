package odms.controller.database.hla;


import com.google.gson.Gson;
import java.io.IOException;
import java.util.HashMap;
import lombok.extern.slf4j.Slf4j;
import odms.commons.model.profile.HLAType;
import odms.controller.http.Request;
import odms.controller.http.Response;


/**
 * Methods that call HLA related database functionality.
 *
 */
@Slf4j
public class HttpHLADAO implements HLADAO {

    /**
     * Gets the HLAType of the given profile.
     *
     * @param profileID the ID of the profile.
     * @return
     */
    @Override
    public HLAType get(Integer profileID) {
        String url = Request.getUrl() + "hla/" + profileID;
        Request request = new Request(url, new HashMap<>());

        Response response = null;
        try {
            response = request.get();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }

        HLAType hlaType = new HLAType();
        Gson gson = new Gson();
        if (response != null && response.getStatus() == 200) {
            hlaType = gson.fromJson(response.getBody(), HLAType.class);
        }

        return hlaType;
    }

    /**
     * Add the HLAype to the given profile.
     *
     * @param profileID the ID of the profile.
     * @param hlaType the HLAs of the patient.
     */
    @Override
    public void add(Integer profileID, HLAType hlaType) {
        String url = Request.getUrl() + "hla/" + profileID;

        Gson gson = new Gson();
        String body = gson.toJson(hlaType);
        Request request = new Request(url, new HashMap<>(), body);

        try {
            request.post();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * Edits the given profile.
     *
     * @param profileID the ID of the profile.
     * @param hlaType the HLAs of the patient.
     */
    @Override
    public void edit(Integer profileID, HLAType hlaType) {
        delete(profileID);
        add(profileID, hlaType);
    }

    /**
     * Deletes the given profile.
     *
     * @param profileID the ID of the profile
     */
    @Override
    public void delete(Integer profileID) {
        String url = Request.getUrl() + "hla/" + profileID;

        Request request = new Request(url, new HashMap<>());
        try {
            request.delete();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

}
