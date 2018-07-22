package odms.controller.history;

import odms.model.history.History;
import odms.model.profile.Profile;
import odms.model.user.User;

import java.util.ArrayList;

public final class HistoryController {

    private HistoryController() {
        throw new UnsupportedOperationException();
    }

    public static ArrayList<History> currentSessionHistory = new ArrayList<>();
    public static int historyPosition;
    public static ArrayList<Profile> deletedProfiles = new ArrayList<>();
    public static ArrayList<User> deletedUsers = new ArrayList<odms.model.user.User>();


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
