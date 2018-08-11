package odms.controller.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import odms.controller.history.CurrentHistory;
import odms.data.ProfileDatabase;

public class ProfileDataIO extends CommonDataIO {

    private static final String DEFAULT_PATH = "example/example.json";
    private static String history = "";
    private static int lastPosition = 1;

    /**
     * Export full profile Database object to the previously used path.
     *
     * @param profileDb Database to be exported to JSON
     */
    public static void saveData(ProfileDatabase profileDb) {
        if (profileDb.getPath() == null) {
            profileDb.setPath(DEFAULT_PATH);
        }
        saveData(profileDb, profileDb.getPath());
    }

    /**
     * Export full profile Database object to specified file.
     *
     * @param profileDb Database to be exported to JSON
     * @param path      target path
     */
    public static void saveData(ProfileDatabase profileDb, String path) {
        profileDb.setPath(path);
        File file = new File(path);
        File historyFile = new File(path.replace(".json", "history.json"));

        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            BufferedWriter writeFile = new BufferedWriter(new FileWriter(file));
            BufferedWriter writeHistoryFile = new BufferedWriter(new FileWriter(historyFile));
            writeFile.write(gson.toJson(profileDb));
            writeFile.close();
            if (history.equals("")) {
                history = gson.toJson(CurrentHistory.getHistory());
            } else if (CurrentHistory.getHistory().get(CurrentHistory.getPosition())
                    != null) {
                history = history.substring(0, history.length() - 1);

                for (int i = lastPosition; i < CurrentHistory.getPosition(); i++) {
                    history = history + "," + gson
                            .toJson(CurrentHistory.getHistory().get(i).toString());
                }
                lastPosition = CurrentHistory.getPosition();
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
    public static ProfileDatabase loadDataFromJSON(String path) {
        File file = new File(path);
        File historyFile = new File(path.replace(".json", "history.json"));

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

    public static String getHistory() {
        return history;
    }

}
