package odms.controller.user;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import javafx.beans.property.SimpleStringProperty;
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
import odms.commons.model.user.User;
import odms.controller.AlertController;
import odms.controller.database.DAOFactory;
import odms.controller.database.user.UserDAO;
import odms.data.DefaultLocale;

/**
 * The users list tab controller.
 */
public class UsersList {
    private odms.view.user.UsersList view;
    private ObservableList<User> usersObservableList;
    private ContextMenu contextMenu;

    /**
     * Public constructor for the ViewUsersController class.
     * @param v instance of ViewUsersView
     */
    public UsersList(odms.view.user.UsersList v) {
        view = v;
    }

    /**
     * Initialize the UsersTable.
     */
    public void setUpUsersTable() {

        fetchData();
        view.getViewUsersTable().setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        view.getViewUsersTable().getColumns().clear();
        TableColumn<User, String> nameCol = new TableColumn<>("Name");
        TableColumn<User, String> usernameCol = new TableColumn<>("Username");
        TableColumn<User, String> userTypeCol = new TableColumn<>("User Type");
        TableColumn<User, String> staffIdCol = new TableColumn<>("Staff Id");

        nameCol.setCellValueFactory(
                new PropertyValueFactory<>("name")
        );
        usernameCol.setCellValueFactory(
                new PropertyValueFactory<>("username")
        );
        userTypeCol.setCellValueFactory(
                new PropertyValueFactory<>("userType")
        );
        staffIdCol.setCellValueFactory(
                user -> new SimpleStringProperty(
                        (DefaultLocale.format(user.getValue().getId()))
                )
        );

        view.getViewUsersTable().getColumns().addAll(nameCol, usernameCol, userTypeCol, staffIdCol);
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
     * Creates and populate the context menu for the table.
     */
    private void createContextMenu() {
        contextMenu = new ContextMenu();
        MenuItem deleteMenuItem = new MenuItem("Delete");
        contextMenu.getItems().add(deleteMenuItem);
        UserDAO server = DAOFactory.getUserDao();

        contextMenu.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> {

            User user = view.getViewUsersTable().getSelectionModel().getSelectedItem();

            if (AlertController.deleteUserConfirmation()) {

                try {
                    server.remove(user);
                } catch (SQLException e) {
                    AlertController.invalidEntry("Error deleting user.");
                }
                refreshViewUsersTable();
                view.editTrueStage((Stage) view.getViewUsersTable().getScene().getWindow());
            }
        });
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
        UserDAO server = DAOFactory.getUserDao();
        try {
            usersObservableList = FXCollections.observableArrayList(server.getAll());
        } catch (SQLException e) {
            AlertController.invalidEntry("Error fetching users from database.");
        }
    }
}
