package odms.controller.profile;

import odms.model.data.NHIConflictException;
import odms.view.profile.ProfileCreateAccountView;
import odms.controller.AlertController;
import odms.controller.CommonController;
import odms.controller.data.ProfileDataIO;
import odms.model.data.ProfileDatabase;
import odms.model.profile.Profile;

import java.io.IOException;
import java.time.LocalDate;

import static odms.controller.AlertController.invalidDate;
import static odms.controller.AlertController.invalidNhi;
import static odms.controller.GuiMain.getCurrentDatabase;

public class ProfileCreateController extends CommonController {

    private static ProfileDatabase currentDatabase = getCurrentDatabase();
    private ProfileCreateAccountView view;

    public ProfileCreateController(ProfileCreateAccountView v) {
        view = v;
    }

    private boolean checkDetailsEntered() {
        if (view.getGivenNamesFieldValue().isEmpty()) {
            AlertController.invalidEntry("Please enter Given Name(s)");
            return false;
        }

        if (view.getsurnamesFieldValue().isEmpty()) {
            AlertController.invalidEntry("Please enter Surname(s)");
            return false;
        }

        if (view.getdobDatePickerValue() == null) {
            AlertController.invalidEntry("Please enter a Date of Birth");
            return false;
        }
        if (view.getNhiField().isEmpty()) {
            AlertController.invalidEntry("Please enter an IRD number");
            return false;
        } else {
            return true;
        }
    }


    /**
     * Scene change to profile profile view if all required fields are filled in.
     * @throws IOException throws IOException
     */
    public Profile createAccount() throws IOException {
        //todo rework this method potentially

        if (checkDetailsEntered()) {
            try {
                String givenNames = view.getGivenNamesFieldValue();
                String surnames = view.getsurnamesFieldValue();
                LocalDate dob = view.getdobDatePickerValue();
                String nhi = view.getNhiField();

                Profile newProfile = new Profile(givenNames, surnames, dob, nhi);
                currentDatabase.addProfile(newProfile);
                ProfileDataIO.saveData(currentDatabase);
                return currentDatabase.getProfile(newProfile.getId());
            } catch (NumberFormatException e) {
                if (view.getNhiField().length() > 9) {
                    AlertController.invalidEntry(
                            "Entered IRD number is too long.\nPlease enter up to 9 digits");
                } else {
                    AlertController.invalidEntry("Invalid NHI number entered");
                }
            } catch (IllegalArgumentException e) {
                //show error window.
                AlertController.invalidEntry();
            } catch (NHIConflictException e) {
                invalidNhi();
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
