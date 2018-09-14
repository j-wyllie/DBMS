package odms.controller.history;

import java.util.ArrayList;
import java.util.List;
import odms.commons.model.history.History;
import odms.commons.model.profile.Profile;

public final class CurrentHistory {

    private CurrentHistory() {
        throw new UnsupportedOperationException();
    }

    private static List<History> currentSessionHistory = new ArrayList<>();
    private static int historyPosition;
    private static List<Profile> deletedProfiles = new ArrayList<>();

    /**
     * Adds a new history to current session history and removed undone ones.
     *
     * @param history the history item
     */
    public static void updateHistory(History history) {
        if (!getHistory().isEmpty() && historyPosition != getHistory().size() - 1) {
            currentSessionHistory
                    .subList(historyPosition,
                            getHistory().size() - 1).clear();
        }
        currentSessionHistory.add(history);
        historyPosition = currentSessionHistory.size() - 1;
    }

    public static List<History> getCurrentSessionHistory() {
        return currentSessionHistory;
    }

    public static List<Profile> getDeletedProfiles() {
        return deletedProfiles;
    }

    public static List<History> getHistory() {
        return currentSessionHistory;
    }

}
