package odms.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import odms.controller.CommonController;

import java.io.IOException;
import odms.controller.GuiMain;
import odms.controller.history.RedoController;
import odms.controller.history.UndoController;
import odms.controller.profile.ProfileEditController;

public class CommonView {
    /**
     * Scene change to log in view.
     *
     * @param event clicking on the logout button.
     */
    @FXML
    protected void changeScene(ActionEvent event, String resourceName) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource(resourceName));
        Scene newScene = new Scene(parent);
        Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        appStage.setScene(newScene);
        appStage.show();
    }

    /**
     * Button handler to make fields editable.
     *
     * @param event clicking on the edit button.
     */
    @FXML
    private void handleEditButtonClicked(ActionEvent event) throws IOException {
//        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/ProfileEdit.fxml"));
//        Scene scene = new Scene(fxmlLoader.load());
//        ProfileEditController controller = fxmlLoader.getController();
//        controller.setCurrentProfile(currentProfile);
//        controller.setIsClinician(isOpenedByClinician);
//        controller.initialize();
//
//        Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
//
//        appStage.setScene(scene);
//        appStage.show();
    }

    /**
     * Button handler to undo last action.
     */
    @FXML
    private void handleUndoButtonClicked() {
        undoController.undo(GuiMain.getCurrentDatabase());
    }

    /**
     * Button handler to redo last undo action.
     */
    @FXML
    private void handleRedoButtonClicked() {
        redoController.redo(GuiMain.getCurrentDatabase());
    }

    private RedoController redoController = new RedoController();
    private UndoController undoController = new UndoController();

}
