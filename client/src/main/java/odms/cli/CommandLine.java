package odms.cli;

import java.sql.SQLException;
import odms.cli.commands.Help;
import odms.cli.commands.Print;
import odms.cli.commands.Profile;
import odms.cli.commands.User;
import odms.commons.model.enums.UserType;
import odms.controller.database.DAOFactory;
import odms.controller.database.DatabaseConnection;
import odms.controller.database.organ.OrganDAO;
import odms.controller.database.profile.ProfileDAO;
import odms.controller.database.user.UserDAO;
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

    private ProfileDAO profileDatabase;
    private UserDAO userDatabase;
    private OrganDAO organDatabase;
    private LineReader reader;
    private Terminal terminal;

    /**
     * Create a standard input/output terminal
     */
    public CommandLine() {
        this.profileDatabase = DAOFactory.getProfileDao();
        this.userDatabase = DAOFactory.getUserDao();
        this.organDatabase = DAOFactory.getOrganDao();

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
     * @param input
     * @param output
     */
    public CommandLine(InputStream input, OutputStream output) {
        this.profileDatabase = DAOFactory.getProfileDao();

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
     * Run implementation so command line can be run in an alternate thread.
     */
    public void run() { initialiseConsole(); }

    /**
     * Initialise the commandline/console interface.
     */
    public void initialiseConsole() {
        Boolean exit = false;
        String input;
        this.userDatabase = DAOFactory.getUserDao();

        System.out.println("Organ profile Management System");
        System.out.println("Please enter your commands below:\n");

        while (!exit) {
            input = reader.readLine("> ").trim();

            terminal.flush();

            ParsedLine parsedInput = reader.getParser().parse(input, 0);

            if (parsedInput.word().toLowerCase().equals("exit") ||
                    parsedInput.word().toLowerCase().equals("quit")) {
                exit = true;
            } else {
                reader.getHighlighter();
                try {
                    processInput(new ArrayList<>(parsedInput.words()), input);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public History getHistory() {
        return reader.getHistory();
    }

    /**
     * Take the input from the console commands and process them accordingly.
     * @param input commands entered from console
     */
    private void processInput(ArrayList<String> input, String rawInput) throws SQLException {
        Commands inputCommand = validateCommandType(input, rawInput);
        DatabaseConnection instance = DatabaseConnection.getInstance();

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
                Print.printAllProfiles(profileDatabase.getAll());
                break;

            case PRINTALLCLINICIANS:
                // Print all clinicians (print all).
                Print.printAllClinicians(userDatabase.getAll());
                break;

            case PRINTALLUSERS:
                // Print all users (print all).
                Print.printAllUsers(userDatabase.getAll());
                break;

            case PRINTDONORS:
                // Print all profiles that are donors (print donors).
                Print.printDonors(profileDatabase.getAll());
                break;

            case PROFILECREATE:
                // Create a new profile.
                Profile.createProfile(rawInput);
                break;

            case PROFILEDATECREATED:
                // Search profiles (profile > date-created).
                System.out.println("Searching for profiles...");
                Profile.viewDateTimeCreatedBySearch(rawInput);
                break;

            case PROFILEDELETE:
                // Delete a profile.
                Profile.deleteProfileByNHI(rawInput);
                System.out.println("profile(s) successfully deleted.");
                break;

            case PROFILEORGANS:
                // Search profiles (profile > donations).
                System.out.println("Searching for profiles...");
                Profile.viewDonationsBySearch(rawInput);
                break;

            case PROFILEUPDATE:
                // Search profiles.
                Profile.updateProfilesBySearch(rawInput);
                break;

            case PROFILEVIEW:
                // Search profiles (profile > view).
                System.out.println("Searching for profiles...");
                Profile.viewAttrBySearch(rawInput);
                break;

            case CLINICIANCREATE:
                // Create a new clinician.
                User.createClinician(rawInput);
                break;

            case CLINICIANDATECREATED:
                // Search clinicians (clinician > date-created).
                System.out.println("Searching for clinicians...");
                User.viewDateTimeCreatedBySearch(rawInput, "clinician");
                break;

            case CLINICIANDELETE:
                // Delete a clinician.
                User.deleteUserBySearch(rawInput, "clinician");
                System.out.println("Clinician(s) successfully deleted.");
                break;

            case CLINICIANUPDATE:
                // Search clinician.
                User.updateUserBySearch(rawInput, "clinician");
                break;

            case CLINICIANEVIEW:
                // Search clinician (clinician > view).
                System.out.println("Searching for clinicians...");
                User.viewAttrBySearch(rawInput, "clinician");
                break;

            case ADMINCREATE:
                // Create a new admin
                User.createAdmin(rawInput);
                break;

            case ADMINDELETE:
                // Delete an admin
                User.deleteUserBySearch(rawInput, UserType.ADMIN.getName());
                break;

            case ADMINUPDATE:
                // Update an admin
                User.updateUserBySearch(rawInput, UserType.ADMIN.getName());
                break;

            case ORGANADD:
                // Add organs to a profile.
                CommandUtils.addOrgansBySearch(rawInput);
                System.out.println("Organ successfully added to profile(s).");
                break;

            case ORGANREMOVE:
                // Remove organs from a profile.
                CommandUtils.removeOrgansBySearch(rawInput);
                System.out.println("Organ successfully removed from profile(s).");
                break;

            case RECEIVERADD:
                // Add organs to a Receiver profile.
                CommandUtils.addReceiverOrgansBySearch(rawInput);
                System.out.println("Organ successfully added to profile(s).");
                break;

            case RECEIVEREMOVE:
                // Remove organs from a printDonors profile.
                CommandUtils.removeReceiverOrgansBySearch(rawInput);
                System.out.println("Organ successfully removed from profile(s).");
                break;

            case ORGANDONATE:
                // Add to donations made by a profile.
                CommandUtils.addDonationsMadeBySearch(rawInput);
                System.out.println("Donation successfully added to profile.");
                break;

            case SQLREADONLY:
                // Execute read-only query.
                CommandUtils.executeDatabaseRead(rawInput);
                break;

            case RESET:
                instance.reset();
                break;

            case RESAMPLE:
                instance.resample();
                break;
//
//            case UNDO:
//                // Undoes the previously done action
//                CommandUtils.undo(currentDatabase);
//                break;
//
//            case REDO:
//                //Redoes the previously undone action
//                CommandUtils.redo(currentDatabase);
//                break;
        }
    }
}
