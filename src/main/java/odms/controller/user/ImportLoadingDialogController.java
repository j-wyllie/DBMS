package odms.controller.user;

import javafx.stage.Stage;
import odms.controller.AlertController;
import odms.controller.profile.ProfileImportTask;
import odms.view.user.ImportLoadingDialogView;

public class ImportLoadingDialogController {

    private final ImportLoadingDialogView view;

    private ProfileImportTask profileImportTask;


    public ImportLoadingDialogController(ImportLoadingDialogView v) {
        view = v;
    }

}
