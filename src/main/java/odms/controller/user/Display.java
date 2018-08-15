package odms.controller.user;

import java.util.ArrayList;
import java.util.Collection;

import javafx.stage.Stage;
import odms.view.user.ClinicianProfile;

/**
 * Controller class for the ClinicianProfile view.
 */
public class Display {

    private Collection<Stage> openProfileStages = new ArrayList<>();

    private ClinicianProfile view;

    /**
     * Sets the view variable.
     * @param v ClinicianProfile view object
     */
    public Display(ClinicianProfile v) {
        view = v;
    }

    /**
     * Add a stage to the list of open profile stages.
     * @param s profile stage
     * @return boolean true is stage added successfully
     */
    public boolean addToOpenProfileStages(Stage s) {
        return openProfileStages.add(s);
    }

    /**
     * Remove a stage from the list of open profile stages.
     * @param stage profile stage
     * @return boolean true is stage removed successfully
     */
    public boolean removeStageFromProfileStages(Stage stage) {
        return openProfileStages.remove(stage);
    }

    /**
     * closes all open profile windows.
     */
    public void closeAllOpenStages() {
        for (Stage stage : openProfileStages) {
            stage.close();
        }
    }
}
