package odms.view;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Duration;
import odms.controller.GuiMain;
import odms.controller.history.RedoController;
import odms.controller.history.UndoController;
import org.controlsfx.control.Notifications;

public class CommonView {
    private static boolean isEdited;
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
     * checks whether the window has been edited
     *
     * @param stage
     * @return true if window has unsaved changes.
     */
    protected static boolean isEdited(Stage stage) {
        return stage.getTitle().contains("(*)");
//        FXMLLoader loader = (FXMLLoader) stage.getScene().getUserData();
//        CommonController controller = loader.getController();
//        controller.toString();
//        return controller.getEdited();
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

    public Boolean getEdited() {
        return isEdited;
    }

    public void setEdited(Boolean edited) {
        isEdited = edited;
    }

    /**
     * Changes the title of the stage to include an astrix to indicate a value has been edited.
     *
     * @param stage the stage to be edited.
     */
    public void editTrueStage(Stage stage) {
        if (!stage.getTitle().contains("(*)")) {
            stage.setTitle(stage.getTitle() + " (*)");
        }
        setEdited(true);
    }

    /**
     * Changes the Edit profile title to include an astrix to indicate a value has been edited.
     *
     * @param event Any key event within the text boxes.
     */
    @FXML
    protected void editTrueKey(javafx.scene.input.KeyEvent event) throws IOException {
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        if (!currentStage.getTitle().contains("(*)")) {
            currentStage.setTitle(currentStage.getTitle() + " (*)");
        }
        setEdited(true);
    }

    /** //todo should these be in commonview?
     * Changes the Edit profile title to include an astrix to indicate a value has been edited.
     *
     * @param event Any click event within the text boxes.
     */
    @FXML
    protected void editTrueClick(MouseEvent event) {
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        if (!currentStage.getTitle().contains("(*)")) {
            currentStage.setTitle(currentStage.getTitle() + " (*)");
        }
        setEdited(true);
    }

    /**
     * Changes the title of the parent window to include an astrix to indicate a value has been
     * edited.
     *
     * @param event Any click event within the text boxes.
     */
    @FXML
    protected void editTrueAction(ActionEvent event, boolean forOwner) {
        if (forOwner) {
            Stage currentStage = (Stage) ((Node) event.getTarget()).getParent().getScene()
                    .getWindow();
            currentStage = (Stage) currentStage.getOwner();
            if (!currentStage.getTitle().contains("(*)")) {
                currentStage.setTitle(currentStage.getTitle() + " (*)");
            }
        } else {
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            if (!currentStage.getTitle().contains("(*)")) {
                currentStage.setTitle(currentStage.getTitle() + " (*)");
            }
        }
    }

    /**
     * Shows a notification on the parent of which the event occurred shows for 2.5 seconds.
     *
     * @param event       The event which is wanted to trigger a notification
     * @param editedField String of which is the thing edited.
     */
    @FXML
    public void showNotification(String editedField, ActionEvent event) throws IOException {
        //todo modify this method by making it common view possibly
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        if (currentStage.getTitle().contains("(*)")) {
            currentStage.setTitle(currentStage.getTitle().replace("(*)", ""));
        }

        Notifications.create()
                .title("Edit Successful")
                .text("The " + editedField + " was edited successfully!")
                .hideAfter(Duration.millis(2500))
                .position(Pos.BOTTOM_LEFT)
                .owner(currentStage)
                .show();
    }

}
