package odms.controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.net.Socket;
/**
 * Main class. GUI boots from here.
 */
@Slf4j
public class GuiMain extends Application {

    private static final String APP_NAME = "ODMS";
    private static final String DOMAIN = "localhost";
    private static final Integer PORT = 6969;


    /**
     * Loads in a default clinician if one does not exist. Opens the login screen
     *
     * @param primaryStage the primary stage
     * @throws IOException file read exception for login fxml
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        if (checkServer()) {
            Parent root = FXMLLoader.load(getClass().getResource("/view/Login.fxml"));
            primaryStage.setScene(new Scene(root));
            primaryStage.setResizable(false);
            primaryStage.setTitle(APP_NAME);
            primaryStage.show();

        } else {
            AlertController.guiPopup("Connection to the server could not be established.\n\n" +
                    "Human Farm servers may be experiencing\ntechnical difficulties. " +
                    "Please check your internet\nconnection and try again.");
        }
    }

    /**
     * Simple check to verify if a connection to the odms server can be established.
     * @return boolean true if server is up, false otherwise
     */
    private boolean checkServer() {
        try (Socket s = new Socket(DOMAIN, PORT)) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }


    /**
     * Launches the GUI of the program.
     * @param args command arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
