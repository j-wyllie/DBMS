package odms.controller;

import odms.cli.CommandUtils;
import odms.data.ProfileDatabase;
import odms.data.UserDatabase;
import odms.profile.Organ;
import odms.profile.Profile;
import odms.user.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class UndoController {

    private static ArrayList<Profile> unaddedProfiles = new ArrayList<>();
    private static int historyPosition;
    public static ArrayList<String> currentSessionHistory;

    public static void undo(ProfileDatabase currentDatabase) {
        historyPosition = HistoryController.getPosition();
        currentSessionHistory = HistoryController.getHistory();
        try {
            String action = currentSessionHistory.get(historyPosition);
            if(action!= "") {
                action = action.substring(0, action.indexOf(" at"));
            }
            if (action.contains("added")) {
                added(currentDatabase, action);
            } else if (action.contains("deleted")) {
                deleted(currentDatabase, action);
            } else if (action.contains("removed")) {
                removed(currentDatabase, action);
            } else if (action.contains("set")) {
                set(currentDatabase, action);
            } else if (action.contains("donate")) {
                donate(currentDatabase, action);
            } else if (action.contains("update") && !action.contains("updated")) {
                update(currentDatabase, action);
            } else if (action.contains("EDITED")) {
                edited(currentDatabase, action);
            } else if (action.contains("updated")) {
                updated(action);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("No commands have been entered");
        }
    }

    private static void updated(String action) {
        int id = Integer.parseInt(action.replaceAll("[\\D]", ""));
        User user = LoginController.userDatabase.getClinician(id);
        String previous = action.substring(action.indexOf("(")+1,action.indexOf(")"));
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

    public static void added(ProfileDatabase currentDatabase, String action) {
        int id = Integer.parseInt(action.replaceAll("[\\D]", ""));
        Profile profile = currentDatabase.getProfile(id);
        currentDatabase.deleteProfile(id);
        unaddedProfiles.add(profile);
        if (historyPosition > 0) {
            historyPosition -= 1;
        }

        HistoryController.setPosition(historyPosition);
    }
    public static void deleted(ProfileDatabase currentDatabase, String action) {
        int oldid = Integer.parseInt(action.replaceAll("[\\D]", ""));
        int id = currentDatabase
                .restoreProfile(oldid, HistoryController.deletedProfiles.get(HistoryController.deletedProfiles.size() - 1));
        HistoryController.deletedProfiles.remove(HistoryController.deletedProfiles.get(HistoryController.deletedProfiles.size() - 1));
        for (int i = 0; i < currentSessionHistory.size() - 1; i++) {
            if (currentSessionHistory.get(i).contains("Profile " + oldid)) {
                currentSessionHistory.set(i,
                        ("Profile " + id + " " + currentSessionHistory.get(i).substring(
                                action.indexOf("Profile " + oldid) + 6 + Integer
                                        .toString(id)
                                        .length())));
            }
        }
        currentSessionHistory
                .set(historyPosition,
                        ("Profile " + id + " deleted at " + LocalDateTime.now()));
        if (historyPosition > 0) {
            historyPosition -= 1;
        }

        HistoryController.setPosition(historyPosition);
}

    public static void removed(ProfileDatabase currentDatabase, String action) throws Exception{
        int id = Integer.parseInt(action.replaceAll("[\\D]", ""));
        Profile profile = currentDatabase.getProfile(id);
        profile.addOrgansDonating(Organ.stringListToOrganSet(Arrays.asList(
                action.substring(
                        action.indexOf("[") + 1,
                        action.indexOf("]")).split(",")
        )));
        if (historyPosition > 0) {
            historyPosition -= 1;
        }

        HistoryController.setPosition(historyPosition);
    }
    public static void set(ProfileDatabase currentDatabase, String action) {
        int id = Integer.parseInt(action.replaceAll("[\\D]", ""));
        Profile profile = currentDatabase.getProfile(id);
        Set<String> organSet = new HashSet<>(Arrays.asList(
                action.substring(action.indexOf("[") + 1, action.indexOf("]")).split(",")));
        profile.removeOrgans(organSet);
        if (historyPosition > 0) {
            historyPosition -= 1;
        }

        HistoryController.setPosition(historyPosition);
    }
    public static void donate(ProfileDatabase currentDatabase, String action) {
        int id = Integer.parseInt(action.replaceAll("[\\D]", ""));
        Profile profile = currentDatabase.getProfile(id);
        Set<String> organSet = new HashSet<>(Arrays.asList(
                action.substring(action.indexOf("[") + 1, action.indexOf("]")).split(",")));
        profile.removeDonations(organSet);
        if (historyPosition > 0) {
            historyPosition -= 1;
        }

        HistoryController.setPosition(historyPosition);
    }
    public static void update(ProfileDatabase currentDatabase, String action){
        int id = Integer.parseInt(
                action.substring(0, action.indexOf("previous")).replaceAll("[\\D]", ""));
        Profile profile = currentDatabase.getProfile(id);
        System.out.println(action);
        String old = action.substring(action.indexOf("ird"), action.indexOf("new"));
        profile.setExtraAttributes(new ArrayList<>(Arrays.asList(old.split(","))));
        if (historyPosition > 0) {
            historyPosition -= 1;
        }

        HistoryController.setPosition(historyPosition);
    }
    public static void edited(ProfileDatabase currentDatabase, String action) {
        int id = Integer.parseInt(
                action.substring(0, action.indexOf("PROCEDURE")).replaceAll("[\\D]", ""));
        Profile profile = currentDatabase.getProfile(id);
        int procedurePlace = Integer.parseInt(
                action.substring(action.indexOf("PROCEDURE"), action.indexOf("EDITED"))
                        .replaceAll("[\\D]", ""));
        String previous = action
                .substring(action.indexOf("PREVIOUS(") + 9, action.indexOf(") OLD"));
        String[] previousValues = previous.split(",");
        String organs = action
                .substring(action.indexOf("[") + 1, action.indexOf("] CURRENT"));
        List<String> List = new ArrayList<>(Arrays.asList(organs.split(",")));
        ArrayList<Organ> organList = new ArrayList<>();
        System.out.println(organs);
        for (String organ : List) {
            System.out.println(organ);
            try {
                organList.add(Organ.valueOf(organ.replace(" ", "")));
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
