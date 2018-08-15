package TestFX.User;

import TestFX.TestFXTest;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import odms.model.enums.UserType;
import odms.model.user.User;
import odms.view.user.ClinicianProfile;
import odms.view.user.UserGeneral;
import org.junit.Before;
import org.junit.BeforeClass;

public class UserTest extends TestFXTest {

    protected User testUserAdmin;

    protected User testUserClinician;

    private void createBasicUsers() {
        testUserAdmin = new User(UserType.ADMIN,"admin");
        setUserDetails(testUserAdmin);

        testUserClinician = new User(UserType.CLINICIAN,"clinician");
        setUserDetails(testUserClinician);
    }

    /**
     * Create clinician window from test user.
     *
     * @param user the test user to make window from.
     */
    protected void createUserGeneralTab(User user) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(
                    getClass().getResource("/view/UserGeneralTab.fxml")
            );

            Scene scene = new Scene(fxmlLoader.load());
            UserGeneral v = fxmlLoader.getController();
            v.initialize(user);

            Platform.runLater(() -> {
                Stage stage = new Stage();
                stage.setTitle(user.getUserType().getName());
                stage.setScene(scene);
                stage.show();
                    });
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
    public static void setupClass() throws TimeoutException {
        setupTestFX();
    }

    @Before
    public void setup() {
        createBasicUsers();
    }
}
