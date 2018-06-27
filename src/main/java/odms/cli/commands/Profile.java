package odms.cli.commands;

import odms.cli.CommandUtils;
import odms.controller.History.HistoryController;
import odms.controller.data.IrdNumberConflictException;
import odms.Model.Data.ProfileDatabase;
import odms.Model.history.History;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

public class Profile extends CommandUtils {

    /**
     * Add history for profile
     *
     * @param Id profile ID
     */
    protected static void addProfileHistory(int Id) {
        History action = new History("Profile",Id,"added","",-1,LocalDateTime.now());
        HistoryController.updateHistory(action);
    }

    /**
     * Create profile.
     *
     * @param currentDatabase Database reference
     * @param rawInput raw command input
     */
    public static void createProfile(ProfileDatabase currentDatabase, String rawInput) {
        try {
            String[] attrList = rawInput.substring(15).split("\"\\s");
            ArrayList<String> attrArray = new ArrayList<>(Arrays.asList(attrList));
            odms.Model.profile.Profile newProfile = new odms.Model.profile.Profile(attrArray);
            currentDatabase.addProfile(newProfile);
            addProfileHistory(newProfile.getId());
            System.out.println("Profile created.");

        } catch (IllegalArgumentException e) {
            System.out.println("Please enter the required attributes correctly.");

        } catch (IrdNumberConflictException e) {
            Integer errorIrdNumber = e.getIrdNumber();
            odms.Model.profile.Profile errorProfile = currentDatabase.searchIRDNumber(errorIrdNumber).get(0);

            System.out.println("Error: IRD Number " + errorIrdNumber +
                " already in use by profile " +
                errorProfile.getGivenNames() + " " +
                errorProfile.getLastNames());

        } catch (Exception e) {
            System.out.println("Please enter a valid command.");
        }
    }

    /**
     * Delete profiles from the database.
     *
     * @param currentDatabase Database reference
     * @param expression Search expression
     */
    public static void deleteProfileBySearch(ProfileDatabase currentDatabase, String expression) {
        if (expression.lastIndexOf("=") == expression.indexOf("=")) {
            String attr = expression.substring(expression.indexOf("\"") + 1,
                expression.lastIndexOf("\""));

            if (expression.substring(8, 8 + "given-names".length()).equals("given-names")) {
                ArrayList<odms.Model.profile.Profile> profileList = currentDatabase.searchGivenNames(attr);

                deleteProfiles(profileList, currentDatabase);
            } else if (expression.substring(8, 8 + "last-names".length()).equals("last-names")) {
                ArrayList<odms.Model.profile.Profile> profileList = currentDatabase.searchLastNames(attr);

                deleteProfiles(profileList, currentDatabase);
            } else if (expression.substring(8, 8 + "ird".length()).equals("ird")) {
                ArrayList<odms.Model.profile.Profile> profileList = currentDatabase
                    .searchIRDNumber(Integer.valueOf(attr));

                deleteProfiles(profileList, currentDatabase);
            }

        } else {
            System.out.println(searchErrorText);
        }
    }

    /**
     * Delete profiles.
     *
     * @param profileList list of profiles
     * @param currentDatabase Database reference
     */
    private static void deleteProfiles(ArrayList<odms.Model.profile.Profile> profileList,
        ProfileDatabase currentDatabase) {
        boolean result;
        if (profileList.size() > 0) {
            for (odms.Model.profile.Profile profile : profileList) {
                result = currentDatabase.deleteProfile(profile.getId());
                if (result) {
                    HistoryController.deletedProfiles.add(profile);
                    HistoryController.updateHistory(new History("Profile",profile.getId(),
                            "deleted","",-1,LocalDateTime.now()));
                }
            }
        } else {
            System.out.println(searchNotFoundText);
        }
    }

    /**
     * Update profile attributes.
     *
     * @param profileList List of profiles
     * @param attrList Attributes to be updated and their values
     */
    private static void updateProfileAttr(ArrayList<odms.Model.profile.Profile> profileList, String[] attrList) {
        if (profileList.size() > 0) {
            ArrayList<String> attrArray = new ArrayList<>(Arrays.asList(attrList));
            for (odms.Model.profile.Profile profile : profileList) {
                History action = new History("Profile" , profile.getId() ,"update",profile.getAttributesSummary(),-1,null);
                profile.setExtraAttributes(attrArray);
                action.setHistoryData(action.getHistoryData()+profile.getAttributesSummary());
                action.setHistoryTimestamp(LocalDateTime.now());
                HistoryController.updateHistory(action);

            }
        } else {
            System.out.println(searchNotFoundText);
        }
    }

