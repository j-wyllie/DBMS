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
import odms.controller.data.UserDataIO;
import odms.model.data.UserDatabase;
import odms.model.user.User;
import odms.view.user.ViewUsersView;

public class ViewUsersController {
    private UserDatabase userDatabase = GuiMain.getUserDatabase();
    private ViewUsersView view;
    private ObservableList<User> usersObservableList;
    private ContextMenu contextMenu;

    /**
     * public constructor for the ViewUsersController class.
     * @param v instance of ViewUsersView
     */
    public ViewUsersController(ViewUsersView v) {
        view = v;
    }

    /**
     * Initialize the UsersTable.
     */
    public void setUpUsersTable() {

        fetchData();
        view.getViewUsersTable().setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        view.getViewUsersTable().getColumns().clear();
        TableColumn nameCol = new TableColumn("Name");
        TableColumn usernameCol = new TableColumn("Username");
        TableColumn userTypeCol = new TableColumn("user Type");
        TableColumn staffIdCol = new TableColumn("Staff Id");

        view.getViewUsersTable().getColumns().addAll(nameCol, usernameCol, userTypeCol, staffIdCol);
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

        view.getViewUsersTable().setItems(usersObservableList);
        createContextMenu();

        //sets the event handler for right clicking on a table item.
        view.getViewUsersTable().addEventHandler(MouseEvent.ANY, t -> {
            if (t.getButton() == MouseButton.SECONDARY) {
                if (view.getViewUsersTable().getSelectionModel().getSelectedItem() != null &&
                        !view.getViewUsersTable().getSelectionModel().getSelectedItem().getDefault()) {
                    contextMenu.show(view.getViewUsersTable(), t.getScreenX(), t.getScreenY());
                } else {
                    contextMenu.hide();
                }
            } else if (t.getButton() == MouseButton.PRIMARY) {
                contextMenu.hide();
            }
        });
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
                User user = view.getViewUsersTable().getSelectionModel().getSelectedItem();
                GuiMain.getUserDatabase().deleteUser(user.getStaffID());
                refreshViewUsersTable();
                view.editTrueStage((Stage) view.getViewUsersTable().getScene().getWindow());
            }
        });
    }

    /**
     * save changes made in the view users window.
     */
    public void saveChanges() {
        UserDataIO.saveUsers(userDatabase);
    }

    /**
     * Refresh the user data in the UsersTable.
     */
    public void refreshViewUsersTable() {
        fetchData();
        view.getViewUsersTable().getItems().clear();
        view.getViewUsersTable().getItems().addAll(usersObservableList);
    }

    /**
     * Gets an observable list of users.
     */
    private void fetchData() {
        usersObservableList = FXCollections.observableArrayList(userDatabase.getUsers());
    }
}
