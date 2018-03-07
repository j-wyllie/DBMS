package odms.commandlineview;

public class Command {

    public static int ValidateCommandType(String cmd)
    {
        String cmdRegexCreate = "([a-z]+)([-]([a-z]+))?((\\s)([a-z]+)(([-]([a-z]+))?)([=][\"](([a-zA-Z0-9][-]?)+)[\"]))*";
        String cmdRegexDonorView = "([a-z]+)([-]([a-z]+))?((\\s)([a-z]+)(([-]([a-z]+))?)([=][\"](([a-zA-Z0-9][-]?)+)[\"]))*(\\s[>]\\s([a-z]+)([-]([a-z]+))?)";
        String cmdRegexDonorUpdate = "([a-z]+)([-]([a-z]+))?((\\s)([a-z]+)(([-]([a-z]+))?)([=][\"](([a-zA-Z0-9][-]?)+)[\"]))*(\\s[>](\\s([a-z]+)([-]([a-z]+))?)([=][\"]([a-zA-Z0-9][-]?)+[\"]))*";
        String cmdRegexOrganUpdate = "([a-z]+)([-]([a-z]+))?((\\s)([a-z]+)(([-]([a-z]+))?)([=][\"](([a-zA-Z0-9][-]?)+)[\"]))*(\\s[>](\\s([a-z]+)([-]([a-z]+))?)([=][\"](([a-zA-Z]([,](\\s)?)*)+)[\"]))*";

        if (cmd.equals("print all")) {
            //print all profiles.
            return 1;
        }
        else if (cmd.equals("print donors")) {
            //print all profiles that are donors.
            return 2;
        }
        else if (cmd.equals("help")) {
            //Show available commands.
            return 3;
        }
        else if (cmd.matches(cmdRegexCreate)) {

            if (cmd.substring(0, 14).equals("create-profile")) {
                //create a new profile.
                return 4;
            }
            else {
                return 11;
            }
        }
        else if (cmd.matches(cmdRegexDonorView)) {

            if (cmd.substring(0, 5).equals("donor")) {

                if (cmd.substring(cmd.indexOf('>')).trim().equals("view")) {
                    //view profiles.
                    return 5;
                }
                else if (cmd.substring(cmd.indexOf('>')).trim().equals("date-created")) {
                    return 6;
                }
                else if (cmd.substring(cmd.indexOf('>')).trim().equals("donations")) {
                    return 7;
                }
                else {
                    return 11;
                }
            }
            else {
                return 11;
            }
        }
        else if (cmd.matches(cmdRegexDonorUpdate)) {

            if (cmd.substring(0, 5).equals("donor")) {
                //set attributes of a profile.
                return 8;
            }
            else {
                return 11;
            }
        }
        else if (cmd.matches(cmdRegexOrganUpdate)) {

            if (cmd.substring(cmd.indexOf('>') + 1, cmd.lastIndexOf('=')).trim().equals("add-organ")) {
                //view profiles.
                return 9;
            }
            else if (cmd.substring(cmd.indexOf('>') + 1, cmd.lastIndexOf('=')).trim().equals("delete-organ")) {
                //view profiles.
                return 10;
            }
            else {
                return 11;
            }
        }
        else {
            return 11;
        }
    }


    public static void Help()
    {
        System.out.println("\nCreate a new donor:");
        System.out.println("create-profile {attributes (given-names, last-names, and dob is required)}");
        System.out.println("e.g. create-profile given-names=\"Abby Rose\" last-names=\"Walker\" dob=\"03-03-1998\"");

        System.out.println("\nView a donor:");
        System.out.println("donor {attributes to search donors by} > view");

        System.out.println("\nView the date a donor was created:");
        System.out.println("donor {attributes to search donors by} > date-created");

        System.out.println("\nView a donors past donations:");
        System.out.println("donor {attributes to search donors by} > donations");

        System.out.println("\nUpdate a donors attributes:");
        System.out.println("donor {attributes to search donors by} > {attributes to update}");
        System.out.println("e.g. donor given-names=\"Abby Rose\" last-names=\"Walker\" dob=\"03-03-1998\" > height=\"169\"");

        System.out.print("\nPrint all profiles: ");
        System.out.println("print all");

        System.out.print("\nPrint all donors: ");
        System.out.println("print donors");

        System.out.print("\nClose the app: ");
        System.out.println("quit");

        System.out.println("\nAttributes:");
        System.out.println("given-names, last-names, dob, dod, gender, height, weight, blood-type, address, region");
    }
}
