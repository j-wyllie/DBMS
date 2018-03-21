package odms.commandlineview;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import odms.data.DonorDatabase;
import odms.donor.Donor;

public class CommandUtils {
    private static String searchErrorText = "Please enter only one search criteria "
                                            + "(given-names, last-names, ird).";
    private static String searchNotFoundText = "There are no donors that match this criteria.";

    private static final String cmdRegexCreate = "([a-z]+)([-]([a-z]+))?((\\s)([a-z]+)(([-]"
                                                 + "([a-z]+))?)([=][\"](([a-zA-Z0-9][-]?(\\s)?)+)"
                                                 + "[\"]))*";

    private static final String cmdRegexDonorView = "([a-z]+)((\\s)([a-z]+)(([-]([a-z]+))?)([=][\"]"
                                                    + "(([a-zA-Z0-9][-]?(\\s)?)+)[\"]))+(\\s[>]\\s"
                                                    + "([a-z]+)([-]([a-z]+))?)";

    private static final String cmdRegexDonorUpdate = "([a-z]+)([-]([a-z]+))?((\\s)([a-z]+)(([-]"
                                                      + "([a-z]+))?)([=][\"](([a-zA-Z0-9][-]?(\\s)"
                                                      + "?)+)[\"]))*(\\s[>])((\\s([a-z]+)([-]([a-z]"
                                                      + "+))?)([=][\"](([a-zA-Z0-9][-]?(\\s)?)+)"
                                                      + "[\"]))*";

    private static final String cmdRegexOrganUpdate = "([a-z]+)([-]([a-z]+))?((\\s)([a-z]+)(([-]"
                                                      + "([a-z]+))?)([=][\"](([a-zA-Z0-9][-]?(\\s)"
                                                      + "?)+)[\"]))*(\\s[>](\\s([a-z]+)([-]([a-z]+)"
                                                      + ")?)([=][\"](([a-zA-Z]([-])?([,](\\s)?)*)+)"
                                                      + "[\"]))*";

    /**
     * Performs checks over the input to match a valid command
     *
     * If nothing is matched than an invalid command has been used.
     *
     * @param cmd the command being validated
     * @return the enum Commands appropriate value
     */
    public static Commands validateCommandType(ArrayList<String> cmd, String rawInput) {

        switch (cmd.get(0).toLowerCase()) {
            case "print":
                switch (cmd.get(1).toLowerCase()) {
                    case "all":
                        return Commands.PRINTALL;
                    case "donors":
                        return Commands.PRINTDONORS;
                }
                break;
            case "help":
                return Commands.HELP;
            case "import":
                return Commands.IMPORT;
            case "export":
                if (cmd.size() > 1) {
                    return Commands.EXPORT;
                }
                break;
            case "create-profile":
                if (rawInput.matches(cmdRegexCreate)) {
                    return Commands.PROFILECREATE;
                }
            case "donor":
                if (rawInput.matches(cmdRegexDonorView)) {
                    switch (rawInput.substring(rawInput.indexOf('>') + 2)) {
                        case "view":
                            return Commands.PROFILEVIEW;
                        case "date-created":
                            return Commands.DONORDATECREATED;
                        case "donations":
                            return Commands.DONORDONATIONS;
                        case "delete":
                            return Commands.PROFILEDELETE;
                    }
                } else if (rawInput.matches(cmdRegexOrganUpdate)
                        && rawInput.contains("organ")) {
                    switch (rawInput.substring(rawInput.indexOf('>') + 2,
                            rawInput.lastIndexOf('=')).trim()) {
                        case "add-organ":
                            return Commands.ORGANADD;
                        case "remove-organ":
                            return Commands.ORGANREMOVE;
                        case "donate":
                            return Commands.ORGANDONATE;
                    }

                } else if (rawInput.matches(cmdRegexDonorUpdate)
                        && cmd.get(0).equals("donor")) {
                    return Commands.DONORUPDATE;
                }
        }
        return Commands.INVALID;
    }

