package odms.controller;

import static odms.controller.AlertController.InvalidUsername;
import static odms.controller.GuiMain.getCurrentDatabase;


import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import odms.data.DonorDatabase;
import odms.donor.Donor;

public class LoginController {

    private static DonorDatabase currentDatabase = getCurrentDatabase();
    private static Donor currentDonor;

    /**
     * TextField to input username.
     */
    @FXML
    private TextField usernameField;

    /**
     * TextField to input password.
     */
    @FXML
    private TextField passwordField;

    /**
     * Scene change to donor profile view if log in credentials are valid.
     * @param event clicking on the login button.
     * @throws IOException
     */
    @FXML
    private void handleLoginButtonClicked(ActionEvent event) throws IOException {

        try {
            int userId = Integer.valueOf(usernameField.getText());
            currentDonor = currentDatabase.getDonor(userId);
        }
        catch (Exception e) {
            System.out.println(e);
            InvalidUsername();
        }
        finally {
            Parent parent = FXMLLoader.load(getClass().getResource("/view/DonorProfile.fxml"));
            Scene newScene = new Scene(parent);
            Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            appStage.setScene(newScene);
            appStage.setTitle("Donor Profile");
            appStage.show();
        }
    }

    /**
     * Scene change to create account view.
     * @param event clicking on the create new account link.
     * @throws IOException
     */
    @FXML
    private void handleCreateNewAccountLinkClicked(ActionEvent event) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("/view/CreateProfile.fxml"));
        Scene newScene = new Scene(parent);
        Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        appStage.setScene(newScene);
        appStage.setTitle("Create Profile");
        appStage.show();
    }

    public static Donor getCurrentDonor() {
        return currentDonor;
    }
}
