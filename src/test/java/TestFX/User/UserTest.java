package TestFX.User;

import static odms.controller.AlertController.invalidUsername;

import TestFX.TestFXTest;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import odms.model.enums.UserType;
import odms.model.user.User;
import odms.view.user.ClinicianProfile;
import org.junit.Before;
import org.junit.BeforeClass;

public class UserTest extends TestFXTest {

    protected User testUserAdmin;

    protected User testUserClinician;

    private void createBasicUsers() {
        testUserAdmin = new User(
                UserType.ADMIN,
                "admin"
        );
        setUserDetails(testUserAdmin);

        testUserClinician = new User(
                UserType.CLINICIAN,
                "clinician"
        );
        setUserDetails(testUserClinician);
    }

    /**
     * Create clinician window from test user.
     *
     * @param user the test user to make window from.
     */
    protected void createUserWindow(User user) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(
                    getClass().getResource("/view/ClinicianProfile.fxml")
            );

            Scene scene = new Scene(fxmlLoader.load());
            ClinicianProfile v = fxmlLoader.getController();
            v.setCurrentUser(user);
            v.initialize();

            Stage stage = new Stage();
            stage.setTitle(user.getUserType().getName());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setUserDetails(User user) {
        user.setPassword("test");
        user.setDefault(true);
        user.setRegion("Canterbury");
        user.setName("Test User");
    }

    @BeforeClass
    public static void setupClass() {
        setupTestFX();
    }

    @Before
    public void setup() {
        createBasicUsers();
    }
}
