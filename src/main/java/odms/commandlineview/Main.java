package odms.commandlineview;

import odms.donor.Donor;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    public static void main(String[] args)
    {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Starting Organ Donor Management System...");
        System.out.println("\nPlease enter your commands below:");

        String expression = scanner.nextLine().trim();

        while (!(expression.equals("quit")))
        {
            int cmd = Command.ValidateCommandType(expression);

            switch (cmd) {

                case 1:
                    //print all profiles (print all).
                    break;

                case 2:
                    //print all profiles that are donors (print donors).
                    break;

                case 3:
                    //show available commands (help).
                    break;

                case 4:
                    //create a new profile.
                    System.out.println("Creating new profile...");

                    String[] attrList = expression.substring(14).split("\\s");
                    ArrayList<String> attrArray = new ArrayList<>();
                    for (String attr : attrList) {
                        attrArray.add(attr);
                    }
                    Donor newDonor = new Donor(attrArray);
                    DonorDatabase.add(newDonor);
                    //store the donor.
                    break;

                case 5:
                    //search profiles (donor > view).
                    System.out.println("Searching for profiles...");
                    //carry out method call in command.
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
                    System.out.println("Please enter a valid command.");
                    break;
            }
            expression = scanner.nextLine().trim();
        }
    }
}
