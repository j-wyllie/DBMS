
package odms.controller;

import com.sun.xml.internal.ws.api.FeatureConstructor;
import java.sql.Date;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javax.swing.Action;
import odms.profile.Procedure;
import odms.profile.Profile;

import java.sql.Connection;
import java.time.LocalDate;

public class AddProcedureController {

    private Profile searchedDonor;
    private DonorProfileController controller;

    @FXML
    private TextField summaryField;

    @FXML
    private TextField dateOfProcedureField;

    @FXML
    private TextField descriptionField;

    @FXML
    private Label warningLabel;

    @FXML
    private Button addButton;

    @FXML
    public void handleAddButtonClicked(ActionEvent actionEvent) {
        String summary = summaryField.getText();
        String dateOfProcedure = dateOfProcedureField.getText();
        String longDescription = descriptionField.getText();

        Procedure procedure;

        try {
            String[] date2 = dateOfProcedure.split("-");
            LocalDate date3 = LocalDate.of(Integer.valueOf(date2[2]), Integer.valueOf(date2[1]), Integer.valueOf(date2[0]));

            if (summary.equals("")) {
                throw new IllegalArgumentException();
            }

            LocalDate dob = controller.getSearchedDonor().getDateOfBirth();
            if (dob.isAfter(date3)){
                throw new IllegalArgumentException();
            }

            if (longDescription.equals("")) {
                procedure = new Procedure(summary, dateOfProcedure);

            } else {
                procedure = new Procedure(summary, dateOfProcedure, longDescription);
            }

            System.out.println(procedure);
            addProcedure(procedure);

        } catch (IllegalArgumentException e) {
            warningLabel.setVisible(true);
        } catch (DateTimeException e) {
            warningLabel.setVisible(true);
        } catch (ArrayIndexOutOfBoundsException e) {
            warningLabel.setVisible(true);

        }
    }

    /**
     * Adds a new condition to the current profile
     * @param procedure
     */
    public void addProcedure(Procedure procedure) {
        searchedDonor.addProcedure(procedure);
        controller.refreshProcedureTable();
        Stage stage = (Stage) addButton.getScene().getWindow();
        stage.close();
//        FXMLLoader fxmlLoader = new FXMLLoader();
//        fxmlLoader.setLocation(getClass().getResource("/view/DonorProfile.fxml"));
//        DonorProfileController controller = fxmlLoader.<DonorProfileController>getController();
//        controller.refreshTable();
    }



    public void init(DonorProfileController controller) {
        this.controller = controller;
        searchedDonor = controller.getSearchedDonor();

    }

}
