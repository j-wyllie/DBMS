package odms.controller.profile;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.concurrent.Task;
import odms.controller.database.MySqlProfileDAO;
import odms.model.profile.Profile;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class ProfileImportTask extends Task<Void> {

    private File file;
    private MySqlProfileDAO mySqlProfileDAO = new MySqlProfileDAO();

    public ProfileImportTask(File file) {
        this.file = file;
    }

    @Override
    protected Void call() throws InvalidFileException {
        try {
            loadDataFromCSV(this.file);
        } catch (InvalidFileException e) {
            throw new InvalidFileException(e.getMessage(), e.getFile());
        }

        return null;
    }

    /**
     * Load the specified csv file instantiating a ProfileDatabase Object.
     *
     * @param csv the csv file that is being loaded
     */
    private void loadDataFromCSV(File csv) throws InvalidFileException {
        try {
            CSVParser csvParser = CSVFormat.DEFAULT.withHeader().parse(new FileReader(csv));
            Integer csvLength = CSVFormat.DEFAULT.withHeader().parse(new FileReader(csv))
                    .getRecords().size();

            parseCsvRecord(csvParser, csvLength);
        } catch (IOException | IllegalArgumentException e) {
            throw new InvalidFileException("CSV file could not be read.", csv);
        }
    }

    /**
     * Loops through the csv records and adds it to the profileDB if it is valid. Updates the counts
     * of successful and failed imports.
     *
     * @param csvParser the csv parser to parse each row.
     * @param csvLength the length of the csv.
     * @return boolean - success or fail
     */
    private void parseCsvRecord(CSVParser csvParser,
            Integer csvLength) {
        int progressCount = 0;
        int successCount = 0;
        int failedCount = 0;
        for (CSVRecord csvRecord : csvParser) {
            if (Thread.currentThread().isInterrupted()) {
                return;
            }

            Profile profile = csvToProfileConverter(csvRecord);
            if (profile != null) {
                try {
                    mySqlProfileDAO.add(profile);
                    successCount++;
                } catch (SQLException e) {
                    failedCount++;
                }
            } else {
                failedCount++;
            }

            progressCount++;
            this.updateProgress(progressCount, csvLength);
            this.updateMessage(successCount + "," + failedCount + "," + progressCount);
        }
    }

    /**
     * Converts a record in the csv to a profile object
     *
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

                Profile profile = new Profile(csvRecord.get("first_names"),
                        csvRecord.get("last_names"), dob, csvRecord.get("nhi"));

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
     *
     * @param nhi the nhi to check
     * @return true if valid and false if not valid
     */
    private boolean isValidNHI(String nhi) {
        String pattern = "^[A-HJ-NP-Z]{3}\\d{4}$";
        Pattern r = Pattern.compile(pattern);

        Matcher m = r.matcher(nhi);
        return m.find();
    }
}
