package seng302;

import ODMS.Donor.Donor;
import ODMS.Donor.Organ;
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
        LocalDate dob = LocalDate.of(1997, 7, 24);
        testDonor = new Donor("John James", "Smith", dob);
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