package odms.commandlineview;

import java.util.Arrays;
import odms.data.DonorDataIO;
import odms.data.DonorDatabase;
import odms.data.IrdNumberConflictException;
import odms.donor.Donor;
import java.util.ArrayList;
import java.util.Scanner;

public class CommandLine {

    private DonorDatabase currentDatabase;

    public CommandLine (DonorDatabase currentDatabase) {
        this.currentDatabase = currentDatabase;
    }

    public void beginCommandEntry() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Starting Organ Donor Management System...");
        System.out.println("\nPlease enter your commands below:");

        String expression = scanner.nextLine().trim();

        while (!(expression.equals("quit")) && !(expression.equals("exit")))
        {
            Commands cmd = CommandUtils.ValidateCommandType(expression);

            switch (cmd) {

                case PRINTALL:
                    // Print all profiles (print all).
                    ArrayList<Donor> allProfiles = currentDatabase.getDonors(false);
                    if (allProfiles.size() > 0) {
                        for (Donor profile : allProfiles) {
                            profile.viewAttributes();
                            System.out.println();
                        }
                    }
                    else {
                        System.out.println("There are no profiles to show.");
                    }
                    break;

                case PRINTDONORS:
                    // Print all profiles that are donors (print donors).
                    ArrayList<Donor> allDonors = currentDatabase.getDonors(true);
                    if (allDonors.size() > 0) {
                        for (Donor donor : allDonors) {
                            donor.viewAttributes();
                            donor.viewOrgans();
                            System.out.println("");
                        }
                    }
                    else {
                        System.out.println("There are no donor profiles to show.");
                    }
                    break;

                case HELP:
                    // Show available commands (help).
                    CommandUtils.help();
                    break;

                case PROFILECREATE:
                    // Create a new profile.
                    try {
                        String[] attrList = expression.substring(15).split("\"\\s");
                        ArrayList<String> attrArray = new ArrayList<>(Arrays.asList(attrList));
                        Donor newDonor = new Donor(attrArray);
                        currentDatabase.addDonor(newDonor);
                        System.out.println("Profile created.");

                    } catch (IllegalArgumentException e) {
                        System.out.println("Please enter the required attributes correctly.");

                    } catch (IrdNumberConflictException e) {
                        Integer errorIrdNumber = e.getIrdNumber();
                        Donor errorDonor = currentDatabase.searchIRDNumber(errorIrdNumber).get(0);

                        System.out.println("Error: IRD Number " + errorIrdNumber +
                                " already in use by donor " +
                                errorDonor.getGivenNames() + " " +
                                errorDonor.getLastNames());

                    } catch (Exception e) {
                        System.out.println("Please enter a valid command.");
                    }

                    break;

                case PROFILEVIEW:
                    // Search profiles (donor > view).
                    System.out.println("Searching for profiles...");
                    //carry out method call in command.
                    CommandUtils.ViewAttrBySearchCriteria(currentDatabase, expression);
                    break;

                case DONORDATECREATED:
                    // Search profiles (donor > date-created).
                    System.out.println("Searching for profiles...");
                    //carry out method call in command.
                    CommandUtils.ViewDateTimeCreatedBySearchCriteria(currentDatabase, expression);
                    break;

                case DONORDONATIONS:
                    // Search profiles (donor > donations).
                    System.out.println("Searching for profiles...");
                    //carry out method call in command.
                    CommandUtils.ViewDonationsBySearchCriteria(currentDatabase, expression);
                    break;

                case DONORUPDATE:
                    // Search profiles.
                    // Carry out method call in command.
                    CommandUtils.UpdateDonorsBySearchCriteria(currentDatabase, expression);
                    System.out.println("Profile(s) successfully updated.");
                    break;

                case ORGANADD:
                    // Add organs to a donors profile.
                    // Carry out method call in command.
                    CommandUtils.AddOrgansBySearchCriteria(currentDatabase, expression);
                    System.out.println("Organ successfully added to profile(s).");
                    break;

                case ORGANREMOVE:
                    // Remove organs from a donors profile.
                    // Carry out method call in command.
                    CommandUtils.removeOrgansBySearchCriteria(currentDatabase, expression);
                    System.out.println("Organ successfully removed from profile(s).");
                    break;

                case INVALID:
                    System.out.println("Please enter a valid command.");
                    break;

                case IMPORT :
                    // Import a file of profiles.
                    try {
                        String filepath = expression.substring(7).trim();
                        currentDatabase = DonorDataIO.loadData(filepath);
                        System.out.println("File " + filepath + " imported successfully!");
                    } catch (Exception e) {
                        System.out.println("Please enter the correct file path.");
                    }
                    break;

                case PROFILEDELETE:
                    // Delete a profile.
                    CommandUtils.deleteDonorBySearchCriteria(currentDatabase, expression);
                    System.out.println("Profile(s) successfully deleted.");
                    break;

                case EXPORT:
                    // Export donor database to file
                    try {
                        String filepath = expression.substring(7).trim();
                        DonorDataIO.saveDonors(currentDatabase, filepath);
                    } catch (Exception e) {
                        System.out.println("Please check file path.");
                    }
                    break;
                case ORGANDONATE:
                    // Add to donations made by a donor.
                    CommandUtils.addDonationsMadeBySearchCriteria(currentDatabase, expression);
                    System.out.println("Donation successfully added to profile.");
                    break;
            }
            expression = scanner.nextLine().trim();
        }
    }
}
