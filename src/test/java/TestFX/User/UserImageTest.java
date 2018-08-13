package TestFX.User;

import javafx.scene.Scene;
import odms.model.user.User;
import org.junit.Test;

public class UserImageTest extends UserTest {

    private void testAddImage(User user) {
//        Scene scene = lookup("#editButton")
        createUserWindow(testUserAdmin);
        clickOn("#editButton");
    }

    @Test
    public void testAddImageToAdmin() {
        testAddImage(testUserAdmin);
    }

    @Test
    public void testAddImageToClinician() {
        testAddImage(testUserClinician);
    }


    // with missing image file
}
