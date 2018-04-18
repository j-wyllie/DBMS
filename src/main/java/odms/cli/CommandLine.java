package odms.cli;

import static odms.cli.CommandUtils.validateCommandType;

import java.io.IOException;
import odms.data.ProfileDataIO;
import odms.data.ProfileDatabase;
import odms.profile.Profile;
import java.util.ArrayList;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.ParsedLine;
import org.jline.reader.impl.DefaultParser;
import org.jline.reader.impl.history.DefaultHistory;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

public class CommandLine {

    private ProfileDatabase currentDatabase;
    private LineReader reader;
    private Terminal terminal;

    public CommandLine (ProfileDatabase currentDatabase) {
        this.currentDatabase = currentDatabase;

        try {
            terminal = TerminalBuilder.terminal();
            reader = LineReaderBuilder.builder()
                .terminal(terminal)
                .appName("ODMS")
                .completer(Commands.commandAutoCompletion())
                // .highlighter(new DefaultHighlighter()) TODO investigate syntax highlighting further
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

        System.out.println("Organ Profile Management System");
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
                processInput(new ArrayList<>(parsedInput.words()),input);
            }

        }
    }

    /**
     * Take the input from the console commands and process them accordingly.
     *
     * @param input commands entered from console
     */
    private void processInput(ArrayList<String> input, String rawInput) {
        Commands inputCommand = validateCommandType(input, rawInput);

        switch (inputCommand) {
            case INVALID:
                System.out.println("Please enter a valid command.");
                break;

            case HELP:
                // Show available commands (help).
                if(rawInput.equals("help")) {
                    CommandUtils.help();
                } else {
                    CommandUtils.helpSpecific(rawInput.substring(5));
                }
                break;

            case PRINTALL:
                // Print all profiles (print all).
                ArrayList<Profile> allProfiles = currentDatabase.getProfiles(false);
                if (allProfiles.size() > 0) {
                    for (Profile profile : allProfiles) {
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
                ArrayList<Profile> allDonors = currentDatabase.getProfiles(true);
                if (allDonors.size() > 0) {
                    for (Profile profile : allDonors) {
                        profile.viewAttributes();
                        profile.viewOrgans();
                        System.out.println();
                    }
                }
                else {
                    System.out.println("There are no profile profiles to show.");
                }
                break;

            case EXPORT:
                // Export profile database to file
                if (input.size() == 2) {
                    String filepath = input.get(1);
                    ProfileDataIO.saveDonors(currentDatabase, filepath);
                } else {
                    System.out.println("Error: Invalid arguments. Expected: 1, "
                            + "Found: " + (input.size() - 1));
                }
                break;

            case IMPORT :
                // Import a file of profiles.
                if (input.size() == 2) {
                    String filepath = input.get(1);
                    currentDatabase = ProfileDataIO.loadData(filepath);
                } else {
                    System.out.println("Error: Invalid arguments. Expected: 1, "
                            + "Found: " + (input.size() - 1));
                }
                break;

            case PROFILECREATE:
                // Create a new profile.
                CommandUtils.createProfile(currentDatabase, rawInput);
                break;

            case PROFILEDELETE:
                // Delete a profile.
                CommandUtils.deleteDonorBySearch(currentDatabase, rawInput);
                System.out.println("Profile(s) successfully deleted.");
                break;

            case PROFILEVIEW:
                // Search profiles (profile > view).
                System.out.println("Searching for profiles...");
                CommandUtils.viewAttrBySearch(currentDatabase, rawInput);
                break;

            case PROFILEDATECREATED:
                // Search profiles (profile > date-created).
                System.out.println("Searching for profiles...");
                CommandUtils.viewDateTimeCreatedBySearch(currentDatabase, rawInput);
                break;

            case PROFILEDONATIONS:
                // Search profiles (profile > donations).
                System.out.println("Searching for profiles...");
                CommandUtils.viewDonationsBySearch(currentDatabase, rawInput);
                break;

            case PROFILEUPDATE:
                // Search profiles.
                CommandUtils.updateProfilesBySearch(currentDatabase, rawInput);
                System.out.println("Profile(s) successfully updated.");
                break;

            case ORGANADD:
                // Add organs to a donors profile.
                CommandUtils.addOrgansBySearch(currentDatabase, rawInput);
                System.out.println("Organ successfully added to profile(s).");
                break;

            case ORGANREMOVE:
                // Remove organs from a donors profile.
                CommandUtils.removeOrgansBySearch(currentDatabase, rawInput);
                System.out.println("Organ successfully removed from profile(s).");
                break;

            case ORGANDONATE:
                // Add to donations made by a profile.
                CommandUtils.addDonationsMadeBySearch(currentDatabase, rawInput);
                System.out.println("Donation successfully added to profile.");
                break;

            case UNDO:
                // Undoes the previously done action
                CommandUtils.undo(currentDatabase);
                break;

            case REDO:
                //Redoes the previously undone action
                CommandUtils.redo(currentDatabase);
                break;
        }
    }
}
