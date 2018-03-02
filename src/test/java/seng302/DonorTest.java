package seng302;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class DonorTest {
    private Donor testDonor;

    @Before
    public  void setUp() {
        LocalDate dob = LocalDate.of(1997, 7, 24);
        testDonor = new Donor("John James", "Smith", dob);
    }

    @Test
    public void testAddDonatableOrgans() {
        ArrayList<Organ> someOrgans = new ArrayList<>();
        someOrgans.add(Organ.BONE);
        someOrgans.add(Organ.HEART);
        someOrgans.add(Organ.CORNEA);

        testDonor.addOrgans(someOrgans);
        assertEquals(someOrgans, testDonor.getOrgans());
    }
}