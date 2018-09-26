package odms.controller.user;

import lombok.extern.slf4j.Slf4j;
import odms.commons.model.enums.OrganEnum;
import odms.commons.model.profile.OrganConflictException;
import odms.commons.model.profile.Profile;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static java.lang.StrictMath.abs;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

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
    public void testGetwaitTime() {
        OrganEnum.BONE.setDate(LocalDateTime.now(), profile1);
        String waittime = AvailableOrgans.getWaitTime(OrganEnum.BONE, profile1.getOrgansRequired(), profile1);
        assertEquals(waittime, "Registered today");
    }

    @Test
    public void testGetwaitTimeInvalid() {
        String waittime = AvailableOrgans.getWaitTime(OrganEnum.BONE, profile1.getOrgansRequired(), profile1);
        assertEquals(waittime, "Insufficient data");
    }

    @Test
    public void testFormatDuration() {
        String duration = AvailableOrgans.formatDuration("", Long.MAX_VALUE);
        assertEquals(duration, "106751991167 days 7 hours ");
    }

    @Test
    public void testCheckWaitTimeMinutesHours() {
        String duration = AvailableOrgans.checkWaitTimeMinutesHours("", Long.MAX_VALUE);
        assertEquals(duration,"2562047788015 hours ");
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
    public void testGetTimeRemaining() {
        profile1.setDateOfBirth(LocalDate.MIN);
        profile1.setDateOfDeath(LocalDateTime.MIN.plusYears(1000000000));
        assert(AvailableOrgans.getTimeRemaining(OrganEnum.BONE, profile1) == Duration.between(LocalDateTime.now(), AvailableOrgans.getExpiryTime(OrganEnum.BONE, profile1))
                .toMillis());
    }

    @Test
    public void testGetExpiryLength() {
        Double num = AvailableOrgans.getExpiryLength(OrganEnum.BONE);
        assert(1.5768E11 == num);
    }
}
