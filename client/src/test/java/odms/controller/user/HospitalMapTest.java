package odms.controller.user;

import com.lynden.gmapsfx.shapes.Polyline;
import odms.commons.model.locations.Hospital;
import odms.view.user.HospitalMap;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class HospitalMapTest {

    public HospitalMap view;
    public odms.controller.user.HospitalMap controller;
    private Hospital hospital1;
    private Hospital hospital2;


    @Before
    public void setup() {
        hospital1 = new Hospital("HospitalTest1", -39.07, 174.05, null, 11);
        hospital2 = new Hospital("HospitalTest2", -40.57, 175.27, null, 14);
        view = new HospitalMap();
        controller = new odms.controller.user.HospitalMap(view);
    }

    @Test
    public void testCalcDistanceHaversine() {
        double distance = controller.calcDistanceHaversine(hospital1.getLatitude(), hospital1.getLongitude(), hospital2.getLatitude(), hospital2.getLongitude());
        assertTrue(distance >= 196.0 && distance <= 197.0);
    }

    @Ignore
    @Test
    public void testCreateHelicopterRoute() {
        Polyline route = controller.createHelicopterRoute(hospital1.getLatitude(), hospital1.getLongitude(), hospital2.getLatitude(), hospital2.getLongitude());
        assertEquals(2, route.getPath().getLength());
        assertEquals("(-40.57, 175.26999999999998)", String.valueOf(route.getPath().getArray().getSlot(0)));
        assertEquals("(-39.07, 174.04999999999995)", String.valueOf(route.getPath().getArray().getSlot(0)));
    }
}
