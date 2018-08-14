package odms.controller.user;

import java.util.ArrayList;
import java.util.Collection;

import javafx.stage.Stage;
import odms.view.user.ClinicianProfile;

public class Display {

    private Collection<Stage> openProfileStages = new ArrayList<>();

    private ClinicianProfile view;

    public Display(ClinicianProfile v) {
        view = v;
    }

    public boolean addToOpenProfileStages(Stage s) {
        return openProfileStages.add(s);
    }

    public boolean removeStageFromProfileStages(Stage stage) {
        return openProfileStages.remove(stage);
    }
}
