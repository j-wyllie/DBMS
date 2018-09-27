package odms.controller.profile;

import static odms.controller.data.MedicationDataIO.getActiveIngredients;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.fxml.FXML;
import odms.commons.model.history.History;
import odms.commons.model.medications.Drug;
import odms.commons.model.medications.Interaction;
import odms.commons.model.profile.Profile;
import odms.controller.AlertController;
import odms.controller.CommonController;
import odms.controller.data.MedicationDataIO;
import odms.controller.database.DAOFactory;
import odms.controller.database.interactions.MedicationInteractionsDAO;
import odms.controller.database.medication.MedicationDAO;
import odms.controller.history.CurrentHistory;

public class Medications extends CommonController {

    private MedicationInteractionsDAO cache;
    private List<Drug> deletedDrugs;

    /**
     * Constructor for ProfileMedicationsController class. Takes a view as a param since the
     * controller and view classes are linked.
     */
    public Medications() {
        cache = DAOFactory.getMedicalInteractionsDao();
        cache.load();
        deletedDrugs = new ArrayList<>();
    }

    /**
     * Refreshes the current and historic drug lists for the current profile.
     */
    private void getDrugs(Profile profile) {
       profile.setCurrentMedications(DAOFactory.getMedicationDao().getAll(profile, true));
       profile.setHistoryOfMedication(DAOFactory.getMedicationDao().getAll(profile, false));
    }

    /**
     * Saves the current and past medications for a profile.
     */
    public void saveDrugs(Profile profile) {
        MedicationDAO medicationDAO = DAOFactory.getMedicationDao();
        for (Drug drug : profile.getCurrentMedications()) {
            if (drug.getId() == null) {
                medicationDAO.add(drug, profile, true);
            } else {
                medicationDAO.update(drug, true);
            }
        }
        for (Drug drug : profile.getHistoryOfMedication()) {
            if (drug.getId() == null) {
                medicationDAO.add(drug, profile, false);
            } else {
                medicationDAO.update(drug, false);
            }
        }
        for (Drug drug : deletedDrugs) {
            if (drug.getId() != null) {
                medicationDAO.remove(drug);
            }
        }
        getDrugs(profile);
    }

    /**
     * adds a drug to the list of current medications a donor is on.
     *
     */
    public void addDrug(Drug drug, Profile p) {
        Profile profile = p;
        LocalDateTime currentTime = LocalDateTime.now();
        profile.getCurrentMedications().add(drug);
        String data = "profile " +
                profile.getId() +
                " added drug " +
                drug.getDrugName() +
                " index of " +
                profile.getCurrentMedications().indexOf(drug) +
                " at " +
                currentTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        profile.getMedicationTimestamps().add(data);
        ProfileGeneralControllerTODOContainsOldProfileMethods
                .generateUpdateInfo(drug.getDrugName(), profile);
        History history = new History("profile", profile.getId(), "added drug",
                drug.getDrugName(), Integer.parseInt(
                data.substring(data.indexOf("index of") + 9, data.indexOf(" at"))),
                LocalDateTime.now());
        CurrentHistory.updateHistory(history);
    }

    /**
     * deletes a drug from the list of current medications if it was added by accident.
     *
     */
    public void deleteDrug(Profile p, Drug drug) {
        LocalDateTime currentTime = LocalDateTime.now();
        Profile profile = p;
        deletedDrugs.add(drug);
        String data = "profile " +
                profile.getId() +
                " removed drug " +
                drug.getDrugName() +
                " index of " +
                profile.getCurrentMedications().indexOf(drug) +
                " at " +
                currentTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        if (profile.getCurrentMedications().contains(drug)) {
            profile.getCurrentMedications().remove(drug);
            profile.getMedicationTimestamps().add(data);
            //todo improve generateUpdateInfo
            ProfileGeneralControllerTODOContainsOldProfileMethods
                    .generateUpdateInfo(drug.getDrugName(), profile);
        } else if (profile.getHistoryOfMedication().contains(drug)) {
            profile.getHistoryOfMedication().remove(drug);
            data = "profile " +
                    profile.getId() +
                    " removed drug from history" +
                    " index of " +
                    profile.getCurrentMedications().indexOf(drug) +
                    " at " +
                    currentTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

            profile.getMedicationTimestamps().add(data);
        }
    }

