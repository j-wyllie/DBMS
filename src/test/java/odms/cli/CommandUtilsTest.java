package odms.cli;

import java.sql.SQLException;
import odms.controller.database.DAOFactory;
import odms.controller.database.ProfileDAO;
import odms.controller.database.UserDAO;
import odms.model.data.NHIConflictException;
import odms.model.data.ProfileDatabase;
import odms.model.data.UserDatabase;
import odms.model.profile.Profile;
import odms.model.user.User;
import org.jline.reader.impl.completer.ArgumentCompleter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

import static odms.cli.CommandUtils.validateCommandType;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CommandUtilsTest {

    private ProfileDatabase profileDb;
    private UserDatabase userDb;
    private Profile profileOne;
    private Profile profileTwo;

    // Output Handling
    private PrintStream stdout;
    private ByteArrayOutputStream result;

    @Before
    public void setup() {
        // Capture output stream
        stdout = System.out;
        result = new ByteArrayOutputStream();
        System.setOut(new PrintStream(result));

        // Database setup
        this.profileDb = new ProfileDatabase();
        this.userDb = new UserDatabase();

        // Create some test profiles
        ArrayList<String> donorOneAttr = new ArrayList<>();
        donorOneAttr.add("given-names=\"John\"");
        donorOneAttr.add("last-names=\"Wayne\"");
        donorOneAttr.add("dob=\"17-01-1998\"");
        donorOneAttr.add("nhi=\"123456789\"");

        ArrayList<String> donorTwoAttr = new ArrayList<>();
        donorTwoAttr.add("given-names=\"Sam\"");
        donorTwoAttr.add("last-names=\"Sick\"");
        donorTwoAttr.add("dob=\"17-01-1997\"");
        donorTwoAttr.add("nhi=\"123456878\"");

        try {
            profileOne = new Profile(donorOneAttr);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        try {
            profileTwo = new Profile(donorTwoAttr);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        try {
            profileDb.addProfile(profileOne);
            profileDb.addProfile(profileTwo);
        } catch (NHIConflictException e) {
            e.printStackTrace();
        }

    }

    @After
    public void cleanup() {
        try {
            Files.deleteIfExists(Paths.get("CommandUtilsTest.json"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Reset output stream
        System.setOut(stdout);
    }

    @Test
    public void testCommandValidation() {
        // Command Strings
        String createProfileTestStr = "create-profile given-names=\"Abby Rose\" last-names=\"Walker\" dob=\"03-03-1998\" nhi=\"123456789\"";
        String createClinicianTestStr = "create-clinician name=\"Bob Ross\"";
        String viewDonorTestStr = "profile dob=\"03-03-1998\" > view";
        String viewDonationsTestStr = "profile dob=\"03-03-1998\" > donations";
        String viewDateCreatedTestStr = "profile dob=\"03-03-1998\" > date-created";
        String updateDonorTestStr = "profile given-names=\"Abby Rose\" last-names=\"Walker\" dob=\"03-03-1998\" > height=\"169\" given-names=\"Abby Rose\"";
        String viewClinicianTestStr = "clinician name=\"Bob Ross\" > view";
        String viewClinicianDateCreatedTestStr = "clinician dob=\"03-03-1998\" > date-created";
        String updateClinicianTestStr = "clinician name=\"Bob Ross\" region=\"Waikato\" > name=\"Johny Sinz\"";
        String printAllProfilesTestStr = "print all profiles";
        String printDonorsTestStr = "print donors";
        String printAllUsersTestStr = "print all users";
        String printCliniciansTestStr = "print clinicians";
        String helpTestStr = "help";
        String addOrganTestStr = "profile given-names=\"Abby Rose\" last-names=\"Walker\" dob=\"03-03-1998\" > add-organ=\"liver, kidney\"";
        String deleteOrganTestStr = "profile given-names=\"Abby Rose\" last-names=\"Walker\" dob=\"03-03-1998\" > remove-organ=\"liver, kidney\"";
        String invalidCommandTestStr = "This is not a command";

        // Command Arrays
        ArrayList<String> createProfileTest = new ArrayList<>(
                Arrays.asList(createProfileTestStr.split("\\s")));
        ArrayList<String> viewDonorTest = new ArrayList<>(
                Arrays.asList(viewDonorTestStr.split("\\s")));
        ArrayList<String> viewDonationsTest = new ArrayList<>(
                Arrays.asList(viewDonationsTestStr.split("\\s")));
        ArrayList<String> viewDateCreatedTest = new ArrayList<>(
                Arrays.asList(viewDateCreatedTestStr.split("\\s")));
        ArrayList<String> updateDonorTest = new ArrayList<>(
                Arrays.asList(updateDonorTestStr.split("\\s")));
        ArrayList<String> createClinicianTest = new ArrayList<>(
                Arrays.asList(createClinicianTestStr.split("\\s")));
        ArrayList<String> viewClinicianTest = new ArrayList<>(
                Arrays.asList(viewClinicianTestStr.split("\\s")));
        ArrayList<String> viewClinicianDateCreatedTest = new ArrayList<>(
                Arrays.asList(viewClinicianDateCreatedTestStr.split("\\s")));
        ArrayList<String> updateClinicianTest = new ArrayList<>(
                Arrays.asList(updateClinicianTestStr.split("\\s")));
        ArrayList<String> printAllProfilesTest = new ArrayList<>(
                Arrays.asList(printAllProfilesTestStr.split("\\s")));
        ArrayList<String> printDonorsTest = new ArrayList<>(
                Arrays.asList(printDonorsTestStr.split("\\s")));
        ArrayList<String> printAllUsersTest = new ArrayList<>(
                Arrays.asList(printAllUsersTestStr.split("\\s")));
        ArrayList<String> printCliniciansTest = new ArrayList<>(
                Arrays.asList(printCliniciansTestStr.split("\\s")));
        ArrayList<String> helpTest = new ArrayList<>(Arrays.asList(helpTestStr.split("\\s")));
        ArrayList<String> addOrganTest = new ArrayList<>(
                Arrays.asList(addOrganTestStr.split("\\s")));
        ArrayList<String> deleteOrganTest = new ArrayList<>(
                Arrays.asList(deleteOrganTestStr.split("\\s")));
        ArrayList<String> invalidCommandTest = new ArrayList<>(
                Arrays.asList(invalidCommandTestStr.split("\\s")));

        // Check tokens validate appropriately
        assertEquals(Commands.PRINTALLPROFILES,
                validateCommandType(printAllProfilesTest, printAllProfilesTestStr));
        assertEquals(Commands.PRINTDONORS,
                validateCommandType(printDonorsTest, printDonorsTestStr));
        assertEquals(Commands.PRINTALLUSERS,
                validateCommandType(printAllUsersTest, printAllUsersTestStr));
        assertEquals(Commands.PRINTCLINICIANS,
                validateCommandType(printCliniciansTest, printCliniciansTestStr));
        assertEquals(Commands.HELP, validateCommandType(helpTest, helpTestStr));
        assertEquals(Commands.PROFILECREATE,
                validateCommandType(createProfileTest, createProfileTestStr));
        assertEquals(Commands.PROFILEVIEW, validateCommandType(viewDonorTest, viewDonorTestStr));
        assertEquals(Commands.PROFILEDATECREATED,
                validateCommandType(viewDateCreatedTest, viewDateCreatedTestStr));
        assertEquals(Commands.PROFILEDONATIONS,
                validateCommandType(viewDonationsTest, viewDonationsTestStr));
        assertEquals(Commands.PROFILEUPDATE,
                validateCommandType(updateDonorTest, updateDonorTestStr));

        assertEquals(Commands.CLINICIANCREATE,
                validateCommandType(createClinicianTest, createClinicianTestStr));
        assertEquals(Commands.CLINICIANEVIEW,
                validateCommandType(viewClinicianTest, viewClinicianTestStr));
        assertEquals(Commands.CLINICIANDATECREATED,
                validateCommandType(viewClinicianDateCreatedTest, viewClinicianDateCreatedTestStr));
        assertEquals(Commands.CLINICIANUPDATE,
                validateCommandType(updateClinicianTest, updateClinicianTestStr));

        assertEquals(Commands.ORGANADD, validateCommandType(addOrganTest, addOrganTestStr));
        assertEquals(Commands.ORGANREMOVE,
                validateCommandType(deleteOrganTest, deleteOrganTestStr));
        assertEquals(Commands.INVALID,
                validateCommandType(invalidCommandTest, invalidCommandTestStr));
    }

    @Test
    public void testArgumentCompleter() {
        ArgumentCompleter completer = Commands.commandAutoCompletion();
        assertEquals(completer.getClass(), ArgumentCompleter.class);
    }

    @Test
    public void testCreateProfileCommand() throws SQLException {
        String givenNames = "Given Names";
        String lastNames = "Last Names";
        String dob = "12-08-1989";
        String nhi = "ABC1234";

        LocalDate dobConverted = LocalDate.of(
                Integer.valueOf(dob.split("-")[2]),
                Integer.valueOf(dob.split("-")[1]),
                Integer.valueOf(dob.split("-")[0])
        );

        String createProfileStr = "create-profile " +
                "given-names=\"" + givenNames + "\" " +
                "last-names=\"" + lastNames + "\" " +
                "dob=\"" + dob + "\" " +
                "nhi=\"" + nhi + "\"";


        odms.cli.commands.Profile.createProfile(createProfileStr);
        ProfileDAO database = DAOFactory.getProfileDao();

        Profile profile = database.search(nhi, 0, 0, null,
                null, null, null).get(0);

        assertEquals(profile.getGivenNames(), givenNames);
        assertEquals(profile.getLastNames(), lastNames);
        assertEquals(profile.getDateOfBirth(), dobConverted);
        assertEquals(profile.getNhi(), nhi);
    }

    @Test
    public void testCreateClinicianCommand() {
        String name = "Jose";
        String region = "Lopez";
        String workAddress = "Christchurch";

        String createClinicianStr = "create-clinician " +
                "name=\"" + name + "\" " +
                "region=\"" + region + "\" " +
                "workAddress=\"" + workAddress + "\" ";

        User user = odms.cli.commands.User.createClinician(createClinicianStr);

        assertEquals(user.getName(), name);
        assertEquals(user.getRegion(), region);
        assertEquals(user.getWorkAddress(), workAddress);
    }


    @Test
    public void testDeleteProfileCommand() {
        String nhi = "ABC1234";
        String deleteProfileStr = "profile " +
                "nhi=\"" + nhi + "\" "
                + "> delete";
        odms.cli.commands.Profile.deleteProfileBySearch(deleteProfileStr);

        assertEquals(profileDb.searchNHI("ABC1234").size(), 0);
    }

    @Test
    public void testDeleteUserCommand() throws SQLException {
        String name = "Bobby";
        String region = "John";
        String workAddress = "Christchurch";

        String createClinicianStr = "create-clinician " +
                "name=\"" + name + "\" " +
                "region=\"" + region + "\" " +
                "workAddress=\"" + workAddress + "\" ";

        User user = odms.cli.commands.User.createClinician(createClinicianStr);

        int staffID = user.getStaffID();
        String deleteProfileStr = "clinician " +
                "staffID=\"" + staffID + "\" "
                + "> delete";
        odms.cli.commands.User.deleteUserBySearch(deleteProfileStr, "clinician");
        UserDAO database = DAOFactory.getUserDao();
        assertEquals(0, database.search(staffID).size());
    }

    @Test
    public void testUpdateProfileCommand() throws SQLException {
        String givenNames = "Boaty McBoatface";
        String nhi = "ABC1234";
        String updateProfileStr = "profile " +
                "nhi=\"" + nhi + "\" "
                + "> "
                + "given-names=\"" + givenNames + "\"";

        odms.cli.commands.Profile.updateProfilesBySearch(updateProfileStr);
        ProfileDAO database = DAOFactory.getProfileDao();

        Profile updatedProfile = database.search(nhi, 0, 0, null,
                null, null, null).get(0);
        assertEquals(updatedProfile.getGivenNames(), givenNames);
    }

    @Test
    public void testUpdateClinicianCommand() throws SQLException {
        String name = "Bobby";
        String region = "Canterbury";
        String workAddress = "Christchurch";

        String createClinicianStr = "create-clinician " +
                "name=\"" + name + "\" " +
                "region=\"" + region + "\" " +
                "workAddress=\"" + workAddress + "\" ";

        User user = odms.cli.commands.User.createClinician(createClinicianStr);
        int staffID = user.getStaffID();

        String newName = "Billy";
        String updateClinicianStr = "clinician " +
                "staffID=\"" + staffID + "\" "
                + "> "
                + "name=\"" + newName + "\"";

        odms.cli.commands.User.updateUserBySearch(updateClinicianStr, "clinician");
        UserDAO database = DAOFactory.getUserDao();

        User updatedUser = database.search(staffID).get(0);
        assertEquals(updatedUser.getName(), newName);
    }

    @Test
    public void testProfileDateCreatedCommand() throws SQLException {
        String nhi = "ABC1234";
        String viewProfileDateStr = "profile " +
                "nhi=\"" + nhi + "\" "
                + "> date-created";

        ProfileDAO database = DAOFactory.getProfileDao();

        Profile profile = database.search(nhi, 0, 0, null, null,
                null, null).get(0);
        odms.cli.commands.Profile.viewDateTimeCreatedBySearch(viewProfileDateStr);

        assertTrue(
                result.toString().trim().split("\\r?\\n")[3]
                        .contains(profile.getTimeOfCreation().toString())
        );

    }

    @Test
    public void testClinicianDateCreatedCommand() {
        String name = "Bobby";
        String region = "Canterbury";
        String workAddress = "Christchurch";

        String createClinicianStr = "create-clinician " +
                "name=\"" + name + "\" " +
                "region=\"" + region + "\" " +
                "workAddress=\"" + workAddress + "\" ";

        int staffID = odms.cli.commands.User.createClinician(createClinicianStr)
                .getStaffID();

        String viewClincianDateStr = "clinician " +
                "staffID=\"" + staffID + "\" "
                + "> date-created";

        User user = userDb.searchStaffID(staffID).get(0);
        odms.cli.commands.User
                .viewDateTimeCreatedBySearch(viewClincianDateStr, "clinician");

        assertTrue(
                result.toString().trim().split("\\r?\\n")[4]
                        .contains(user.getTimeOfCreation().toString())
        );

    }
}
