package odms.controller.profile;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;
import lombok.extern.slf4j.Slf4j;
import odms.commons.model.enums.CountriesEnum;
import odms.commons.model.profile.Profile;
import odms.controller.database.DAOFactory;
import odms.controller.database.profile.ProfileDAO;
import odms.data.NHIConflictException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

/**
 * Task to import parse a csv file to a user object.
 */
@Slf4j
public class ProfileImportTask extends Task<Void> {

    private static final int VALID_DOD_LENGTH = 3;
    private static final String DATE_SPLITTER = "/";

    private BooleanProperty finished = new SimpleBooleanProperty();
    private BooleanProperty reverted = new SimpleBooleanProperty();

    private File file;
    private ProfileDAO server = DAOFactory.getProfileDao();
    private List<Profile> successfulProfiles = new ArrayList<>();
    private int progressCount;
    private int successCount;
    private int failedCount;
    private Integer csvLength;
    private boolean rollback;
    private boolean cancelled;


    /**
     * Gives a CSV file to the profile import task.
     *
     * @param file CSV file to be parsed.
     */
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
     * @param csv the csv file that is being loaded.
     * @throws InvalidFileException thrown when the CSV file could not be read.
     */
    private void loadDataFromCSV(File csv) throws InvalidFileException {
        try {
            CSVParser csvParser = CSVFormat.DEFAULT.withHeader().parse(new FileReader(csv));
            csvLength = CSVFormat.DEFAULT.withHeader().parse(new FileReader(csv))
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
     */
    private void parseCsvRecord(CSVParser csvParser, Integer csvLength) {
        while (!cancelled) {

            if (!finished.getValue()) {
                for (CSVRecord csvRecord : csvParser) {
                    if (Thread.currentThread().isInterrupted()) {
                        return;
                    } else if (rollback) {
                        removeProfiles();
                        Thread.currentThread().interrupt();
                    } else {

                        addProfile(csvRecord);
                        updateProgressBar(csvLength);
                    }
                }
                finished.setValue(true);
            }
        }
        removeProfiles();
    }

    /**
     * Tries to add a profile to the database. Updates the failed count, success count and progress
     * count.
     *
     * @param csvRecord The current csv record.
     */
    private void addProfile(CSVRecord csvRecord) {
        Profile profile = csvToProfileConverter(csvRecord);
        if (profile != null) {
            try {
                server.add(profile);
                successCount++;
                successfulProfiles.add(profile);
            } catch (SQLException | NHIConflictException e) {
                failedCount++;
            }
        } else {
            failedCount++;
        }
        progressCount++;
    }

    /**
     * Converts a record in the csv to a profile object.
     *
     * @param csvRecord the record to be converted.
     * @return the profile object.
     */
    private Profile csvToProfileConverter(CSVRecord csvRecord) {
        String nhi = csvRecord.get("nhi");
        if (isValidNHI(nhi)) {
            try {
                String[] dobString = csvRecord.get("date_of_birth").split(DATE_SPLITTER);
                LocalDate dob = LocalDate.of(
                        Integer.valueOf(dobString[2]),
                        Integer.valueOf(dobString[0]),
                        Integer.valueOf(dobString[1])
                );

                Profile profile = createProfile(csvRecord, nhi, dob);
                if (profile == null) {
                    return null;
                }

                return profile;
            } catch (IndexOutOfBoundsException | IllegalArgumentException e) {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * Sets all of the profile attributes from the CSV.
     *
     * @param csvRecord Current CSV record.
     * @param nhi Profiles NHI.
     * @param dob Profiles Date of Birth.
     * @return The created profile.
     */
    private Profile createProfile(CSVRecord csvRecord, String nhi, LocalDate dob) {
        Profile profile = new Profile(csvRecord.get("first_names"),
                csvRecord.get("last_names"), dob, nhi);

        String dateOfDeath = csvRecord.get("date_of_death");
        if (!dateOfDeath.isEmpty()) {
            String[] dodString = dateOfDeath.split(DATE_SPLITTER);

            // If the dod is invalid then don't upload
            if (dodString.length != VALID_DOD_LENGTH) {
                return null;
            }

            LocalDateTime dod = LocalDateTime.of(
                    Integer.valueOf(dodString[2]),
                    Integer.valueOf(dodString[0]),
                    Integer.valueOf(dodString[1]), 0, 0
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
        profile.setCountry(CountriesEnum.getEnumByString(csvRecord.get("country")));
        profile.setBirthCountry(CountriesEnum.getEnumByString(csvRecord.get("birth_country")));
        profile.setPhone(csvRecord.get("home_number"));
        profile.setMobilePhone(csvRecord.get("mobile_number"));
        profile.setEmail(csvRecord.get("email"));
        return profile;
    }

    /**
     * Checks if the nhi is valid (3 characters (no O or I) followed by 4 numbers).
     *
     * @param nhi the nhi to check.
     * @return true if valid and false if not valid.
     */
    private boolean isValidNHI(String nhi) {
        String pattern = "^[A-HJ-NP-Z]{3}\\d{4}$";
        Pattern r = Pattern.compile(pattern);

        Matcher m = r.matcher(nhi);
        return m.find();
    }

    /**
     * Sets the rollback boolean value to true.
     */
    public void rollback() {
        rollback = true;
    }

    /**
     * Removes the profiles from the database and counts down the progress bar.
     */
    private void removeProfiles() {
        for (Profile profile : successfulProfiles) {
            if (Thread.currentThread().isInterrupted()) {
                return;
            }

            server.removeByNhi(profile);
            successCount--;
            progressCount--;

            updateProgressBar(csvLength);
        }

        while (failedCount != 0) {
            long now = System.currentTimeMillis();
            long waitTime = System.currentTimeMillis() + 1;
            progressCount--;
            failedCount--;
            while (now < waitTime) {
                now = System.currentTimeMillis();
            }

            updateProgressBar(csvLength);
        }
        reverted.setValue(true);
        Thread.currentThread().interrupt();
    }

    public void setCancelled() {
        cancelled = true;
    }

    /**
     * Updates the progress bar.
     *
     * @param csvLength CSV Length.
     */
    private void updateProgressBar(Integer csvLength) {
        this.updateProgress(progressCount, csvLength);
        this.updateMessage(
                String.format("%d,%d,%d", successCount, failedCount,
                        progressCount));
    }

    public BooleanProperty getFinished() {
        return finished;
    }

    public BooleanProperty getReverted() {
        return reverted;
    }
}


