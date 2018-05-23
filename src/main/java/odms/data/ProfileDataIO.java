package odms.data;

import com.google.gson.Gson;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import com.google.gson.GsonBuilder;
import odms.cli.CommandUtils;

public class ProfileDataIO extends CommonDataIO {

    private static final String defaultPath = "example/example.json";
    private static String history = "";

    /**
     * Export full Profile Database object to the previously used path.
     *
     * @param profileDb Database to be exported to JSON
     */
    public static void saveData(ProfileDatabase profileDb) {
        if (profileDb.getPath() == null) {
            profileDb.setPath(defaultPath);
        }
        saveData(profileDb, profileDb.getPath());
    }

    /**
     * Export full Profile Database object to specified file.
     *
     * @param profileDb Database to be exported to JSON
     * @param path target path
     */
    public static void saveData(ProfileDatabase profileDb, String path) {
        profileDb.setPath(path);
        File file = new File(path);
        File historyFile = new File(path.replace(".json","History.json"));

        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            BufferedWriter writeFile = new BufferedWriter(new FileWriter(file));
            BufferedWriter writeHistoryFile = new BufferedWriter(new FileWriter(historyFile));
            writeFile.write(gson.toJson(profileDb));
            writeFile.close();
            if (history.equals("")) {
                history = gson.toJson(CommandUtils.getHistory());
            } else {
                history = history.substring(0, history.length()-1);
                history = history+","+gson.toJson(CommandUtils.getHistory()).substring(1);
            }
            writeHistoryFile.write(history);
            writeHistoryFile.close();
            System.out.println("File " + file.getName() + " exported successfully!");

        } catch (IOException e) {
            System.out.println("IO exception, please check the specified file");
            System.out.println("File requested: " + path);
        }
    }

    /**
     * Reads a file from the provided filename or path, converts to string.
     *
     * @param file filename or path
     * @return String of contents of provided file
     */
    private static String fileToString(File file) {
        StringBuilder fileBuffer = new StringBuilder();

        UserDataIO.loadDataInBuffer(file, fileBuffer);

        return fileBuffer.toString();
    }

    /**
     * Load the specified ProfileDatabase JSON file instantiating a ProfileDatabase Object.
     *
     * @param path specified ProfileDatabase JSON to load
     * @return ProfileDatabase
     */
    public static ProfileDatabase loadData(String path) {
        File file = new File(path);
        File historyFile = new File(path.replace(".json","History.json"));

        //if it's a new external file then this history file will not exist so maybe we should try to create it?
        // This fixes the FileNotFoundError but not really sure, I'll hit you up about this Jack
        try {
            historyFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ProfileDatabase profileDb = new ProfileDatabase();
        profileDb.setPath(path);

        try {
            history = fileToString(historyFile);
            Gson gson = new Gson();

            return gson.fromJson(
                    ProfileDataIO.fileToString(file),
                    ProfileDatabase.class
            );

        } catch (Exception e) {
            e.printStackTrace();
        }

        return profileDb;
    }

    public static String getHistory() { return history;}

}
