package odms.view.user;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import odms.commons.model.user.User;
import odms.data.DefaultLocale;

public class UserGeneral {

    @FXML
    private Label givenNamesLabel;
    @FXML
    private Label staffIdLabel;
    @FXML
    private Label addressLabel;
    @FXML
    private Label regionLabel;

    /**
     * Initializes all of the labels and checks the user type.
     *
     * @param currentUser The current user logged in.
     */
    public void initialize(User currentUser) {
        givenNamesLabel.setText(givenNamesLabel.getText() + (
                        currentUser.getName() != null ? currentUser.getName() : ""));
        staffIdLabel.setText(
                staffIdLabel.getText() + (
                        currentUser.getId() != null ?
                                DefaultLocale.format(currentUser.getId()) : ""));
        addressLabel.setText(
                addressLabel.getText() +
                        (currentUser.getWorkAddress() != null ? currentUser.getWorkAddress() : "")
        );
        regionLabel.setText(
                regionLabel.getText() +
                        (currentUser.getRegion() != null ? currentUser.getRegion() : "")
        );
    }
}
