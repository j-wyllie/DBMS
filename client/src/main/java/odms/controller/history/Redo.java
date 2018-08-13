package odms.controller.history;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import odms.commons.model.enums.OrganEnum;
import odms.commons.model.history.History;
import odms.commons.model.medications.Drug;
import odms.commons.model.profile.Condition;
import odms.commons.model.profile.OrganConflictException;
import odms.commons.model.profile.Profile;
import odms.commons.model.user.User;
import odms.controller.profile.UndoRedoCLIService;
import odms.view.LoginView;

public class Redo extends UndoRedo {

    private static ArrayList<Profile> unaddedProfiles = new ArrayList<>();
    private static int historyPosition;
    private static ArrayList<History> currentSessionHistory;

    /**
     * Redoes an action
     *
     * @param currentDatabase
     */
    public void redo(ProfileDatabase currentDatabase) {
        //todo change the methods to controller functions
        try {
            currentSessionHistory = CurrentHistory.getHistory();
            historyPosition = CurrentHistory.getPosition();
            if (historyPosition != currentSessionHistory.size() - 1) {
                historyPosition += 1;
                History action;
                if (historyPosition == 0) {
                    historyPosition = 1;
                    action = currentSessionHistory.get(historyPosition);
                    historyPosition = 0;
                } else {
                    action = currentSessionHistory.get(historyPosition);
                }
                redirect(currentDatabase, action);
                CurrentHistory.setPosition(historyPosition);
                System.out.println("Command redone");
            } else {
                System.out.println("There are no commands to redo");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("No commands have been entered.");
        }
    }

    /**
     * Redoes a donation being done
     *
     * @param currentDatabase
     * @param action
     */
    public void addedDonated(ProfileDatabase currentDatabase, History action) {
        Profile profile = currentDatabase.getProfile(action.getHistoryId());
        String organ = action.getHistoryData();
        UndoRedoCLIService.addOrganDonated(OrganEnum.valueOf(organ), profile);
    }

    /**
     * Redoes a received organ being added
     *
     * @param currentDatabase
     * @param action
     */
    public void addedReceived(ProfileDatabase currentDatabase, History action) {
        Profile profile = currentDatabase.getProfile(action.getHistoryId());
        String organ = action.getHistoryData();
        UndoRedoCLIService.addOrgansReceived(new HashSet<OrganEnum>(
                Collections.singletonList(OrganEnum.valueOf(organ))), profile);
    }

    /**
     * Redoes a conditon being removed
     *
     * @param currentDatabase
     * @param action
     */
    public void removedCondition(ProfileDatabase currentDatabase, History action) {
        Profile profile = currentDatabase.getProfile(action.getHistoryId());
        int c = action.getHistoryDataIndex();
        Condition condition = UndoRedoCLIService.getCurrentConditions(profile).get(c);
        UndoRedoCLIService.removeCondition(condition, profile);
    }

    /**
     * redoes an condition being added
     *
     * @param currentDatabase
     * @param action
     */
    public void addCondition(ProfileDatabase currentDatabase, History action) {
        Profile profile = currentDatabase.getProfile(action.getHistoryId());
        String s = action.getHistoryData();
        String[] values = s.split(",");
        Condition condition = new Condition(values[0], values[1], null, Boolean.valueOf(values[2]));
        UndoRedoCLIService.addCondition(condition, profile);
    }

    /**
     * Redoes a drug being deleted
     *
     * @param currentDatabase
     * @param action
     */
    public void deleteDrug(ProfileDatabase currentDatabase, History action)
            throws IndexOutOfBoundsException {
        Profile profile = currentDatabase.getProfile(action.getHistoryId());
        int d = action.getHistoryDataIndex();
        List<Drug> drugs = profile.getCurrentMedications();
        UndoRedoCLIService.deleteDrug(drugs.get(d), profile);
    }

    /**
     * Redoes a drug being added to history
     *
     * @param currentDatabase
     * @param action
     */
    public void stopDrug(ProfileDatabase currentDatabase, History action)
            throws IndexOutOfBoundsException {
        Profile profile = currentDatabase.getProfile(action.getHistoryId());
        int d = action.getHistoryDataIndex();
        List<Drug> drugs = profile.getCurrentMedications();
        Drug drug = drugs.get(d);
        UndoRedoCLIService.moveDrugToHistory(drug, profile);
        LocalDateTime currentTime = LocalDateTime.now();
        History data = new History("profile", profile.getId(), "stopped", drug.getDrugName(),
                profile.getHistoryOfMedication().indexOf(drug), currentTime);
        CurrentHistory.currentSessionHistory.set(historyPosition - 1, data);
    }

    /**
     * Redoes a drug being added to current
     *
     * @param currentDatabase
     * @param action
     */
    public void renewDrug(ProfileDatabase currentDatabase, History action)
            throws IndexOutOfBoundsException {
        Profile profile = currentDatabase.getProfile(action.getHistoryId());
        int d = action.getHistoryDataIndex();
        List<Drug> drugs = profile.getHistoryOfMedication();
        Drug drug = drugs.get(d);
        UndoRedoCLIService.moveDrugToCurrent(drug, profile);
        LocalDateTime currentTime = LocalDateTime.now();
        History data = new History("profile", profile.getId(), "started", drug.getDrugName(),
                profile.getCurrentMedications().indexOf(drug), currentTime);
        CurrentHistory.currentSessionHistory.set(historyPosition - 1, data);
    }

    /**
     * Redoes a drug being added
     *
     * @param currentDatabase
     * @param action
     */
    public void addDrug(ProfileDatabase currentDatabase, History action)
            throws IndexOutOfBoundsException {
        Profile profile = currentDatabase.getProfile(action.getHistoryId());
        if (action.getHistoryAction().contains("history")) {
            String drug = action.getHistoryData();
            Drug d = new Drug(drug);
            UndoRedoCLIService.addDrug(d, profile);
            UndoRedoCLIService.moveDrugToHistory(d, profile);
        } else {
            String drug = action.getHistoryData();
            UndoRedoCLIService.addDrug(new Drug(drug), profile);
        }
    }

    /**
     * Redoes a clinician being updated
     *
     * @param action
     */
    public void updated(History action) {
        User user = LoginView.getCurrentUser();
        String newString = action.getHistoryData()
                .substring(action.getHistoryData().indexOf("new ") + 4);
        String[] newValues = newString.split(",");
        user.setName(newValues[1].replace("name=", ""));
        user.setStaffID(Integer.valueOf(newValues[0].replace("staffId=", "").replace(" ", "")));
        user.setWorkAddress(newValues[2].replace("workAddress=", ""));
        user.setRegion(newValues[3].replace("region=", ""));
    }

    /**
     * Redoes a profile being added
     *
     * @param currentDatabase
     * @param action
     */
    public void added(ProfileDatabase currentDatabase, History action) {
        int oldid = action.getHistoryId();
        int id = currentDatabase
                .restoreProfile(oldid, unaddedProfiles.get(unaddedProfiles.size() - 1));
        unaddedProfiles.remove(unaddedProfiles.get(unaddedProfiles.size() - 1));
        for (int i = 0; i < currentSessionHistory.size() - 1; i++) {
            if (currentSessionHistory.get(i).getHistoryId() == oldid) {
                currentSessionHistory.get(i).setHistoryId(id);
            }
        }
    }

    /**
     * Redoes a profile being deleted
     *
     * @param currentDatabase
     * @param action
     */
    public void deleted(ProfileDatabase currentDatabase, History action) {
        Profile profile = currentDatabase.getProfile(action.getHistoryId());
        currentDatabase.deleteProfile(action.getHistoryId());
        CurrentHistory.deletedProfiles.add(profile);
    }

    /**
     * Redoes a organ being removed
     *
     * @param currentDatabase
     * @param action
     * @throws Exception
     */
    public void removed(ProfileDatabase currentDatabase, History action) throws Exception {
        Profile profile = currentDatabase.getProfile(action.getHistoryId());
        UndoRedoCLIService.removeOrgansDonating(OrganEnum.stringListToOrganSet(
                Arrays.asList(action.getHistoryData().replace(' ', '_').split(","))), profile);
    }

    /**
     * Redoes a organ being set
     *
     * @param currentDatabase
     * @param action
     * @throws OrganConflictException
     */
    public void set(ProfileDatabase currentDatabase, History action) throws OrganConflictException {
        Profile profile = currentDatabase.getProfile(action.getHistoryId());
        UndoRedoCLIService.addOrgansDonating(OrganEnum.stringListToOrganSet(
                Arrays.asList(action.getHistoryData().replace(' ', '_').split(",")
                )), profile);
    }

    /**
     * Redoes an organ being donated
     *
     * @param currentDatabase
     * @param action
     */
    public void donate(ProfileDatabase currentDatabase, History action) {
        Profile profile = currentDatabase.getProfile(action.getHistoryId());
        UndoRedoCLIService.addOrgansDonated(OrganEnum.stringListToOrganSet(
                Arrays.asList(action.getHistoryData().replace(' ', '_').split(","))), profile);
    }

    /**
     * Redoes a profile being updated
     *
     * @param currentDatabase
     * @param action
     */
    public void update(ProfileDatabase currentDatabase, History action) {
        Profile profile = currentDatabase.getProfile(action.getHistoryId());
        String newInfo = action.getHistoryData()
                .substring(action.getHistoryData().indexOf("new ") + 4);
        UndoRedoCLIService.setExtraAttributes(new ArrayList<String>(Arrays.asList(newInfo.split(","))), profile);
    }

    /**
     * Redoes a procedure being edited
     *
     * @param currentDatabase
     * @param action
     */
    public void edited(ProfileDatabase currentDatabase, History action) {
        Profile profile = currentDatabase.getProfile(action.getHistoryId());
        int procedurePlace = action.getHistoryDataIndex();
        String previous = action.getHistoryData()
                .substring(action.getHistoryData().indexOf("CURRENT(") + 8);
        String[] previousValues = previous.split(",");
        String organs;
        List<OrganEnum> organList = new ArrayList<>();
        organs = action.getHistoryData();
        List<String> list = new ArrayList<>(Arrays.asList(organs.split(",")));
        for (String organ : list) {
            System.out.println(organ);
            organList.add(OrganEnum.valueOf(organ.replace(" ", "").replace("NEWORGANS[", "")));
        }
        profile.getAllProcedures().get(procedurePlace).setSummary(previousValues[0]);
        profile.getAllProcedures().get(procedurePlace).setDate(LocalDate.parse(previousValues[1]));
        if (previousValues.length == 3) {
            profile.getAllProcedures().get(procedurePlace)
                    .setLongDescription(previousValues[2]);
        }
        profile.getAllProcedures().get(procedurePlace).setOrgansAffected(organList);
    }
}
