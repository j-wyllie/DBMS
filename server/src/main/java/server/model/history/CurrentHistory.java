package server.model.history;

import java.util.ArrayList;
import server.model.profile.Profile;
import server.model.user.User;


public final class CurrentHistory {

    private CurrentHistory() {
        throw new UnsupportedOperationException();
    }

    public static ArrayList<History> currentSessionHistory = new ArrayList<>();
    public static int historyPosition;
    public static ArrayList<Profile> deletedProfiles = new ArrayList<>();
    public static ArrayList<User> deletedUsers = new ArrayList<User>();


    /**
     * Adds a new history to current session history and removed undone ones
     *
     * @param history
     */
    public static void updateHistory(History history) {
        if (getHistory().size() != 0 &&
                getPosition() != getHistory().size() - 1) {
            currentSessionHistory
                    .subList(getPosition(),
                            getHistory().size() - 1).clear();
        }
        currentSessionHistory.add(history);
        historyPosition = currentSessionHistory.size() - 1;
    }

    public static ArrayList<History> getHistory() {
        return currentSessionHistory;
    }


    public static int getPosition() {
        return historyPosition;
    }

    public static void setPosition(int num) {
        historyPosition = num;
    }

}
