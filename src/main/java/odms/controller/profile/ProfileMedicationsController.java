package odms.controller.profile;

import java.util.HashMap;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import odms.controller.AlertController;
import odms.controller.CommonController;
import odms.controller.data.MedicationDataIO;
import odms.controller.database.DAOFactory;
import odms.controller.database.MedicationInteractionsDAO;
import odms.controller.history.HistoryController;
import odms.model.history.History;
import odms.model.medications.Drug;
import odms.model.medications.Interaction;
import odms.model.profile.Profile;
import odms.view.profile.ProfileMedicationsView;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;

import static odms.controller.data.MedicationDataIO.getActiveIngredients;

public class ProfileMedicationsController extends CommonController {

    ProfileMedicationsView view;
    private MedicationInteractionsDAO cache;

    public ProfileMedicationsController(ProfileMedicationsView profileMedicationsView) {
        view = profileMedicationsView;
        cache = DAOFactory.getMedicalInteractionsDao();
        cache.load();

    }

    /**
     * adds a drug to the list of current medications a donor is on.
     *
     */
    public void addDrug(Drug drug) {
        Profile profile = view.getCurrentProfile();
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
        //todo improve generateUpdateInfo
        ProfileGeneralControllerTODOContainsOldProfileMethods
                .generateUpdateInfo(drug.getDrugName(), profile);
        //todo maybe "profile" needs to be changed to "Profile"
        History history = new History("profile", profile.getId(), "added drug",
                drug.getDrugName(), Integer.parseInt(
                data.substring(data.indexOf("index of") + 9, data.indexOf(" at"))),
                LocalDateTime.now());
        HistoryController.updateHistory(history);
    }

    /**
     * deletes a drug from the list of current medications if it was added by accident.
     *
     */
    public void deleteDrug() {
        Drug drug = getSelectedDrug();
        LocalDateTime currentTime = LocalDateTime.now();
        Profile profile = view.getCurrentProfile();
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
    public void moveDrugToHistory(Drug drug) {
        LocalDateTime currentTime = LocalDateTime.now();
        Profile profile = view.getCurrentProfile();
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
    public void moveDrugToCurrent(Drug drug) {
        Profile profile = view.getCurrentProfile();
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
    public ArrayList<String> viewActiveIngredients() throws IOException {
        Drug drug = getSelectedDrug();
        ArrayList<String> activeIngredients;
        activeIngredients = getActiveIngredients(drug.getDrugName());
        return activeIngredients;

    }

    public Drug getSelectedDrug() {
        Drug drug = view.getSelectedHistoricDrug();
        if (drug == null) {
            drug = view.getSelectedCurrentDrug();
        }
        if (drug == null) {
            return null;
        }
        return drug;
    }

    /**
     * Converts ObservableList of drugs to ArrayList of drugs.
     *
     * @param drugs ObservableList of drugs.
     * @return ArrayList of drugs.
     */
    public ArrayList<Drug> convertObservableToArray(ObservableList<Drug> drugs) {
        ArrayList<Drug> toReturn = new ArrayList<>();
        for (Drug drug : drugs) {
            if (drug != null) {
                toReturn.add(drug);
            }
        }
        return toReturn;
    }

    /**
     * Button handler to get and display drug interactions on TableView
     * tableViewDrugInteractionsName and tableViewDrugInteractions.
     *
     */
    private ArrayList<Drug> getDrugsList() {
        ArrayList<Drug> drugs;
        if (convertObservableToArray(
                view.getSelectedCurrentDrugs()).size() == 2) {
            drugs = convertObservableToArray(
                    view.getSelectedCurrentDrugs());
        } else {
            if (view.getSelectedHistoricDrugs().size() == 2) {
                drugs = convertObservableToArray(
                        view.getSelectedHistoricDrugs());
            } else {
                drugs = convertObservableToArray(
                        view.getSelectedCurrentDrugs());
                drugs.add(view.getSelectedHistoricDrug());
            }
        }
        return drugs;
    }

    public Map<String, String> getRawInteractions() throws IOException {
        Map<String, String> interactionsRaw = new HashMap<>();
        Profile currentProfile = view.getCurrentProfile();
        ArrayList<Drug> drugs = getDrugsList();

        Interaction interaction = cache.get(drugs.get(0).getDrugName(), drugs.get(1).getDrugName());

        if (!(interaction == null)) {
            interactionsRaw = MedicationDataIO.getDrugInteractions(interaction, currentProfile.getGender(),
                    currentProfile.getAge());
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


    public ObservableList<String> getObservableDrugsList() {
        ArrayList<Drug> drugs = getDrugsList();
        ObservableList<String> drugsList = FXCollections.observableArrayList();
        drugsList.add("Interactions between:");
        drugsList.add(drugs.get(0).getDrugName());
        drugsList.add(drugs.get(1).getDrugName());
        return drugsList;
    }

    /**
     * Button handler to remove medications from the current medications and move them to historic.
     *
     */
    @FXML
    public void moveToHistory() {
        Profile currentProfile = view.getCurrentProfile();
        ArrayList<Drug> drugs = convertObservableToArray(
                view.getSelectedCurrentDrugs());

        for (Drug drug : drugs) {
            if (drug != null) {
                moveDrugToHistory(drug);
                String data = currentProfile.getMedicationTimestamps()
                        .get(currentProfile.getMedicationTimestamps().size() - 1);
                History history = new History("profile", currentProfile.getId(),
                        "stopped", drug.getDrugName(),
                        Integer.parseInt(data.substring(data.indexOf("index of") + 9,
                                data.indexOf(" at"))), LocalDateTime.now());
                HistoryController.updateHistory(history);
            }
        }
    }

    /**
     * Button handler to remove medications from the historic list and add them back to the current
     * list of drugs.
     *
     */
    @FXML
    public void moveToCurrent() {
        Profile currentProfile = view.getCurrentProfile();
        ArrayList<Drug> drugs = convertObservableToArray(
                view.getSelectedHistoricDrugs());

        for (Drug drug : drugs) {
            if (drug != null) {
                moveDrugToCurrent(drug);
                String data = currentProfile.getMedicationTimestamps()
                        .get(currentProfile.getMedicationTimestamps().size() - 1);
                History history = new History("profile", currentProfile.getId(),
                        "started", drug.getDrugName(), Integer.parseInt(data.substring(
                                data.indexOf("index of") + 9,
                                data.indexOf(" again"))), LocalDateTime.now());
                HistoryController.updateHistory(history);
            }
        }
    }
}
