package odms.controller.User;

import static odms.controller.AlertController.saveChanges;
import static odms.controller.GuiMain.getUserDatabase;

import java.io.IOException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import odms.controller.AlertController;
import odms.controller.CommonController;
import odms.controller.GuiMain;
import odms.controller.data.UserDataIO;
import odms.Model.Data.UserDatabase;
import odms.Model.user.User;

public class ViewUsersController extends CommonController {

    private UserDatabase userDatabase = GuiMain.getUserDatabase();
    private User currentUser;
    private ObservableList<User> usersObservableList;

    ContextMenu contextMenu;

    @FXML
    TableView<User> viewUsersTable;

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public void setUpUsersTable() {

        fetchData();
        viewUsersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        viewUsersTable.getColumns().clear();
        TableColumn nameCol = new TableColumn("Name");
        TableColumn usernameCol = new TableColumn("Username");
        TableColumn userTypeCol = new TableColumn("User Type");
        TableColumn staffIdCol = new TableColumn("Staff Id");

        viewUsersTable.getColumns().addAll(nameCol, usernameCol, userTypeCol, staffIdCol);
        nameCol.setCellValueFactory(
                new PropertyValueFactory<User, String>("name")
        );
        usernameCol.setCellValueFactory(
                new PropertyValueFactory<User, String>("username")
        );
        userTypeCol.setCellValueFactory(
                new PropertyValueFactory<User, String>("userType")
        );
        staffIdCol.setCellValueFactory(
                new PropertyValueFactory<User, String>("staffId")
        );

        viewUsersTable.setItems(usersObservableList);
        createContextMenu();

        //sets the event handler for right clicking on a table item.
        viewUsersTable.addEventHandler(MouseEvent.ANY, t -> {
            if (t.getButton() == MouseButton.SECONDARY) {
                if (viewUsersTable.getSelectionModel().getSelectedItem() != null &&
                    !viewUsersTable.getSelectionModel().getSelectedItem().getDefault()) {
                    contextMenu.show(viewUsersTable, t.getScreenX(), t.getScreenY());
                } else {
                    contextMenu.hide();
                }
            } else if (t.getButton() == MouseButton.PRIMARY) {
                contextMenu.hide();
            }
        });
    }

    /**
     * Gets an observable list of users.
     */
    private void fetchData() {
        usersObservableList = FXCollections.observableArrayList(userDatabase.getUsers());
    }

    /**
     * creates and populate the context menu for the table.
     */
    private void createContextMenu() {
        contextMenu = new ContextMenu();
        MenuItem deleteMenuItem = new MenuItem("Delete");
        contextMenu.getItems().add(deleteMenuItem);

        contextMenu.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> {
            if (AlertController.deleteUserConfirmation()) {
                User user = viewUsersTable.getSelectionModel().getSelectedItem();
                GuiMain.getUserDatabase().deleteUser(user.getStaffID());
                refreshViewUsersTable();
                editTrueStage((Stage) viewUsersTable.getScene().getWindow());
            }
        });
    }

    private void refreshViewUsersTable() {
        fetchData();
        viewUsersTable.getItems().clear();
        viewUsersTable.getItems().addAll(usersObservableList);
    }

    @FXML
    public void handleCreateUserBtnPressed() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/view/UserCreate.fxml"));

        Scene scene = new Scene(fxmlLoader.load());
        UserCreateController controller = fxmlLoader.getController();
        controller.initialize();

        Stage stage = new Stage();
        stage.setTitle("Create User");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.initOwner(viewUsersTable.getScene().getWindow());
        stage.initModality(Modality.WINDOW_MODAL);
        stage.centerOnScreen();
        stage.setOnHiding((ob) -> refreshViewUsersTable());
        stage.show();
    }

    public void handleViewUsersSaveBtn(ActionEvent actionEvent) throws IOException{
        if (saveChanges()) {
            showNotification("Users File", actionEvent);
            UserDataIO.saveUsers(getUserDatabase());
        }
    }
}
