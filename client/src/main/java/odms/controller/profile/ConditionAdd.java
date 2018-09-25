package odms.controller.profile;

import odms.commons.model.profile.Condition;
import odms.commons.model.profile.Profile;
import odms.controller.CommonController;
import odms.controller.database.DAOFactory;
import odms.controller.database.condition.ConditionDAO;
import odms.controller.history.CurrentHistory;
import odms.commons.model.history.History;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class ConditionAdd extends CommonController {

    public void add(Profile profile, Condition condition) {
        ConditionDAO server = DAOFactory.getConditionDao();
        server.add(profile, condition);

        profile.getAllConditions().add(condition);
        LocalDateTime currentTime = LocalDateTime.now();
        History action = new History("profile", profile.getId(),
                "added condition",
                "(" + condition.getName() + "," + condition.getDateOfDiagnosisString() +
                        "," + condition.getChronic() + ")",
                  getCurrentConditions(profile).indexOf(condition), currentTime);
        CurrentHistory.updateHistory(action);
    }

    /**
     * Gets all the current conditions of the user
     *
     * @return the current conditions of the user
     */
    public ArrayList<Condition> getCurrentConditions(Profile profile) {
        ArrayList<Condition> currentConditions = new ArrayList<>();
        for (Condition condition : profile.getAllConditions()) {
            if (!condition.getCured()) {
                currentConditions.add(condition);
            }
        }
        return currentConditions;
    }

    public Condition parseCondition(String name, LocalDate dateDiagnosed, Boolean isChronic, Boolean isCured, Profile profile, LocalDate dateCured) {
        LocalDate dob = profile.getDateOfBirth();
        Condition condition;

        // create condition
        if (isCured) {
            condition = new Condition(name, dateDiagnosed, dateCured, isChronic);
        } else {
            condition = new Condition(name, dateDiagnosed, isChronic);
        }

        // throw exception if not valid
        if (dob.isAfter(dateDiagnosed) || dateDiagnosed.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException();
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException();
        }

        return condition;
    }
}
