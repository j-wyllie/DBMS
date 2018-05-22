package odms.controller;

import odms.History.History;
import odms.data.ProfileDatabase;
import odms.enums.OrganEnum;
import odms.medications.Drug;
import odms.profile.Condition;
import odms.profile.OrganConflictException;
import odms.profile.Profile;
import odms.user.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class RedoController {
    private static ArrayList<Profile> unaddedProfiles = new ArrayList<>();
    private static int historyPosition;
    public static ArrayList<History> currentSessionHistory;

    public static void redo(ProfileDatabase currentDatabase) {
        try {
            currentSessionHistory = HistoryController.getHistory();
            historyPosition = HistoryController.getPosition();
            if (historyPosition != currentSessionHistory.size()-1) {
                historyPosition += 1;
                History action;
                if (historyPosition == 0) {
                    historyPosition = 1;
                    action = currentSessionHistory.get(historyPosition);
                    historyPosition = 0;
                } else {
                    action = currentSessionHistory.get(historyPosition);
                }
                if (action.getHistoryAction().equals("added")) {
                    added(currentDatabase, action);
                } else if (action.getHistoryAction().equals("deleted")) {
                    deleted(currentDatabase, action);
                } else if (action.getHistoryAction().equals("removed")) {
                    removed(currentDatabase, action);
                } else if (action.getHistoryAction().equals("set")) {
                    set(currentDatabase, action);
                } else if (action.getHistoryAction().equals("donate")) {
                    donate(currentDatabase, action);
                } else if (action.getHistoryAction().equals("update")) {
                    update(currentDatabase, action);
                } else if (action.getHistoryAction().equals("EDITED")) {
                    edited(currentDatabase, action);
                } else if (action.getHistoryAction().equals("updated")) {
                    updated(action);
                } else if (action.getHistoryAction().equals("added drug")) {
                    addDrug(currentDatabase, action);
                } else if (action.getHistoryAction().equals("removed drug")) {
                    deleteDrug(currentDatabase, action);
                } else if (action.getHistoryAction().equals("stopped")) {
                    stopDrug(currentDatabase, action);
                } else if (action.getHistoryAction().equals("started")) {
                    renewDrug(currentDatabase, action);
                } else if (action.getHistoryAction().equals("added condition")) {
                    addCondition(currentDatabase,action);
                } else if (action.getHistoryAction().equals("removed condition")) {
                    removedCondition(currentDatabase,action);
                } else if (action.getHistoryAction().equals("received")) {
                    addedReceived(currentDatabase, action);
                } else if (action.getHistoryAction().equals("donated")) {
                    addedDonated(currentDatabase, action);
                }
                HistoryController.setPosition(historyPosition);
                System.out.println("Command redone");
            } else {
                System.out.println("There are no commands to redo");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("No commands have been entered.");
        }
    }

    private static void addedDonated(ProfileDatabase currentDatabase, History action) {
        Profile profile = currentDatabase.getProfile(action.getHistoryId());
        String organ = action.getHistoryData();
        profile.addOrganDonated(OrganEnum.valueOf(organ));
    }

    private static void addedReceived(ProfileDatabase currentDatabase, History action) {
        Profile profile = currentDatabase.getProfile(action.getHistoryId());
        String organ = action.getHistoryData();
        profile.addOrganReceived(OrganEnum.valueOf(organ));
    }

    private static void removedCondition(ProfileDatabase currentDatabase, History action) {
        Profile profile = currentDatabase.getProfile(action.getHistoryId());
        int c = action.getHistoryDataIndex();
        Condition condition = profile.getCurrentConditions().get(c);
        profile.removeCondition(condition);
    }

    private static void addCondition(ProfileDatabase currentDatabase, History action) {
        Profile profile = currentDatabase.getProfile(action.getHistoryId());
        String s = action.getHistoryData();
        String[] values = s.split(",");
        Condition condition = new Condition(values[0], values[1], null, Boolean.valueOf(values[2]));
        profile.addCondition(condition);
    }

    private static void deleteDrug(ProfileDatabase currentDatabase, History action) {
        Profile profile = currentDatabase.getProfile(action.getHistoryId());
        int d = action.getHistoryDataIndex();
        ArrayList<Drug> drugs = profile.getCurrentMedications();
        profile.deleteDrug(drugs.get(d));
    }

    private static void stopDrug(ProfileDatabase currentDatabase, History action) {
        Profile profile = currentDatabase.getProfile(action.getHistoryId());
        int d = action.getHistoryDataIndex();
        ArrayList<Drug> drugs = profile.getCurrentMedications();
        Drug drug = drugs.get(d);
        profile.moveDrugToHistory(drug);
        LocalDateTime currentTime = LocalDateTime.now();
        History data = new History("Donor" , profile.getId()  , "stopped"  , drug.getDrugName() , profile.getHistoryOfMedication().indexOf(drug) , currentTime);
        HistoryController.currentSessionHistory.set(historyPosition-1, data);
    }

    private static void renewDrug(ProfileDatabase currentDatabase, History action) {
        Profile profile = currentDatabase.getProfile(action.getHistoryId());
        int d = action.getHistoryDataIndex();
        ArrayList<Drug> drugs = profile.getHistoryOfMedication();
        Drug drug = drugs.get(d);
        profile.moveDrugToCurrent(drug);
        LocalDateTime currentTime = LocalDateTime.now();
        History data = new History("Donor" , profile.getId()  , "started"  , drug.getDrugName() , profile.getHistoryOfMedication().indexOf(drug) , currentTime);
        HistoryController.currentSessionHistory.set(historyPosition-1, data);
    }

    private static void addDrug(ProfileDatabase currentDatabase, History action) {
        Profile profile = currentDatabase.getProfile(action.getHistoryId());
        if(action.getHistoryAction().contains("history")) {
            String drug = action.getHistoryData();
            Drug d = new Drug(drug);
            profile.addDrug(d);
            profile.moveDrugToHistory(d);
        } else {
            String drug = action.getHistoryData();
            profile.addDrug(new Drug(drug));
        }
    }

    private static void updated(History action) {
        //todo update
        int id = action.getHistoryId();
        User user = LoginController.getCurrentUser();
        String newString = action.getHistoryData();
        String[] newValues = newString.split(",");
        user.setName(newValues[1].replace("name=",""));
        user.setStaffId(Integer.valueOf(newValues[0].replace("staffId=","").replace(" ","")));
        user.setWorkAddress(newValues[2].replace("workAddress=",""));
        user.setRegion(newValues[3].replace("region=",""));
    }

    public static void added(ProfileDatabase currentDatabase, History action) {
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
    public static void deleted(ProfileDatabase currentDatabase, History action) {
        Profile profile = currentDatabase.getProfile(action.getHistoryId());
        currentDatabase.deleteProfile(action.getHistoryId());
        HistoryController.deletedProfiles.add(profile);
    }

    public static void removed(ProfileDatabase currentDatabase, History action) throws Exception{
        Profile profile = currentDatabase.getProfile(action.getHistoryId());
        profile.removeOrgansDonating(OrganEnum.stringListToOrganSet(Arrays.asList(action.getHistoryData().split(","))));
    }
    public static void set(ProfileDatabase currentDatabase, History action) throws OrganConflictException {
        Profile profile = currentDatabase.getProfile(action.getHistoryId());
        profile.addOrgansDonating(OrganEnum.stringListToOrganSet(Arrays.asList(action.getHistoryData().split(",")
        )));
    }
    public static void donate(ProfileDatabase currentDatabase, History action) {
        Profile profile = currentDatabase.getProfile(action.getHistoryId());
        profile.addOrgansDonated(OrganEnum.stringListToOrganSet(Arrays.asList(action.getHistoryData().split(","))));
    }
    public static void update(ProfileDatabase currentDatabase, History action){
        //todo
        Profile profile = currentDatabase.getProfile(action.getHistoryId());
        String newInfo = action.getHistoryData();
        profile.setExtraAttributes(new ArrayList<>(Arrays.asList(newInfo.split(","))));
    }
    public static void edited(ProfileDatabase currentDatabase, History action) {
        //todo
        Profile profile = currentDatabase.getProfile(action.getHistoryId());
        int procedurePlace = action.getHistoryDataIndex();
        String previous = action.getHistoryData();
        String[] previousValues = previous.split(",");
        String organs;
        ArrayList<OrganEnum> organList = new ArrayList<>();
        organs = action.getHistoryData();
        List<String> List = new ArrayList<>(Arrays.asList(organs.split(",")));
        for(String organ : List){
            System.out.println(organ);
            organList.add(OrganEnum.valueOf(organ.replace(" ","").replace("NEWORGANS[","")));
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
