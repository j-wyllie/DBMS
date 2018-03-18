package odms.commandlineview;

import static odms.commandlineview.CommandUtils.validateCommandType;

import java.io.IOException;
import java.util.Arrays;
import odms.data.DonorDataIO;
import odms.data.DonorDatabase;
import odms.data.IrdNumberConflictException;
import odms.donor.Donor;
import java.util.ArrayList;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.ParsedLine;
import org.jline.reader.impl.DefaultHighlighter;
import org.jline.reader.impl.DefaultParser;
import org.jline.reader.impl.history.DefaultHistory;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

public class CommandLine {
    private DonorDatabase currentDatabase;
    private LineReader reader;
    private Terminal terminal;

    public CommandLine (DonorDatabase currentDatabase) {
        this.currentDatabase = currentDatabase;

        try {
            terminal = TerminalBuilder.terminal();
            reader = LineReaderBuilder.builder()
                .terminal(terminal)
                .appName("ODMS")
                .completer(Commands.commandAutoCompletion())
//                .highlighter(new DefaultHighlighter()) TODO investigate syntax highlighting further
                .history(new DefaultHistory())
                .parser(new DefaultParser())
                .build();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Initialise the commandline/console interface.
     */
    public void initialiseConsole() {
        Boolean exit = false;
        String input;

        System.out.println("Organ Donor Management System");
        System.out.println("\nPlease enter your commands below:");

        while (!exit) {
            input = reader.readLine("> ").trim();

            terminal.flush();

            ParsedLine parsedInput = reader.getParser().parse(input, 0);

            if (parsedInput.word().toLowerCase().equals("exit") ||
                parsedInput.word().toLowerCase().equals("quit")) {
                exit = true;
            } else {
                reader.getHighlighter();
                processInput(new ArrayList<>(parsedInput.words()));
            }

        }
    }

    /**
     * Take the input from the console commands and process them accordingly.
     *
     * @param input commands entered from console
     */
    private void processInput(ArrayList<String> input) {
        Commands inputCommand = validateCommandType(String.join(" ", input).trim());
        String inputExpression = String.join(" ", input).trim();

        switch (inputCommand) {
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
                        System.out.println();
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
                    String[] attrList = inputExpression.substring(15).split("\"\\s");
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
                CommandUtils.viewAttrBySearch(currentDatabase, inputExpression);
                break;

            case DONORDATECREATED:
                // Search profiles (donor > date-created).
                System.out.println("Searching for profiles...");
                CommandUtils.viewDateTimeCreatedBySearch(currentDatabase, inputExpression);
                break;

            case DONORDONATIONS:
                // Search profiles (donor > donations).
                System.out.println("Searching for profiles...");
                CommandUtils.viewDonationsBySearch(currentDatabase, inputExpression);
                break;

            case DONORUPDATE:
                // Search profiles.
                CommandUtils.updateProfilesBySearch(currentDatabase, inputExpression);
                System.out.println("Profile(s) successfully updated.");
                break;

            case ORGANADD:
                // Add organs to a donors profile.
                CommandUtils.addOrgansBySearch(currentDatabase, inputExpression);
                System.out.println("Organ successfully added to profile(s).");
                break;

            case ORGANREMOVE:
                // Remove organs from a donors profile.
                CommandUtils.removeOrgansBySearch(currentDatabase, inputExpression);
                System.out.println("Organ successfully removed from profile(s).");
                break;

            case INVALID:
                System.out.println("Please enter a valid command.");
                break;

            case IMPORT :
                // Import a file of profiles.
                try {
                    String filepath = inputExpression.substring(7).trim();
                    currentDatabase = DonorDataIO.loadData(filepath);
                    System.out.println("File " + filepath + " imported successfully!");
                } catch (Exception e) {
                    System.out.println("Please enter the correct file path.");
                }
                break;

            case PROFILEDELETE:
                // Delete a profile.
                CommandUtils.deleteDonorBySearch(currentDatabase, inputExpression);
                System.out.println("Profile(s) successfully deleted.");
                break;

            case EXPORT:
                // Export donor database to file
                try {
                    String filepath = inputExpression.substring(7).trim();
                    DonorDataIO.saveDonors(currentDatabase, filepath);
                } catch (Exception e) {
                    System.out.println("Please check file path.");
                }
                break;
            case ORGANDONATE:
                // Add to donations made by a donor.
                CommandUtils.addDonationsMadeBySearch(currentDatabase, inputExpression);
                System.out.println("Donation successfully added to profile.");
                break;
        }
    }
}
