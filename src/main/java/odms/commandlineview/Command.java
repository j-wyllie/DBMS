package odms.commandlineview;

public class Command {

    public static int ValidateCommandType(String cmd)
    {
        String cmdRegexCreate = "([a-z]+)([-]([a-z]+))?((\\s)([a-z]+)(([-]([a-z]+))?)([=][\"]([a-z]+)[\"]))*";
        String cmdRegexDonorView = "([a-z]+)([-]([a-z]+))?((\\s)([a-z]+)(([-]([a-z]+))?)([=][\"]([a-z]+)[\"]))*(\\s[>]\\s([a-z]+)([-]([a-z]+))?)";
        String cmdRegexDonorUpdate = "([a-z]+)([-]([a-z]+))?((\\s)([a-z]+)(([-]([a-z]+))?)([=][\"]([a-z]+)[\"]))*(\\s[>](\\s([a-z]+)([-]([a-z]+))?)([=][\"]([a-z]+)[\"]))*";

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
                return 7;
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
                    return 9;
                }
            }
            else {
                return 9;
            }
        }
        else if (cmd.matches(cmdRegexDonorUpdate)) {

            if (cmd.substring(0, 5).equals("donor")) {
                //set attributes of a profile.
                return 8;
            }
            else {
                return 9;
            }
        }
        else {
            return 9;
        }
    }
}
