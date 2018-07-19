package odms.controller.user;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import odms.controller.AlertController;
import odms.controller.GuiMain;
import odms.model.data.UserDatabase;
import odms.model.user.User;

public class ViewUsersController {

    private UserDatabase userDatabase = GuiMain.getUserDatabase();
    private User currentUser;
    private ObservableList<User> usersObservableList;

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public void setUpUsersTable() {

        fetchData();
        viewUsersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        viewUsersTable.getColumns().clear();
        TableColumn nameCol = new TableColumn("Name");
        TableColumn usernameCol = new TableColumn("Username");
        TableColumn userTypeCol = new TableColumn("user Type");
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
}
