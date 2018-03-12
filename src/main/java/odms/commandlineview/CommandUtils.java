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

    public static Commands ValidateCommandType(String cmd)
    {
        String cmdRegexCreate = "([a-z]+)([-]([a-z]+))?((\\s)([a-z]+)(([-]([a-z]+))?)([=][\"]"
                                + "(([a-zA-Z0-9][-]?(\\s)?)+)[\"]))*";

        String cmdRegexDonorView = "([a-z]+)((\\s)([a-z]+)(([-]([a-z]+))?)([=][\"]"
                                   + "(([a-zA-Z0-9][-]?(\\s)?)+)[\"]))+(\\s[>]\\s([a-z]+)([-]([a-z]+))?)";

        String cmdRegexDonorUpdate = "([a-z]+)([-]([a-z]+))?((\\s)([a-z]+)(([-]([a-z]+))?)([=][\"]"
                                     + "(([a-zA-Z0-9][-]?(\\s)?)+)[\"]))*(\\s[>])((\\s([a-z]+)([-]"
                                     + "([a-z]+))?)([=][\"](([a-zA-Z0-9][-]?(\\s)?)+)[\"]))*";

        String cmdRegexOrganUpdate = "([a-z]+)([-]([a-z]+))?((\\s)([a-z]+)(([-]([a-z]+))?)([=][\"]"
                                     + "(([a-zA-Z0-9][-]?(\\s)?)+)[\"]))*(\\s[>](\\s([a-z]+)([-]"
                                     + "([a-z]+))?)([=][\"](([a-zA-Z]([-])?([,](\\s)?)*)+)[\"]))*";

        if (cmd.equals("print all")) {
            // Print all profiles.
            return Commands.PRINTALL;
        } else if (cmd.equals("print donors")) {
            // Print all profiles that are donors.
            return Commands.PRINTDONORS;
        } else if (cmd.equals("help")) {
            // Show available commands.
            return Commands.HELP;
        } else if (cmd.contains("import")) {
            // Import a file of donors.
            return Commands.IMPORT;
        } else if (cmd.contains("export")) {
            // Export the Donor Database to files
            return Commands.EXPORT;
        } else if (cmd.matches(cmdRegexCreate)) {

            try {
                if (cmd.substring(0, 14).equals("create-profile")) {
                    // Create a new profile.
                    return Commands.PROFILECREATE;
                } else {
                    return Commands.INVALID;
                }
            } catch (Exception e) {
                return Commands.INVALID;
            }
        } else if (cmd.matches(cmdRegexDonorView)) {

            try {
                if (cmd.substring(0, 5).equals("donor")) {

                    switch (cmd.substring(cmd.indexOf('>') + 1).trim()) {
                        case "view":
                            // view profiles
                            return Commands.PROFILEVIEW;
                        case "date-created":
                            return Commands.DONORDATECREATED;
                        case "donations":
                            return Commands.DONORDONATIONS;
                        case "delete":
                            return Commands.PROFILEDELETE;
                        default:
                            return Commands.INVALID;
                    }
                } else {
                    return Commands.INVALID;
                }
            } catch (Exception e) {
                return Commands.INVALID;
            }
        } else if (cmd.matches(cmdRegexOrganUpdate) && (cmd.contains("organ") || cmd.contains("donate"))) {

            try {
                switch (cmd.substring(cmd.indexOf('>') + 1, cmd.lastIndexOf('=')).trim()) {
                    case "add-organ":
                        return Commands.ORGANADD;
                    case "remove-organ":
                        return Commands.ORGANREMOVE;
                    case "donate":
                        return Commands.ORGANDONATE;
                    default:
                        return Commands.INVALID;
                }
            } catch (Exception e) {
                return Commands.INVALID;
            }
            
        } else if (cmd.matches(cmdRegexDonorUpdate)) {

            try {
                if (cmd.substring(0, 5).equals("donor")) {
                    //set attributes of a profile.
                    return Commands.DONORUPDATE;
                } else {
                    return Commands.INVALID;
                }
            } catch (Exception e) {
                return Commands.INVALID;
            }
            
        } else {
            return Commands.INVALID;
        }
    }

    public static void ViewAttrBySearchCriteria(DonorDatabase currentDatabase, String expression) {
        if (expression.substring(6, 17).equals("given-names")) {
            if (expression.lastIndexOf("=") == expression.indexOf("=")) {

                String attr = expression.substring(expression.indexOf("\"") + 1, expression.lastIndexOf("\""));
                ArrayList<Donor> donorList = currentDatabase.searchGivenNames(attr);
                printSearchResults(donorList);
            }
            else {
                System.out.println(searchErrorText);
            }
        }
        else if (expression.substring(6, 16).equals("last-names"))
        {
            if (expression.lastIndexOf("=") == expression.indexOf("=")) {

                String attr = expression.substring(expression.indexOf("\"") + 1, expression.lastIndexOf("\""));
                ArrayList<Donor> donorList = currentDatabase.searchLastNames(attr);
                printSearchResults(donorList);
            }
            else {
                System.out.println(searchErrorText);
            }
        }
        else if (expression.substring(6, 9).equals("ird"))
        {
            if (expression.lastIndexOf("=") == expression.indexOf("=")) {

                String attr = expression.substring(expression.indexOf("\"") + 1, expression.lastIndexOf("\""));
                ArrayList<Donor> donorList = currentDatabase.searchIRDNumber(Integer.valueOf(attr));
                printSearchResults(donorList);
            }
            else {
                System.out.println(searchErrorText);
            }
        }
        else {
            System.out.println(searchErrorText);
        }
    }

    private static void printSearchResults(ArrayList<Donor> results) {
        if (results.size() > 0) {
            for (Donor donor : results) {
                donor.viewAttributes();
                System.out.println();
            }
        } else {
            System.out.println(searchNotFoundText);
        }
    }

    public static void ViewDateTimeCreatedBySearchCriteria(DonorDatabase currentDatabase, String expression) {
        if (expression.substring(6, 17).equals("given-names")) {
            if (expression.lastIndexOf("=") == expression.indexOf("=")) {

                String attr = expression
                    .substring(expression.indexOf("\"") + 1, expression.lastIndexOf("\""));
                ArrayList<Donor> donorList = currentDatabase.searchGivenNames(attr);

                if (donorList.size() > 0) {
                    printDonorList(donorList);
                } else {
                    System.out.println(searchNotFoundText);
                }
            } else {
                System.out.println(searchErrorText);
            }
        } else if (expression.substring(6, 16).equals("last-names")) {
            if (expression.lastIndexOf("=") == expression.indexOf("=")) {

                String attr = expression
                    .substring(expression.indexOf("\"") + 1, expression.lastIndexOf("\""));
                ArrayList<Donor> donorList = currentDatabase.searchLastNames(attr);

                if (donorList.size() > 0) {
                    printDonorList(donorList);
                } else {
                    System.out.println(searchNotFoundText);
                }
            } else {
                System.out.println(searchErrorText);
            }
        } else if (expression.substring(6, 9).equals("ird")) {
            if (expression.lastIndexOf("=") == expression.indexOf("=")) {

                String attr = expression
                    .substring(expression.indexOf("\"") + 1, expression.lastIndexOf("\""));
                ArrayList<Donor> donorList = currentDatabase.searchIRDNumber(Integer.valueOf(attr));

                if (donorList.size() > 0) {
                    printDonorList(donorList);
                } else {
                    System.out.println(searchNotFoundText);
                }
            } else {
                System.out.println(searchErrorText);
            }
        } else {
            System.out.println(searchErrorText);
        }
    }
    
    public static void ViewDonationsBySearchCriteria(DonorDatabase currentDatabase, String expression) {
        if (expression.substring(6, 17).equals("given-names")) {
            if (expression.lastIndexOf("=") == expression.indexOf("=")) {

                String attr = expression
                    .substring(expression.indexOf("\"") + 1, expression.lastIndexOf("\""));
                ArrayList<Donor> donorList = currentDatabase.searchGivenNames(attr);

                if (donorList.size() > 0) {
                    printDonorDonations(donorList);
                } else {
                    System.out.println(searchNotFoundText);
                }
            } else {
                System.out.println(searchErrorText);
            }
        } else if (expression.substring(6, 16).equals("last-names")) {
            if (expression.lastIndexOf("=") == expression.indexOf("=")) {

                String attr = expression
                    .substring(expression.indexOf("\"") + 1, expression.lastIndexOf("\""));
                ArrayList<Donor> donorList = currentDatabase.searchLastNames(attr);

                if (donorList.size() > 0) {
                    printDonorDonations(donorList);
                } else {
                    System.out.println(searchNotFoundText);
                }
            } else {
                System.out.println(searchErrorText);
            }
        } else if (expression.substring(6, 9).equals("ird")) {
            if (expression.lastIndexOf("=") == expression.indexOf("=")) {

                String attr = expression
                    .substring(expression.indexOf("\"") + 1, expression.lastIndexOf("\""));
                ArrayList<Donor> donorList = currentDatabase.searchIRDNumber(Integer.valueOf(attr));

                if (donorList.size() > 0) {
                    printDonorDonations(donorList);
                } else {
                    System.out.println(searchNotFoundText);
                }
            } else {
                System.out.println(searchErrorText);
            }
        } else {
            System.out.println(searchErrorText);
        }
    }

    public static void UpdateDonorsBySearchCriteria(DonorDatabase currentDatabase, String expression) {
        String[] attrList = expression.substring(expression.indexOf('>') + 1).trim().split("\"\\s");

        if (expression.substring(6, 17).equals("given-names")) {
            if (expression.substring(0, expression.lastIndexOf('>')).lastIndexOf("=") == 
                expression.substring(0, expression.lastIndexOf('>')).indexOf("=")) {

                String attr = expression
                    .substring(expression.indexOf("\"") + 1, expression.indexOf(">") - 2);
                ArrayList<Donor> donorList = currentDatabase.searchGivenNames(attr);

                if (donorList.size() > 0) {
                    updateDonorAttr(donorList, attrList);
                } else {
                    System.out.println(searchNotFoundText);
                }
            } else {
                System.out.println(searchErrorText);
            }
        } else if (expression.substring(6, 16).equals("last-names")) {
            if (expression.substring(0, expression.lastIndexOf('>')).lastIndexOf("=") == 
                expression.substring(0, expression.lastIndexOf('>')).indexOf("=")) {

                String attr = expression
                    .substring(expression.indexOf("\"") + 1, expression.indexOf(">") - 2);
                ArrayList<Donor> donorList = currentDatabase.searchLastNames(attr);

                if (donorList.size() > 0) {
                    updateDonorAttr(donorList, attrList);
                } else {
                    System.out.println(searchNotFoundText);
                }
            } else {
                System.out.println(searchErrorText);
            }
        } else if (expression.substring(6, 9).equals("ird")) {
            if (expression.substring(0, expression.lastIndexOf('>')).lastIndexOf("=") == 
                expression.substring(0, expression.lastIndexOf('>')).indexOf("=")) {

                String attr = expression
                    .substring(expression.indexOf("\"") + 1, expression.indexOf(">") - 2);
                ArrayList<Donor> donorList = currentDatabase.searchIRDNumber(Integer.valueOf(attr));

                if (donorList.size() > 0) {
                    updateDonorAttr(donorList, attrList);
                } else {
                    System.out.println(searchNotFoundText);
                }
            } else {
                System.out.println(searchErrorText);
            }
        } else {
            System.out.println(searchErrorText);
        }
    }

    public static void AddOrgansBySearchCriteria(DonorDatabase currentDatabase, String expression) {
        String[] organList = expression.substring(
            expression.lastIndexOf("=") + 1).replace("\"", "").split(",");

        if (expression.substring(6, 17).equals("given-names")) {
            if (expression.substring(0, expression.lastIndexOf('>')).lastIndexOf("=") == 
                expression.substring(0, expression.lastIndexOf('>')).indexOf("=")) {

                String attr = expression
                    .substring(expression.indexOf("\"") + 1, expression.indexOf(">") - 2);
                ArrayList<Donor> donorList = currentDatabase.searchGivenNames(attr);

                if (donorList.size() > 0) {
                    addOrgans(donorList, organList);
                } else {
                    System.out.println(searchNotFoundText);
                }
            } else {
                System.out.println(searchErrorText);
            }
        } else if (expression.substring(6, 16).equals("last-names")) {
            if (expression.substring(0, expression.lastIndexOf('>')).lastIndexOf("=") == expression
                .substring(0, expression.lastIndexOf('>')).indexOf("=")) {

                String attr = expression
                    .substring(expression.indexOf("\"") + 1, expression.indexOf(">") - 2);
                ArrayList<Donor> donorList = currentDatabase.searchLastNames(attr);

                if (donorList.size() > 0) {
                    addOrgans(donorList, organList);
                } else {
                    System.out.println(searchNotFoundText);
                }
            } else {
                System.out.println(searchErrorText);
            }
        } else if (expression.substring(6, 9).equals("ird")) {
            test(currentDatabase, expression, organList);
        } else {
            System.out.println(searchErrorText);
        }
    }

    public static void removeOrgansBySearchCriteria(DonorDatabase currentDatabase, String expression) {
        String[] organList = expression.substring(expression.lastIndexOf("=") + 1).replace("\"", "")
            .split(",");

        if (expression.substring(6, 17).equals("given-names")) {
            if (expression.substring(0, expression.lastIndexOf('>')).lastIndexOf("=") == expression
                .substring(0, expression.lastIndexOf('>')).indexOf("=")) {

                String attr = expression
                    .substring(expression.indexOf("\"") + 1, expression.indexOf(">") - 2);
                ArrayList<Donor> donorList = currentDatabase.searchGivenNames(attr);

                if (donorList.size() > 0) {
                    removeOrgans(donorList, organList);
                } else {
                    System.out.println(searchNotFoundText);
                }
            } else {
                System.out.println(searchErrorText);
            }
        } else if (expression.substring(6, 16).equals("last-names")) {
            if (expression.substring(0, expression.lastIndexOf('>')).lastIndexOf("=") == expression
                .substring(0, expression.lastIndexOf('>')).indexOf("=")) {

                String attr = expression
                    .substring(expression.indexOf("\"") + 1, expression.indexOf(">") - 2);
                ArrayList<Donor> donorList = currentDatabase.searchLastNames(attr);

                if (donorList.size() > 0) {
                    removeOrgans(donorList, organList);
                } else {
                    System.out.println(searchNotFoundText);
                }
            } else {
                System.out.println(searchErrorText);
            }
        } else if (expression.substring(6, 9).equals("ird")) {
            if (expression.substring(0, expression.lastIndexOf('>')).lastIndexOf("=") == expression
                .substring(0, expression.lastIndexOf('>')).indexOf("=")) {

                String attr = expression
                    .substring(expression.indexOf("\"") + 1, expression.indexOf(">") - 2);
                ArrayList<Donor> donorList = currentDatabase.searchIRDNumber(Integer.valueOf(attr));

                if (donorList.size() > 0) {
                    removeOrgans(donorList, organList);
                } else {
                    System.out.println(searchNotFoundText);
                }
            } else {
                System.out.println(searchErrorText);
            }
        } else {
            System.out.println(searchErrorText);
        }
    }
    
    public static void addDonationsMadeBySearchCriteria(DonorDatabase currentDatabase, String expression) {
        String[] organList = expression.substring(expression.lastIndexOf("=") + 1)
            .replace("\"", "").split(",");

        if (expression.substring(6, 17).equals("given-names")) {
            if (expression.substring(0, expression.lastIndexOf('>')).lastIndexOf("=") == 
                expression.substring(0, expression.lastIndexOf('>')).indexOf("=")) {

                String attr = expression
                    .substring(expression.indexOf("\"") + 1, expression.indexOf(">") - 2);
                ArrayList<Donor> donorList = currentDatabase.searchGivenNames(attr);

                if (donorList.size() > 0) {
                    addDonation(donorList, organList);
                } else {
                    System.out.println(searchNotFoundText);
                }
            } else {
                System.out.println(searchErrorText);
            }
        } else if (expression.substring(6, 16).equals("last-names")) {
            if (expression.substring(0, expression.lastIndexOf('>')).lastIndexOf("=") == 
                expression.substring(0, expression.lastIndexOf('>')).indexOf("=")) {

                String attr = expression
                    .substring(expression.indexOf("\"") + 1, expression.indexOf(">") - 2);
                ArrayList<Donor> donorList = currentDatabase.searchLastNames(attr);

                if (donorList.size() > 0) {
                    addDonation(donorList, organList);
                } else {
                    System.out.println(searchNotFoundText);
                }
            } else {
                System.out.println(searchErrorText);
            }
        } else if (expression.substring(6, 9).equals("ird")) {
            test(currentDatabase, expression, organList);
        } else {
            System.out.println(searchErrorText);
        }
    }

    private static void test(DonorDatabase currentDatabase, String expression, String[] organList) {
        if (expression.substring(0, expression.lastIndexOf('>')).lastIndexOf("=") == 
            expression.substring(0, expression.lastIndexOf('>')).indexOf("=")) {

            String attr = expression.substring(expression.indexOf("\"") + 1, expression.indexOf(">") - 2);
            ArrayList<Donor> donorList = currentDatabase.searchIRDNumber(Integer.valueOf(attr));

            if (donorList.size() > 0) {
                addOrgans(donorList, organList);
            } else {
                System.out.println(searchNotFoundText);
            }
        } else {
            System.out.println(searchErrorText);
        }

    }

    public static void deleteDonorBySearchCriteria(DonorDatabase currentDatabase, String expression) {
        if (expression.substring(6, 17).equals("given-names")) {
            if (expression.lastIndexOf("=") == expression.indexOf("=")) {

                String attr = expression
                    .substring(expression.indexOf("\"") + 1, expression.lastIndexOf("\""));
                ArrayList<Donor> donorList = currentDatabase.searchGivenNames(attr);

                if (donorList.size() > 0) {
                    deleteDonors(donorList, currentDatabase);
                } else {
                    System.out.println(searchNotFoundText);
                }
            } else {
                System.out.println(searchErrorText);
            }
        } else if (expression.substring(6, 16).equals("last-names")) {
            if (expression.lastIndexOf("=") == expression.indexOf("=")) {

                String attr = expression
                    .substring(expression.indexOf("\"") + 1, expression.lastIndexOf("\""));
                ArrayList<Donor> donorList = currentDatabase.searchLastNames(attr);

                if (donorList.size() > 0) {
                    deleteDonors(donorList, currentDatabase);
                } else {
                    System.out.println(searchNotFoundText);
                }
            } else {
                System.out.println(searchErrorText);
            }
        } else if (expression.substring(6, 9).equals("ird")) {
            if (expression.lastIndexOf("=") == expression.indexOf("=")) {

                String attr = expression
                    .substring(expression.indexOf("\"") + 1, expression.lastIndexOf("\""));
                ArrayList<Donor> donorList = currentDatabase.searchIRDNumber(Integer.valueOf(attr));

                if (donorList.size() > 0) {
                    deleteDonors(donorList, currentDatabase);
                } else {
                    System.out.println(searchNotFoundText);
                }
            } else {
                System.out.println(searchErrorText);
            }
        } else {
            System.out.println(searchErrorText);
        }
    }


    private static void printDonorList(ArrayList<Donor> donorList) {
        for (Donor donor : donorList) {
            System.out.println("IRD: " + donor.getIrdNumber());
            System.out.println("Given Names: " + donor.getGivenNames());
            System.out.println("Last Names: " + donor.getLastNames());
            System.out.println("Date/Time Created: " + donor.getTimeOfCreation());
            System.out.println();
        }
    }


    private static void printDonorDonations(ArrayList<Donor> donorList) {
        for (Donor donor : donorList) {
            System.out.println("IRD: " + donor.getIrdNumber());
            System.out.println("Given Names: " + donor.getGivenNames());
            System.out.println("Last Names: " + donor.getLastNames());
            donor.viewDonations();
            System.out.println();
        }
    }


    private static void updateDonorAttr(ArrayList<Donor> donorList, String[] attrList) {
        ArrayList<String> attrArray = new ArrayList(Arrays.asList(attrList));
        
        for (Donor donor : donorList) {
            donor.setExtraAttributes(attrArray);
        }
    }


    private static void deleteDonors(ArrayList<Donor> donorList, DonorDatabase currentDatabase) {
        for (Donor donor : donorList) {
            currentDatabase.deleteDonor(donor.getId());
        }
    }


    private static void addOrgans(ArrayList<Donor> donorList, String[] organList) {
        Set<String> organSet = new HashSet(Arrays.asList(organList));
        
        for (Donor donor : donorList) {
            try {
                donor.addOrgans(organSet);
            } catch (IllegalArgumentException e) {
                System.out.println("This organ already exists.");
            }
        }
    }

    private static void addDonation(ArrayList<Donor> donorList, String[] organList) {
        Set<String> organSet = new HashSet(Arrays.asList(organList));
        
        for (Donor donor : donorList) {
            try {
                donor.addDonations(organSet);
            } catch (IllegalArgumentException e) {
                System.out.println("This organ already exists.");
            }
        }
    }

    private static void removeOrgans(ArrayList<Donor> donorList, String[] organList) {
        Set<String> organSet = new HashSet(Arrays.asList(organList));
        
        for (Donor donor : donorList) {
            try {
                donor.removeOrgans(organSet);
            } catch (IllegalArgumentException e) {
                System.out.println("This organ doesn't exists.");
            }
        }
    }

    public static void help() {
        System.out.println("\nCreate a new donor:");
        System.out.println("create-profile {attributes (given-names, last-names, dob and ird is required)}");
        System.out.println("e.g. create-profile given-names=\"Abby Rose\" last-names=\"Walker\" dob=\"03-03-1998\" ird=\"123456789\"");

        System.out.println("\nView a donor:");
        System.out.println("donor {attributes to search donors by} > view");

        System.out.println("\nView the date a donor was created:");
        System.out.println("donor {attributes to search donors by} > date-created");

        System.out.println("\nView a donors past donations:");
        System.out.println("donor {attributes to search donors by} > donations");

        System.out.println("\nUpdate a donors attributes:");
        System.out.println("donor {attributes to search donors by} > {attributes to update}");
        System.out.println("e.g. donor given-names=\"Abby Rose\" last-names=\"Walker\" dob=\"03-03-1998\" > height=\"169\"");

        System.out.println("\nAdd an organ to donate:");
        System.out.println("donor {attributes to search donors by} > add-organ=\" {list of organs to donate} \"");
        System.out.println("e.g. donor given-names=\"Abby Rose\" last-names=\"Walker\" dob=\"03-03-1998\" > add-organ=\"liver, kidney\"");

        System.out.println("\nRemove an organ to donate:");
        System.out.println("donor {attributes to search donors by} > remove-organ=\" {list of organs to remove} \"");
        System.out.println("e.g. donor given-names=\"Abby Rose\" last-names=\"Walker\" dob=\"03-03-1998\" > remove-organ=\"liver, kidney\"");

        System.out.print("\nPrint all profiles: ");
        System.out.println("print all");

        System.out.print("\nPrint all donors: ");
        System.out.println("print donors");

        System.out.print("\nClose the app: ");
        System.out.println("quit");

        System.out.println("\nAttributes:");
        System.out.println("given-names, last-names, dob, dod, gender, height, weight, blood-type, address, region, ird");

        System.out.println("\nOrgans:");
        System.out.println("Liver, Kidney, Pancreas, Heart, Lung, Intestine, Cornea, Middle Ear, Skin, Bone, Bone Marrow, Connective Tissue");
    }
}
