package odms.controller.profile;

import static odms.controller.AlertController.invalidDate;
import static odms.controller.AlertController.invalidEntry;
import static odms.controller.AlertController.invalidNhi;

import odms.commons.model.profile.Profile;
import odms.controller.database.DAOFactory;
import odms.data.NHIConflictException;
import odms.view.profile.CreateAccount;
import odms.controller.CommonController;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

public class ProfileCreate extends CommonController {

    private CreateAccount view;

    public ProfileCreate(CreateAccount v) {
        view = v;
    }

    private boolean checkDetailsEntered() {
        if (view.getGivenNamesFieldValue().isEmpty()) {
            invalidEntry("Please enter Given Name(s)");
            return false;
        }

        if (view.getsurnamesFieldValue().isEmpty()) {
            invalidEntry("Please enter Surname(s)");
            return false;
        }

        if (view.getdobDatePickerValue() == null) {
            invalidEntry("Please enter a Date of Birth");
            return false;
        }
        if (view.getNhiField().isEmpty()) {
            invalidEntry("Please enter an IRD number");
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
                DAOFactory.getProfileDao().add(newProfile);
            } catch (NumberFormatException e) {
                if (view.getNhiField().length() > 9) {
                    invalidEntry(
                            "Entered IRD number is too long.\nPlease enter up to 9 digits");
                } else {
                    invalidEntry("Invalid NHI number entered");
                }
            } catch (IllegalArgumentException e) {
                //show error window.
                invalidEntry();
            } catch (ArrayIndexOutOfBoundsException e) {
                invalidDate();
            } catch (SQLException e) {
                invalidEntry("Database could not parse this profile");
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
