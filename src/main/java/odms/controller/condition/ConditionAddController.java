package odms.controller.condition;

import odms.view.profile.ProfileAddConditionView;
import odms.controller.history.HistoryController;
import odms.model.history.History;
import odms.model.profile.Condition;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import odms.view.profile.ProfileMedicalHistoryView;

public class ConditionAddController {
    private ProfileAddConditionView view;

    public ConditionAddController(ProfileAddConditionView v) {
        view = v;
    }

    public void add() throws Exception {
        Condition condition = checkIfValid();
        view.getCurrentProfile().getAllConditions().add(condition);
        LocalDateTime currentTime = LocalDateTime.now();
        History action = new History("profile", view.getCurrentProfile().getId(),
                "added condition",
                "(" + condition.getName() + "," + condition.getDateOfDiagnosisString() +
                        "," + condition.getChronic() + ")",
                  getCurrentConditions().indexOf(condition), currentTime);
        HistoryController.updateHistory(action);
    }

    /**
     * Gets all the current conditions of the user
     *
     * @return the current conditions of the user
     */
    public ArrayList<Condition> getCurrentConditions() {
        ArrayList<Condition> currentConditions = new ArrayList<>();
        for (Condition condition : view.getCurrentProfile().getAllConditions()) {
            if (!condition.getCured()) {
                currentConditions.add(condition);
            }
        }
        return currentConditions;
    }

    public Condition checkIfValid() {
        String name = view.getNameFieldText();
        LocalDate dateDiagnosed = view.getDateDiagnosed();
        Boolean isChronic = view.getIsChronic();
        LocalDate dateCured = view.getDateCured();
        Condition condition;
        LocalDate dob = view.getCurrentProfile().getDateOfBirth();
        if (dob.isAfter(dateDiagnosed) || dateDiagnosed.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException();
        }
        if (view.getIsCured()) {
            if (dateCured.isAfter(LocalDate.now()) || dob.isAfter(dateCured) || dateDiagnosed
                    .isAfter(dateCured)) {
                throw new IllegalArgumentException();
            } else {
                condition = new Condition(name, dateDiagnosed, dateCured, isChronic);
            }
        } else {
            condition = new Condition(name, dateDiagnosed, isChronic);
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException();
        }
        return condition;
    }
}
