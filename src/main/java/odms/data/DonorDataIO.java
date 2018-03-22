package odms.data;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import odms.commandlineview.CommandUtils;

public class DonorDataIO {
    private static String history = "";

    /**
     * Export full DonorDatabase object to specified JSON file.
     *
     * @param donorDb Database to be exported to JSON
     * @param path target path
     */
    public static void saveDonors(DonorDatabase donorDb, String path) {
        File file = new File(path);
        File historyFile = new File(path.replace(".json","History.json"));

        try {
            Gson gson = new Gson();
            BufferedWriter writeFile = new BufferedWriter(new FileWriter(file));
            BufferedWriter writeHistoryFile = new BufferedWriter(new FileWriter(historyFile));
            writeFile.write(gson.toJson(donorDb));
            writeFile.close();
            if(history.equals("")) {
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
        String lineBuffer;

        try {
            BufferedReader readFile = new BufferedReader(new FileReader(file));

            while ((lineBuffer = readFile.readLine()) != null) {
                fileBuffer.append(lineBuffer);
            }

            readFile.close();

        } catch (FileNotFoundException e) {
            System.out.println("File not found");
            System.out.println("File requested: " + file);
        } catch (IOException e) {
            System.out.println("IO exception, please check the specified file");
            System.out.println("File requested: " + file);
        }

        return fileBuffer.toString();

    }


    /**
     * Load the specified DonorDatabase JSON file instantiating a DonorDatabase Object.
     *
     * @param path specified DonorDatabase JSON to load
     * @return DonorDatabase
     */
    public static DonorDatabase loadData(String path) {
        File file = new File(path);
        File historyFile = new File(path.replace(".json","History.json"));
        DonorDatabase donorDb = new DonorDatabase();

        try {
            history = fileToString(historyFile);
            System.out.println(history);
            Gson gson = new Gson();

            return gson.fromJson(
                    DonorDataIO.fileToString(file),
                    DonorDatabase.class
            );

        } catch (Exception e) {
            e.printStackTrace();
        }

        return donorDb;
    }

    public static String getHistory() { return history;}

}
