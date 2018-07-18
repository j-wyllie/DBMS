package odms.controller.profile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import odms.controller.history.HistoryController;
import odms.model.history.History;
import odms.model.profile.Condition;
import odms.view.profile.ProfileMedicalHistoryView;

public class ProfileConditionController {

    private ProfileMedicalHistoryView view;

    public ProfileConditionController(ProfileMedicalHistoryView profileMedicalHistoryView) {
        view = profileMedicalHistoryView;
    }

    /**
     * Gets all the cured conditions of the user
     *
     * @return the cured conditions of the user
     */
    public ArrayList<Condition> getCuredConditions() {
        ArrayList<Condition> curedConditions = new ArrayList<>();
        for (Condition condition : view.getCurrentProfile().getAllConditions()) {
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
    public ArrayList<Condition> getCurrentConditions() {
        //todo potentially add parent controller for conditions
        ArrayList<Condition> currentConditions = new ArrayList<>();
        for (Condition condition : view.getCurrentProfile().getAllConditions()) {
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
    public void removeCondition(Condition condition) {
        view.getCurrentProfile().getAllConditions().remove(condition);
    }

    public ArrayList<Condition> convertConditionObservableToArray(
            ObservableList<Condition> conditions) {
        ArrayList<Condition> toReturn = new ArrayList<>();
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
    public void delete() throws IOException {
        ArrayList<Condition> conditions = view.getSelectedConditions();
        for (Condition condition : conditions) {
            if (condition != null) {
                removeCondition(condition);
                LocalDateTime currentTime = LocalDateTime.now();
                History action = new History("profile", view.getCurrentProfile().getId(),
                        " removed condition", "(" + condition.getName() +
                        "," + condition.getDateOfDiagnosis() + "," + condition.getChronic() + "," +
                        condition.getDateCuredString() + ")", getCurrentConditions().indexOf(condition), currentTime);
                HistoryController.updateHistory(action);
            }
        }
    }

    /**
     * Button handler to handle toggle chronic button clicked, only available to clinicians
     */
    @FXML
    public void toggleChronic() {
        ArrayList<Condition> conditions = view.getSelectedConditions();
        for (Condition condition : conditions) {
            if (condition != null) {

                condition.setIsChronic(!condition.getChronic());
                if (condition.getChronic()) {
                    condition.setChronicText("CHRONIC");
                    condition.setIsCured(false);
                } else {
                    condition.setChronicText("");
                }

            }
        }
    }


    /**
     * Button handler to handle toggle cured button clicked, only available to clinicians
     *
     */
    @FXML
    public void toggleCured() {
        ArrayList<Condition> conditions = view.getSelectedConditions();
        for (Condition condition : conditions) {
            if (condition != null) {

                if (!condition.getChronic()) {
                    condition.setIsCured(!condition.getCured());
                } else {
                    System.out.println("condition must be unmarked as Chronic before being Cured!");
                }

                if (condition.getCured()) {
                    condition.setDateCured(LocalDate.now());
                } else {
                    condition.setDateCured(null);
                }

            }
        }
    }

    public void addCondition(Condition condition) {
        view.getCurrentProfile().getAllConditions().add(condition);
    }
}
