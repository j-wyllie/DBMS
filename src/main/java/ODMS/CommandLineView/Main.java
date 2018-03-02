package ODMS.CommandLineView;

import java.util.Scanner;

public class Main {

    public static void main(String[] args)
    {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Starting Organ Donor Management System...");
        System.out.println("\nPlease enter your commands below:\n");

        String expression = scanner.next();

        while (expression != "quit")
        {
            if (expression.contains("create-profile")) {
                //create profile.
            }
            else if (expression.contains("donor")) {
                //search profiles.
            }
            else if (expression.contains("print all")) {
                //print all profiles.
            }
            else {
                System.out.println("Please enter a valid command.");
            }
        }
    }
}
