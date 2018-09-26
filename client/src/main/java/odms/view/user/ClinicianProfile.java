package odms.view.user;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import odms.commons.model.enums.UserType;
import odms.commons.model.user.User;
import odms.controller.CommonController;
import odms.controller.data.ImageDataIO;
import odms.controller.database.DAOFactory;
import odms.controller.database.common.CommonDAO;
import odms.controller.user.DataManagement;
import odms.controller.user.Display;
import odms.view.CommonView;
import odms.view.SettingsPopup;
import odms.view.SocialFeedTab;

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
    private Tab generalTab;
    @FXML
    private Tab searchTab;
    @FXML
    private Tab transplantTab;
    @FXML
    private Tab availableOrgansTab;
    @FXML
    private Tab socialFeedTab;
    @FXML
    private Tab organMapTab;
    @FXML
    private ImageView profileImage;
    @FXML
    private GridPane bannerPane;
    @FXML
    private Button logoutButton;
    @FXML
    private Button importBtn;

    private Display userProfileController = new Display(this);

    private TransplantWaitingList transplantWaitingList;

    private AvailableOrgans availableOrgansTabView;
    private SocialFeedTab socialFeed;

    /**
     * Button handler to make fields editable.
     *
     * @param event clicking on the edit button.
     */
    @FXML
    private void handleEditButtonClicked(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/view/ClinicianProfileEdit.fxml"));

        Scene scene = new Scene(fxmlLoader.load());
        ClinicianEdit v = fxmlLoader.getController();
        v.initialize(currentUser);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Edit profile");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Scene change to log in view.
     *
     * @param event clicking on the logout button.
     * @throws IOException if the scene cannot be changed.
     */
    @FXML
    private void handleLogoutButtonClicked(ActionEvent event) throws IOException {
        userProfileController.closeAllOpenStages();
        CommonDAO server = DAOFactory.getCommonDao();
        server.logout();
        currentUser = null;
        if (socialFeed != null) {
            socialFeed.pauseTimer();
        }
        if (availableOrgansTabView != null) {
            availableOrgansTabView.pauseTimers();
        }
        changeScene(event, "/view/Login.fxml", "Login");
    }

    /**
     * This set the transplantWaitingList to null, to prevent the transplant waiting list from being
     * updated if the waiting list tab is not open.
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
     * Opens settings popup on click.
     */
    @FXML
    private void handleSettingsButtonAction(ActionEvent event) throws IOException {
        Node source = (Node) event.getSource();
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/SettingsPopup.fxml"));
        stage.setTitle("Settings");
        stage.setResizable(false);
        stage.initOwner(source.getScene().getWindow());
        stage.setScene(new Scene(loader.load()));
        SettingsPopup controller = loader.getController();
        controller.initialize(currentUser);
        stage.show();
    }

    @FXML
    private void handleImportButtonAction() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "Data Files (*.csv)",
                "*.csv"
        );
        fileChooser.getExtensionFilters().add(extFilter);
        Stage stage = (Stage) clinicianFullName.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);
        DataManagement dataManagementController = new odms.controller.user.DataManagement(
                this);
        dataManagementController.handleFile(file, (Stage) clinicianFullName.getScene().getWindow());
    }

    /**
     * Initializes the controller for the console tab.
     */
    public void handleConsoleTabClicked() {
        if (consoleTab.isSelected()) {
            setTransplantWaitingListNull();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/UserConsoleTab.fxml"));
            try {
                consoleTab.setContent(loader.load());
                ConsoleTab console = loader.getController();
                consoleTab.setOnSelectionChanged(event -> {
                    if (!consoleTab.isSelected()) {
                        console.stopInputCapture();
                    } else {
                        console.captureInput();
                    }
                });
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Initializes the controller for the view users Tab.
     */
    public void handleViewUsersTabClicked() {
        if (listUsersTab.isSelected()) {
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
    }

    /**
     * Initializes the controller for the general details Tab.
     */
    public void handleGeneralTabClicked() {
        if (generalTab.isSelected()) {
            setTransplantWaitingListNull();
            if (currentUser != null) {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/view/UserGeneralTab.fxml"));
                try {
                    generalTab.setContent(loader.load());
                    UserGeneral userGeneralTabView = loader.getController();
                    userGeneralTabView.initialize(currentUser);
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

    /**
     * Initializes the controller for available organs. Adds onSelectionChange listeners to the tab
     * to pause and commence the timers.
     */
    public void handleTabAvailableClicked() {
        if (availableOrgansTab.isSelected()) {
            setTransplantWaitingListNull();
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/view/UserAvailableOrgansTab.fxml"));
            try {
                availableOrgansTab.setContent(loader.load());
                availableOrgansTabView = loader.getController();
                availableOrgansTabView.initialize(currentUser, this);

                availableOrgansTab.setOnSelectionChanged(event -> {
                    if (!availableOrgansTab.isSelected()) {
                        availableOrgansTabView.pauseTimers();
                    } else {
                        availableOrgansTabView.startTimers();
                    }
                });
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Initializes the map and organ expiry lists.
     */
    public void handleTabOrganMapClicked() {
        if (organMapTab.isSelected()) {
            setTransplantWaitingListNull();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/UserMapTab.fxml"));
            try {
                organMapTab.setContent(loader.load());
                OrganMap organMapTabView = loader.getController();
                organMapTabView.initialize(currentUser, this);
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
    }


    /**
     * Sets up the social feed tab when it's clicked. Adds a listener to start and top the timer.
     */
    @FXML
    private void handleSocialFeedTabClicked() {
        if (socialFeedTab.isSelected()) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/SocialFeedTab.fxml"));
            try {
                socialFeedTab.setContent(loader.load());
                socialFeed = loader.getController();
                socialFeed.initialise();

                socialFeedTab.setOnSelectionChanged(event -> {
                    if (!socialFeedTab.isSelected()) {
                        socialFeed.pauseTimer();
                    } else {
                        socialFeed.startTimer();
                    }
                });
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Hides/Shows certain nodes if the clinician does / does not have permission to view them.
     */
    private void setupAdmin() {
        if (currentUser.getUserType() == UserType.CLINICIAN) {
            importBtn.setDisable(true);
            listUsersTab.setDisable(true);
            consoleTab.setDisable(true);
        } else {
            importBtn.setDisable(false);
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
        if (searchTab.isSelected()) {
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
    }

    /**
     * Initializes the controller for the transplant waiting list.
     */
    public void handleTransplantWaitingListTabClicked() {
        if (transplantTab.isSelected()) {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/view/UserTransplantWaitingListTab.fxml"));
            try {
                transplantTab.setContent(loader.load());
                TransplantWaitingList userTransplantWaitingListTabView = loader.getController();
                transplantWaitingList = userTransplantWaitingListTabView;
                userTransplantWaitingListTabView.initialize(currentUser, this);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Adds a profile stage from the open stage list.
     *
     * @param s the stage to add
     */
    public void addToOpenProfileStages(Stage s) {
        userProfileController.addToOpenProfileStages(s);
    }

    /**
     * Removes a profile stage from the open stage list.
     *
     * @param stage the stage to close
     */
    public void closeStage(Stage stage) {
        userProfileController.removeStageFromProfileStages(stage);
        openProfileStages.remove(stage);
    }

    public TransplantWaitingList getTransplantWaitingList() {
        return transplantWaitingList;
    }

    public User getCurrentUser() {
        return currentUser;
    }
}
