package odms.cli.commands;

import java.util.Arrays;

public class Help {

    /**
     * Output a list of all commands and explanations/examples of use
     */
    public static void help() {
        System.out.println("\nCreate a new profile:");
        System.out.println("create-profile {attributes (given-names, last-names, dob and ird is "
            + "required)}");
        System.out.println("e.g. create-profile given-names=\"Abby Rose\" last-names=\"Walker\" "
            + "dob=\"03-03-1998\" ird=\"123456789\"");

        System.out.println("\nView a profile:");
        System.out.println("profile {attributes to search profiles by} > view");

        System.out.println("\nView the date a profile was created:");
        System.out.println("profile {attributes to search profiles by} > date-created");

        System.out.println("\nView a profiles past donations:");
        System.out.println("profile {attributes to search profiles by} > donations");

        System.out.println("\nUpdate a profiles attributes:");
        System.out.println("profile {attributes to search profiles by} > {attributes to update}");
        System.out
            .println("e.g. profile given-names=\"Abby Rose\" last-names=\"Walker\" dob=\"03-03"
                + "-1998\" > height=\"169\"");

        System.out.println("\nAdd an organ to donate:");
        System.out
            .println("profile {attributes to search profiles by} > add-organ=\" {list of organs "
                + "to donate} \"");
        System.out
            .println("e.g. profile given-names=\"Abby Rose\" last-names=\"Walker\" dob=\"03-03"
                + "-1998\" > add-organ=\"liver, kidney\"");

        System.out.println("\nRemove an organ to donate:");
        System.out.println("profile {attributes to search profiles by} > remove-organ=\" {list of "
            + "organs to remove} \"");
        System.out.println("e.g. profile given-names=\"Abby Rose\" last-names=\"Walker\" dob=\"03-"
            + "03-1998\" > remove-organ=\"liver, kidney\"");

        System.out.println("\nAdd an organ to receive:");
        System.out
                .println("profile {attributes to search profiles by} > receive-organ=\" {list of organs "
                        + "to donate} \"");
        System.out
                .println("e.g. profile given-names=\"Abby Rose\" last-names=\"Walker\" dob=\"03-03"
                        + "-1998\" > receive-organ=\"liver, kidney\"");

        System.out.println("\nRemove an organ to receive:");
        System.out.println("profile {attributes to search profiles by} > removereceive-organ=\" {list of "
                + "organs to remove} \"");
        System.out.println("e.g. profile given-names=\"Abby Rose\" last-names=\"Walker\" dob=\"03-"
                + "03-1998\" > removereceive-organ=\"liver, kidney\"");

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
        System.out.println(
            "Liver, Kidney, Pancreas, Heart, Lung, Intestine, Cornea, Middle Ear, Skin, Bone, "
                + "Bone Marrow, Connective Tissue");

    }

    /**
     * Output usage information for specified command.
     *
     * @param cmd command requesting usage for
     */
    public static void helpSpecific(String cmd) {
        String[] cmdArray = {"create-profile", "view", "date-created", "donations", "update",
            "add-organ", "remove-organ", "print all", "print donors", "quit", "attributes",
            "organs"};
        String[] definitionArray = {"\nCreate a new profile", "\nView a profile",
            "\nView the date a profile was created", "\nView a profiles past donations",
            "\nUpdate a profiles attributes",
            "\nAdd an organ to donate", "\nRemove an organ to donate", "\nPrint all profiles ",
            "\nPrint all donors ", "\nClose the app ",
            "The possible attriubtes are : given-names, last-names, dob, dod, gender, height, "
                + "weight, blood-type, address, region, ird",
            "The possible organs are : Liver, Kidney, Pancreas, Heart, Lung, Intestine, Cornea, "
                + "Middle Ear, Skin, Bone, Bone Marrow, Connective Tissue"};
        String[] exampleArray = {
            "create-profile {attributes (given-names, last-names, dob and ird is required)}",
            "profile {attributes to search profiles by} > view",
            "profile {attributes to search profiles by} > date-created",
            "profile {attributes to search profiles by} > donations",
            "profile {attributes to search profiles by} > {attributes to update}"
            ,
            "profile {attributes to search profiles by} > add-organ=\" {list of organs to donate} \"",
            "profile {attributes to search profiles by} > remove-organ=\" {list of organs to remove} \"",
            "print all: ", "print donors", "quit"};
        if (Arrays.asList(cmdArray).contains(cmd)) {
            int position = Arrays.asList(cmdArray).indexOf(cmd);
            System.out.println(definitionArray[position]);
            if (position < 10) {
                System.out.println("The command is entered in this format:");
                System.out.println(exampleArray[position]);
            }
        } else {
            System.out.println("Invalid command");
        }
    }

}
