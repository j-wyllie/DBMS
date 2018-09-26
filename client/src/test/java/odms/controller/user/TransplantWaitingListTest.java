package odms.controller.user;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import odms.commons.model.enums.OrganEnum;
import odms.commons.model.profile.Profile;
import org.junit.Before;
import org.junit.Test;

public class TransplantWaitingListTest {

    private static final TransplantWaitingList CONTROLLER = new TransplantWaitingList();
    private String searchString1;
    private String searchString2;
    private String searchString3;
    private Profile testProfile1;
    private Profile testProfile2;
    private Profile testProfile3;
    private Profile testProfile4;
    private List<Entry<Profile, OrganEnum>> receiverList;

    @Before
    public void setUp() {
        searchString1 = "b";
        searchString2 = "Jane";
        searchString3 = "";
        receiverList = new ArrayList<>();

        testProfile1 = new Profile("Ben", "Boyce", "12-12-1212", "ABC123");
        testProfile2 = new Profile("Barry", "Mann", "11-12-1212", "ABC124");
        testProfile3 = new Profile("Sarah", "boyce", "10-12-1212", "ABC125");
        testProfile4 = new Profile("Connie", "Janey", "13-12-1212", "ABC126");

        receiverList.add(new SimpleEntry<>(testProfile1, OrganEnum.BONE));
        receiverList.add(new SimpleEntry<>(testProfile2, OrganEnum.LIVER));
        receiverList.add(new SimpleEntry<>(testProfile2, OrganEnum.BONE));
        receiverList.add(new SimpleEntry<>(testProfile3, OrganEnum.HEART));
        receiverList.add(new SimpleEntry<>(testProfile4, OrganEnum.SKIN));
    }

    @Test
    public void searchWaitingListSingleLetter() {
        List<Entry<Profile, OrganEnum>> result =
                CONTROLLER.searchWaitingList(receiverList, searchString1);

        assertTrue(result.contains(new SimpleEntry<>(testProfile1, OrganEnum.BONE)));
        assertTrue(result.contains(new SimpleEntry<>(testProfile2, OrganEnum.LIVER)));
        assertTrue(result.contains(new SimpleEntry<>(testProfile2, OrganEnum.BONE)));
        assertTrue(result.contains(new SimpleEntry<>(testProfile3, OrganEnum.HEART)));
        assertEquals(4, result.size());
    }

    @Test
    public void searchWaitingListName() {
        List<Entry<Profile, OrganEnum>> result =
                CONTROLLER.searchWaitingList(receiverList, searchString2);

        assertTrue(result.contains(new SimpleEntry<>(testProfile4, OrganEnum.SKIN)));
        assertEquals(1, result.size());
    }

    @Test
    public void searchWaitingListEmptyString() {
        List<Entry<Profile, OrganEnum>> result =
                CONTROLLER.searchWaitingList(receiverList, searchString3);

        assertEquals(receiverList, result);
        assertEquals(5, result.size());
    }
}