package odms.controller;

import java.io.File;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import odms.controller.history.Redo;
import odms.controller.history.Undo;

public class CommonController {
    //todo rework commoncontroller by cleaning up methods and removing or replacing them

    private Redo redoController = new Redo();
    private Undo undoController = new Undo();

    /**
     * JavaFX Scene loader
     *
     * @param event the ActionEvent
     * @param scene the fxml path
     * @param title the window title
     * @throws IOException if the path is invalid
     */
    protected void showScene(ActionEvent event, String scene, String title) throws IOException {
        showScene(event, scene, title, false);
    }

    /**
     * JavaFX Scene loader
     *
     * @param event      the ActionEvent
     * @param scene      the fxml path
     * @param title      the window title
     * @param resizeable if the window can be resized
     * @throws IOException if the path is invalid
     */
    protected void showScene(ActionEvent event, String scene, String title, Boolean resizeable)
            throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource(scene));
        Scene newScene = new Scene(parent);
        Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        appStage.setScene(newScene);
        appStage.setResizable(resizeable);
        appStage.setTitle(title);
        appStage.centerOnScreen();
        appStage.show();
    }

    /**
     * Button handler to undo last action.
     */
    public void undo() {
        undoController.undo(GuiMain.getCurrentDatabase());
    }

    /**
     * Button handler to redo last undo action.
     */
    public void redo() {
        redoController.redo(GuiMain.getCurrentDatabase());
    }

}
