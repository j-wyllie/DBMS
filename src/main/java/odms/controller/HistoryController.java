package odms.controller;

import odms.profile.Profile;

import java.util.ArrayList;

public class HistoryController {
    public static ArrayList<String> currentSessionHistory = new ArrayList<>();
    public static int historyPosition = 0;
    public static ArrayList<Profile> deletedProfiles = new ArrayList<>();

    public static void updateHistory(String action) {
        if (getHistory().size() != 0) {
            if (getPosition()
                    != getHistory().size() - 1) {
                currentSessionHistory
                        .subList(getPosition(),
                                getHistory().size() - 1).clear();
            }
        }
        currentSessionHistory.add(action);
        historyPosition = currentSessionHistory.size() - 1;
    }
    public static ArrayList<String> getHistory() {
        return currentSessionHistory;
    }


    public static int getPosition() {
        return historyPosition;
    }
    public static void setPosition(int num) {historyPosition = num;}

}
