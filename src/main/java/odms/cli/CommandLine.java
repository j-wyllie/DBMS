package odms.cli;

import static odms.cli.CommandUtils.validateCommandType;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import odms.cli.commands.Help;
import odms.cli.commands.Print;
import odms.cli.commands.Profile;
import odms.data.ProfileDataIO;
import odms.data.ProfileDatabase;
import org.jline.reader.History;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.ParsedLine;
import org.jline.reader.impl.DefaultParser;
import org.jline.reader.impl.history.DefaultHistory;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

public class CommandLine implements Runnable {

    private ProfileDatabase currentDatabase;
    private LineReader reader;
    private Terminal terminal;

    public CommandLine(ProfileDatabase currentDatabase) {
        this.currentDatabase = currentDatabase;

        try {
            terminal = TerminalBuilder.terminal();
            reader = LineReaderBuilder.builder()
                .terminal(terminal)
                .appName("ODMS")
                .completer(Commands.commandAutoCompletion())
                .history(new DefaultHistory())
                .parser(new DefaultParser())
                .build();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public CommandLine(ProfileDatabase currentDatabase, InputStream input, OutputStream output) {
        this.currentDatabase = currentDatabase;

        try {
            terminal = TerminalBuilder.builder()
                    .system(false)
                    .streams(input, output)
                    .build();

            reader = LineReaderBuilder.builder()
                    .terminal(terminal)
                    .appName("ODMS")
                    .completer(Commands.commandAutoCompletion())
                    .history(new DefaultHistory())
                    .parser(new DefaultParser())
                    .build();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        initialiseConsole();
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

    public History getHistory() { return reader.getHistory(); }

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
                    Help.help();
                } else {
                    Help.helpSpecific(rawInput.substring(5));
                }
                break;

            case PRINTALL:
                // Print all profiles (print all).
                Print.printAll(currentDatabase);
                break;

            case PRINTDONORS:
                // Print all profiles that are donors (print donors).
                Print.printDonors(currentDatabase);
                break;

            case EXPORT:
                // Export profile database to file
                if (input.size() == 2) {
                    String filepath = input.get(1);
                    ProfileDataIO.saveData(currentDatabase, filepath);
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
                Profile.createProfile(currentDatabase, rawInput);
                break;

            case PROFILEDATECREATED:
                // Search profiles (profile > date-created).
                System.out.println("Searching for profiles...");
                Profile.viewDateTimeCreatedBySearch(currentDatabase, rawInput);
                break;

            case PROFILEDELETE:
                // Delete a profile.
                Profile.deleteProfileBySearch(currentDatabase, rawInput);
                System.out.println("Profile(s) successfully deleted.");
                break;

            case PROFILEDONATIONS:
                // Search profiles (profile > donations).
                System.out.println("Searching for profiles...");
                Profile.viewDonationsBySearch(currentDatabase, rawInput);
                break;

            case PROFILEUPDATE:
                // Search profiles.
                Profile.updateProfilesBySearch(currentDatabase, rawInput);
                System.out.println("Profile(s) successfully updated.");
                break;

            case PROFILEVIEW:
                // Search profiles (profile > view).
                System.out.println("Searching for profiles...");
                Profile.viewAttrBySearch(currentDatabase, rawInput);
                break;

            case ORGANADD:
                // Add organs to a printDonors profile.
                CommandUtils.addOrgansBySearch(currentDatabase, rawInput);
                System.out.println("Organ successfully added to profile(s).");
                break;

            case ORGANREMOVE:
                // Remove organs from a printDonors profile.
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
