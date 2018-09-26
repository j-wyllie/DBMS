package server.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import odms.commons.model.profile.HLAType;
import org.sonar.api.internal.google.gson.Gson;
import server.model.database.DAOFactory;
import server.model.database.hla.HLA;
import server.model.database.hla.HLADAO;
import server.model.enums.DataTypeEnum;
import spark.Request;
import spark.Response;

@Slf4j
public class HLAController {

    /**
     * Prevent instantiation of static class.
     */
    private HLAController() {
        throw new UnsupportedOperationException();
    }

    /**
     * Gets a HLAType for a given profile.
     *
     * @param req the get request object with no body.
     * @param res the response object with HLAType as body.
     * @return the response body, a HLAType.
     */
    public static String get(Request req, Response res) {
        Gson gson = new Gson();
        HLADAO database = DAOFactory.getHLADAO();
        Integer profileID = Integer.valueOf(req.params("id"));

        HLAType hlaType = new HLAType();

        try {
            List<HLA> HLAgroupX = database.getGroupX(profileID);
            List<HLA> HLAgroupY = database.getGroupY(profileID);
            List<HLA> HLAsecondary = database.getNonPrimary(profileID);

            Map<String, Integer> HLATypeGroupX = new HashMap<>();
            for (HLA hla : HLAgroupX) {
                HLATypeGroupX.put(hla.getAlphaValue(), hla.getNumericValue());
            }
            hlaType.setGroupX(HLATypeGroupX);

            Map<String, Integer> HLATypeGroupY = new HashMap<>();
            for (HLA hla : HLAgroupY) {
                HLATypeGroupY.put(hla.getAlphaValue(), hla.getNumericValue());
            }
            hlaType.setGroupY(HLATypeGroupY);

            Map<String, Integer> HLATypeSecondary = new HashMap<>();
            for (HLA hla : HLAsecondary) {
                HLATypeSecondary.put(hla.getAlphaValue(), hla.getNumericValue());
            }
            hlaType.setSecondaryAntigens(HLATypeSecondary);
        } catch (Exception e) {
            res.status(500);
            return e.getMessage();
        }

        String responseBody = gson.toJson(hlaType);

        res.type(DataTypeEnum.JSON.toString());
        res.status(200);
        return responseBody;
    }

    /**
     * Adds a HLAType to the database.
     *
     * @param req the request object with HLAType as body.
     * @param res the empty response object.
     * @return the response body.
     */
    public static String add(Request req, Response res) {
        Gson gson = new Gson();
        HLADAO database = DAOFactory.getHLADAO();
        Integer profileID = Integer.valueOf(req.params("id"));

        HLAType hlaType = gson.fromJson(req.body(), HLAType.class);

        try {
            Map<String, Integer> HLATypeX = hlaType.getGroupX();
            for (String key : HLATypeX.keySet()) {
                HLA hla = new HLA(key, HLATypeX.get(key), true, false);
                database.add(profileID, hla);
            }

            Map<String, Integer> HLATypeY = hlaType.getGroupY();
            for (String key : HLATypeY.keySet()) {
                HLA hla = new HLA(key, HLATypeY.get(key), false, true);
                database.add(profileID, hla);
            }

            Map<String, Integer> HLATypeSecondary = hlaType.getSecondaryAntigens();
            for (String key : HLATypeSecondary.keySet()) {
                HLA hla = new HLA(key, HLATypeSecondary.get(key), false, false);
                database.add(profileID, hla);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            res.status(500);
            return e.getMessage();
        }

        res.status(201);
        return "HLAType added";
    }

    /**
     * Deletes the given HLAType.
     *
     * @param req the empty request body.
     * @param res the empty response.
     * @return the response body.
     */
    public static String delete(Request req, Response res) {
        HLADAO database = DAOFactory.getHLADAO();
        Integer profileID = Integer.valueOf(req.params("id"));

        try {
            database.removeAll(profileID);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            res.status(500);
            return e.getMessage();
        }

        res.status(201);
        return "HLAs deleted";
    }
}
