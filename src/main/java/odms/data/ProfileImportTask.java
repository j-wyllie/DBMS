package odms.data;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.concurrent.Task;
import odms.profile.Profile;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class ProfileImportTask extends Task<Void> {

    private File file;
    private ProfileDatabase db;

    public ProfileImportTask(File file) {
        this.file = file;
    }

    @Override
    protected Void call() throws InvalidFileException {
        try {
            db = loadDataFromCSV(this.file);
        } catch (InvalidFileException e) {
            throw new InvalidFileException(e.getMessage(), e.getFile());
        }

        return null;
    }

    /**
     * Load the specified csv file instantiating a ProfileDatabase Object.
     *
     * @param csv the csv file that is being loaded
     * @return ProfileDatabase the updated profile database.
     */
    private ProfileDatabase loadDataFromCSV(File csv) throws InvalidFileException {
        ProfileDatabase profileDb = new ProfileDatabase();
        int progressCount = 0;
        int successCount = 0;
        int failedCount = 0;
        try {
            CSVParser csvParser = CSVFormat.DEFAULT.withHeader().parse(new FileReader(csv));
            Integer csvLength = CSVFormat.DEFAULT.withHeader().parse(new FileReader(csv)).getRecords().size();

            profileDb = parseCsvRecord(profileDb, progressCount, successCount, failedCount, csvParser,
                    csvLength);
        } catch (IOException | IllegalArgumentException e) {
            throw new InvalidFileException("CSV file could not be read.", csv);
        }
        return profileDb;
    }

    private ProfileDatabase parseCsvRecord(ProfileDatabase profileDb, int progressCount, int successCount,
            int failedCount, CSVParser csvParser, Integer csvLength) {
        for (CSVRecord csvRecord : csvParser) {
            Profile profile = csvToProfileConverter(csvRecord);
            if (profile != null) {
                try {
                    profileDb.addProfile(profile);
                    successCount++;

                } catch (NHIConflictException e) {
                    failedCount++;
                }
            } else {
                failedCount++;
            }

            progressCount++;
            this.updateProgress(progressCount, csvLength);
            this.updateMessage("Successful: " + successCount + "\nFailed: " + failedCount + "\nTotal Profiles: " + progressCount);
        }
        return profileDb;
    }

    /**
     * Converts a record in the csv to a profile object
     * @param csvRecord the record to be converted
     * @return the profile object
     */
    private Profile csvToProfileConverter(CSVRecord csvRecord) {
        if (isValidNHI(csvRecord.get("nhi"))) {
            try {
                String[] dobString = csvRecord.get("date_of_birth").split("/");
                LocalDate dob = LocalDate.of(
                        Integer.valueOf(dobString[2]),
                        Integer.valueOf(dobString[0]),
                        Integer.valueOf(dobString[1])
                );

                Profile profile = new Profile(csvRecord.get("first_names"), csvRecord.get("last_names"), dob, csvRecord.get("nhi"));

                if (!csvRecord.get("date_of_death").isEmpty()) {
                    String[] dodString = csvRecord.get("date_of_death").split("/");

                    // If the dod is invalid then don't upload
                    if (dodString.length != 3) {
                        return null;
                    }

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
                profile.setStreetNumber(csvRecord.get("street_number"));
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
            } catch (IndexOutOfBoundsException | IllegalArgumentException e) {
                return null;
            }
        } else {
            return null;
        }
    }


    /**
     * Checks if the nhi is valid (3 characters (no O or I) followed by 4 numbers)
     * public at the moment so we can test it, probably needs to be moved somewhere TODO
     * @param nhi the nhi to check
     * @return true if valid and false if not valid
     */
    protected boolean isValidNHI(String nhi) {
        String pattern = "^[A-HJ-NP-Z]{3}\\d{4}$";
        Pattern r = Pattern.compile(pattern);

        Matcher m = r.matcher(nhi);
        return m.find();
    }

    public ProfileDatabase getDb() {
        return db;
    }
}
