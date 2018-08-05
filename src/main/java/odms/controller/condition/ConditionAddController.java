package odms.controller.condition;

import odms.controller.CommonController;
import odms.view.profile.ProfileAddConditionView;
import odms.controller.history.HistoryController;
import odms.model.history.History;
import odms.model.profile.Condition;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class ConditionAddController extends CommonController {
    private ProfileAddConditionView view;

    public ConditionAddController(ProfileAddConditionView v) {
        view = v;
    }

    public void add() throws Exception {

        Condition condition = parseCondition();
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

    public Condition parseCondition() {
        Condition condition;

        String name = view.getNameFieldText();
        LocalDate dateDiagnosed = view.getDateDiagnosed();
        Boolean isChronic = view.getIsChronic();
        LocalDate dob = view.getCurrentProfile().getDateOfBirth();

        // create condition
        if (view.getIsCured()) {
            LocalDate dateCured = view.getDateCured();
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
