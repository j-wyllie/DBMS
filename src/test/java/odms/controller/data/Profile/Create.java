package odms.controller.data.Profile;

import javafx.fxml.FXMLLoader;
import odms.controller.profile.ProfileCreateController;
import odms.model.profile.Profile;
import odms.view.profile.ProfileCreateAccountView;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

public class Create {
    public ProfileCreateAccountView view;
    public ProfileCreateController controller;

    @Before
    public void setup() throws IOException{
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(
                getClass().getResource("/view/ProfileCreate.fxml"));
        fxmlLoader.load();
        view = fxmlLoader.getController();
        System.out.println(view);
        controller = view.getController();
    }

    @Test
    public void testCreateAccountInvalid() throws IOException {
        view.setGivenNamesFieldValue("John");
        view.setsurnamesFieldValue("Smith");
        view.setdobDatePickerValue(LocalDate.now());
        view.setNhiField("ABC1234");
        controller.createAccount();
    }


}
