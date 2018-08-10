package server.model.history;

import java.time.LocalDateTime;

public class History {

    private String historyType;
    private Integer historyId;
    private String historyAction;
    private String historyData;
    private Integer historyDataIndex;
    private LocalDateTime historyTimestamp;

    public History(String type, int id, String action, String data, int index,
            LocalDateTime timestamp) {
        historyType = type;
        historyId = id;
        historyAction = action;
        historyData = data;
        historyDataIndex = index;
        historyTimestamp = timestamp;
    }

    public String toString() {
        String historyString;
        if (historyDataIndex != -1) {
            historyString = historyType + " " + historyId + " performed action " + historyAction +
                    " with data being " + historyData + " at an index of "
                    + historyDataIndex + " at " + historyTimestamp;
        } else {
            historyString = historyType + " " + historyId + " performed action " + historyAction
                    + " with data being " + historyData + " at an index of "
                    + historyDataIndex + " at " + historyTimestamp;
        }
        return historyString;
    }

    public String getHistoryType() {
        return historyType;
    }

    public int getHistoryId() {
        return historyId;
    }

    public void setHistoryId(int id) {
        historyId = id;
    }

    public String getHistoryAction() {
        return historyAction;
    }

    public String getHistoryData() {
        return historyData;
    }

    public void setHistoryData(String s) {
        historyData = s;
    }

    public int getHistoryDataIndex() {
        return historyDataIndex;
    }

    public void setHistoryDataIndex(int index) {
        historyDataIndex = index;
    }

    public LocalDateTime getHistoryTimestamp() {
        return historyTimestamp;
    }

    public void setHistoryTimestamp(LocalDateTime t) {
        historyTimestamp = t;
    }
}