    /**
     * Displays profiles attributes via the search methods
     *
     * @param currentDatabase Database reference
     * @param expression Search expression being used for searching
     */
    public static void viewAttrBySearch(DonorDatabase currentDatabase, String expression) {
        if (expression.lastIndexOf("=") == expression.indexOf("=")) {
            String attr = expression.substring(expression.indexOf("\"") + 1,
                expression.lastIndexOf("\""));

            if (expression.substring(6, 17).equals("given-names")) {
                printSearchResults(currentDatabase.searchGivenNames(attr));
            } else if (expression.substring(6, 16).equals("last-names")) {
                printSearchResults(currentDatabase.searchLastNames(attr));
            } else if (expression.substring(6, 9).equals("ird")) {
                printSearchResults(currentDatabase.searchIRDNumber(Integer.valueOf(attr)));
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
    public static void viewDateTimeCreatedBySearch(DonorDatabase currentDatabase, String expression) {
        String attr = expression.substring(expression.indexOf("\"") + 1,
            expression.lastIndexOf("\""));

        if (expression.substring(6, 17).equals("given-names")) {
            if (expression.lastIndexOf("=") == expression.indexOf("=")) {
                ArrayList<Donor> donorList = currentDatabase.searchGivenNames(attr);

                printProfileList(donorList);
            } else {
                System.out.println(searchErrorText);
            }
        } else if (expression.substring(6, 16).equals("last-names")) {
            if (expression.lastIndexOf("=") == expression.indexOf("=")) {
                ArrayList<Donor> donorList = currentDatabase.searchLastNames(attr);

                printProfileList(donorList);
            } else {
                System.out.println(searchErrorText);
            }
        } else if (expression.substring(6, 9).equals("ird")) {
            if (expression.lastIndexOf("=") == expression.indexOf("=")) {
                ArrayList<Donor> donorList = currentDatabase.searchIRDNumber(Integer.valueOf(attr));

                printProfileList(donorList);
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
    public static void viewDonationsBySearch(DonorDatabase currentDatabase, String expression) {
        String attr = expression.substring(expression.indexOf("\"") + 1,
            expression.lastIndexOf("\""));
        ArrayList<Donor> donorList = null;

        if (expression.substring(6, 17).equals("given-names")) {

            if (expression.lastIndexOf("=") == expression.indexOf("=")) {
                donorList = currentDatabase.searchGivenNames(attr);
            } else {
                System.out.println(searchErrorText);
            }
        } else if (expression.substring(6, 16).equals("last-names")) {
            if (expression.lastIndexOf("=") == expression.indexOf("=")) {
                donorList = currentDatabase.searchLastNames(attr);
            } else {
                System.out.println(searchErrorText);
            }
        } else if (expression.substring(6, 9).equals("ird")) {
            if (expression.lastIndexOf("=") == expression.indexOf("=")) {
                donorList = currentDatabase.searchIRDNumber(Integer.valueOf(attr));
            } else {
                System.out.println(searchErrorText);
            }

        } else {
            System.out.println(searchErrorText);
        }

        if (donorList != null && donorList.size() > 0) {
            printDonorDonations(donorList);
        } else {
            System.out.println("No matching profiles found.");
        }

    }

    /**
     * Update profile attributes.
     *
     * @param currentDatabase Database reference
     * @param expression Search expression
     */
    public static void updateProfilesBySearch(DonorDatabase currentDatabase, String expression) {
        String[] attrList = expression.substring(expression.indexOf('>') + 1)
            .trim()
            .split("\"\\s");

        if (expression.substring(0, expression.lastIndexOf('>')).lastIndexOf("=") ==
            expression.substring(0, expression.lastIndexOf('>')).indexOf("=")) {
            String attr = expression.substring(expression.indexOf("\"") + 1,
                expression.indexOf(">") - 2);

            if (expression.substring(6, 17).equals("given-names")) {
                ArrayList<Donor> donorList = currentDatabase.searchGivenNames(attr);

                updateProfileAttr(donorList, attrList);
            } else if (expression.substring(6, 16).equals("last-names")) {
                ArrayList<Donor> donorList = currentDatabase.searchLastNames(attr);

                updateProfileAttr(donorList, attrList);
            } else if (expression.substring(6, 9).equals("ird")) {
                ArrayList<Donor> donorList = currentDatabase.searchIRDNumber(Integer.valueOf(attr));

                updateProfileAttr(donorList, attrList);
            }

        } else {
            System.out.println(searchErrorText);
        }
    }

    /**
     * Add organs to a profile.
     *
     * @param currentDatabase Database reference
     * @param expression Search expression
     */
    public static void addOrgansBySearch(DonorDatabase currentDatabase, String expression) {
        String[] organList = expression.substring(
            expression.lastIndexOf("=") + 1).replace("\"", "").split(",");

        if (expression.substring(0, expression.lastIndexOf('>')).lastIndexOf("=") ==
            expression.substring(0, expression.lastIndexOf('>')).indexOf("=")) {
            String attr = expression.substring(expression.indexOf("\"") + 1,
                expression.indexOf(">") - 2);

            if (expression.substring(6, 17).equals("given-names")) {
                ArrayList<Donor> donorList = currentDatabase.searchGivenNames(attr);

                addOrgans(donorList, organList);
            } else if (expression.substring(6, 16).equals("last-names")) {

                ArrayList<Donor> donorList = currentDatabase.searchLastNames(attr);

                addOrgans(donorList, organList);
            } else if (expression.substring(6, 9).equals("ird")) {
                test(currentDatabase, expression, organList);
            }

        } else {
            System.out.println(searchErrorText);
        }
    }

    /**
     * Remove organs available for donation to a donor profile.
     *
     * @param currentDatabase Database reference
     * @param expression Search expression
     */
    public static void removeOrgansBySearch(DonorDatabase currentDatabase, String expression) {
        String[] organList = expression.substring(expression.lastIndexOf("=") + 1).
            replace("\"", "")
            .split(",");

        // TODO should we be able to remove organs using search by names, as this means it will remove
        // TODO for all john smiths etc
        if (expression.substring(0, expression.lastIndexOf('>')).lastIndexOf("=") ==
            expression.substring(0, expression.lastIndexOf('>')).indexOf("=")) {
            String attr = expression.substring(expression.indexOf("\"") + 1,
                expression.indexOf(">") - 2);

            if (expression.substring(6, 17).equals("given-names")) {
                ArrayList<Donor> donorList = currentDatabase.searchGivenNames(attr);

                removeOrgans(donorList, organList);
            } else if (expression.substring(6, 16).equals("last-names")) {
                ArrayList<Donor> donorList = currentDatabase.searchLastNames(attr);

                removeOrgans(donorList, organList);
            } else if (expression.substring(6, 9).equals("ird")) {
                ArrayList<Donor> donorList = currentDatabase.searchIRDNumber(Integer.valueOf(attr));

                removeOrgans(donorList, organList);
            }
        } else {
            System.out.println(searchErrorText);
        }
    }

    /**
     * Add organs available for donation to a donor profile.
     *
     * @param currentDatabase Database reference
     * @param expression Search expression
     */
    public static void addDonationsMadeBySearch(DonorDatabase currentDatabase,
        String expression) {
        String[] organList = expression.substring(expression.lastIndexOf("=") + 1)
            .replace("\"", "").split(",");

        if (expression.substring(0, expression.lastIndexOf('>')).lastIndexOf("=") ==
            expression.substring(0, expression.lastIndexOf('>')).indexOf("=")) {
            String attr = expression.substring(expression.indexOf("\"") + 1,
                expression.indexOf(">") - 2);

            if (expression.substring(6, 17).equals("given-names")) {
                ArrayList<Donor> donorList = currentDatabase.searchGivenNames(attr);

                if (donorList.size() > 0) {
                    addDonation(donorList, organList);
                } else {
                    System.out.println(searchNotFoundText);
                }

            } else if (expression.substring(6, 16).equals("last-names")) {
                ArrayList<Donor> donorList = currentDatabase.searchLastNames(attr);

                if (donorList.size() > 0) {
                    addDonation(donorList, organList);
                } else {
                    System.out.println(searchNotFoundText);
                }

            } else if (expression.substring(6, 9).equals("ird")) {
                test(currentDatabase, expression, organList);
            } else {
                System.out.println(searchErrorText);
            }

        } else {
            System.out.println(searchErrorText);
        }
    }

    // TODO check what main purpose this serves and name appropriately
    private static void test(DonorDatabase currentDatabase, String expression, String[] organList) {
        String attr = expression.substring(expression.indexOf("\"") + 1,
                expression.indexOf(">") - 2);
        ArrayList<Donor> donorList = currentDatabase.searchIRDNumber(Integer.valueOf(attr));

        addOrgans(donorList, organList);
    }

    /**
     * Delete donors from the database.
     * @param currentDatabase Database reference
     * @param expression Search expression
     */
    public static void deleteDonorBySearch(DonorDatabase currentDatabase, String expression) {
        if (expression.lastIndexOf("=") == expression.indexOf("=")) {
            String attr = expression.substring(expression.indexOf("\"") + 1,
                expression.lastIndexOf("\""));

            if (expression.substring(6, 17).equals("given-names")) {
                ArrayList<Donor> donorList = currentDatabase.searchGivenNames(attr);

                deleteProfiles(donorList, currentDatabase);
            } else if (expression.substring(6, 16).equals("last-names")) {
                ArrayList<Donor> donorList = currentDatabase.searchLastNames(attr);

                deleteProfiles(donorList, currentDatabase);
            } else if (expression.substring(6, 9).equals("ird")) {
                ArrayList<Donor> donorList = currentDatabase.searchIRDNumber(Integer.valueOf(attr));

                deleteProfiles(donorList, currentDatabase);
            }

        } else {
            System.out.println(searchErrorText);
        }
    }

    /**
     * Display and print profile details in a list.
     *
     * @param profileList List of profiles
     */
    private static void printProfileList(ArrayList<Donor> profileList) {
        for (Donor donor : profileList) {
            System.out.println("IRD: " + donor.getIrdNumber());
            System.out.println("Given Names: " + donor.getGivenNames());
            System.out.println("Last Names: " + donor.getLastNames());
            System.out.println("Date/Time Created: " + donor.getTimeOfCreation());
            System.out.println();
        }
    }

    /**
     * Display and print donor donations.
     *
     * @param donorList list of donors
     */
    private static void printDonorDonations(ArrayList<Donor> donorList) {
        for (Donor donor : donorList) {
            System.out.println("IRD: " + donor.getIrdNumber());
            System.out.println("Given Names: " + donor.getGivenNames());
            System.out.println("Last Names: " + donor.getLastNames());
            donor.viewDonations();
            System.out.println();
        }
    }

    /**
     * Display and print all search results from Donor array.
     * If array empty, no search results have been found.
     *
     * @param donorList Results from searching
     */
    private static void printSearchResults(ArrayList<Donor> donorList) {
        if (donorList.size() > 0) {
            for (Donor donor : donorList) {
                donor.viewAttributes();
                System.out.println();
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
    private static void updateProfileAttr(ArrayList<Donor> profileList, String[] attrList) {
        if (profileList.size() > 0) {
            ArrayList<String> attrArray = new ArrayList<>(Arrays.asList(attrList));

            for (Donor donor : profileList) {
                donor.setExtraAttributes(attrArray);
            }
        } else {
            System.out.println(searchNotFoundText);
        }
    }

    /**
     * Delete profiles.
     *
     * @param profileList list of profiles
     * @param currentDatabase Database reference
     */
    private static void deleteProfiles(ArrayList<Donor> profileList, DonorDatabase currentDatabase) {
        if (profileList.size() > 0) {
            for (Donor donor : profileList) {
                currentDatabase.deleteDonor(donor.getId());
            }
        } else {
            System.out.println(searchNotFoundText);
        }
    }

    /**
     * Add organ donations.
     *
     * @param donorList list of donors
     * @param organList list of organs to be added
     */
    private static void addOrgans(ArrayList<Donor> donorList, String[] organList) {
        if (donorList.size() > 0) {
            Set<String> organSet = new HashSet<>(Arrays.asList(organList));

            for (Donor donor : donorList) {
                try {
                    donor.addOrgans(organSet);
                } catch (IllegalArgumentException e) {
                    System.out.println("This organ already exists.");
                }
            }
        } else {
            System.out.println(searchNotFoundText);
        }
    }

    /**
     * TODO Is this a duplicate function for a reason, unsure
     *
     * @param donorList
     * @param organList
     */
    private static void addDonation(ArrayList<Donor> donorList, String[] organList) {
        if (donorList.size() > 0) {
            Set<String> organSet = new HashSet<>(Arrays.asList(organList));

            for (Donor donor : donorList) {
                try {
                    donor.addDonations(organSet);
                } catch (IllegalArgumentException e) {
                    System.out.println("This organ already exists.");
                }
            }
        } else {
            System.out.println(searchNotFoundText);
        }
    }

    /**
     * Remove organ from donations list for donors.
     *
     * @param donorList list of donors
     * @param organList list of organs to be removed
     */
    private static void removeOrgans(ArrayList<Donor> donorList, String[] organList) {
        if (donorList.size() > 0) {
            Set<String> organSet = new HashSet<>(Arrays.asList(organList));

            for (Donor donor : donorList) {
                try {
                    donor.removeOrgans(organSet);
                } catch (IllegalArgumentException e) {
                    System.out.println("This organ doesn't exist.");
                }
            }
        } else {
            System.out.println(searchNotFoundText);
        }
    }

    public static void help() {
        System.out.println("\nCreate a new donor:");
        System.out.println("create-profile {attributes (given-names, last-names, dob and ird is "
                           + "required)}");
        System.out.println("e.g. create-profile given-names=\"Abby Rose\" last-names=\"Walker\" "
                           + "dob=\"03-03-1998\" ird=\"123456789\"");

        System.out.println("\nView a donor:");
        System.out.println("donor {attributes to search donors by} > view");

        System.out.println("\nView the date a donor was created:");
        System.out.println("donor {attributes to search donors by} > date-created");

        System.out.println("\nView a donors past donations:");
        System.out.println("donor {attributes to search donors by} > donations");

        System.out.println("\nUpdate a donors attributes:");
        System.out.println("donor {attributes to search donors by} > {attributes to update}");
        System.out.println("e.g. donor given-names=\"Abby Rose\" last-names=\"Walker\" dob=\"03-03"
                           + "-1998\" > height=\"169\"");

        System.out.println("\nAdd an organ to donate:");
        System.out.println("donor {attributes to search donors by} > add-organ=\" {list of organs "
                           + "to donate} \"");
        System.out.println("e.g. donor given-names=\"Abby Rose\" last-names=\"Walker\" dob=\"03-03"
                           + "-1998\" > add-organ=\"liver, kidney\"");

        System.out.println("\nRemove an organ to donate:");
        System.out.println("donor {attributes to search donors by} > remove-organ=\" {list of "
                           + "organs to remove} \"");
        System.out.println("e.g. donor given-names=\"Abby Rose\" last-names=\"Walker\" dob=\"03-"
                           + "03-1998\" > remove-organ=\"liver, kidney\"");

        System.out.print("\nPrint all profiles: ");
        System.out.println("print all");

        System.out.print("\nPrint all donors: ");
        System.out.println("print donors");

        System.out.print("\nClose the app: ");
        System.out.println("quit");

        System.out.println("\nAttributes:");
        System.out.println("given-names, last-names, dob, dod, gender, height, weight, blood-type,"
                           + " address, region, ird");

        System.out.println("\nOrgans:");
        System.out.println("Liver, Kidney, Pancreas, Heart, Lung, Intestine, Cornea, Middle Ear, "
                           + "Skin, Bone, Bone Marrow, Connective Tissue");
    }
}
