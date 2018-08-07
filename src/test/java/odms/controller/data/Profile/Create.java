package odms.controller.data.Profile;

import javafx.fxml.FXMLLoader;
import odms.controller.profile.ProfileCreate;
import odms.view.profile.CreateAccount;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDate;

public class Create {
    public CreateAccount view;
    public ProfileCreate controller;

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
