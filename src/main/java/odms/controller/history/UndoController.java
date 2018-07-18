package odms.controller.history;

import odms.model.data.ProfileDatabase;
import odms.model.enums.OrganEnum;
import odms.model.history.History;
import odms.model.medications.Drug;
import odms.model.profile.Condition;
import odms.model.profile.Profile;
import odms.model.user.User;
import odms.view.LoginView;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class UndoController extends UndoRedoController {

    private static ArrayList<Profile> unaddedProfiles = new ArrayList<>();
    private static int historyPosition;
    private static ArrayList<History> currentSessionHistory;

    /**
     * Performs logic for undoes
     *
     * @param currentDatabase
     */
    public void undo(ProfileDatabase currentDatabase) {
        //todo change the methods to controller functions
        historyPosition = HistoryController.getPosition();
        currentSessionHistory = HistoryController.getHistory();
        try {
            History action = currentSessionHistory.get(historyPosition);
            if (action != null) {
                redirect(currentDatabase, action);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("No commands have been entered");
        }
    }

    /**
     * Undoes organs being donated
     *
     * @param currentDatabase
     * @param action
     */
    public void addedDonated(ProfileDatabase currentDatabase, History action) {
        Profile profile = currentDatabase.getProfile(action.getHistoryId());
        String organ = action.getHistoryData();
        profile.removeOrganDonated(OrganEnum.valueOf(organ));
    }

    /**
     * Undoes organs being received
     *
     * @param currentDatabase
     * @param action
     */
    public void addedReceived(ProfileDatabase currentDatabase, History action) {
        Profile profile = currentDatabase.getProfile(action.getHistoryId());
        String organ = action.getHistoryData();
        profile.removeOrganReceived(OrganEnum.valueOf(organ));
    }

    /**
     * Undoes removed conditions
     *
     * @param currentDatabase
     * @param action
     */
    public void removedCondition(ProfileDatabase currentDatabase, History action) {
        Profile profile = currentDatabase.getProfile(action.getHistoryId());
        String s = action.getHistoryData();
        String[] values = s.split(",");
        String diagDate = values[1].substring(8) + "-" + values[1].substring(5, 7) + "-" + values[1]
                .substring(0, 4);
        String cureDate = null;
        System.out.println(diagDate);
        if (!values[3].equals("null")) {
            cureDate = values[3].substring(8) + "-" + values[3].substring(5, 7) + "-" + values[3]
                    .substring(0, 4);
        }
        Condition condition = new Condition(values[0], diagDate, cureDate,
                Boolean.valueOf(values[2]));
        profile.addCondition(condition);
        LocalDateTime currentTime = LocalDateTime.now();
        History newAction = new History("Donor", profile.getId(), "removed condition",
                condition.getName() + "," + condition.getDateOfDiagnosis() + "," + condition
                        .getChronic() + "," +
                        condition.getDateCuredString(),
                profile.getCurrentConditions().indexOf(condition), currentTime);
        currentSessionHistory.set(historyPosition, newAction);
        if (historyPosition > 0) {
            historyPosition -= 1;
        }
        HistoryController.setPosition(historyPosition);
    }

    /**
     * Undoes added conditions
     *
     * @param currentDatabase
     * @param action
     */
    public void addCondition(ProfileDatabase currentDatabase, History action) {
        Profile profile = currentDatabase.getProfile(action.getHistoryId());
        int c = action.getHistoryDataIndex();
        Condition condition = profile.getCurrentConditions().get(c);
        profile.removeCondition(condition);
        if (historyPosition > 0) {
            historyPosition -= 1;
        }
        HistoryController.setPosition(historyPosition);
    }

    /**
     * Undoes a drug being moved to history
     *
     * @param currentDatabase
     * @param action
     */
    public void stopDrug(ProfileDatabase currentDatabase, History action)
            throws IndexOutOfBoundsException {
        try {
            Profile profile = currentDatabase.getProfile(action.getHistoryId());
            int d = action.getHistoryDataIndex();
            ArrayList<Drug> drugs = profile.getHistoryOfMedication();
            Drug drug = drugs.get(d);
            profile.moveDrugToCurrent(drug);
            LocalDateTime currentTime = LocalDateTime.now();
            History newAction = new History("profile", profile.getId(), "stopped",
                    drug.getDrugName(),
                    profile.getCurrentMedications().indexOf(drug), currentTime);
            HistoryController.currentSessionHistory.set(historyPosition, newAction);
            if (historyPosition > 0) {
                historyPosition -= 1;
            }
            HistoryController.setPosition(historyPosition);
        } catch (IndexOutOfBoundsException e) {

        }
    }

    /**
     * Undoes a drug being moved to current
     *
     * @param currentDatabase
     * @param action
     */
    public void renewDrug(ProfileDatabase currentDatabase, History action) {
        try {
            Profile profile = currentDatabase.getProfile(action.getHistoryId());
            int d = action.getHistoryDataIndex();
            ArrayList<Drug> drugs = profile.getCurrentMedications();
            Drug drug = drugs.get(d);
            profile.moveDrugToHistory(drug);
            LocalDateTime currentTime = LocalDateTime.now();
            History newAction = new History("profile", profile.getId(), "started",
                    drug.getDrugName(),
                    profile.getHistoryOfMedication().indexOf(drug), currentTime);
            HistoryController.currentSessionHistory.set(historyPosition, newAction);
            if (historyPosition > 0) {
                historyPosition -= 1;
            }
            HistoryController.setPosition(historyPosition);
        } catch (IndexOutOfBoundsException e) {

        }
    }

    /**
     * Undoes a drug being added
     *
     * @param currentDatabase
     * @param action
     */
    public void addDrug(ProfileDatabase currentDatabase, History action) {
        try {
            Profile profile = currentDatabase.getProfile(action.getHistoryId());
            int d = action.getHistoryDataIndex();
            ArrayList<Drug> drugs = profile.getCurrentMedications();
            profile.deleteDrug(drugs.get(d));
            if (historyPosition > 0) {
                historyPosition -= 1;
            }
            HistoryController.setPosition(historyPosition);
        } catch (IndexOutOfBoundsException e) {

        }
    }

    /**
     * Undoes a drug being deleted
     *
     * @param currentDatabase
     * @param action
     */
    public void deleteDrug(ProfileDatabase currentDatabase, History action)
            throws IndexOutOfBoundsException {
        try {
            Profile profile = currentDatabase.getProfile(action.getHistoryId());
            if (action.getHistoryAction().contains("history")) {
                String drug = action.getHistoryData();
                Drug d = new Drug(drug);
                profile.addDrug(d);
                profile.moveDrugToHistory(d);
            } else {
                String drug = action.getHistoryData();
                profile.addDrug(new Drug(drug));
            }
            if (historyPosition > 0) {
                historyPosition -= 1;
            }
            HistoryController.setPosition(historyPosition);
        } catch (IndexOutOfBoundsException e) {

        }
    }

    /**
     * Undoes an update to a clinician
     *
     * @param action
     */
    public void updated(History action) {
        User user = LoginView.getCurrentUser();
        String previous = action.getHistoryData()
                .substring(action.getHistoryData().indexOf("previous ") + 9,
                        action.getHistoryData().indexOf("new "));
        String[] previousValues = previous.split(",");
        user.setName(previousValues[1].replace("name=", ""));
        user.setStaffID(Integer.valueOf(previousValues[0].replace("staffId=", "").
                replace(" ", "")));
        user.setWorkAddress(previousValues[2].replace("workAddress=", ""));
        user.setRegion(previousValues[3].replace("region=", ""));
        if (historyPosition > 0) {
            historyPosition -= 1;
        }
        HistoryController.setPosition(historyPosition);
    }

    /**
     * Undoes a profile being added
     *
     * @param currentDatabase
     * @param action
     */
    public void added(ProfileDatabase currentDatabase, History action) {
        Profile profile = currentDatabase.getProfile(action.getHistoryId());
        currentDatabase.deleteProfile(action.getHistoryId());
        unaddedProfiles.add(profile);
        if (historyPosition > 0) {
            historyPosition -= 1;
        }
        HistoryController.setPosition(historyPosition);
    }

    /**
     * Undoes a profile being deleted
     *
     * @param currentDatabase
     * @param action
     */
    public void deleted(ProfileDatabase currentDatabase, History action) {
        int oldid = action.getHistoryId();
        int id = currentDatabase
                .restoreProfile(oldid, HistoryController.deletedProfiles
                        .get(HistoryController.deletedProfiles.size() - 1));
        HistoryController.deletedProfiles.remove(HistoryController.deletedProfiles
                .get(HistoryController.deletedProfiles.size() - 1));
        for (int i = 0; i < currentSessionHistory.size() - 1; i++) {
            if (currentSessionHistory.get(i).getHistoryId() == oldid) {
                currentSessionHistory.get(i).setHistoryId(id);
            }
        }
        if (historyPosition > 0) {
            historyPosition -= 1;
        }
        HistoryController.setPosition(historyPosition);
    }

    /**
     * Undoes an organ being removed from organs donating
     *
     * @param currentDatabase
     * @param action
     * @throws Exception
     */
    public void removed(ProfileDatabase currentDatabase, History action) throws Exception {
        //todo overhaul organ donating stuff
        Profile profile = currentDatabase.getProfile(action.getHistoryId());
        profile.addOrgansDonating(OrganEnum.stringListToOrganSet(
                Arrays.asList(action.getHistoryData().replace(' ', '_').split(","))));
        if (historyPosition > 0) {
            historyPosition -= 1;
        }
        HistoryController.setPosition(historyPosition);
    }

    /**
     * Undoes organs being set to donating
     *
     * @param currentDatabase
     * @param action
     */
    public void set(ProfileDatabase currentDatabase, History action) {
        Profile profile = currentDatabase.getProfile(action.getHistoryId());
        String[] stringOrgans = action.getHistoryData().replace(' ', '_').split(",");
        System.out.println(stringOrgans);
        Set<OrganEnum> organSet = OrganEnum.stringListToOrganSet(Arrays.asList(stringOrgans));
        profile.removeOrgansDonating(organSet);
        if (historyPosition > 0) {
            historyPosition -= 1;
        }

        HistoryController.setPosition(historyPosition);
    }

    /**
     * Undoes an organ being donated
     *
     * @param currentDatabase
     * @param action
     */
    public void donate(ProfileDatabase currentDatabase, History action) {
        Profile profile = currentDatabase.getProfile(action.getHistoryId());
        String[] stringOrgans = action.getHistoryData().replace(' ', '_').split(",");
        Set<OrganEnum> organSet = OrganEnum.stringListToOrganSet(Arrays.asList(stringOrgans));
        profile.removeOrgansDonated(organSet);
        if (historyPosition > 0) {
            historyPosition -= 1;
        }
        HistoryController.setPosition(historyPosition);
    }

    /**
     * Undoes a profile being updated
     *
     * @param currentDatabase
     * @param action
     */
    public void update(ProfileDatabase currentDatabase, History action) {
        Profile profile = currentDatabase.getProfile(action.getHistoryId());
        System.out.println(action);
        String old = action.getHistoryData()
                .substring(action.getHistoryData().indexOf("previous ") + 9,
                        action.getHistoryData().indexOf(" new "));
        profile.setExtraAttributes(new ArrayList<>(Arrays.asList(old.split(","))));
        if (historyPosition > 0) {
            historyPosition -= 1;
        }
        HistoryController.setPosition(historyPosition);
    }

    /**
     * Undoes a procedure being edited
     *
     * @param currentDatabase
     * @param action
     */
    public void edited(ProfileDatabase currentDatabase, History action) {
        Profile profile = currentDatabase.getProfile(action.getHistoryId());
        int procedurePlace = action.getHistoryDataIndex();
        String previous = action.getHistoryData()
                .substring(action.getHistoryData().indexOf("PREVIOUS(") + 9,
                        action.getHistoryData().indexOf("CURRENT("));
        String[] previousValues = previous.split(",");
        String organs = action.getHistoryData();
        List<String> List = new ArrayList<>(Arrays.asList(organs.split(",")));
        ArrayList<OrganEnum> organList = new ArrayList<>();
        System.out.println(organs);
        for (String organ : List) {
            System.out.println(organ);
            try {
                organList.add(OrganEnum.valueOf(organ.replace(" ", "")));
            } catch (IllegalArgumentException e) {
                System.out.println(e);
            }
        }
        profile.getAllProcedures().get(procedurePlace).setSummary(previousValues[0]);
        profile.getAllProcedures().get(procedurePlace)
                .setDate(LocalDate.parse(previousValues[1]));
        if (previousValues.length == 3) {
            profile.getAllProcedures().get(procedurePlace)
                    .setLongDescription(previousValues[2]);
        }
        profile.getAllProcedures().get(procedurePlace).setOrgansAffected(organList);
        if (historyPosition > 0) {
            historyPosition -= 1;
        }
        HistoryController.setPosition(historyPosition);
    }

}
