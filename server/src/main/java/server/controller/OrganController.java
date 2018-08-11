package server.controller;

import java.util.HashSet;
import java.util.Set;
import odms.commons.model.enums.OrganEnum;
import odms.commons.model.profile.OrganConflictException;
import odms.commons.model.profile.Profile;
import org.sonar.api.internal.google.gson.Gson;
import org.sonar.api.internal.google.gson.JsonElement;
import server.model.database.DAOFactory;
import server.model.database.organ.OrganDAO;
import spark.Request;
import spark.Response;

public class OrganController {

    /**
     * Gets a list of all organs for a profile.
     * @param req sent to the endpoint.
     * @param res sent back.
     * @return the response body.
     */
    public String getAll(Request req, Response res) {
        Set<OrganEnum> organs;
        int profileId;
        boolean donated;
        boolean donating;
        boolean required;
        boolean received;

        try {
            profileId = Integer.valueOf(req.params("id"));
            donated = Boolean.valueOf(req.queryParams("donated"));
            donating = Boolean.valueOf(req.queryParams("donating"));
            required = Boolean.valueOf(req.queryParams("required"));
            received = Boolean.valueOf(req.queryParams("received"));
        } catch (Exception e) {
            res.status(500);
            return "Bad Request";
        }

        try {
            organs = getOrgans(new Profile(profileId), donated, donating, required, received);
        } catch (Exception e) {
            res.status(500);
            return e.getMessage();
        }

        Gson gson = new Gson();
        String responseBody = gson.toJson(organs);

        res.type("application/json");
        res.status(200);

        return responseBody;
    }

    /**
     * Adds an organ to a profile in persistent storage.
     * @param req sent to the endpoint.
     * @param res sent back.
     * @return the response body.
     */
    public String add(Request req, Response res) {
        int profileId;
        String organ;
        boolean donated;
        boolean donating;
        boolean required;
        boolean received;

        try {
            profileId = Integer.valueOf(req.params("id"));
            organ = req.body();
            donated = Boolean.valueOf(req.queryParams("donated"));
            donating = Boolean.valueOf(req.queryParams("donating"));
            required = Boolean.valueOf(req.queryParams("required"));
            received = Boolean.valueOf(req.queryParams("received"));
        } catch (Exception e) {
            res.status(500);
            return "Bad Request";
        }

        try {
            addOrgan(new Profile(profileId), organ, donated, donating, required, received);
        } catch (Exception e) {
            res.status(500);
            return e.getMessage();
        }

        res.status(201);
        return "Organ added";
    }

    /**
     * Removes an organ from a profile in persistent storage.
     * @param req sent to the endpoint.
     * @param res sent back.
     * @return the response body.
     */
    public String delete(Request req, Response res) {
        int profileId;
        String organ;
        boolean donated;
        boolean donating;
        boolean required;
        boolean received;

        try {
            profileId = Integer.valueOf(req.params("id"));
            organ = req.body();
            donated = Boolean.valueOf(req.queryParams("donated"));
            donating = Boolean.valueOf(req.queryParams("donating"));
            required = Boolean.valueOf(req.queryParams("required"));
            received = Boolean.valueOf(req.queryParams("received"));
        } catch (Exception e) {
            res.status(500);
            return "Bad Request";
        }

        try {
            removeOrgan(new Profile(profileId), organ, donated, donating, required, received);
        } catch (Exception e) {
            res.status(500);
            return e.getMessage();
        }

        res.status(200);
        return "Organ removed";
    }

    /**
     * Gets the organs from the database bases on organ usage.
     * @param profile to get the organs for.
     * @param donated are required.
     * @param donating are required.
     * @param required are required.
     * @param received are required.
     * @return the set of organs based on the criteria.
     */
    private Set<OrganEnum> getOrgans(Profile profile, boolean donated, boolean donating,
            boolean required, boolean received) {
        OrganDAO database = DAOFactory.getOrganDao();

        if (donated) {
            return database.getDonations(profile);
        }
        if (donating) {
            return database.getDonating(profile);
        }
        if (required) {
            return database.getRequired(profile);
        }
        if (received) {
            return database.getReceived(profile);
        }
        return new HashSet<>();
    }

    /**
     * Add an organ to a profile in persistent storage.
     * @param profile to add the organ to.
     * @param organ to add.
     * @param donated are required.
     * @param donating are required.
     * @param required are required.
     * @param received are required.
     * @throws OrganConflictException error.
     */
    private void addOrgan(Profile profile, String organ, boolean donated, boolean donating,
            boolean required, boolean received) throws OrganConflictException {
        OrganEnum organEnum = OrganEnum.valueOf(organ);
        OrganDAO database = DAOFactory.getOrganDao();

        if (donated) {
            database.addDonation(profile, organEnum);
        }
        if (donating) {
            database.addDonating(profile, organEnum);
        }
        if (required) {
            database.addRequired(profile, organEnum);
        }
        if (received) {
            database.addReceived(profile, organEnum);
        }
    }

    /**
     * Removes an organ from a profile in persistent storage.
     * @param profile to add the organ to.
     * @param organ to add.
     * @param donated are required.
     * @param donating are required.
     * @param required are required.
     * @param received are required.
     */
    private void removeOrgan(Profile profile, String organ, boolean donated, boolean donating,
            boolean required, boolean received) {
        OrganEnum organEnum = OrganEnum.valueOf(organ);
        OrganDAO database = DAOFactory.getOrganDao();

        if (donated) {
            database.removeDonation(profile, organEnum);
        }
        if (donating) {
            database.removeDonating(profile, organEnum);
        }
        if (required) {
            database.removeRequired(profile, organEnum);
        }
        if (received) {
            database.removeReceived(profile, organEnum);
        }
    }
}
