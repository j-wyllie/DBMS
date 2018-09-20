package odms.commons.model.history;

import java.util.ArrayList;
import java.util.List;

public final class CurrentHistory {

    private CurrentHistory() {
        throw new UnsupportedOperationException();
    }

    private static List<History> currentSessionHistory = new ArrayList<>();
    private static int historyPosition;

    /**
     * Adds a new history to current session history and removed undone ones
     *
     * @param history the history item
     */
    public static void updateHistory(History history) {
        if (!getHistory().isEmpty() &&
                historyPosition != getHistory().size() - 1) {
            currentSessionHistory
                    .subList(historyPosition,
                            getHistory().size() - 1).clear();
        }
        currentSessionHistory.add(history);
        historyPosition = currentSessionHistory.size() - 1;
    }

    public static List<History> getHistory() {
        return currentSessionHistory;
    }

    public static void setPosition(int num) {
        historyPosition = num;
    }

}
