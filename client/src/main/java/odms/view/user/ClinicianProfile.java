package odms.view.user;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import odms.commons.model.enums.UserType;
import odms.commons.model.user.User;
import odms.controller.data.ImageDataIO;
import odms.controller.user.Display;
import odms.view.CommonView;

/**
 * Handles all of the tabs for the user profile view.
 */
@Slf4j
public class ClinicianProfile extends CommonView {
    private User currentUser;

    @FXML
    private Label clinicianFullName;
    @FXML
    private Label roleLabel;
    @FXML
    private Tab listUsersTab;
    @FXML
    private Tab consoleTab;
    @FXML
    private Tab dataManagementTab;
    @FXML
    private Tab generalTab;
    @FXML
    private Tab searchTab;
    @FXML
    private Tab transplantTab;
    @FXML
    private Tab availableOrgansTab;
    @FXML
    private Tab organMapTab;
    @FXML
    private ImageView profileImage;
    @FXML
    private GridPane bannerPane;
    @FXML
    private Button logoutButton;

    private Display userProfileController = new Display(this);

    private TransplantWaitingList transplantWaitingList;

    /**
     * Scene change to log in view.
     *
     * @param event clicking on the logout button.
     * @throws IOException if the scene cannot be changed.
     */
    @FXML
    private void handleLogoutButtonClicked(ActionEvent event) throws IOException {
        userProfileController.closeAllOpenStages();
        currentUser = null;
        changeScene(event, "/view/Login.fxml", "Login");
    }

    /**
     * This set the transplantWaitingList to null, to prevent the transplant waiting list from
     * being updated if the waiting list tab is not open
     */
    private void setTransplantWaitingListNull() {
        transplantWaitingList = null;
    }

    /**
     * Sets all the clinicians details in the GUI.
     */
    @FXML
    private void setClinicianDetails() {
        roleLabel.setText(currentUser.getUserType().getName());
        clinicianFullName.setText(currentUser.getName());
    }

    /**
     * Initializes the controller for the console tab.
     */
    public void handleConsoleTabClicked() {
        setTransplantWaitingListNull();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/UserConsoleTab.fxml"));
        try {
            consoleTab.setContent(loader.load());
            ConsoleTab console = loader.getController();
            // don't initialize as it will double print.
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * Initializes the controller for the view users Tab.
     */
    public void handleViewUsersTabClicked() {
        setTransplantWaitingListNull();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ListUsersTab.fxml"));
        try {
            listUsersTab.setContent(loader.load());
            UsersList listUsersView = loader.getController();
            listUsersView.initialize((Stage) clinicianFullName.getScene().getWindow());
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * Initializes the controller for the general details Tab
     */
    public void handleGeneralTabClicked() {
        setTransplantWaitingListNull();
        if (currentUser != null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/UserGeneralTab.fxml"));
            try {
                generalTab.setContent(loader.load());
                UserGeneral userGeneralTabView = loader.getController();
                userGeneralTabView.initialize(currentUser);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Initializes the controller for the data management tab.
     */
    public void handleTabDataManagementClicked() {
        setTransplantWaitingListNull();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/DataManagement.fxml"));
        try {
            dataManagementTab.setContent(loader.load());
            DataManagement userDataManagementTabView = loader.getController();
            userDataManagementTabView.initialize(currentUser);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * Initializes the controller for available organs.
     */
    public void handleTabAvailableClicked() {
        setTransplantWaitingListNull();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/UserAvailableOrgansTab.fxml"));
        try {
            availableOrgansTab.setContent(loader.load());
            AvailableOrgans availableOrgansTabView = loader.getController();
            availableOrgansTabView.initialize(currentUser, this);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * Initializes the map and organ expiry lists.
     */
    public void handleTabOrganMapClicked() {
        setTransplantWaitingListNull();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/UserMapTab.fxml"));
        try {
            organMapTab.setContent(loader.load());
            OrganMap organMapTabView = loader.getController();
            System.out.println(organMapTabView);
            organMapTabView.initialize(currentUser, this);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Hides/Shows certain nodes if the clinician does / does not have permission to view them.
     */
    private void setupAdmin() {
        if (currentUser.getUserType() == UserType.CLINICIAN) {
            dataManagementTab.setDisable(true);
            listUsersTab.setDisable(true);
            consoleTab.setDisable(true);
        } else {
            dataManagementTab.setDisable(false);
            listUsersTab.setDisable(false);
            consoleTab.setDisable(false);

            bannerPane.getStyleClass().clear();
            bannerPane.getStyleClass().add("banner-admin");

            logoutButton.getStyleClass().clear();
            logoutButton.getStyleClass().add("button-admin-logout");
        }
    }

    /**
     * Sets the profile image.
     *
     * @throws MalformedURLException if the URL to the image is malformed
     */
    private void setProfileImage() throws MalformedURLException {
        File image = ImageDataIO.getImagePath(currentUser.getPictureName());

        if (image == null || !image.exists()) {
            image = new File(
                    getClass().getResource("/profile_images/default.png").getFile()
            );
        }
        profileImage.setImage(new Image(image.toURI().toURL().toString()));
    }

    /**
     * Sets up all of the tabs and opens the general tab.
     */
    public void initialize() {
        if (currentUser != null) {
            handleGeneralTabClicked();
            setClinicianDetails();
            setupAdmin();
            try {
                setProfileImage();
            } catch (MalformedURLException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    /**
     * Initializes the controller for the profile search Tab.
     */
    public void handleSearchTabClicked() {
        setTransplantWaitingListNull();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/UserSearchTab.fxml"));
        try {
            searchTab.setContent(loader.load());
            Search userSearchView = loader.getController();
            userSearchView.initialize(currentUser, this);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * Initializes the controller for the transplant waiting list.
     */
    public void handleTransplantWaitingListTabClicked() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/UserTransplantWaitingListTab.fxml"));
        try {
            transplantTab.setContent(loader.load());
            TransplantWaitingList userTransplantWaitingListTabView = loader.getController();
            transplantWaitingList = userTransplantWaitingListTabView;
            userTransplantWaitingListTabView.initialize(currentUser, this);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * Adds a profile stage from the open stage list.
     * @param s the stage to add
     */
    public boolean addToOpenProfileStages(Stage s) {
        return userProfileController.addToOpenProfileStages(s);
    }

    /**
     * Removes a profile stage from the open stage list.
     * @param stage the stage to close
     */
    public void closeStage(Stage stage) {
        userProfileController.removeStageFromProfileStages(stage);
        openProfileStages.remove(stage);
    }

    public TransplantWaitingList getTransplantWaitingList() {
        return transplantWaitingList;
    }
}
