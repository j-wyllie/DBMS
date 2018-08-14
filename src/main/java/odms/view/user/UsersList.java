package odms.view.user;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import odms.model.user.User;
import odms.view.CommonView;

import java.io.IOException;

import static odms.controller.AlertController.saveChanges;

public class UsersList extends CommonView {
    private odms.controller.user.UsersList controller = new odms.controller.user.UsersList(this);
    private User currentUser;

    @FXML
    TableView<User> viewUsersTable;

    @FXML
    public void handleCreateUserBtnPressed() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/view/UserCreate.fxml"));

        //todo replace controller creation with view?
        Scene scene = new Scene(fxmlLoader.load());
        UserCreate v = fxmlLoader.getController();
        v.initialize();

        Stage stage = new Stage();
        stage.setTitle("Create user");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.initOwner(viewUsersTable.getScene().getWindow());
        stage.initModality(Modality.WINDOW_MODAL);
        stage.centerOnScreen();
        stage.setOnHiding((ob) -> controller.refreshViewUsersTable());
        stage.show();
    }

    @FXML
    public void handleViewUsersSaveBtn(ActionEvent actionEvent) throws IOException {
        if (saveChanges()) {
            showNotification("Users File", actionEvent);
            controller.saveChanges();
        }
    }

    public TableView<User> getViewUsersTable() {
        return viewUsersTable;
    }

    public void setUpUsersTable() {
        controller.setUpUsersTable();
    }

    @FXML
    public void initialize() {
        setUpUsersTable();
    }

    public void setCurrentUser(User user) {
        //todo currentUser isn't used but the method is called in ClinicianProfileView so it might have a purpose
        currentUser = user;
    }
}
