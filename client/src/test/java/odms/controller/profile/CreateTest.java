package odms.controller.profile;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import odms.commons.model.profile.Profile;
import odms.controller.database.profile.HttpProfileDAO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(HttpProfileDAO.class)
public class CreateTest {
    ProfileCreate controller;

    @Before
    public void setup() {
        PowerMockito.stub(PowerMockito.method(HttpProfileDAO.class, "add"))
                .toReturn(true);
        controller = new ProfileCreate();
    }

    @Test
    public void testCreateAccountInvalid() {
        Profile profile = controller.createAccount("John","Smith", LocalDate.now(),"ABC1234",9);
        assertEquals("John", profile.getGivenNames());
    }
}