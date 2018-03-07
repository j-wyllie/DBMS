package odms.commandlineview;

import odms.donor.Donor;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    public static void main(String[] args)
    {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Starting Organ donor Management System...");
        System.out.println("\nPlease enter your commands below:\n");

        String expression = scanner.nextLine().trim();

        while (expression != "quit")
        {
            String cmdRegexCreate = "([a-z]+)([-]([a-z]+))?((\\s)([a-z]+)(([-]([a-z]+))?)([=][\"]([a-z]+)[\"]))*";
            String cmdRegexDonorView = "([a-z]+)([-]([a-z]+))?((\\s)([a-z]+)(([-]([a-z]+))?)([=][\"]([a-z]+)[\"]))*(\\s[>]\\s([a-z]+)([-]([a-z]+))?)";
            String cmdRegexDonorUpdate = "([a-z]+)([-]([a-z]+))?((\\s)([a-z]+)(([-]([a-z]+))?)([=][\"]([a-z]+)[\"]))*(\\s[>](\\s([a-z]+)([-]([a-z]+))?)([=][\"]([a-z]+)[\"]))*";

            if (expression == "print all") {
                //print all profiles.
            }
            else if (expression == "print donors") {
                //print all profiles that are donors.
            }
            else if (expression == "help") {
                //Show available commands.
            }
            else if (expression.matches(cmdRegexCreate)) {

                if (expression.substring(0, 14).equals("create-profile")) {
                    //create a new profile.
                    System.out.println("Creating new profile...");

                    String[] attrList = expression.substring(14).split("\\s");
                    ArrayList<String> attrArray = new ArrayList<>();

                    for (String attr : attrList) {
                        attrArray.add(attr);
                    }
                    Donor newDonor = new Donor(attrArray);
                    //store the donor.
                }
                else {
                    System.out.println("Please enter a valid command.");
                }
            }
            else if (expression.matches(cmdRegexDonorView)) {

                if (expression.substring(0, 5).equals("donor")) {
                    //search profiles.
                    System.out.println("Searching for profiles...");
                    //carry out method call in command.

                }
                else {
                    System.out.println("Please enter a valid command.");
                }
            }
            else if (expression.matches(cmdRegexDonorUpdate)) {

                if (expression.substring(0, 5).equals("donor")) {
                    //search profiles.
                    System.out.println("Updating profiles...");
                    //carry out method call in command.
                }
                else {
                    System.out.println("Please enter a valid command.");
                }
            }
            else {
                System.out.println("Please enter a valid command.");
            }

            expression = scanner.nextLine().trim();
        }
    }
}
