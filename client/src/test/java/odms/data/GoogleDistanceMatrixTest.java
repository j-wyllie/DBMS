package odms.data;

import odms.commons.model.locations.Hospital;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class GoogleDistanceMatrixTest {


    public Hospital hospitalDunedin;
    public Hospital hospitalChristchurch;
    public Hospital hospitalTaranaki;


    @Before
    public void setup() {

        hospitalDunedin = new Hospital("Dunedin hospital", -45.869212, 170.508920, null, 97);
        hospitalChristchurch = new Hospital("Christchurch hospital", -43.533806, 172.625894, null, 98);
        hospitalTaranaki = new Hospital("Taranaki hospital", -39.072759, 174.056320, null, 99);

    }


    @Test
    public void testCalculateDuration() {

        double duration;

        try {
            duration = new GoogleDistanceMatrix().getDuration(
                    hospitalChristchurch.getLatitude(), hospitalDunedin.getLongitude(),
                    hospitalChristchurch.getLatitude(), hospitalChristchurch.getLongitude()
            );
        } catch (IOException e) {
            // Fail
            duration = -1.0;
        }

        assertTrue(duration >= 196.0 && duration <= 197.0);

    }

    @Test
    public void testCalculateDurationFail() {

        try {
            new GoogleDistanceMatrix().getDuration(
                    hospitalTaranaki.getLatitude(), hospitalTaranaki.getLongitude(),
                    hospitalTaranaki.getLatitude(), hospitalTaranaki.getLongitude()
            );
            fail("Should of failed on the origin and destination being the same location");
        } catch (IOException e) {
            Assert.assertTrue(true);
        }
    }

}
