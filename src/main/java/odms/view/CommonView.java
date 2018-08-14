package odms.view;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import odms.controller.GuiMain;
import odms.controller.history.Redo;
import odms.controller.history.Undo;
import odms.model.profile.Profile;
import odms.model.user.User;
import odms.view.profile.Display;
import odms.view.profile.ProfileEdit;
import odms.view.user.ClinicianProfile;
import org.controlsfx.control.Notifications;

public class CommonView {
    private static boolean isEdited = false;

    protected static Collection<Stage> openProfileStages = new ArrayList<>();

    /**
     * Scene change to log in view.
     *
     * @param event clicking on the logout button.
     */
    @FXML
    protected void changeScene(ActionEvent event, String resourceName, String title) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource(resourceName));
        Scene newScene = new Scene(parent);
        Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        appStage.setTitle(title);
        appStage.setScene(newScene);
        appStage.show();
    }

    @FXML
    protected void createPopup(ActionEvent actionEvent, String fxmlFile, String title) {
        try {
            //todo create a general pop-up window method?
            Node source = (Node) actionEvent.getSource();
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource(fxmlFile));

            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.initOwner(source.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
        }
    }

    protected void createNewAdminWindow(User currentUser) {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/view/ClinicianProfile.fxml"));

        try {
            Scene scene = new Scene(fxmlLoader.load());

            ClinicianProfile controller = fxmlLoader.getController();
            controller.setCurrentUser(currentUser);
            controller.initialize();

            Stage stage = new Stage();
            stage.setTitle("Admin");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * checks whether the window has been edited
     *
     * @param stage
     * @return true if window has unsaved changes.
     */
    protected static boolean isEdited(Stage stage) {
        return stage.getTitle().contains("(*)");
    }

    /**
     * Button handler to make fields editable.
     *
     * @param event clicking on the edit button.
     */
    protected void handleProfileEditButtonClicked(ActionEvent event, Profile currentProfile, Boolean isOpenedByClinician) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/ProfileEdit.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        ProfileEdit controller = fxmlLoader.getController();
        controller.initialize(currentProfile, isOpenedByClinician);

        Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        appStage.setScene(scene);
        appStage.show();
    }

    /**
     * Button handler to make fields editable.
     *
     * @param event clicking on the edit button.
     */
    @FXML
    private void handleUserEditButtonClicked(ActionEvent event) throws IOException {
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

    private Redo redoController = new Redo();
    private Undo undoController = new Undo();

    public Boolean getEdited() {
        return isEdited;
    }

    private void setEdited(Boolean edited) {
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
    protected void showNotification(String editedField, ActionEvent event) throws IOException {
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

    /**
     * Shows a notification on the parent of which the event occurred shows for 2.5 seconds. For
     * unsuccessful events.
     *
     * @param event       The event which is wanted to trigger a notification
     * @param editedField String of which is the thing edited.
     */
    @FXML
    protected void showNotificationFailed(String editedField, ActionEvent event) throws IOException {
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        if (currentStage.getTitle().contains("(*)")) {
            currentStage.setTitle(currentStage.getTitle().replace("(*)", ""));
        }

        Notifications.create()
                .title("Edit Unsuccessful")
                .text("The " + editedField + " changes were not changed.")
                .hideAfter(Duration.millis(2500))
                .position(Pos.BOTTOM_LEFT)
                .owner(currentStage)
                .show();
    }

    /**
     * Creates a new window when a row in the search table is double clicked. The new window
     * contains a donors profile.
     *
     * @param profile The donor object that has been clicked on
     * @param parentView The parent view of the stage being created
     */
    @FXML
    protected void createNewDonorWindow(Profile profile, ClinicianProfile parentView) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/view/ProfileDisplay.fxml"));

            Scene scene = new Scene(fxmlLoader.load());
            Display controller = fxmlLoader.getController();
            controller.initialize(profile, true, parentView.getTransplantWaitingList());

            Stage stage = new Stage();
            if (profile.getPreferredName() != null && !profile.getPreferredName().isEmpty()) {
                stage.setTitle(profile.getPreferredName() + "'s profile");
            } else {
                stage.setTitle(profile.getFullName() + "'s profile");
            }
            stage.setScene(scene);
            parentView.addToOpenProfileStages(stage);
            stage.show();
            stage.setOnCloseRequest((WindowEvent event) -> {
                parentView.closeStage(stage);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * File picker to choose only supported image types.
     *
     * @param pictureText user feedback text to update on profile picture edit
     */
    protected File chooseImage(Label pictureText, Stage stage) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Open Image File");
        ExtensionFilter extFilter = new ExtensionFilter(
                "Image Files (*.jpg;*.png)",
                "*.jpg", "*.png"
        );
        chooser.getExtensionFilters().add(extFilter);

        File file = chooser.showOpenDialog(stage);

        if (file != null) {
            if (file.length() > 1000000) {
                pictureText.setText("Photos must be less than 1 mb! \n"
                        + "Choose another ");
                return null;
            }

            pictureText.setText(file.getName());
        }

        return file;
    }

    /**
     * Checks if there are unsaved changes in any open window.
     *
     * @return true if there are unsaved changes.
     */
    public static boolean checkUnsavedChanges(Stage currentStage) {
        for (Stage stage : openProfileStages) {
            //todo maybe need to move isEdited from common controller to common view?
            if (isEdited(stage) && stage.isShowing()) {
                return true;
            }
        }

        return isEdited(currentStage);
    }

    /**
     * returns a string that is the file extension of given file
     *
     * @param file File to retrieve extension from
     */
    protected String getFileExtension(File file) {
        String name = file.getName();
        try {
            return name.substring(name.lastIndexOf('.') + 1);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * closes all open profile windows that the user has opened.
     */
    protected static void closeAllOpenProfiles() {
        for (Stage stage : openProfileStages) {
            if (stage.isShowing()) {
                stage.close();
            }
        }
    }
}
