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
            int cmd = CommandUtils.ValidateCommandType(expression);

            switch (cmd) {

                case 1:
                    //print all profiles (print all).
                    ArrayList<Donor> allProfiles = currentDatabase.getDonors(false);
                    if (allProfiles.size() > 0) {
                        for (Donor profile : allProfiles) {
                            profile.viewAttributes();
                            System.out.println("\n");
                        }
                    }
                    else {
                        System.out.println("There are no profiles to show.");
                    }
                    break;

                case 2:
                    //print all profiles that are donors (print donors).
                    ArrayList<Donor> allDonors = currentDatabase.getDonors(true);
                    if (allDonors.size() > 0) {
                        for (Donor donor : allDonors) {
                            donor.viewAttributes();
                            donor.viewOrgans();
                            System.out.println("\n");
                        }
                    }
                    else {
                        System.out.println("There are no donor profiles to show.");
                    }
                    break;

                case 3:
                    //show available commands (help).
                    CommandUtils.Help();
                    break;

                case 4:
                    //create a new profile.
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

                case 5:
                    //search profiles (donor > view).
                    System.out.println("Searching for profiles...");
                    //carry out method call in command.
                    CommandUtils.ViewAttrBySearchCriteria(currentDatabase, expression);
                    break;

                case 6:
                    //search profiles (donor > date-created).
                    System.out.println("Searching for profiles...");
                    //carry out method call in command.
                    CommandUtils.ViewDateTimeCreatedBySearchCriteria(currentDatabase, expression);
                    break;

                case 7:
                    //search profiles (donor > donations).
                    System.out.println("Searching for profiles...");
                    //carry out method call in command.
                    CommandUtils.ViewDonationsBySearchCriteria(currentDatabase, expression);
                    break;

                case 8:
                    //search profiles.
                    System.out.println("Updating profiles...");
                    //carry out method call in command.
                    CommandUtils.UpdateDonorsBySearchCriteria(currentDatabase, expression);
                    break;

                case 9:
                    //add organs to a donors profile.
                    System.out.println("Adding organs to profile...");
                    //carry out method call in command.
                    CommandUtils.AddOrgansBySearchCriteria(currentDatabase, expression);
                    break;

                case 10:
                    //remove organs from a donors profile.
                    System.out.println("Removing organs from profile...");
                    //carry out method call in command.
                    CommandUtils.RemoveOrgansBySearchCriteria(currentDatabase, expression);
                    break;

                case 11:
                    System.out.println("Please enter a valid command.");
                    break;

                case 12:
                    //import a file of profiles.
                    try {
                        String filepath = expression.substring(7).trim();
                        currentDatabase = DonorDataIO.loadData(filepath);
                        System.out.println("File " + filepath + " imported successfully!");
                    } catch (Exception e) {
                        System.out.println("Please enter the correct file path.");
                    }
                    break;

                case 13:
                    //delete a profile.
                    CommandUtils.DeleteDonorBySearchCriteria(currentDatabase, expression);
                    break;

                case 14:
                    // export donor database to file
                    try {
                        String filepath = expression.substring(7).trim();
                        DonorDataIO.saveDonors(currentDatabase, filepath);
                    } catch (Exception e) {
                        System.out.println("Please check file path.");
                    }

            }
            expression = scanner.nextLine().trim();
        }
    }
}
