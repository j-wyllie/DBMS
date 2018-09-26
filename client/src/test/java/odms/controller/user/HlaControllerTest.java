package odms.controller.user;

import odms.commons.model.profile.HLAType;
import odms.controller.HlaController;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class HlaControllerTest {
    private HLAType same1 = new HLAType(1,2,3,4,5,6,7,8,9,10,11,12);
    private HLAType same2 = new HLAType(1,2,3,4,5,6,7,8,9,10,11,12);

    private HLAType cross1 = new HLAType(1,2,3,4,5,6,7,8,9,10,11,12);
    private HLAType cross2 = new HLAType(7,8,9,10,11,12,1,2,3,4,5,6);

    private HLAType half1 = new HLAType(1,2,3,4,5,6,7,8,9,10,11,12);
    private HLAType half2 = new HLAType(1,2,3,4,5,6,1,2,3,4,5,6);

    private HLAType zero1 = new HLAType(1,1,1,1,1,1,1,1,1,1,1,1);
    private HLAType zero2 = new HLAType(2,2,2,2,2,2,2,2,2,2,2,2);



    @Test
    public void testMatchScore(){
        assertEquals(new Integer(100), HlaController.matchScore(cross1, cross2));
        assertEquals(new Integer(100), HlaController.matchScore(same1, same2));
        assertEquals(new Integer(50), HlaController.matchScore(half1, half2));
        assertEquals(new Integer(0), HlaController.matchScore(zero1, zero2));
    }



}
