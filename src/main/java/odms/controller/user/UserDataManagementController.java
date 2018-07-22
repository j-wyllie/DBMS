package odms.controller.user;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import odms.controller.GuiMain;
import odms.controller.data.ProfileDataIO;
import odms.model.user.User;
import odms.view.user.ClinicianProfileView;

import java.io.File;
import java.io.IOException;
import odms.view.user.UserDataManagementTabView;

public class UserDataManagementController {

    UserDataManagementTabView view;

    public UserDataManagementController(UserDataManagementTabView v) {
        view = v;
    }

    /**
     * Imports new json file. Closes all open windows and re-initializes the admin view.
     *
     * @param stage Stage to be close
     * @param file  file to be set as database
     */
    public void importAndCloseWindows(Stage stage, File file, User currentUser) {
        GuiMain.setCurrentDatabase(ProfileDataIO.loadData(file.getPath()));

        ClinicianProfileView.closeAllOpenProfiles();
        stage.close();

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/view/ClinicianProfile.fxml"));

        try {
            Scene scene = new Scene(fxmlLoader.load());

            ClinicianProfileView v = fxmlLoader.getController();
            v.setCurrentUser(currentUser);
            v.initialize();

            stage = new Stage();
            stage.setTitle("Admin");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
