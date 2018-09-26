package odms.controller.user;

import static org.junit.Assert.assertEquals;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import odms.commons.model.enums.OrganEnum;
import odms.commons.model.profile.OrganConflictException;
import odms.commons.model.profile.Profile;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

@Slf4j
public class AvailableOrgansTest {
    private Profile profile1;
    private Profile profile2;
    private Profile profile3;

    @Before
    public void setup() {
        profile1 = new Profile("Bob", "Smith", "27-01-1998", "ABC1234");
        profile2 = new Profile("Wendy", "Smith", "01-01-1998", "ABC1235");
        profile3 = new Profile("Jordan", "Smith", "01-02-1998", "ABC1236");
        try {
            profile1.addOrganRequired(OrganEnum.BONE);
            profile2.addOrganDonating(OrganEnum.BONE);
        } catch (OrganConflictException e){
            log.error("Organ Conflict In Test");
        }
        profile1.setDateOfDeath(LocalDateTime.now());
    }

    @Test
    public void testGetWaitTimeRawValid() {
        OrganEnum.BONE.setDate(LocalDateTime.now(), profile1);
        int waittime = Math.toIntExact(AvailableOrgans.getWaitTimeRaw(OrganEnum.BONE, profile1.getOrgansRequired(), profile1));
        assert(waittime >= 0);
    }

    @Test
    public void testGetWaitTimeRawinvalid() {
        int waittime = Math.toIntExact(AvailableOrgans.getWaitTimeRaw(OrganEnum.BONE, profile1.getOrgansRequired(), profile1));
        System.out.println(waittime);
        assert(waittime == -1);
    }

    @Test
    public void testGetwaitTimeInvalid() {
        String waittime = AvailableOrgans.getWaitTime(OrganEnum.BONE, profile1.getOrgansRequired(), profile1);
        assertEquals("Insufficient data", waittime);
    }

    @Test
    public void testFormatDuration() {
        String duration = AvailableOrgans.formatDuration("", Long.MAX_VALUE);
        assertEquals("106751991167 days 7 hours ", duration);
    }

    @Test
    public void testCheckWaitTimeMinutesHours() {
        String duration = AvailableOrgans.checkWaitTimeMinutesHours("", Long.MAX_VALUE);
        assertEquals("2562047788015 hours ", duration);
    }

    @Test
    public void testCheckOrganExpiredInvalid() {
        AvailableOrgans controller = new AvailableOrgans();
        assert(!controller.checkOrganExpired(OrganEnum.BONE, profile1));
    }

    @Test
    public void testGetExpiryTime() {
        profile1.setDateOfBirth(LocalDate.MIN);
        profile1.setDateOfDeath(LocalDateTime.MIN.plusYears(1));
        LocalDateTime expiryTime = AvailableOrgans.getExpiryTime(OrganEnum.BONE, profile1);
        assertEquals(LocalDateTime.MIN.plusYears(6), expiryTime);
    }

    @Test
    public void testGetExpiryLength() {
        Double num = AvailableOrgans.getExpiryLength(OrganEnum.BONE);
        assert(1.5768E11 == num);
    }
}
