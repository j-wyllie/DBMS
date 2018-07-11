package odms.cli;

import odms.cli.commands.Help;
import odms.cli.commands.Print;
import odms.cli.commands.Profile;
import odms.cli.commands.User;
import odms.controller.GuiMain;
import odms.controller.data.ProfileDataIO;
import odms.controller.data.UserDataIO;
import odms.model.data.ProfileDatabase;
import odms.model.data.UserDatabase;
import org.jline.reader.History;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.ParsedLine;
import org.jline.reader.impl.DefaultParser;
import org.jline.reader.impl.history.DefaultHistory;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import static odms.cli.CommandUtils.validateCommandType;

public class CommandLine implements Runnable {

    private ProfileDatabase currentDatabase;
    private UserDatabase currentDatabaseUsers;
    private LineReader reader;
    private Terminal terminal;

    /**
     * Create a standard input/output terminal
     *
     * @param currentDatabase
     */
    public CommandLine(ProfileDatabase currentDatabase, UserDatabase currentDatabaseUsers) {
        this.currentDatabase = currentDatabase;
        this.currentDatabaseUsers = currentDatabaseUsers;

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

    /**
     * Create a virtual terminal command line
     *
     * @param currentDatabase
     * @param input
     * @param output
     */
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

    /**
     * Run implementation so command line can be run in an alternate thread
     */
    public void run() {
        initialiseConsole();
    }

    /**
     * Initialise the commandline/console interface.
     */
    public void initialiseConsole() {
        Boolean exit = false;
        String input;
        currentDatabaseUsers = GuiMain.getUserDatabase();

        System.out.println("Organ profile Management System");
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
                processInput(new ArrayList<>(parsedInput.words()), input);
            }

        }
    }

    public History getHistory() {
        return reader.getHistory();
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
                System.out.println("Please enter a valid command...");
                break;

            case HELP:
                // Show available commands (help).
                if (rawInput.equals("help")) {
                    Help.help();
                } else {
                    Help.helpSpecific(rawInput.substring(5));
                }
                break;

            case PRINTALLPROFILES:
                // Print all profiles (print all).
                Print.printAllProfiles(currentDatabase);
                break;

            case PRINTCLINICIANS:
                // Print all clinicians (print all).
                Print.printAllClinicians(currentDatabaseUsers);
                break;

            case PRINTALLUSERS:
                // Print all users (print all).
                Print.printAllUsers(currentDatabaseUsers);
                break;

            case PRINTDONORS:
                // Print all profiles that are donors (print donors).
                Print.printDonors(currentDatabase);
                break;

            case EXPORT:
                // Export profile database to file
                if (input.size() == 1) {
                    ProfileDataIO.saveData(currentDatabase);
                    UserDataIO.saveUsers(currentDatabaseUsers);
                }
                if (input.size() == 2) {
                    String filepath = input.get(1);
                    ProfileDataIO.saveData(currentDatabase, filepath);
                    UserDataIO.saveUsers(currentDatabaseUsers, filepath);
                }
                break;

            case IMPORT:
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
                System.out.println("profile(s) successfully deleted.");
                break;

            case PROFILEDONATIONS:
                // Search profiles (profile > donations).
                System.out.println("Searching for profiles...");
                Profile.viewDonationsBySearch(currentDatabase, rawInput);
                break;

            case PROFILEUPDATE:
                // Search profiles.
                Profile.updateProfilesBySearch(currentDatabase, rawInput);
                System.out.println("profile(s) successfully updated.");
                break;

            case PROFILEVIEW:
                // Search profiles (profile > view).
                System.out.println("Searching for profiles...");
                Profile.viewAttrBySearch(currentDatabase, rawInput);
                break;

            case CLINICIANCREATE:
                // Create a new clinician.
                User.createClinician(currentDatabaseUsers, rawInput);
                break;

            case CLINICIANDATECREATED:
                // Search clinicians (clinician > date-created).
                System.out.println("Searching for clinicians...");
                User.viewDateTimeCreatedBySearch(currentDatabaseUsers, rawInput, "clinician");
                break;

            case CLINICIANDELETE:
                // Delete a clinician.
                User.deleteUserBySearch(currentDatabaseUsers, rawInput, "clinician");
                System.out.println("Clinician(s) successfully deleted.");
                break;

            case CLINICIANUPDATE:
                // Search clinician.
                User.updateUserBySearch(currentDatabaseUsers, rawInput, "clinician");
                System.out.println("Clinician(s) successfully updated.");
                break;

            case CLINICIANEVIEW:
                // Search clinician (clinician > view).
                System.out.println("Searching for clinicians...");
                User.viewAttrBySearch(currentDatabaseUsers, rawInput, "clinician");
                break;

            case ORGANADD:
                // Add organs to a profile.
                CommandUtils.addOrgansBySearch(currentDatabase, rawInput);
                System.out.println("Organ successfully added to profile(s).");
                break;

            case ORGANREMOVE:
                // Remove organs from a profile.
                CommandUtils.removeOrgansBySearch(currentDatabase, rawInput);
                System.out.println("Organ successfully removed from profile(s).");
                break;

            case RECEIVERADD:
                // Add organs to a Receiver profile.
                CommandUtils.addReceiverOrgansBySearch(currentDatabase, rawInput);
                System.out.println("Organ successfully added to profile(s).");
                break;

            case RECEIVEREMOVE:
                // Remove organs from a printDonors profile.
                CommandUtils.removeReceiverOrgansBySearch(currentDatabase, rawInput);
                System.out.println("Organ successfully removed from profile(s).");
                break;

            case ORGANDONATE:
                // Add to donations made by a profile.
                CommandUtils.addDonationsMadeBySearch(currentDatabase, rawInput);
                System.out.println("Donation successfully added to profile.");
                break;

            case SQLREADONLY:
                // Execute read-only query.
                CommandUtils.executeDatabaseRead(rawInput);
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
