package server.model.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class UserDataIO extends CommonDataIO {

    private static final String DEFAULT_PATH = "example/user.json";

    /**
     * Export full user Database object to the previously used path.
     *
     * @param userDb Database to be exported to JSON
     */
    public static void saveUsers(UserDatabase userDb) {
        if (userDb.getPath() == null) {
            userDb.setPath(DEFAULT_PATH);
        }
        saveUsers(userDb, userDb.getPath());
    }

    /**
     * Export full user Database object to a specified file.
     *
     * @param userDb Database to be exported to JSON
     * @param path   The location of the saved file
     */
    public static void saveUsers(UserDatabase userDb, String path) {
        userDb.setPath(path);
        File file = new File(path);

        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            BufferedWriter writeFile = new BufferedWriter(new FileWriter(file));

            writeFile.write(gson.toJson(userDb));

            writeFile.close();

            System.out.println("File exported successfully!");

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

        loadDataInBuffer(file, fileBuffer);

        return fileBuffer.toString();
    }

    /**
     * Load the specified DonorDatabase JSON file instantiating a DonorDatabase Object.
     *
     * @param path The location of the saved file
     * @return UserDatabase
     */
    public static UserDatabase loadData(String path) {
        File file = new File(path);
        UserDatabase userDb = new UserDatabase();
        userDb.setPath(path);

        try {
            Gson gson = new Gson();

            return gson.fromJson(
                    UserDataIO.fileToString(file),
                    UserDatabase.class
            );

        } catch (Exception e) {
            e.printStackTrace();
        }

        return userDb;
    }
}
