package odms.controller.profile;

import javafx.fxml.FXMLLoader;
import odms.commons.model.profile.Profile;
import odms.view.profile.CreateAccount;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDate;

import static org.junit.Assert.assertEquals;


public class CreateTest {
    ProfileCreate controller;

    @Before
    public void setup() throws IOException {
        controller = new ProfileCreate();
    }

    @Test
    public void testCreateAccountInvalid() {
        Profile profile = controller.createAccount("John","Smith", LocalDate.now(),"ABC1234",9);
        assertEquals(profile.getGivenNames(),"John");
    }
}