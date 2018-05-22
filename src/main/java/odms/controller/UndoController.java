package odms.controller;

import odms.History.History;
import odms.cli.CommandUtils;
import odms.data.ProfileDatabase;
import odms.data.UserDatabase;
import odms.enums.OrganEnum;
import odms.medications.Drug;
import odms.profile.Condition;
import odms.profile.Profile;
import odms.user.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class UndoController {

    private static ArrayList<Profile> unaddedProfiles = new ArrayList<>();
    private static int historyPosition;
    public static ArrayList<History> currentSessionHistory;

    public static void undo(ProfileDatabase currentDatabase) {
        historyPosition = HistoryController.getPosition();
        currentSessionHistory = HistoryController.getHistory();
        try {
            History action = currentSessionHistory.get(historyPosition);
            int end = 0;
            if(action!= null) {
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
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("No commands have been entered");
        }
    }

    private static void addedDonated(ProfileDatabase currentDatabase, History action) {
        Profile profile = currentDatabase.getProfile(action.getHistoryId());
        String organ = action.getHistoryData();
        profile.removeOrganDonated(OrganEnum.valueOf(organ));
    }

    private static void addedReceived(ProfileDatabase currentDatabase, History action) {
        Profile profile = currentDatabase.getProfile(action.getHistoryId());
        String organ = action.getHistoryData();
        profile.removeOrganReceived(OrganEnum.valueOf(organ));
    }

    private static void removedCondition(ProfileDatabase currentDatabase, History action) {
        Profile profile = currentDatabase.getProfile(action.getHistoryId());
        String s = action.getHistoryData();
        String[] values = s.split(",");
        String diagDate = values[1].substring(8)+"-"+values[1].substring(5,7)+"-"+values[1].substring(0,4);
        String cureDate = null;
        System.out.println(diagDate);
        if(!values[3].equals("null")) {
            cureDate = values[3].substring(8) + "-" + values[3].substring(5, 7) + "-" + values[3].substring(0, 4);
        }
        Condition condition = new Condition(values[0], diagDate, cureDate, Boolean.valueOf(values[2]));
        profile.addCondition(condition);
        LocalDateTime currentTime = LocalDateTime.now();
        History newAction = new History("Donor",profile.getId(),"removed condition",condition.getName()+","+condition.getDateOfDiagnosis()+","+condition.getChronic()+","+condition.getDateCuredString(),profile.getCurrentConditions().indexOf(condition), currentTime);
        currentSessionHistory.set(historyPosition,newAction);
        if (historyPosition > 0) {
            historyPosition -= 1;
        }

        HistoryController.setPosition(historyPosition);
    }

    private static void addCondition(ProfileDatabase currentDatabase, History action) {
        Profile profile = currentDatabase.getProfile(action.getHistoryId());
        int c = action.getHistoryDataIndex();
        Condition condition = profile.getCurrentConditions().get(c);
        profile.removeCondition(condition);
        if (historyPosition > 0) {
            historyPosition -= 1;
        }

        HistoryController.setPosition(historyPosition);
    }

    private static void stopDrug(ProfileDatabase currentDatabase, History action) {
        Profile profile = currentDatabase.getProfile(action.getHistoryId());
        int d = action.getHistoryDataIndex();
        ArrayList<Drug> drugs = profile.getHistoryOfMedication();
        Drug drug = drugs.get(d);
        profile.moveDrugToCurrent(drug);
        LocalDateTime currentTime = LocalDateTime.now();
        History newAction = new History("Donor ",profile.getId(), "stopped", drug.getDrugName(), profile.getCurrentMedications().indexOf(drug) ,currentTime);
        HistoryController.currentSessionHistory.set(historyPosition, newAction);
        if (historyPosition > 0) {
            historyPosition -= 1;
        }

        HistoryController.setPosition(historyPosition);
    }

    private static void renewDrug(ProfileDatabase currentDatabase, History action) {
        Profile profile = currentDatabase.getProfile(action.getHistoryId());
        int d = action.getHistoryDataIndex();
        ArrayList<Drug> drugs = profile.getCurrentMedications();
        Drug drug = drugs.get(d);
        profile.moveDrugToHistory(drug);
        LocalDateTime currentTime = LocalDateTime.now();
        History newAction = new History("Donor ",profile.getId(), "started", drug.getDrugName(), profile.getCurrentMedications().indexOf(drug) ,currentTime);
        HistoryController.currentSessionHistory.set(historyPosition, newAction);
        if (historyPosition > 0) {
            historyPosition -= 1;
        }

        HistoryController.setPosition(historyPosition);
    }

    private static void addDrug(ProfileDatabase currentDatabase, History action) {
        Profile profile = currentDatabase.getProfile(action.getHistoryId());
        int d = action.getHistoryDataIndex();
        ArrayList<Drug> drugs = profile.getCurrentMedications();
        profile.deleteDrug(drugs.get(d));
        if (historyPosition > 0) {
            historyPosition -= 1;
        }

        HistoryController.setPosition(historyPosition);
    }

    private static void deleteDrug(ProfileDatabase currentDatabase, History action) {
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
        if (historyPosition > 0) {
            historyPosition -= 1;
        }

        HistoryController.setPosition(historyPosition);
    }

    private static void updated(History action) {
        User user = LoginController.getCurrentUser();
        String previous =  action.getHistoryData().substring(action.getHistoryData().indexOf("previous ")+9,action.getHistoryData().indexOf("new "));
        String[] previousValues = previous.split(",");
        user.setName(previousValues[1].replace("name=",""));
        user.setStaffId(Integer.valueOf(previousValues[0].replace("staffId=","").replace(" ","")));
        user.setWorkAddress(previousValues[2].replace("workAddress=",""));
        user.setRegion(previousValues[3].replace("region=",""));
        if (historyPosition > 0) {
            historyPosition -= 1;
        }

        HistoryController.setPosition(historyPosition);
    }

    public static void added(ProfileDatabase currentDatabase, History action) {
        Profile profile = currentDatabase.getProfile(action.getHistoryId());
        currentDatabase.deleteProfile(action.getHistoryId());
        unaddedProfiles.add(profile);
        if (historyPosition > 0) {
            historyPosition -= 1;
        }

        HistoryController.setPosition(historyPosition);
    }
    public static void deleted(ProfileDatabase currentDatabase, History action) {
        int oldid = action.getHistoryId();
        int id = currentDatabase
                .restoreProfile(oldid, HistoryController.deletedProfiles.get(HistoryController.deletedProfiles.size() - 1));
        HistoryController.deletedProfiles.remove(HistoryController.deletedProfiles.get(HistoryController.deletedProfiles.size() - 1));
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

    public static void removed(ProfileDatabase currentDatabase, History action) throws Exception{
        Profile profile = currentDatabase.getProfile(action.getHistoryId());
        profile.addOrgansDonating(OrganEnum.stringListToOrganSet(Arrays.asList(action.getHistoryData())));
        if (historyPosition > 0) {
            historyPosition -= 1;
        }
        HistoryController.setPosition(historyPosition);
    }
    public static void set(ProfileDatabase currentDatabase, History action) {
        Profile profile = currentDatabase.getProfile(action.getHistoryId());
        String[] stringOrgans = action.getHistoryData().split(",");
        Set<OrganEnum> organSet = OrganEnum.stringListToOrganSet(Arrays.asList(stringOrgans));
        profile.removeOrgansDonating(organSet);
        if (historyPosition > 0) {
            historyPosition -= 1;
        }

        HistoryController.setPosition(historyPosition);
    }
    public static void donate(ProfileDatabase currentDatabase, History action) {
        Profile profile = currentDatabase.getProfile(action.getHistoryId());
        String[] stringOrgans = action.getHistoryData().split(",");
        Set<OrganEnum> organSet = OrganEnum.stringListToOrganSet(Arrays.asList(stringOrgans));
        profile.removeOrgansDonated(organSet);
        if (historyPosition > 0) {
            historyPosition -= 1;
        }

        HistoryController.setPosition(historyPosition);
    }
    public static void update(ProfileDatabase currentDatabase, History action){
        Profile profile = currentDatabase.getProfile(action.getHistoryId());
        System.out.println(action);
        String old =  action.getHistoryData().substring(action.getHistoryData().indexOf("previous ")+9,action.getHistoryData().indexOf(" new "));
        profile.setExtraAttributes(new ArrayList<>(Arrays.asList(old.split(","))));
        if (historyPosition > 0) {
            historyPosition -= 1;
        }

        HistoryController.setPosition(historyPosition);
    }
    public static void edited(ProfileDatabase currentDatabase, History action) {
        Profile profile = currentDatabase.getProfile(action.getHistoryId());
        int procedurePlace = action.getHistoryDataIndex();
        String previous = action.getHistoryData().substring(action.getHistoryData().indexOf("PREVIOUS(")+9,action.getHistoryData().indexOf("CURRENT("));
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
