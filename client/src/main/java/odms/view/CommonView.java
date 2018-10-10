package odms.view;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;
import odms.commons.model.profile.Profile;
import odms.commons.model.user.User;
import odms.view.profile.Display;
import odms.view.profile.ProfileEdit;
import odms.view.user.ClinicianProfile;
import org.controlsfx.control.Notifications;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Contains common methods between the views.
 */
@Slf4j
public class CommonView {

    private static boolean isEdited = false;

    protected static Collection<Stage> openProfileStages = new ArrayList<>();

    /**
     * Scene change to log in view.
     *
     * @param event clicking on the logout button.
     */
    @FXML
    protected void changeScene(ActionEvent event, String resourceName, String title)
            throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource(resourceName));
        Scene newScene = new Scene(parent);
        Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        appStage.setTitle(title);
        appStage.setScene(newScene);
        appStage.show();
    }

    @FXML
    protected FXMLLoader createPopup(ActionEvent actionEvent, String fxmlFile, String title) {
        try {
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
            return fxmlLoader;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * Creates a new admin window.
     * @param currentUser the current user.
     */
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
            log.error(e.getMessage(), e);
        }
    }

    /**
     * checks whether the window has been edited.
     *
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
    protected void handleProfileEditButtonClicked(ActionEvent event, Profile currentProfile,
            Boolean isOpenedByClinician, User currentUser) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/ProfileEdit.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        ProfileEdit controller = fxmlLoader.getController();
        controller.initialize(currentProfile, isOpenedByClinician, currentUser);

        Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        appStage.setScene(scene);
        appStage.show();
    }

    public Boolean getEdited() {
        return isEdited;
    }

    private static void setEdited(Boolean edited) {
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

    /**
      * indicate a value has been edited.
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
     * @param event The event which is wanted to trigger a notification
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
     * @param event The event which is wanted to trigger a notification
     * @param editedField String of which is the thing edited.
     */
    @FXML
    protected void showNotificationFailed(String editedField, ActionEvent event)
            throws IOException {
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
     * @param user the current logged in clin/admin
     */
    @FXML
    protected void createNewDonorWindow(Profile profile, ClinicianProfile parentView, User user) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/view/ProfileDisplay.fxml"));

            Scene scene = new Scene(fxmlLoader.load());
            Display controller = fxmlLoader.getController();
            controller.initialize(profile, true, parentView.getTransplantWaitingList(), user);

            Stage stage = new Stage();
            if (profile.getPreferredName() != null && !profile.getPreferredName().isEmpty()) {
                stage.setTitle(profile.getPreferredName() + "'s profile");
            } else {
                stage.setTitle(profile.getFullName() + "'s profile");
            }
            stage.setScene(scene);
            parentView.addToOpenProfileStages(stage);
            stage.show();
            stage.setOnCloseRequest((WindowEvent event) -> parentView.closeStage(stage));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
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
            if (isEdited(stage) && stage.isShowing()) {
                return true;
            }
        }

        return isEdited(currentStage);
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

    protected void hideTableHeader(TableView tableView) {
        tableView.widthProperty().addListener((observableValue, oldValue, newValue) -> {
            Pane header = (Pane) tableView.lookup("TableHeaderRow");
            if (header.isVisible()) {
                header.setMaxHeight(0);
                header.setMinHeight(0);
                header.setPrefHeight(0);
                header.setVisible(false);
            }
        });
    }

    /**
     * Checks if the nhi is valid (3 characters (no O or I) followed by 4 numbers).
     *
     * @param nhi the nhi to check.
     * @return true if valid and false if not valid.
     */
    static boolean isValidNHI(String nhi) {
        String pattern = "^[A-HJ-NP-Z]{3}\\d{4}$";
        Pattern r = Pattern.compile(pattern);

        Matcher m = r.matcher(nhi.toUpperCase());
        return m.find();
    }

    /**
     * Tries to connect to the twitter api.
     *
     * @return True if the connection can be established.
     */
    protected static boolean netIsAvailable() {
        try {
            final URL url = new URL("https://google.com/");
            final URLConnection conn = url.openConnection();
            conn.setConnectTimeout(1000);
            conn.connect();
            conn.getInputStream().close();
            return true;

        } catch (MalformedURLException e) {
            log.error(e.getMessage(), e);
        } catch (IOException e) {
            return false;
        }
        return false;
    }
}