    /**
     * Update profile attributes.
     *
     * @param currentDatabase Database reference
     * @param expression Search expression
     */
    public static void updateProfilesBySearch(ProfileDatabase currentDatabase, String expression) {
        String[] attrList = expression.substring(expression.indexOf('>') + 1)
            .trim()
            .split("\"\\s");

        if (expression.substring(0, expression.lastIndexOf('>')).lastIndexOf("=") ==
            expression.substring(0, expression.lastIndexOf('>')).indexOf("=")) {
            String attr = expression.substring(expression.indexOf("\"") + 1,
                expression.indexOf(">") - 2);

            if (expression.substring(8, 8 + "given-names".length()).equals("given-names")) {
                ArrayList<odms.Model.profile.Profile> profileList = currentDatabase.searchGivenNames(attr);

                updateProfileAttr(profileList, attrList);
            } else if (expression.substring(8, 8 + "last-names".length()).equals("last-names")) {
                ArrayList<odms.Model.profile.Profile> profileList = currentDatabase.searchLastNames(attr);

                updateProfileAttr(profileList, attrList);
            } else if (expression.substring(8, 8 + "ird".length()).equals("ird")) {
                ArrayList<odms.Model.profile.Profile> profileList = currentDatabase
                    .searchIRDNumber(Integer.valueOf(attr));

                updateProfileAttr(profileList, attrList);
            }

        } else {
            System.out.println(searchErrorText);
        }
    }

    /**
     * Displays profiles attributes via the search methods
     *
     * @param currentDatabase Database reference
     * @param expression Search expression being used for searching
     */
    public static void viewAttrBySearch(ProfileDatabase currentDatabase, String expression) {
        if (expression.lastIndexOf("=") == expression.indexOf("=")) {
            String attr = expression.substring(expression.indexOf("\"") + 1,
                expression.lastIndexOf("\""));

            if (expression.substring(8, 8 + "given-names".length()).equals("given-names")) {
                Print.printProfileSearchResults(currentDatabase.searchGivenNames(attr));
            } else if (expression.substring(8, 8 + "last-names".length()).equals("last-names")) {
                Print.printProfileSearchResults(currentDatabase.searchLastNames(attr));
            } else if (expression.substring(8, 8 + "ird".length()).equals("ird")) {
                Print.printProfileSearchResults(currentDatabase.searchIRDNumber(Integer.valueOf(attr)));
            } else {
                System.out.println(searchErrorText);
            }

        } else {
            System.out.println(searchErrorText);
        }
    }

    /**
     * View date and time of profile creation.
     *
     * @param currentDatabase Database reference
     * @param expression Search expression
     */
    public static void viewDateTimeCreatedBySearch(ProfileDatabase currentDatabase,
        String expression) {
        String attr = expression.substring(expression.indexOf("\"") + 1,
            expression.lastIndexOf("\""));

        if (expression.substring(8, 8 + "given-names".length()).equals("given-names")) {
            if (expression.lastIndexOf("=") == expression.indexOf("=")) {
                ArrayList<odms.Model.profile.Profile> profileList = currentDatabase.searchGivenNames(attr);

                Print.printProfileList(profileList);
            } else {
                System.out.println(searchErrorText);
            }
        } else if (expression.substring(8, 8 + "last-names".length()).equals("last-names")) {
            if (expression.lastIndexOf("=") == expression.indexOf("=")) {
                ArrayList<odms.Model.profile.Profile> profileList = currentDatabase.searchLastNames(attr);

                Print.printProfileList(profileList);
            } else {
                System.out.println(searchErrorText);
            }
        } else if (expression.substring(8, 8 + "ird".length()).equals("ird")) {
            if (expression.lastIndexOf("=") == expression.indexOf("=")) {
                ArrayList<odms.Model.profile.Profile> profileList = currentDatabase
                    .searchIRDNumber(Integer.valueOf(attr));

                Print.printProfileList(profileList);
            } else {
                System.out.println(searchErrorText);
            }

        } else {
            System.out.println(searchErrorText);
        }
    }

    /**
     * View organs available for donation.
     *
     * @param currentDatabase Database reference
     * @param expression Search expression
     */
    public static void viewDonationsBySearch(ProfileDatabase currentDatabase, String expression) {
        String attr = expression.substring(expression.indexOf("\"") + 1,
            expression.lastIndexOf("\""));
        ArrayList<odms.Model.profile.Profile> profileList = null;

        if (expression.substring(8, 8 + "given-names".length()).equals("given-names")) {

            if (expression.lastIndexOf("=") == expression.indexOf("=")) {
                profileList = currentDatabase.searchGivenNames(attr);
            } else {
                System.out.println(searchErrorText);
            }
        } else if (expression.substring(8, 8 + "last-names".length()).equals("last-names")) {
            if (expression.lastIndexOf("=") == expression.indexOf("=")) {
                profileList = currentDatabase.searchLastNames(attr);
            } else {
                System.out.println(searchErrorText);
            }
        } else if (expression.substring(8, 8 + "ird".length()).equals("ird")) {
            if (expression.lastIndexOf("=") == expression.indexOf("=")) {
                profileList = currentDatabase.searchIRDNumber(Integer.valueOf(attr));
            } else {
                System.out.println(searchErrorText);
            }

        } else {
            System.out.println(searchErrorText);
        }

        if (profileList != null && profileList.size() > 0) {
            Print.printProfileDonations(profileList);
        } else {
            System.out.println("No matching profiles found.");
        }

    }

}
