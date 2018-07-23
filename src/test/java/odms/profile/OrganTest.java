package odms.profile;

import static junit.framework.TestCase.assertEquals;

import java.time.LocalDate;
import odms.enums.OrganEnum;
import org.junit.Test;

public class OrganTest {

    @Test
    public void testCorrectDateOnCreation() {
        Organ organ = new Organ(OrganEnum.LIVER);
        assertEquals(LocalDate.now(), organ.getDateOfRegistration());
    }
}
