package odms.view.user;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import odms.controller.user.Display;
import odms.model.user.User;
import odms.model.enums.UserType;
import odms.view.CommonView;

import java.io.IOException;

public class ClinicianProfile extends CommonView {
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
    private Tab availableOrgansTab;
    @FXML
    private odms.controller.user.DataManagement userDataManagementController;
    private UserGeneral userGeneralTabView = new UserGeneral();
    private ConsoleTab userConsoleTabView = new ConsoleTab();
    private UsersList viewUsersView = new UsersList();

    private Display userProfileController = new Display(this);


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
            ConsoleTab userConsoleTabView = loader.getController();
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
            UsersList viewUsersView = loader.getController();
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
                UserGeneral userGeneralTabView = loader.getController();
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
            DataManagement userDataManagementTabView = loader.getController();
            userDataManagementTabView.initialize(currentUser);
        } catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    public void handleTabAvailableClicked() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/UserAvailableOrgansTab.fxml"));
        try {
            availableOrgansTab.setContent(loader.load());
            AvailableOrgans availableOrgansTabView = loader.getController();
            availableOrgansTabView.initialize(currentUser);
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
            Search userSearchView = loader.getController();
            userSearchView.initialize(currentUser, this);
        } catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    public void handleTransplantWaitingListTabClicked() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/UserTransplantWaitingListTab.fxml"));
        try {
            transplantTab.setContent(loader.load());
            TransplantWaitingList userTransplantWaitingListTabView = loader.getController();
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
