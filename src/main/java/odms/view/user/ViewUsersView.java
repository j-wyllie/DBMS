package odms.view.user;

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
import odms.controller.GuiMain;
import odms.controller.data.UserDataIO;
import odms.controller.user.ViewUsersController;
import odms.model.data.UserDatabase;
import odms.model.user.User;
import odms.view.CommonView;

import java.io.IOException;

public class ViewUsersView extends CommonView {
    private ViewUsersController controller = new ViewUsersController(this);
    ContextMenu contextMenu;

    @FXML
    TableView<User> viewUsersTable;

    @FXML
    public void handleCreateUserBtnPressed() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/view/UserCreate.fxml"));

        //todo replace controller creation with view?
        Scene scene = new Scene(fxmlLoader.load());
        UserCreateControllerTODO controller = fxmlLoader.getController();
        controller.initialize();

        Stage stage = new Stage();
        stage.setTitle("Create user");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.initOwner(viewUsersTable.getScene().getWindow());
        stage.initModality(Modality.WINDOW_MODAL);
        stage.centerOnScreen();
        stage.setOnHiding((ob) -> refreshViewUsersTable());
        stage.show();
    }
}
