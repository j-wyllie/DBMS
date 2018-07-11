package odms.controller.profile;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import odms.view.profile.ProfileCreateAccountView;
import odms.view.profile.ProfileDisplayController;
import odms.controller.AlertController;
import odms.controller.CommonController;
import odms.controller.data.IrdNumberConflictException;
import odms.controller.data.ProfileDataIO;
import odms.model.data.ProfileDatabase;
import odms.model.profile.Profile;

import java.io.IOException;
import java.time.LocalDate;

import static odms.controller.AlertController.invalidDate;
import static odms.controller.AlertController.invalidIrd;
import static odms.controller.GuiMain.getCurrentDatabase;

public class ProfileCreateController extends CommonController {

    private static ProfileDatabase currentDatabase = getCurrentDatabase();
    private ProfileCreateAccountView view;

    public ProfileCreateController(ProfileCreateAccountView v) {
        view = v;
    }

    private String checkDetailsEntered() {
        //todo rework this to not return strings (potentially by including alert controller calls in here
        if (view.getGivenNamesFieldValue().isEmpty()) {
            return "Please enter Given Name(s)";
        }
        if (view.getsurnamesFieldValue().isEmpty()) {
            return "Please enter Surname(s)";

        }
        if (view.getdobDatePickerValue().equals(null)) {
            return "Please enter a Date of Birth";
        }
        if (view.getirdNumberFieldValue().isEmpty()) {
            return "Please enter an IRD number";
        } else {
            return "";
        }
    }


    /**
     * Scene change to profile profile view if all required fields are filled in.
     * @throws IOException throws IOException
     */
    public Profile createAccount() throws IOException {
        //todo rework this method potentially
        String detailsCheckString = checkDetailsEntered();

        if (!detailsCheckString.equals("")) {
            AlertController.invalidEntry(detailsCheckString);
        } else {
            try {
                String givenNames = view.getGivenNamesFieldValue();
                String surnames = view.getsurnamesFieldValue();
                LocalDate dob = view.getdobDatePickerValue();
                Integer ird = Integer.valueOf(view.getirdNumberFieldValue());

                Profile newProfile = new Profile(givenNames, surnames, dob, ird);
                currentDatabase.addProfile(newProfile);
                ProfileDataIO.saveData(currentDatabase);
                return (currentDatabase.getProfile(newProfile.getId()));
            } catch (NumberFormatException e) {
                if (view.getirdNumberFieldValue().length() > 9) {
                    AlertController.invalidEntry(
                            "Entered IRD number is too long.\nPlease enter up to 9 digits");
                } else {
                    AlertController.invalidEntry("Invalid IRD number entered");
                }
            } catch (IllegalArgumentException e) {
                //show error window.
                AlertController.invalidEntry();
            } catch (IrdNumberConflictException e) {
                invalidIrd();
            } catch (ArrayIndexOutOfBoundsException e) {
                invalidDate();
            }
        }
        return null;
    }

    /**
     * Scene change to login view.
     * @throws IOException throws IOException
     */
    public void login() throws IOException {
        //login verification will go here when needed
    }
}
