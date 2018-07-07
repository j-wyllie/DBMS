package odms.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import odms.controller.HistoryController;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import odms.profile.Profile;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class ProfileDataIO extends CommonDataIO {

    private static final String defaultPath = "example/example.json";
    private static String history = "";
    private static int lastPosition = 1;

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
        File historyFile = new File(path.replace(".json","history.json"));

        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            BufferedWriter writeFile = new BufferedWriter(new FileWriter(file));
            BufferedWriter writeHistoryFile = new BufferedWriter(new FileWriter(historyFile));
            writeFile.write(gson.toJson(profileDb));
            writeFile.close();
            if(history.equals("")) {
                history = gson.toJson(HistoryController.getHistory());
            } else if (HistoryController.getHistory().get(HistoryController.getPosition()) != null){
                history = history.substring(0, history.length()-1);

                for (int i = lastPosition; i < HistoryController.getPosition(); i++) {
                    history = history+"," + gson.toJson(HistoryController.getHistory().get(i).toString());
                }
                lastPosition = HistoryController.getPosition();
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

    public static ProfileDatabase loadDataFromCSV(File csv) throws InvalidFileException {
        ProfileDatabase profileDb = new ProfileDatabase();

        try {
            CSVParser csvParser = CSVFormat.EXCEL.withHeader().parse(new FileReader(csv));
            for (CSVRecord csvRecord : csvParser) {
                Profile profile = csvToProfileConverter(csvRecord);
                if (profile != null) {
                    profileDb.addProfile(profile);
                }
            }
        } catch (IOException e) {
            throw new InvalidFileException("CSV file could not be read.", csv);
        } catch (NHIConflictException e) {
            e.printStackTrace();
        }

        return profileDb;
    }

    private static Profile csvToProfileConverter(CSVRecord csvRecord) {
        String[] dobString = csvRecord.get("date_of_birth").split("/");
        LocalDate dob = LocalDate.of(
                Integer.valueOf(dobString[2]),
                Integer.valueOf(dobString[0]),
                Integer.valueOf(dobString[1])
        );

        if (isValidNHI(csvRecord.get("nhi"))) {
            Profile profile = new Profile(csvRecord.get("first_names"), csvRecord.get("last_names"), dob, csvRecord.get("nhi"));


            if (!csvRecord.get("date_of_death").isEmpty()) {
                String[] dodString = csvRecord.get("date_of_death").split("/");
                LocalDate dod = LocalDate.of(
                        Integer.valueOf(dodString[2]),
                        Integer.valueOf(dodString[0]),
                        Integer.valueOf(dodString[1])
                );
                profile.setDateOfDeath(dod);
            }

            profile.setGender(csvRecord.get("birth_gender"));
            profile.setPreferredGender(csvRecord.get("gender"));
            profile.setBloodType(csvRecord.get("blood_type"));
            profile.setHeight(Double.valueOf(csvRecord.get("height")));
            profile.setWeight(Double.valueOf(csvRecord.get("weight")));
            profile.setStreetNumber(Integer.valueOf(csvRecord.get("street_number")));
            profile.setStreetName(csvRecord.get("street_name"));
            profile.setNeighbourhood(csvRecord.get("neighborhood"));
            profile.setCity(csvRecord.get("city"));
            profile.setRegion(csvRecord.get("region"));
            profile.setZipCode(csvRecord.get("zip_code"));
            profile.setCountry(csvRecord.get("country"));
            profile.setBirthCountry(csvRecord.get("birth_country"));
            profile.setPhone(csvRecord.get("home_number"));
            profile.setMobilePhone(csvRecord.get("mobile_number"));
            profile.setEmail(csvRecord.get("email"));

            return profile;
        }
        return null;
    }

    private static boolean isValidNHI(String nhi) {
        String pattern = "^[A-HJ-NP-Z]{3}\\d{4}$";
        Pattern r = Pattern.compile(pattern);

        Matcher m = r.matcher(nhi);
        return m.find();
    }


}
