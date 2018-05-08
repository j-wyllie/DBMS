package odms.controller;

import odms.data.ProfileDatabase;
import odms.profile.Organ;
import odms.profile.OrganConflictException;
import odms.profile.Profile;
import odms.user.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
                    System.out.println(historyPosition);
                    System.out.println(currentSessionHistory);
                    action = currentSessionHistory.get(historyPosition);
                }
                int end = action.indexOf(" at");
                action = action.substring(0, action.indexOf(" at"));
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
                }  else if (action.contains("updated")) {
                    updated(action, end);
                }
                HistoryController.setPosition(historyPosition);
                System.out.println("Command redone");
            } else {
                System.out.println("There are no commands to redo");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("No commands have been entered.");
        }
    }

    private static void updated(String action, int end) {
        int id = Integer.parseInt(action.replaceAll("[\\D]", ""));
        User user = LoginController.userDatabase.getClinician(id);
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
        Set<String> organSet = new HashSet<>(Arrays.asList(
                action.substring(action.indexOf("[") + 1, action.indexOf("]")).split(",")));
        profile.removeOrgans(organSet);
    }
    public static void set(ProfileDatabase currentDatabase, String action) throws OrganConflictException {
        int id = Integer.parseInt(action.replaceAll("[\\D]", ""));
        Profile profile = currentDatabase.getProfile(id);
        profile.addOrgansDonating(Organ.stringListToOrganSet(Arrays.asList(
                action.substring(
                        action.indexOf("[") + 1,
                        action.indexOf("]")).split(",")
        )));
    }
    public static void donate(ProfileDatabase currentDatabase, String action) {
        int id = Integer.parseInt(action.replaceAll("[\\D]", ""));
        Profile profile = currentDatabase.getProfile(id);
        profile.addOrgansDonated(
                Organ.stringListToOrganSet(
                        Arrays.asList(
                                action.substring(
                                        action.indexOf("[") + 1,
                                        action.indexOf("]")).split(",")
                        )
                )
        );
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
        ArrayList<Organ> organList = new ArrayList<>();
        organs = action.substring(action.indexOf("NEWORGANS["), action.indexOf("]END"));
        List<String> List = new ArrayList<>(Arrays.asList(organs.split(",")));
        for(String organ : List){
            System.out.println(organ);
            organList.add(Organ.valueOf(organ.replace(" ","").replace("NEWORGANS[","")));
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
