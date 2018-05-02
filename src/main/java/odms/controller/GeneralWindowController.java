package odms.controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.stage.Stage;

import java.io.IOException;

public abstract class GeneralWindowController {

    /**
     * Changes the Edit Profile title to include an astrix to indicate a value has been edited.
     * @param event Any key event within the text boxes.
     */

    @FXML
    public void editTrue(javafx.scene.input.KeyEvent event) throws IOException {
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.setTitle("Edit Profile (*)");
    }
}
