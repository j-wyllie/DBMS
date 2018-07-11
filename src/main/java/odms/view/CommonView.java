package odms.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import odms.controller.CommonController;

import java.io.IOException;

public class CommonView {
    /**
     * Scene change to log in view.
     *
     * @param event clicking on the logout button.
     */
    @FXML
    public void changeScene(ActionEvent event, String resourceName) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource(resourceName));
        Scene newScene = new Scene(parent);
        Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        appStage.setScene(newScene);
        appStage.show();
    }

}
