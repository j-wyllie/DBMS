package odms.controller;

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
    public static ArrayList<String> currentSessionHistory;

    public static void redo(ProfileDatabase currentDatabase) {
        try {
            currentSessionHistory = HistoryController.getHistory();
            historyPosition = HistoryController.getPosition();
            if (historyPosition != currentSessionHistory.size()-1) {
                historyPosition += 1;
                String action;
                if (historyPosition == 0) {
                    historyPosition = 1;
                    action = currentSessionHistory.get(historyPosition);
                    historyPosition = 0;
                } else {
                    action = currentSessionHistory.get(historyPosition);
                }
                int end = action.indexOf(" at");
                action = action.substring(0, action.indexOf(" at"));
                if (action.contains("added") && !action.contains("drug") && !action.contains("condition")&& !action.contains("received") && !action.contains("donated")) {
                    added(currentDatabase, action);
                } else if (action.contains("deleted")) {
                    deleted(currentDatabase, action);
                } else if (action.contains("removed") && !action.contains("drug") && !action.contains("condition")&& !action.contains("received") && !action.contains("donated")) {
                    removed(currentDatabase, action);
                } else if (action.contains("set")) {
                    set(currentDatabase, action);
                } else if (action.contains("donate")) {
                    donate(currentDatabase, action);
                } else if (action.contains("update") && !action.contains("updated")) {
                    update(currentDatabase, action);
                } else if (action.contains("EDITED")) {
                    edited(currentDatabase, action);
                }  else if (action.contains("updated")) {
                    updated(action, end);
                }  else if (action.contains("drug") && !(action.contains("removed"))) {
                    addDrug(currentDatabase, action, end);
                } else if (action.contains("removed drug")) {
                    deleteDrug(currentDatabase, action, end);
                } else if (action.contains("stopped")) {
                    stopDrug(currentDatabase, action);
                } else if (action.contains("started")) {
                    renewDrug(currentDatabase, action);
                }  else if (action.contains("added condition")) {
                    addCondition(currentDatabase,action,end);
                }  else if (action.contains("removed condition")) {
                    removedCondition(currentDatabase,action);
                }  else if (action.contains("received") &&action.contains("added")) {
                    addedReceived(currentDatabase, action);
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

    private static void addedReceived(ProfileDatabase currentDatabase, String action) {
        int id = Integer.parseInt(action.substring(0,action.indexOf("added")).replaceAll("[\\D]", ""));
        Profile profile = currentDatabase.getProfile(id);
        String organ = action.substring(action.indexOf("added ")+6, action.indexOf(" to"));
        profile.addOrganReceived(OrganEnum.valueOf(organ));
    }

    private static void removedCondition(ProfileDatabase currentDatabase, String action) {
        int id = Integer.parseInt(action.substring(0,action.indexOf("removed")).replaceAll("[\\D]", ""));
        Profile profile = currentDatabase.getProfile(id);
        int c = Integer.parseInt(action.substring(action.indexOf("index of")).replaceAll("[\\D]", ""));
        Condition condition = profile.getCurrentConditions().get(c);
        profile.removeCondition(condition);
    }

    private static void addCondition(ProfileDatabase currentDatabase, String action, int end) {
        int id = Integer.parseInt(action.substring(0,action.indexOf("added")).replaceAll("[\\D]", ""));
        Profile profile = currentDatabase.getProfile(id);
        String s = action.substring(action.indexOf("(")+1,action.indexOf(")"));
        String[] values = s.split(",");
        Condition condition = new Condition(values[0], values[1], null, Boolean.valueOf(values[2]));
        profile.addCondition(condition);
    }

    private static void deleteDrug(ProfileDatabase currentDatabase, String action, int end) {
        int id = Integer.parseInt(action.substring(0,action.indexOf("drug")).replaceAll("[\\D]", ""));
        Profile profile = currentDatabase.getProfile(id);
        int d = Integer.parseInt(action.substring(action.indexOf("index of")).replaceAll("[\\D]", ""));
        ArrayList<Drug> drugs = profile.getCurrentMedications();
        profile.deleteDrug(drugs.get(d));
    }

    private static void stopDrug(ProfileDatabase currentDatabase, String action) {
        int id = Integer.parseInt(action.substring(0,action.indexOf("stopped")).replaceAll("[\\D]", ""));
        Profile profile = currentDatabase.getProfile(id);
        int d = Integer.parseInt(action.substring(action.indexOf("stopped")).replaceAll("[\\D]", ""));
        ArrayList<Drug> drugs = profile.getCurrentMedications();
        Drug drug = drugs.get(d);
        profile.moveDrugToHistory(drug);
        LocalDateTime currentTime = LocalDateTime.now();
        String data = "Donor " + profile.getId()  + " stopped "  + drug.getDrugName() + " index of "+ profile.getHistoryOfMedication().indexOf(drug) + " at" +currentTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        HistoryController.currentSessionHistory.set(historyPosition-1, data);
    }

    private static void renewDrug(ProfileDatabase currentDatabase, String action) {
        int id = Integer.parseInt(action.substring(0,action.indexOf("started")).replaceAll("[\\D]", ""));
        Profile profile = currentDatabase.getProfile(id);
        int d = Integer.parseInt(action.substring(action.indexOf("started")).replaceAll("[\\D]", ""));
        ArrayList<Drug> drugs = profile.getHistoryOfMedication();
        Drug drug = drugs.get(d);
        profile.moveDrugToCurrent(drug);
        LocalDateTime currentTime = LocalDateTime.now();
        String data = "Donor " + profile.getId()  + " started using "  + drug.getDrugName() + " index of "+ profile.getCurrentMedications().indexOf(drug) + "again at" +currentTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        HistoryController.currentSessionHistory.set(historyPosition-1, data);
    }

    private static void addDrug(ProfileDatabase currentDatabase, String action, int end) {
        int id = Integer.parseInt(action.substring(0,action.indexOf("drug")).replaceAll("[\\D]", ""));
        Profile profile = currentDatabase.getProfile(id);
        if(action.contains("history")) {
            String drug = action.substring(action.indexOf("tory")+5,action.indexOf(" index of"));
            Drug d = new Drug(drug);
            profile.addDrug(d);
            profile.moveDrugToHistory(d);
        } else {
            String drug = action.substring(action.indexOf("drug") + 5,action.indexOf(" index of"));
            profile.addDrug(new Drug(drug));
        }
    }

    private static void updated(String action, int end) {
        int id = Integer.parseInt(action.replaceAll("[\\D]", ""));
        User user = LoginController.getCurrentUser();
        String newString = action.substring(action.indexOf("new = (")+7,end);
        String[] newValues = newString.split(",");
        user.setName(newValues[1].replace("name=",""));
        user.setStaffId(Integer.valueOf(newValues[0].replace("staffId=","").replace(" ","")));
        user.setWorkAddress(newValues[2].replace("workAddress=",""));
        user.setRegion(newValues[3].replace("region=",""));
    }

    public static void added(ProfileDatabase currentDatabase, String action) {
        int oldid = Integer.parseInt(action.replaceAll("[\\D]", ""));
        int id = currentDatabase
                .restoreProfile(oldid, unaddedProfiles.get(unaddedProfiles.size() - 1));
        unaddedProfiles.remove(unaddedProfiles.get(unaddedProfiles.size() - 1));
        for (int i = 0; i < currentSessionHistory.size() - 1; i++) {
            if (currentSessionHistory.get(i).contains("Profile " + oldid)) {
                currentSessionHistory.set(i,
                        ("Profile " + id + currentSessionHistory.get(i).substring(
                                action.indexOf("Profile " + oldid) + 6 + Integer.toString(id)
                                        .length())));
            }
        }
        currentSessionHistory.set(historyPosition,
                ("Profile " + id + " added at " + LocalDateTime.now()));
    }
    public static void deleted(ProfileDatabase currentDatabase, String action) {
        int id = Integer.parseInt(action.replaceAll("[\\D]", ""));
        Profile profile = currentDatabase.getProfile(id);
        currentDatabase.deleteProfile(id);
        HistoryController.deletedProfiles.add(profile);
    }

    public static void removed(ProfileDatabase currentDatabase, String action) throws Exception{
        int id = Integer.parseInt(action.replaceAll("[\\D]", ""));
        Profile profile = currentDatabase.getProfile(id);
        profile.removeOrgansDonating(OrganEnum.stringListToOrganSet(Arrays.asList(
                action.substring(action.indexOf("[") + 1, action.indexOf("]")).split(","))));
    }
    public static void set(ProfileDatabase currentDatabase, String action) throws OrganConflictException {
        int id = Integer.parseInt(action.replaceAll("[\\D]", ""));
        Profile profile = currentDatabase.getProfile(id);
        profile.addOrgansDonating(OrganEnum.stringListToOrganSet(Arrays.asList(
                action.substring(
                        action.indexOf("[") + 1,
                        action.indexOf("]")).split(",")
        )));
    }
    public static void donate(ProfileDatabase currentDatabase, String action) {
        int id = Integer.parseInt(action.replaceAll("[\\D]", ""));
        Profile profile = currentDatabase.getProfile(id);
        profile.addOrgansDonated(OrganEnum.stringListToOrganSet(Arrays.asList(action.substring(action.indexOf("[") + 1,action.indexOf("]")).split(","))));
    }
    public static void update(ProfileDatabase currentDatabase, String action){
        int id = Integer.parseInt(
                action.substring(0, action.indexOf("previous")).replaceAll("[\\D]", ""));
        Profile profile = currentDatabase.getProfile(id);
        String newInfo = action.substring(action.indexOf("ird"));
        profile.setExtraAttributes(new ArrayList<>(Arrays.asList(newInfo.split(","))));
    }
    public static void edited(ProfileDatabase currentDatabase, String action) {
        int id = Integer.parseInt(action.substring(0, action.indexOf("PROCEDURE")).replaceAll("[\\D]", ""));
        Profile profile = currentDatabase.getProfile(id);
        int procedurePlace = Integer.parseInt(action.substring(action.indexOf("PROCEDURE"), action.indexOf("EDITED")).replaceAll("[\\D]", ""));
        String previous = action.substring(action.indexOf("CURRENT(")+8, action.indexOf(") NEW"));
        String[] previousValues = previous.split(",");
        String organs;
        ArrayList<OrganEnum> organList = new ArrayList<>();
        organs = action.substring(action.indexOf("NEWORGANS["), action.indexOf("]END"));
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
