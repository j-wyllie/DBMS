package odms.view.profile;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import odms.commons.model.profile.HLAType;
import javafx.stage.Modality;
import javafx.stage.Stage;
import odms.commons.model.profile.Profile;
import odms.commons.model.user.User;
import odms.controller.AlertController;
import odms.controller.HlaController;
import odms.controller.database.DAOFactory;
import odms.controller.database.hla.HLADAO;
import odms.data.DefaultLocale;
import odms.view.CommonView;

import java.io.IOException;

public class ProfileMedical extends CommonView {

    @FXML
    private Label bloodTypeLabel;
    @FXML
    private Label smokerLabel;
    @FXML
    private Label alcoholConsumptionLabel;
    @FXML
    private Label bloodPressureLabel;
    @FXML
    private Label bmiLabel;
    @FXML
    private Button bloodDonationButton;
    @FXML
    private Label bloodDonationLabel;

    @FXML
    private Label hlaXALabel;

    @FXML
    private Label hlaXBLabel;

    @FXML
    private Label hlaXCLabel;

    @FXML
    private Label hlaXDPLabel;

    @FXML
    private Label hlaXDQLabel;

    @FXML
    private Label hlaXDRLabel;

    @FXML
    private Label hlaYALabel;

    @FXML
    private Label hlaYBLabel;

    @FXML
    private Label hlaYCLabel;

    @FXML
    private Label hlaYDPLabel;

    @FXML
    private Label hlaYDQLabel;

    @FXML
    private Label hlaYDRLabel;

    @FXML
    private ListView secondaryHlaListView;

    private Profile currentProfile;
    private Boolean isOpenedByClinician;
    private User currentUser;
    private HlaController hlaController = new HlaController();

    private void setUpDetails() {
        if (currentProfile.getAlcoholConsumption() != null) {
            alcoholConsumptionLabel.setText(
                    alcoholConsumptionLabel.getText() +
                            currentProfile.getAlcoholConsumption()
            );
        }
        if (currentProfile.getBloodPressure() != null) {
            bloodPressureLabel.setText(
                    bloodPressureLabel.getText() + currentProfile.getBloodPressure()
            );
        }
        if (currentProfile.getBloodType() != null) {
            bloodTypeLabel.setText(bloodTypeLabel.getText() + currentProfile.getBloodType());
        }
        if (currentProfile.getHeight() != 0.0 && currentProfile.getWeight() != 0.0) {
            DecimalFormat df = new DecimalFormat("#.##");
            df.setRoundingMode(RoundingMode.CEILING);

            bmiLabel.setText(bmiLabel.getText() + DefaultLocale.format(currentProfile.getBmi()));
        }
        if (currentProfile.getIsSmoker() != null) {
            smokerLabel.setText(smokerLabel.getText() + currentProfile.getIsSmoker());
        }

        // HLA text setters
        HLADAO hladao = DAOFactory.getHlaDAO();
        HLAType hlaType = hladao.get(currentProfile.getId());

        if (hlaType.getGroupX().get("A") != null) {
            hlaXALabel.setText("A" + String.valueOf(hlaType.getGroupX().get("A")));
        } else {
            hlaXALabel.setText("N/A");
        }

        if (hlaType.getGroupX().get("B") != null) {
            hlaXBLabel.setText("B" + String.valueOf(hlaType.getGroupX().get("B")));
        } else {
            hlaXBLabel.setText("N/A");
        }

        if (hlaType.getGroupX().get("C") != null) {
            hlaXCLabel.setText("C" + String.valueOf(hlaType.getGroupX().get("C")));
        } else {
            hlaXCLabel.setText("N/A");
        }

        if (hlaType.getGroupX().get("DP") != null) {
            hlaXDPLabel.setText("DP" + String.valueOf(hlaType.getGroupX().get("DP")));
        } else {
            hlaXDPLabel.setText("N/A");
        }

        if (hlaType.getGroupX().get("DQ") != null) {
            hlaXDQLabel.setText("DQ" + String.valueOf(hlaType.getGroupX().get("DQ")));
        } else {
            hlaXDQLabel.setText("N/A");
        }

        if (hlaType.getGroupX().get("DR") != null) {
            hlaXDRLabel.setText("DR" + String.valueOf(hlaType.getGroupX().get("DR")));
        } else {
            hlaXDRLabel.setText("N/A");
        }

        if (hlaType.getGroupY().get("A") != null) {
            hlaYALabel.setText("A" + String.valueOf(hlaType.getGroupY().get("A")));
        } else {
            hlaYALabel.setText("N/A");
        }

        if (hlaType.getGroupY().get("B") != null) {
            hlaYBLabel.setText("B" + String.valueOf(hlaType.getGroupY().get("B")));
        } else {
            hlaYBLabel.setText("N/A");
        }

        if (hlaType.getGroupY().get("C") != null) {
            hlaYCLabel.setText("C" + String.valueOf(hlaType.getGroupY().get("C")));
        } else {
            hlaYCLabel.setText("N/A");
        }

        if (hlaType.getGroupY().get("DP") != null) {
            hlaYDPLabel.setText("DP" + String.valueOf(hlaType.getGroupY().get("DP")));
        } else {
            hlaYDPLabel.setText("N/A");
        }

        if (hlaType.getGroupY().get("DQ") != null) {
            hlaYDQLabel.setText("DQ" + String.valueOf(hlaType.getGroupY().get("DQ")));
        } else {
            hlaYDQLabel.setText("N/A");
        }

        if (hlaType.getGroupY().get("DR") != null) {
            hlaYDRLabel.setText("DR" + String.valueOf(hlaType.getGroupY().get("DR")));
        } else {
            hlaYDRLabel.setText("N/A");
        }

        List<String> secondaryAntigenList = hlaController.getSecondaryHLAs(currentProfile.getId());
        ObservableList<String> secondaryAntigens = FXCollections.observableArrayList(secondaryAntigenList);
        secondaryHlaListView.setItems(secondaryAntigens);

        updateBloodDonationLabel();
    }

    public void initialize(Profile p, Boolean isOpenedByClinician) {
        if (isOpenedByClinician) {
            bloodDonationButton.setVisible(false);
        } else {
            bloodDonationButton.setVisible(true);
        }
        currentProfile = p;
        if (currentProfile != null) {
            setUpDetails();
        }
    }

    /**
     * Updates the blood donation label after donating blood
     * in order to display the correct number of points.
     */
    public void updateBloodDonationLabel() {
        bloodDonationLabel.setText("Blood Donation Points: " +
                DefaultLocale.format(currentProfile.getBloodDonationPoints()));
    }

    /**
     * Opens a window for donating blood.
     */
    @FXML
    public void onBtnDonationClicked(ActionEvent actionEvent) throws IOException {
        Node source = (Node) actionEvent.getSource();
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/view/BloodDonation.fxml"));

        Scene scene = new Scene(fxmlLoader.load());

        BloodDonation view = fxmlLoader.getController();
        try {
            view.initialize(currentProfile, this);
            Stage stage = new Stage();
            stage.setTitle("Blood Donation");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.initOwner(source.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.centerOnScreen();
            stage.show();
        } catch (NullPointerException e) {
            AlertController.guiPopup("You have not set a blood type yet.");
        }


    }
}
