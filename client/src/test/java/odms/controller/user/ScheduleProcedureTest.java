package odms.controller.user;

import odms.commons.model.enums.OrganEnum;
import odms.commons.model.locations.Hospital;
import odms.commons.model.profile.Procedure;
import odms.commons.model.profile.Profile;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(PowerMockRunner.class)
@PrepareForTest(odms.view.user.ScheduleProcedure.class)
public class ScheduleProcedureTest {
    private ScheduleProcedure controller = new ScheduleProcedure();
    private odms.view.user.ScheduleProcedure view = new odms.view.user.ScheduleProcedure();

    private Profile donor;
    private Profile receiver;
    private Hospital hospital;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Before
    public void setup() throws Exception {
        donor = new Profile(99999);
        donor.setNhi("XXX9999");
        donor.addOrganDonating(OrganEnum.HEART);
        donor.setBloodType("B+");
        donor.setDateOfBirth(LocalDate.of(1998,01,01));
        donor.setEmail("nobody@doesnotexist.com");

        receiver = new Profile(99998);
        receiver.setNhi("XXX9998");
        receiver.addOrganRequired(OrganEnum.HEART);
        receiver.setBloodType("B+");
        receiver.setDateOfBirth(LocalDate.of(1998,01,01));
        receiver.setEmail("nobody1@doesnotexist.com");


        hospital = new Hospital("Hell's Gate Hospital", -43.522719, 172.582987, "University of Canterbury");

        PowerMockito.stub(PowerMockito.method(
                odms.view.user.OrganMap.class, "getCurrentDonor")).toReturn(donor);
        PowerMockito.stub(PowerMockito.method(
                odms.view.user.OrganMap.class, "getCurrentReceiver")).toReturn(receiver);

        PowerMockito.stub(PowerMockito.method(
                odms.view.user.ScheduleProcedure.class, "getDonor")).toReturn(donor);
        PowerMockito.stub(PowerMockito.method(
                odms.view.user.ScheduleProcedure.class, "getReceiver")).toReturn(receiver);
        PowerMockito.stub(PowerMockito.method(
                odms.view.user.ScheduleProcedure.class, "getSelectedHospital")).toReturn(hospital);
        PowerMockito.stub(PowerMockito.method(
                odms.view.user.ScheduleProcedure.class, "getDonorCheck")).toReturn(true);
        PowerMockito.stub(PowerMockito.method(
                odms.view.user.ScheduleProcedure.class, "getReceiverCheck")).toReturn(true);
        PowerMockito.stub(PowerMockito.method(
                odms.view.user.ScheduleProcedure.class, "getSelectedOrgan")).toReturn(OrganEnum.HEART);
        PowerMockito.stub(PowerMockito.method(
                odms.view.user.ScheduleProcedure.class, "getDatePickerValue")).toReturn(LocalDateTime.MAX);

        controller.setView(view);
    }

    @Test
    public void testGetDonationOrgans() {
        List<OrganEnum> expected =  new ArrayList<>();
        expected.add(OrganEnum.HEART);
        List<OrganEnum> organs = controller.getDonatingOrgans();
        assertEquals(expected, organs);
    }

    @Test
    public void testScheduleProcedure() {
        controller.scheduleProcedure();
        List<Procedure> procedures = donor.getAllProcedures();
        assertEquals(1, procedures.size());
    }
}
