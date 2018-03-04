package seng302;

import odms.Donor.Donor;
import odms.Donor.Organ;

import java.lang.reflect.Array;
import java.util.HashSet;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class DonorTest {
    private Donor testDonor;

    @Before
    public  void setUp() {

        ArrayList<String> donorAttr = new ArrayList<String>();
        donorAttr.add("first-names=John");
        donorAttr.add("last-names=Smithy Smith Face");
        donorAttr.add("blood-type=O+");
        donorAttr.add("gender=male");
        donorAttr.add("region=Christchurch");
        donorAttr.add("weight=83.2");

        LocalDate dob = LocalDate.of(1997, 7, 24);
        testDonor = new Donor("John James", "Smith", dob, "321856156", donorAttr);

        testDonor.viewAttributes();
    }

    @Test
    public void testAddDonatableOrgans() {
        Set<Organ> someOrgans = new HashSet<>();
        someOrgans.add(Organ.BONE);
        someOrgans.add(Organ.HEART);
        someOrgans.add(Organ.CORNEA);

        testDonor.addOrgans(someOrgans);
        assertEquals(someOrgans, testDonor.getOrgans());
    }
}