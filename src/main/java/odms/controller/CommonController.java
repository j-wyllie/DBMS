package odms.controller;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

class CommonController {
    private final String appTitle = "ODMS";

    /**
     * Scene change to log in view.
     *
     * @param event clicking on the logout button.
     */
    @FXML
    public void showLoginScene(ActionEvent event) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("/view/Login.fxml"));
        Scene newScene = new Scene(parent);
        Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        appStage.setScene(newScene);
        appStage.setResizable(false);
        appStage.setTitle(appTitle);
        appStage.show();
    }

    protected void showScene(ActionEvent event, String scene) throws IOException {
        showScene(event, scene, false);
    }

    protected void showScene(ActionEvent event, String scene, Boolean resizeable) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource(scene));
        Scene newScene = new Scene(parent);
        Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        appStage.setScene(newScene);
        appStage.setResizable(resizeable);
        appStage.setTitle(appTitle);
        appStage.show();
    }

}
