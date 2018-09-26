package odms.controller.profile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import odms.commons.model.history.History;
import odms.commons.model.profile.Condition;
import odms.commons.model.profile.Profile;
import odms.controller.database.DAOFactory;
import odms.controller.database.condition.ConditionDAO;
import odms.controller.history.CurrentHistory;
import odms.view.profile.ProfileMedicalHistory;

public class ConditionGeneral {

    /**
     * Gets all the cured conditions of the user.
     * @return the cured conditions of the user
     */
    public List<Condition> getCuredConditions(Profile p) {
        List<Condition> curedConditions = new ArrayList<>();
        for (Condition condition : p.getAllConditions()) {
            if (condition.getCured()) {
                curedConditions.add(condition);
            }
        }

        return curedConditions;
    }

    /**
     * Gets all the current conditions of the user
     *
     * @return the current conditions of the user
     */
    public List<Condition> getCurrentConditions(Profile p) {
        //todo potentially add parent controller for conditions
        List<Condition> currentConditions = new ArrayList<>();
        for (Condition condition : p.getAllConditions()) {
            if (!condition.getCured()) {
                currentConditions.add(condition);
            }
        }
        return currentConditions;
    }



    /**
     * removes a condition from the user
     *
     * @param condition to be removed
     */
    public void removeCondition(Condition condition,Profile p) {
        p.getAllConditions().remove(condition);
        ConditionDAO server = DAOFactory.getConditionDao();
        server.remove(condition);
    }

    public List<Condition> convertConditionObservableToArray(
            ObservableList<Condition> conditions) {
        //todo not called might have a use though or another class isn't calling it properly
        List<Condition> toReturn = new ArrayList<>();
        for (int i = 0; i < conditions.size(); i++) {
            if (conditions.get(i) != null) {
                toReturn.add(conditions.get(i));
            }
        }
        return toReturn;
    }


    /**
     * Button handler to handle delete button clicked, only available to clinicians
     *
     */
    @FXML
    public void delete(Profile p, List<Condition> conditions) throws IOException {
        for (Condition condition : conditions) {
            if (condition != null) {
                removeCondition(condition,p);
            }
        }
    }

    /**
     * Button handler to handle toggle chronic button clicked, only available to clinicians
     */
    @FXML
    public void toggleChronic(Profile p, List<Condition> conditions) {
        for (Condition condition : conditions) {
            if (condition != null) {

                condition.setIsChronic(!condition.getChronic());

                if (condition.getChronic()) {
                    condition.setChronicText("CHRONIC");
                    condition.setIsCured(false);
                } else {
                    condition.setChronicText("");
                }
                ConditionDAO server = DAOFactory.getConditionDao();
                server.update(condition);
            }
        }
    }


    /**
     * Button handler to handle toggle cured button clicked, only available to clinicians
     *
     */
    @FXML
    public void toggleCured(Profile p, List<Condition> conditions) {
        for (Condition condition : conditions) {
            if (condition != null) {

                if (!condition.getChronic()) {
                    condition.setIsCured(!condition.getCured());
                } else {
                    throw new IllegalArgumentException("Can not cure if Chronic");
                }

                if (condition.getCured()) {
                    condition.setDateCured(LocalDate.now());
                } else {
                    condition.setDateCured(null);
                }
                ConditionDAO server = DAOFactory.getConditionDao();
                server.update(condition);
            }
        }
    }

    public void addCondition(Condition condition,Profile p) {
        p.getAllConditions().add(condition);
        ConditionDAO server = DAOFactory.getConditionDao();
        server.add(p, condition);
    }

}
