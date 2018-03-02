package seng302;

// TODO All IO sort of stuff here

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class DataWarehouse {

    public static void saveData() {

    }

    private void saveClients() {

    }

    private void saveClientsHistory() {

    }

    public void saveClient() {

    }

    /**
     * Reads a file from the provided filename or path, converts to string.
     *
     * @param file filename or path
     * @return String of contents of provided file
     */
    private static String fileToString(String file) {
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

    public void loadData(String path, ClientDatabase clientDB) {
        ArrayList<Client> clientsBuffer = new ArrayList<>();

        try {
            Gson gson = new Gson();

            String testString = DataWarehouse.fileToString(path);

            ClientDatabase test = gson.fromJson(testString, ClientDatabase.class);

            for (Client client : test.clients) {
                System.out.println(client.client + " " + client.id);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

        public static void main(String[] args) {

        DataWarehouse testDW = new DataWarehouse();
        ClientDatabase testCD = new ClientDatabase();

        // testing style to be determined
        System.out.println("test main");

        String testPath = "example.json";

        // test loading
        System.out.println("Test path: " + testPath);
        testDW.loadData(testPath, testCD);

    }

}
