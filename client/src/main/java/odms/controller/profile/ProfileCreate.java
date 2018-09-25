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

    /**
     * Scene change to profile profile view if all required fields are filled in.
     */
    public Profile createAccount(String givenNames, String surnames, LocalDate dob, String nhi, int nhiLength) {
            try {
                Profile newProfile = new Profile(givenNames, surnames, dob, nhi);
                DAOFactory.getProfileDao().add(newProfile);
                return newProfile;
            } catch (NumberFormatException e) {
                if (nhiLength > 9) {
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
            } catch (NHIConflictException e) {
                invalidEntry("NHI number is already used by another account");
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
