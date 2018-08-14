package odms.view.user;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.stage.Stage;
import odms.controller.user.Display;
import odms.model.enums.OrganEnum;
import odms.model.profile.Profile;
import odms.model.user.User;
import odms.model.enums.UserType;
import odms.model.user.User;
import odms.view.CommonView;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Handles all of the tabs for the user profile view.
 */
public class ClinicianProfile extends CommonView {
    private User currentUser;

    @FXML
    private Label clinicianFullName;

    @FXML
    private Label donorStatusLabel;
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

    private Display userProfileController = new Display(this);

    /**
     * Scene change to log in view.
     *
     * @param event clicking on the logout button.
     * @throws IOException if the scene cannot be changed.
     */
    @FXML
    private void handleLogoutButtonClicked(ActionEvent event) throws IOException {
        currentUser = null;
        changeScene(event, "/view/Login.fxml");
    }

    /**
     * Sets all the clinicians details in the GUI.
     */
    @FXML
    private void setClinicianDetails() {
        donorStatusLabel.setText(currentUser.getUserType().getName());
        clinicianFullName.setText(currentUser.getName());
    }

    /**
     * Initializes the controller for the console tab.
     */
    public void handleConsoleTabClicked() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/UserConsoleTab.fxml"));
        try {
            consoleTab.setContent(loader.load());
            ConsoleTab userConsoleTabView = loader.getController();
            userConsoleTabView.initialize();
        } catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    /**
     * Initializes the controller for the view users Tab.
     */
    public void handleViewUsersTabClicked() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ListUsersTab.fxml"));
        try {
            listUsersTab.setContent(loader.load());
            UsersList listUsersView = loader.getController();
            listUsersView.initialize((Stage) clinicianFullName.getScene().getWindow());
        } catch (IOException e){
            e.printStackTrace();

            System.out.println(e.getMessage());
        }
    }

    /**
     * Initializes the general tab controller.
     */
    public void handleGeneralTabClicked() {
        if (currentUser != null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/UserGeneralTab.fxml"));
            try {
                generalTab.setContent(loader.load());
                UserGeneral userGeneralTabView = loader.getController();
                userGeneralTabView.initialize(currentUser);
            } catch (IOException e){
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * Initializes the controller for the data management tab.
     */
    public void handleTabDataManagementClicked() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/DataManagement.fxml"));
        try {
            dataManagementTab.setContent(loader.load());
            DataManagement userDataManagementTabView = loader.getController();
            userDataManagementTabView.initialize(currentUser);
        } catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    /**
     * Initializes the controller for available organs.
     */
    public void handleTabAvailableClicked() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/UserAvailableOrgansTab.fxml"));
        try {
            availableOrgansTab.setContent(loader.load());
            AvailableOrgans availableOrgansTabView = loader.getController();
            availableOrgansTabView.initialize(currentUser, this);
        } catch (IOException e){
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
        }
    }

    /**
     * Sets up all of the tabs and opens the general tab.
     */
    public void initialize() {
        if (currentUser != null) {
            handleGeneralTabClicked();
            setClinicianDetails();
            setupAdmin();
        }
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    /**
     * Initializes the search tab controller.
     */
    public void handleSearchTabClicked() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/UserSearchTab.fxml"));
        try {
            searchTab.setContent(loader.load());
            Search userSearchView = loader.getController();
            userSearchView.initialize(currentUser, this);
        } catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    /**
     * Initializes the controller for the transplant waiting list.
     */
    public void handleTransplantWaitingListTabClicked() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/UserTransplantWaitingListTab.fxml"));
        try {
            Thread checkOrgan = new Thread() {
                public void run() {
                    try {
                        odms.controller.user.AvailableOrgans controller = new odms.controller.user.AvailableOrgans();
                        List<Map.Entry<Profile, OrganEnum>> availableOrgans = controller
                                .getAllOrgansAvailable();
                        for(Map.Entry<Profile, OrganEnum> m : availableOrgans) {
                            controller.checkOrganExpired(m.getValue(), m.getKey(), m);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            };
            checkOrgan.start();
            transplantTab.setContent(loader.load());
            TransplantWaitingList userTransplantWaitingListTabView = loader.getController();
            userTransplantWaitingListTabView.initialize(currentUser, this);
        } catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    /**
     * Adds a stage to the list of currently open profile stages.
     * @param s stage to add.
     */
    public void addToOpenProfileStages(Stage s) {
        userProfileController.addToOpenProfileStages(s);
    }

    /**
     * Removes a stage from the currently open stages.
     * @param stage stage to remove.
     */
    public void closeStage(Stage stage) {
        userProfileController.removeStageFromProfileStages(stage);
        openProfileStages.remove(stage);
    }
}