    /**
     * Moves the drug to the history of drugs the donor has taken.
     *
     * @param drug the drug to be moved to the history
     */
    public void moveDrugToHistory(Drug drug, Profile profile) {
        LocalDateTime currentTime = LocalDateTime.now();
        if (profile.getCurrentMedications().contains(drug)) {
            profile.getCurrentMedications().remove(drug);
            profile.getHistoryOfMedication().add(drug);
            String data = "profile " +
                    profile.getId() +
                    " stopped " +
                    drug.getDrugName() +
                    " index of " +
                    profile.getHistoryOfMedication().indexOf(drug) +
                    " at " +
                    currentTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

            profile.getMedicationTimestamps().add(data);
            //todo improve generateUpdateInfo
            ProfileGeneralControllerTODOContainsOldProfileMethods
                    .generateUpdateInfo(drug.getDrugName(), profile);
        }

    }

    /**
     * Moves the drug to the list of current drugs the donor is taking.
     *
     * @param drug the drug to be moved to the current drug list
     */
    public void moveDrugToCurrent(Drug drug, Profile p) {
        Profile profile = p;
        LocalDateTime currentTime = LocalDateTime.now();
        if (profile.getHistoryOfMedication().contains(drug)) {
            profile.getHistoryOfMedication().remove(drug);
            profile.getCurrentMedications().add(drug);
            String data = "profile " +
                    profile.getId() +
                    " started using " +
                    drug.getDrugName() +
                    " index of " +
                    profile.getCurrentMedications().indexOf(drug) +
                    " again at " +
                    currentTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

            profile.getMedicationTimestamps().add(data);
            //todo improve generateUpdateInfo
            ProfileGeneralControllerTODOContainsOldProfileMethods
                    .generateUpdateInfo(drug.getDrugName(), profile);
        }

    }

    /**
     * Button handler to view a drugs active ingredients
     *
     */
    @FXML
    public List<String> viewActiveIngredients(Drug drug) throws IOException {
        List<String> activeIngredients;
        activeIngredients = getActiveIngredients(drug.getDrugName());
        return activeIngredients;

    }

    public Map<String, String> getRawInteractions(Profile p, ArrayList<Drug> drugs) throws IOException {
        Map<String, String> interactionsRaw = new HashMap<>();
        Profile currentProfile = p;
        Interaction interaction = cache.get(drugs.get(0).getDrugName(), drugs.get(1).getDrugName());

        if (interaction != null) {
            interactionsRaw = MedicationDataIO.getDrugInteractions(interaction, currentProfile.getGender(),
                    currentProfile.getAge());
        } else {
            interactionsRaw.put("error", null);
        }
        return interactionsRaw;
    }

    /**
     * Clears the cache and handles the messages.
     */
    public void clearCache() {
        if (AlertController.generalConfirmation("Are you sure you want to clear the cache?"
                + "\nThis cannot be undone.")) {
            cache.clear();
            if (!cache.save()) {
                AlertController.guiPopup("There was an error saving the cache.\nThe cache has been "
                        + "cleared, but could not be saved.");
            }
        }
    }

    /**
     * Button handler to remove medications from the current medications and move them to historic.
     *
     */
    public void moveToHistory(Profile currentProfile, ArrayList<Drug> drugs) {
        for (Drug drug : drugs) {
            if (drug != null) {
                moveDrugToHistory(drug, currentProfile);
                String data = currentProfile.getMedicationTimestamps()
                        .get(currentProfile.getMedicationTimestamps().size() - 1);
                History history = new History("profile", currentProfile.getId(),
                        "stopped", drug.getDrugName(),
                        Integer.parseInt(data.substring(data.indexOf("index of") + 9,
                                data.indexOf(" at"))), LocalDateTime.now());
//                CurrentHistory.updateHistory(history);
                //TODO: update history it breaks.
            }
        }
    }

    /**
     * Button handler to remove medications from the historic list and add them back to the current
     * list of drugs.
     *
     */
    public void moveToCurrent(Profile currentProfile,ArrayList<Drug> drugs) {
        for (Drug drug : drugs) {
            if (drug != null) {
                moveDrugToCurrent(drug, currentProfile);
                String data = currentProfile.getMedicationTimestamps()
                        .get(currentProfile.getMedicationTimestamps().size() - 1);
                History history = new History("profile", currentProfile.getId(),
                        "started", drug.getDrugName(), Integer.parseInt(data.substring(
                                data.indexOf("index of") + 9,
                                data.indexOf(" again"))), LocalDateTime.now());
                CurrentHistory.updateHistory(history);
            }
        }
    }
}
