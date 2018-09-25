package odms.controller.user;

import javafx.collections.ObservableList;
import lombok.extern.slf4j.Slf4j;
import odms.commons.model.enums.OrganEnum;
import odms.commons.model.profile.OrganConflictException;
import odms.commons.model.profile.Profile;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class OrganMapTest{

    private Profile profile1;
    private Profile profile2;
    private Profile profile3;


    /**
     * Creates three profiles two with organs and one without.
     */
    @Before
    public void setup() {
        profile1 = new Profile("Bob", "Smith", "27-01-1998", "ABC1234");
        profile2 = new Profile("Wendy", "Smith", "01-01-1998", "ABC1235");
        profile3 = new Profile("Jordan", "Smith", "01-02-1998", "ABC1236");
        try {
            profile1.addOrganDonating(OrganEnum.BONE);
            profile2.addOrganDonating(OrganEnum.BONE);
        } catch (OrganConflictException e){
            log.error("Organ Conflict In Test");
        }
        profile1.setDateOfDeath(LocalDateTime.now());
    }

    @Test
    public void getDeadDonorsTestTwo() {
        OrganMap controller = new OrganMap();
        List<Profile> allDonors = new ArrayList<>();
        allDonors.add(profile1);
        allDonors.add(profile2);
        allDonors.add(profile3);
        ObservableList<Profile> deadDonors = controller.sortDeadDonorListForExpired(allDonors);
        assertEquals(deadDonors.size(), 2);
    }

    @Test
    public void getDeadDonorsTestOne() {
        OrganMap controller = new OrganMap();
        List<Profile> allDonors = new ArrayList<>();
        allDonors.add(profile1);
        allDonors.add(profile3);
        ObservableList<Profile> deadDonors = controller.sortDeadDonorListForExpired(allDonors);
        assertEquals(deadDonors.size(), 1);
    }

    @Test
    public void getDeadDonorsTestZero() {
        OrganMap controller = new OrganMap();
        List<Profile> allDonors = new ArrayList<>();
        allDonors.add(profile3);
        ObservableList<Profile> deadDonors = controller.sortDeadDonorListForExpired(allDonors);
        assertEquals(deadDonors.size(), 0);
    }



}
