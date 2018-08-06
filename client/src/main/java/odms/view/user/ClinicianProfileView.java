package odms.view.user;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import odms.controller.user.UserDataManagementController;
import odms.controller.user.UserProfileController;
import odms.model.user.User;
import odms.model.enums.UserType;
import odms.view.CommonView;

import java.io.IOException;

public class ClinicianProfileView extends CommonView {
    private User currentUser;

    @FXML
    private static AnchorPane clinicianAp;

    @FXML
    private Label clinicianFullName;

    @FXML
    private Label donorStatusLabel;
    @FXML
    private Tab viewUsersTab;
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
    private UserDataManagementController userDataManagementController;
    private UserGeneralTabView userGeneralTabView = new UserGeneralTabView();
    private UserConsoleTabView userConsoleTabView = new UserConsoleTabView();
    private ViewUsersView viewUsersView = new ViewUsersView();

    private UserProfileController userProfileController = new UserProfileController(this);


    /**
     * Scene change to log in view.
     *
     * @param event clicking on the logout button.
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


    public void handleConsoleTabClicked() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/UserConsoleTab.fxml"));
        try {
            consoleTab.setContent(loader.load());
            UserConsoleTabView userConsoleTabView = loader.getController();
            userConsoleTabView.initialize();
        } catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    /**
     * Initializes the controller for the view users Tab
     */
    public void handleViewUsersTabClicked() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ViewUsersTab.fxml"));
        try {
            viewUsersTab.setContent(loader.load());
            ViewUsersView viewUsersView = loader.getController();
            viewUsersView.initialize();
        } catch (IOException e){
            System.out.println(e.getMessage());
        }

//        viewUsersView.setCurrentUser(currentUser);
    }

    public void handleGeneralTabClicked() {
        if (currentUser != null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/UserGeneralTab.fxml"));
            try {
                generalTab.setContent(loader.load());
                UserGeneralTabView userGeneralTabView = loader.getController();
                userGeneralTabView.initialize(currentUser);
            } catch (IOException e){
                System.out.println(e.getMessage());
            }
        }
    }

    public void handleTabDataManagementClicked() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/DataManagement.fxml"));
        try {
            dataManagementTab.setContent(loader.load());
            UserDataManagementTabView userDataManagementTabView = loader.getController();
            userDataManagementTabView.initialize(currentUser);
        } catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    /**
     * Hides/Shows certain nodes if the clinician does / does not have permission to view them
     */
    private void setupAdmin() {
        if (currentUser.getUserType() == UserType.CLINICIAN) {
            dataManagementTab.setDisable(true);
            viewUsersTab.setDisable(true);
            consoleTab.setDisable(true);
        } else {
            dataManagementTab.setDisable(false);
            viewUsersTab.setDisable(false);
            consoleTab.setDisable(false);
        }
    }

    @FXML
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

    public void handleSearchTabClicked() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/UserSearchTab.fxml"));
        try {
            searchTab.setContent(loader.load());
            UserSearchView userSearchView = loader.getController();
            userSearchView.initialize(currentUser, this);
        } catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    public void handleTransplantWaitingListTabClicked() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/UserTransplantWaitingListTab.fxml"));
        try {
            transplantTab.setContent(loader.load());
            UserTransplantWaitingListTabView userTransplantWaitingListTabView = loader.getController();
            userTransplantWaitingListTabView.initialize(currentUser, this);
        } catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    public boolean addToOpenProfileStages(Stage s) {
        return userProfileController.addToOpenProfileStages(s);
    }

    public void closeStage(Stage stage) {
        userProfileController.removeStageFromProfileStages(stage);

        openProfileStages.remove(stage);
    }
}
