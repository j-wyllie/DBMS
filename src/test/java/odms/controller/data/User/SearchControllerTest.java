package odms.controller.data.User;

import static junit.framework.TestCase.assertEquals;

import java.util.ArrayList;
import javafx.beans.property.SimpleStringProperty;
import odms.controller.user.UserSearchController;
import odms.model.profile.Profile;
import odms.view.user.UserSearchView;
import org.junit.Before;
import org.junit.Test;

public class SearchControllerTest {
    UserSearchView view = new UserSearchView();
    UserSearchController controller = new UserSearchController(view);
    Profile testProfile;
    SimpleStringProperty expected = new SimpleStringProperty();
    @Before
    public void setUp() {
        ArrayList<String> profileAttr = new ArrayList<>();

        profileAttr.add("given-names=\"John\"");
        profileAttr.add("last-names=\"Smithy Smith Face\"");
        profileAttr.add("dob=\"01-01-2000\"");
        profileAttr.add("dod=\"01-01-2050\"");
        profileAttr.add("nhi=\"123456879\"");

        testProfile = null;

        try {
            testProfile = new Profile(profileAttr);
        } catch (IllegalArgumentException e) {
            // pass
        }
    }

    @Test
    public void testDonorReceiverPropertyDonorReceiver() {
        testProfile.setDonor(true);
        testProfile.setReceiver(true);
        expected.setValue("Donor/Receiver");

        assertEquals(expected.toString(), controller.donorReceiverProperty(testProfile).toString());
    }

    @Test
    public void testDonorReceiverPropertyDonor() {

        testProfile.setDonor(true);
        testProfile.setReceiver(false);
        expected.setValue("Donor");

        assertEquals(expected.toString(), controller.donorReceiverProperty(testProfile).toString());
    }

    @Test
    public void testDonorReceiverPropertyReceiver() {

        testProfile.setDonor(false);
        testProfile.setReceiver(true);
        expected.setValue("Receiver");

        assertEquals(expected.toString(), controller.donorReceiverProperty(testProfile).toString());
    }

    @Test
    public void testDonorReceiverPropertyNull() {
        testProfile.setDonor(false);
        testProfile.setReceiver(false);
        expected.setValue(null);

        assertEquals(expected.toString(), controller.donorReceiverProperty(testProfile).toString());
    }
}
