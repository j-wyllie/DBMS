package odms.controller.data.Profile;

import odms.controller.profile.ProfileEditController;
import odms.model.profile.Profile;
import odms.view.profile.ProfileEditView;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class Edit {
    //todo Tests can't work using a view as it can't be loaded and can't set values for fxml elements if it hasn't been loaded

    public ProfileEditView view;
    public ProfileEditController controller;
    public Profile currentProfile;

    @Before
    public void setup() {
        ArrayList<String> profileOneAttr = new ArrayList<>();
        profileOneAttr.add("given-names=\"John\"");
        profileOneAttr.add("last-names=\"Wayne\"");
        profileOneAttr.add("dob=\"17-01-1998\"");
        profileOneAttr.add("nhi=\"123456879\"");
        currentProfile = new Profile(profileOneAttr);
        view = new ProfileEditView();
        view.initialize(currentProfile, false);
        controller = new ProfileEditController(view);
    }

    @Test
    public void testSaveDateOfBirthNullValue() {
        try {
            controller.saveDateOfBirth();
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testSaveDateOfBirthCorrectValue() {
        try {
            LocalDate oldValue = currentProfile.getDateOfBirth();
            view.setdobDatePicker(LocalDate.now());
            controller.saveDateOfBirth();
            assertNotEquals(oldValue, currentProfile.getDateOfBirth());
        } catch (IllegalArgumentException e) {
            fail();
        }
    }

    @Test
    public void testSaveGivenNamesNullValue() {
        try {
            controller.saveGivenNames();
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testSaveGivenNamesCorrectValue() {
        try {
            String oldValue = currentProfile.getGivenNames();
            view.setGivenNamesField("Jim");
            controller.saveGivenNames();
            assertNotEquals(oldValue, currentProfile.getGivenNames());
        } catch (IllegalArgumentException e) {
            fail();
        }
    }

    @Test
    public void testSaveLastNamesNullValue() {
        try {
            controller.saveLastNames();
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testSaveLastNamesCorrectValue() {
        try {
            String oldValue = currentProfile.getLastNames();
            view.setLastNamesField("Smith");
            controller.saveLastNames();
            assertNotEquals(oldValue, currentProfile.getLastNames());
        } catch (IllegalArgumentException e) {
            fail();
        }
    }

    @Test
    public void testSaveDateOfDeathNullValue() {
        try {
            controller.saveDateOfDeath();
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testSaveDateOfDeathCorrectValue() {
        try {
            LocalDate oldValue = currentProfile.getDateOfDeath();
            view.setDODDatePicker(LocalDate.now());
            controller.saveDateOfDeath();
            assertNotEquals(oldValue, currentProfile.getDateOfDeath());
        } catch (IllegalArgumentException e) {
            fail();
        }
    }

    @Test
    public void testSaveNHINullValue() {
        try {
            controller.saveNhi();
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testSaveNHICorrectValue() {
        try {
            String oldValue = currentProfile.getNhi();
            view.setNhiField("587095144");
            controller.saveNhi();
            assertNotEquals(oldValue, currentProfile.getNhi());
        } catch (IllegalArgumentException e) {
            fail();
        }
    }

    @Test
    public void testSaveAddressNullValue() {
        try {
            controller.saveAddress();
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSaveAddressCorrectValue() {
        try {
            String oldValue = currentProfile.getAddress();
            view.setAddressField("51 Somewhere St");
            controller.saveAddress();
            assertNotEquals(oldValue, currentProfile.getAddress());
        } catch (IllegalArgumentException e) {
            fail();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSaveEmailNullValue() {
        try {
            controller.saveEmail();
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testSaveEmailCorrectValue() {
        try {
            String oldValue = currentProfile.getEmail();
            view.setAddressField("an.email@gmail.com");
            controller.saveEmail();
            assertNotEquals(oldValue, currentProfile.getEmail());
        } catch (IllegalArgumentException e) {
            fail();
        }
    }

    @Test
    public void testSaveGenderNullValue() {
        try {
            controller.saveGender();
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testSaveGenderCorrectValue() {
        try {
            String oldValue = currentProfile.getGender();
            //todo work out what the object is
            view.setComboGender("MALE");
            controller.saveGender();
            assertNotEquals(oldValue, currentProfile.getGender());
        } catch (IllegalArgumentException e) {
            fail();
        }
    }

    @Test
    public void testSaveHeightNullValue() {
        try {
            controller.saveHeight();
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testSaveHeightCorrectValue() {
        try {
            Double oldValue = currentProfile.getHeight();
            view.setHeightField("1.42");
            controller.saveHeight();
            assertNotEquals(oldValue, currentProfile.getHeight());
        } catch (IllegalArgumentException e) {
            fail();
        }
    }

    @Test
    public void testSaveWeightNullValue() {
        try {
            controller.saveWeight();
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testSaveWeightCorrectValue() {
        try {
            Double oldValue = currentProfile.getWeight();
            view.setHeightField("74.0");
            controller.saveWeight();
            assertNotEquals(oldValue, currentProfile.getWeight());
        } catch (IllegalArgumentException e) {
            fail();
        }
    }

    @Test
    public void testSavePhoneNullValue() {
        try {
            controller.savePhone();
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testSavePhoneCorrectValue() {
        try {
            String oldValue = currentProfile.getPhone();
            view.setPhoneField("0274511902");
            controller.savePhone();
            assertNotEquals(oldValue, currentProfile.getPhone());
        } catch (IllegalArgumentException e) {
            fail();
        }
    }

    @Test
    public void testSavePreferredGenderNullValue() {
        try {
            String oldValue = currentProfile.getPreferredGender();
            controller.savePreferredGender();
            assertEquals(oldValue, currentProfile.getPreferredGender());
        } catch (IllegalArgumentException e) {
            fail();
        }
    }

    @Test
    public void testSavePreferredGenderCorrectValue() {
        try {
            String oldValue = currentProfile.getPreferredGender();
            view.setPhoneField("Female");
            controller.savePreferredGender();
            assertNotEquals(oldValue, currentProfile.getPreferredGender());
        } catch (IllegalArgumentException e) {
            fail();
        }
    }

    @Test
    public void testSavePreferredNameCorrectValue() {
        try {
            String oldValue = currentProfile.getPreferredName();
            view.setPhoneField("Jimmy");
            controller.savePreferredName();
            assertNotEquals(oldValue, currentProfile.getPreferredName());
        } catch (IllegalArgumentException e) {
            fail();
        }
    }

    @Test
    public void testSaveRegionNullValue() {
        try {
            controller.saveRegion();
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testSaveRegionCorrectValue() {
        try {
            String oldValue = currentProfile.getRegion();
            view.setPhoneField("Canterbury");
            controller.saveRegion();
            assertNotEquals(oldValue, currentProfile.getRegion());
        } catch (IllegalArgumentException e) {
            fail();
        }
    }

    @Test
    public void testSaveAlcoholConsumptionCorrectValue() {
        try {
            String oldValue = currentProfile.getAlcoholConsumption();
            view.setAlcoholConsumptionField("120");
            controller.saveAlcoholConsumption();
            assertNotEquals(oldValue, currentProfile.getAlcoholConsumption());
        } catch (IllegalArgumentException e) {
            fail();
        }
    }

    @Test
    public void testSaveBloodPressureCorrectValue() {
        try {
            String oldValue = currentProfile.getBloodPressure();
            view.setBloodPressureField("12/14");
            controller.saveBloodPressure();
            assertNotEquals(oldValue, currentProfile.getBloodPressure());
        } catch (IllegalArgumentException e) {
            fail();
        }
    }

    @Test
    public void testSaveBloodTypeCorrectValue() {
        try {
            String oldValue = currentProfile.getBloodType();
            view.setBloodTypeField("O-");
            controller.saveBloodType();
            assertNotEquals(oldValue, currentProfile.getBloodType());
        } catch (IllegalArgumentException e) {
            fail();
        }
    }

    @Test
    public void testSaveisSmokerCorrectValue() {
        try {
            Boolean oldValue = currentProfile.getIsSmoker();
            view.setIsSmokerRadioButton(true);
            controller.saveIsSmoker();
            assertNotEquals(oldValue, currentProfile.getIsSmoker());
        } catch (IllegalArgumentException e) {
            fail();
        }
    }

}
