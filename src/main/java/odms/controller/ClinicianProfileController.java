package odms.controller;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ClinicianProfileController {

    //Get the default clinician
    //private static User currentUser = getCurrentUser();

    /**
     * Label to display the clinicians given names
     */
    @FXML
    private Label givenNamesLabel;

    /**
     * Label to display the clinicians last names.
     */
    @FXML
    private Label lastNamesLabel;

    /**
     * Label to display the clinicians staff ID.
     */
    @FXML
    private Label staffIdLabel;

    /**
     * Label to display the clinicians work address.
     */
    @FXML
    private Label addressLabel;

    /**
     * Label to display the clinicians region.
     */
    @FXML
    private Label regionLabel;

}
