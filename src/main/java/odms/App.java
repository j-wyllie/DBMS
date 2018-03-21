package odms;

import odms.commandlineview.CommandLine;
import odms.commandlineview.CommandUtils;
import odms.data.DonorDatabase;

public class App
{
    private static DonorDatabase donorDb = new DonorDatabase();
    private static CommandLine commandLine = new CommandLine(donorDb);

    public static void main( String[] args ) {
        CommandUtils.currentSessionHistory.add("");
        try {
            commandLine.initialiseConsole();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}