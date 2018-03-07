package odms.commandlineview;

import odms.data.DonorDatabase;
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
                    break;

                case 2:
                    //print all profiles that are donors (print donors).
                    break;

                case 3:
                    //show available commands (help).
                    CommandUtils.Help();
                    break;

                case 4:
                    //create a new profile.

                    String[] attrList = expression.substring(15).split("\"\\s");
                    ArrayList<String> attrArray = new ArrayList<>();
                    for (String attr : attrList) {
                        attrArray.add(attr);
                    }

                    try {
                        Donor newDonor = new Donor(attrArray);
                        currentDatabase.addDonor(newDonor);
                        System.out.println("Profile created.");
                    }
                    catch (IllegalArgumentException e) {
                        System.out.println("Please enter the required attributes correctly.");
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
                    break;

                case 7:
                    //search profiles (donor > donations).
                    System.out.println("Searching for profiles...");
                    //carry out method call in command.
                    break;

                case 8:
                    //search profiles.
                    System.out.println("Updating profiles...");
                    //carry out method call in command.
                    break;

                case 9:
                    //add organs to a donors profile.
                    System.out.println("Adding organs to profile...");
                    //carry out method call in command.
                    break;

                case 10:
                    //remove organs from a donors profile.
                    System.out.println("Removing organs from profile...");
                    //carry out method call in command.
                    break;

                case 11:
                    System.out.println("Please enter a valid command.");
                    break;
            }
            expression = scanner.nextLine().trim();
        }
    }
}
