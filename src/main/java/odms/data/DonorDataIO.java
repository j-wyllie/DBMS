package odms.data;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class DonorDataIO {

    /**
     * Export full DonorDatabase object to specified JSON file.
     *
     * @param donorDb Database to be exported to JSON
     * @param path target path
     */
    public static void saveDonors(DonorDatabase donorDb, String path) {
        File file = new File(path);

        try {
            Gson gson = new Gson();
            BufferedWriter writeFile = new BufferedWriter(new FileWriter(file));

            writeFile.write(gson.toJson(donorDb));

            writeFile.close();

            System.out.println("File " + file.getName() + " saved successfully!");

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

        // TODO investigate better way to handle db creation.
        DonorDatabase donorDb = new DonorDatabase();

        try {
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

}
