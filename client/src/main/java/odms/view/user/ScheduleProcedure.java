package odms.view.user;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import odms.commons.model.profile.Profile;
import odms.view.CommonView;

/**
 * The view for scheduling an organ donation.
 */
public class ScheduleProcedure extends CommonView {

    private odms.controller.user.ScheduleProcedure controller = new odms.controller.user.ScheduleProcedure();

    private Profile donor;
    private Profile receiver;

    @FXML
    private Label donorNameLabel;
    @FXML
    private Label receiverNameLabel;

    /**
     * Initialize the component and use the parent view to get donor and receiver.
     *
     * @param parentView the organ map parent view.
     */
    public void initialize(OrganMap parentView) {
        controller.setView(this);
        this.donor = parentView.getCurrentDonor();
        this.receiver = parentView.getCurrentReceiver();
        setLabels();
    }


    private void setLabels() {
        donorNameLabel.setText(donor.getFullName());
        receiverNameLabel.setText(receiver.getFullName());
    }
}
