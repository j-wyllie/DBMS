package odms.controller.user;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import javafx.stage.Stage;
import odms.view.user.ClinicianProfileView;

public class UserProfileController {

    private Collection<Stage> openProfileStages = new ArrayList<>();

    private ClinicianProfileView view;

    public UserProfileController(ClinicianProfileView v) {
        view = v;
    }

    public boolean addToOpenProfileStages(Stage s) {
        return openProfileStages.add(s);
    }

    public boolean removeStageFromProfileStages(Stage stage) {
        return openProfileStages.remove(stage);
    }
}
