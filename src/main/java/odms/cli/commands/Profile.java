package odms.cli.commands;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import odms.cli.CommandUtils;
import odms.data.IrdNumberConflictException;
import odms.data.ProfileDatabase;

public class Profile extends CommandUtils {

    /**
     * Add history for profile
     *
     * @param Id profile ID
     */
    protected static void addProfileHistory(int Id) {
        if (currentSessionHistory.size() != 0) {
            if (historyPosition != currentSessionHistory.size() - 1) {
                currentSessionHistory.subList(historyPosition, currentSessionHistory.size() - 1)
                    .clear();
            }
        }
        currentSessionHistory.add("Profile " + Id + " added at " + LocalDateTime.now());
        historyPosition = currentSessionHistory.size() - 1;
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
            odms.profile.Profile newProfile = new odms.profile.Profile(attrArray);
            currentDatabase.addProfile(newProfile);
            addProfileHistory(newProfile.getId());
            System.out.println("Profile created.");

        } catch (IllegalArgumentException e) {
            System.out.println("Please enter the required attributes correctly.");

        } catch (IrdNumberConflictException e) {
            Integer errorIrdNumber = e.getIrdNumber();
            odms.profile.Profile errorProfile = currentDatabase.searchIRDNumber(errorIrdNumber).get(0);

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
                ArrayList<odms.profile.Profile> profileList = currentDatabase.searchGivenNames(attr);

                deleteProfiles(profileList, currentDatabase);
            } else if (expression.substring(8, 8 + "last-names".length()).equals("last-names")) {
                ArrayList<odms.profile.Profile> profileList = currentDatabase.searchLastNames(attr);

                deleteProfiles(profileList, currentDatabase);
            } else if (expression.substring(8, 8 + "ird".length()).equals("ird")) {
                ArrayList<odms.profile.Profile> profileList = currentDatabase
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
    private static void deleteProfiles(ArrayList<odms.profile.Profile> profileList,
        ProfileDatabase currentDatabase) {
        boolean result;
        if (profileList.size() > 0) {
            for (odms.profile.Profile profile : profileList) {
                result = currentDatabase.deleteProfile(profile.getId());
                if (result) {
                    deletedProfiles.add(profile);
                    if (currentSessionHistory.size() != 0) {
                        if (historyPosition != currentSessionHistory.size() - 1) {
                            currentSessionHistory
                                .subList(historyPosition, currentSessionHistory.size() - 1).clear();
                        }
                    }
                    currentSessionHistory
                        .add("Profile " + profile.getId() + " deleted at " + LocalDateTime.now());
                    historyPosition = currentSessionHistory.size() - 1;
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
    private static void updateProfileAttr(ArrayList<odms.profile.Profile> profileList, String[] attrList) {
        if (profileList.size() > 0) {
            ArrayList<String> attrArray = new ArrayList<>(Arrays.asList(attrList));
            String action;
            for (odms.profile.Profile profile : profileList) {
                action = "Profile " + profile.getId() + " updated details previous = " + profile
                    .getAttributesSummary() + " new = ";
                profile.setExtraAttributes(attrArray);
                action = action + profile.getAttributesSummary() + " at " + LocalDateTime.now();
                if (currentSessionHistory.size() != 0) {
                    if (historyPosition != currentSessionHistory.size() - 1) {
                        currentSessionHistory
                            .subList(historyPosition, currentSessionHistory.size() - 1).clear();
                    }
                }
                currentSessionHistory.add(action);
                historyPosition = currentSessionHistory.size() - 1;

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
                ArrayList<odms.profile.Profile> profileList = currentDatabase.searchGivenNames(attr);

                updateProfileAttr(profileList, attrList);
            } else if (expression.substring(8, 8 + "last-names".length()).equals("last-names")) {
                ArrayList<odms.profile.Profile> profileList = currentDatabase.searchLastNames(attr);

                updateProfileAttr(profileList, attrList);
            } else if (expression.substring(8, 8 + "ird".length()).equals("ird")) {
                ArrayList<odms.profile.Profile> profileList = currentDatabase
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
                Print.printSearchResults(currentDatabase.searchGivenNames(attr));
            } else if (expression.substring(8, 8 + "last-names".length()).equals("last-names")) {
                Print.printSearchResults(currentDatabase.searchLastNames(attr));
            } else if (expression.substring(8, 8 + "ird".length()).equals("ird")) {
                Print.printSearchResults(currentDatabase.searchIRDNumber(Integer.valueOf(attr)));
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
                ArrayList<odms.profile.Profile> profileList = currentDatabase.searchGivenNames(attr);

                Print.printProfileList(profileList);
            } else {
                System.out.println(searchErrorText);
            }
        } else if (expression.substring(8, 8 + "last-names".length()).equals("last-names")) {
            if (expression.lastIndexOf("=") == expression.indexOf("=")) {
                ArrayList<odms.profile.Profile> profileList = currentDatabase.searchLastNames(attr);

                Print.printProfileList(profileList);
            } else {
                System.out.println(searchErrorText);
            }
        } else if (expression.substring(8, 8 + "ird".length()).equals("ird")) {
            if (expression.lastIndexOf("=") == expression.indexOf("=")) {
                ArrayList<odms.profile.Profile> profileList = currentDatabase
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
        ArrayList<odms.profile.Profile> profileList = null;

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
