package odms.donor;

import java.util.HashSet;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class DonorTest {
    @Test
    public void testCreateUser() throws InstantiationError {
        Donor testDonor = null;

        ArrayList<String> donorAttr = new ArrayList<String>();
        donorAttr.add("given-names=John");
        donorAttr.add("last-names=Smithy Smith Face");
        donorAttr.add("dob=17-01-1998");
        donorAttr.add("ird=123456879");

        try {
            testDonor = new Donor(donorAttr);
        } catch (InstantiationException e) {
            //pass
        }

        assertTrue(testDonor != null);
    }

    @Test(expected = InstantiationException.class)
    public void testCreateUserNoIRD() throws InstantiationException {
        ArrayList<String> donorAttr = new ArrayList<String>();
        donorAttr.add("given-names=John");
        donorAttr.add("last-names=Smithy Smith Face");
        donorAttr.add("dob=17-01-1998");

        Donor donorOnlyAttr = new Donor(donorAttr);
    }

    @Test(expected = InstantiationException.class)
    public void testCreateUserNoDOB() throws InstantiationException {
        ArrayList<String> donorAttr = new ArrayList<String>();
        donorAttr.add("given-names=John");
        donorAttr.add("last-names=Smithy Smith Face");
        donorAttr.add("ird=123456879");;

        Donor donorOnlyAttr = new Donor(donorAttr);
    }

    @Test(expected = InstantiationException.class)
    public void testCreateUserNoFirstName() throws InstantiationException {
        ArrayList<String> donorAttr = new ArrayList<String>();
        donorAttr.add("last-names=Smithy Smith Face");
        donorAttr.add("dob=17-01-1998");
        donorAttr.add("ird=123456879");

        Donor donorOnlyAttr = new Donor(donorAttr);
    }

    @Test(expected = InstantiationException.class)
    public void testCreateUserNoLastName() throws InstantiationException {
        ArrayList<String> donorAttr = new ArrayList<String>();
        donorAttr.add("given-names=John");
        donorAttr.add("dob=17-01-1998");
        donorAttr.add("ird=123456879");

        Donor donorOnlyAttr = new Donor(donorAttr);
    }

    @Test
    public void testAddDonatableOrgans() {
        ArrayList<String> donorAttr = new ArrayList<String>();
        donorAttr.add("given-names=John");
        donorAttr.add("last-names=Smithy Smith Face");
        donorAttr.add("dob=17-01-1998");
        donorAttr.add("ird=123456879");

        Donor testDonor = null;
        try {
            testDonor = new Donor(donorAttr);
        } catch (InstantiationException e) {
            // pass
        }

        Set<Organ> someOrgans = new HashSet<>();
        someOrgans.add(Organ.BONE);
        someOrgans.add(Organ.HEART);
        someOrgans.add(Organ.CORNEA);
        testDonor.addOrgans(someOrgans);

        assertEquals(someOrgans, testDonor.getOrgans());
    }
}