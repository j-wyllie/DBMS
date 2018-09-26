package odms.controller.user;

import odms.commons.model.profile.HLAType;
import odms.controller.HlaController;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;

import static junit.framework.TestCase.assertEquals;

/**
 * Runs tests that confirm correct scoring in the HlaController file.
 *
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest(HlaController.class)
public class HlaControllerTest {
    private HLAType same1 = new HLAType(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);
    private HLAType same2 = new HLAType(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);

    private HLAType cross1 = new HLAType(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);
    private HLAType cross2 = new HLAType(7, 8, 9, 10, 11, 12, 1, 2, 3, 4, 5, 6);

    private HLAType half1 = new HLAType(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);
    private HLAType half2 = new HLAType(1, 2, 3, 4, 5, 6, 1, 2, 3, 4, 5, 6);

    private HLAType zero1 = new HLAType(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1);
    private HLAType zero2 = new HLAType(2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2);

    private HLAType null1 = new HLAType(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1);
    private HLAType null2 = new HLAType(null, null, null, null, null, null, null, null, null,
            null, null, null);


    private HlaController controller;

    @Before
    public void setup() {
        controller = new HlaController();
    }

    @Test
    public void testMatchScoreSame() {
        ArrayList<HLAType> hlas = new ArrayList<>();
        hlas.add(same1);
        hlas.add(same2);
        PowerMockito.stub(
                PowerMockito.method(HlaController.class, "getDatabaseHLA")
        ).toReturn(hlas);

        assertEquals("100%", controller.getMatchString(1, 2));

    }

    @Test
    public void testMatchScoreCross() {
        ArrayList<HLAType> hlas = new ArrayList<>();
        hlas.add(cross1);
        hlas.add(cross2);
        PowerMockito.stub(
                PowerMockito.method(HlaController.class, "getDatabaseHLA")
        ).toReturn(hlas);

        assertEquals("100%", controller.getMatchString(3, 4));
    }

    @Test
    public void testMatchScoreHalf() {
        ArrayList<HLAType> hlas = new ArrayList<>();
        hlas.add(half1);
        hlas.add(half2);
        PowerMockito.stub(
                PowerMockito.method(HlaController.class, "getDatabaseHLA")
        ).toReturn(hlas);

        assertEquals("50%", controller.getMatchString(5, 6));
    }

    @Test
    public void testMatchScoreZero() {
        ArrayList<HLAType> hlas = new ArrayList<>();
        hlas.add(zero1);
        hlas.add(zero2);
        PowerMockito.stub(
                PowerMockito.method(HlaController.class, "getDatabaseHLA")
        ).toReturn(hlas);

        assertEquals("0%", controller.getMatchString(7, 8));
    }

    @Test
    public void testMatchScoreNull() {
        ArrayList<HLAType> hlas = new ArrayList<>();
        hlas.add(null2); //null2 is the receiver
        hlas.add(null1);
        PowerMockito.stub(
                PowerMockito.method(HlaController.class, "getDatabaseHLA")
        ).toReturn(hlas);

        assertEquals("No HLA", controller.getMatchString(9, 10));
    }

}
