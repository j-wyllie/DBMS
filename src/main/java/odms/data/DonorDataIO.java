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

            System.out.println("File " + file.getName() + " exported successfully!");

        } catch (IOException e) {
            System.out.println("IO exception, please check the specified " + path);
        }
    }

    /**
     * Reads a file from the provided filename or path, converts to string.
     *
     * @param file filename or path
     * @return String of contents of provided file
     */
    private static String fileToString(File file) throws FileNotFoundException, IOException {
        String lineBuffer;

        StringBuilder fileBuffer = new StringBuilder();
        BufferedReader readFile = new BufferedReader(new FileReader(file));

        while ((lineBuffer = readFile.readLine()) != null) {
            fileBuffer.append(lineBuffer);
        }

        readFile.close();

        return fileBuffer.toString();
    }

    /**
     * Load the specified DonorDatabase JSON file instantiating a DonorDatabase Object.
     *
     * @param filepath specified DonorDatabase JSON to load
     * @return DonorDatabase
     */
    public static DonorDatabase loadData(String filepath) {
        File file = new File(filepath);
        DonorDatabase donorDb = new DonorDatabase();

        try {
            Gson gson = new Gson();

            donorDb = gson.fromJson(
                DonorDataIO.fileToString(file),
                DonorDatabase.class
            );
            System.out.println("File " + filepath + " imported successfully!");
        } catch (FileNotFoundException e) {
            System.out.println("File '" + file + "' not found");
        } catch (IOException e) {
            System.out.println("IO exception, please check " + file);
        }

        return donorDb;
    }
}
