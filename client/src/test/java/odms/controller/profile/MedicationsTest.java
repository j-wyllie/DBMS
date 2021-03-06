package odms.controller.profile;

import odms.commons.model.medications.Drug;
import odms.commons.model.profile.Profile;
import odms.view.profile.MedicationsGeneral;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class MedicationsTest {
    public MedicationsGeneral view;
    public Medications controller;
    public Profile currentProfile;
    public Drug drug;

    @Before
    public void setup() {
        ArrayList<String> profileOneAttr = new ArrayList<>();
        profileOneAttr.add("given-names=\"John\"");
        profileOneAttr.add("last-names=\"Wayne\"");
        profileOneAttr.add("dob=\"17-01-1998\"");
        profileOneAttr.add("nhi=\"123456879\"");
        currentProfile = new Profile(profileOneAttr);
        currentProfile.setId(999);
        controller = new Medications();
        drug = new Drug("Drog");
    }

    @Before
    @Test
    public void testAddValidDrug() {
        controller.addDrug(drug, currentProfile);
        assertEquals(currentProfile.getCurrentMedications().get(0), drug);
    }

    @Test
    public void testDeleteValidDrugFromCurrent() {
        controller.deleteDrug(currentProfile, drug);
        assertFalse(currentProfile.getCurrentMedications().contains(drug));
    }

    @Before
    @Test
    public void testMoveDrugToHistory() {
        controller.moveDrugToHistory(drug, currentProfile);
        assert(currentProfile.getHistoryOfMedication().contains(drug));
    }

    @Test
    public void testDeleteValidDrugFromHistory() {
        controller.deleteDrug(currentProfile, drug);
        assertFalse(currentProfile.getHistoryOfMedication().contains(drug));
    }

    @Test
    public void testMoveDrugToCurrent() {
        controller.moveDrugToCurrent(drug,currentProfile);
        assert(currentProfile.getCurrentMedications().contains(drug));
    }

    @Test
    public void testViewActiveIngredients() throws IOException{
        assertNotEquals(controller.viewActiveIngredients(drug), null);
    }

    @Test
    public void testGetRawInteractions() throws IOException{
        ArrayList<Drug> drugs = new ArrayList<>();
        drugs.add(drug);
        drugs.add(drug);
        assertNotEquals(controller.getRawInteractions(currentProfile, drugs), null);
    }

}
