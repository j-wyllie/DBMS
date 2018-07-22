package odms.controller.profile;

import odms.controller.data.ProfileDataIO;
import odms.view.profile.ProfileHistoryViewTODO;

public class ProfileHistoryTabController {

    private ProfileHistoryViewTODO view;

    public ProfileHistoryTabController(ProfileHistoryViewTODO v) {
        view = v;
    }

    public String getHistory() {
        String history = ProfileDataIO.getHistory();
        history = history.replace(",", " ").replace("]", "")
                .replace("[", "").replace("\\u003d", "=");
        String[] histories = history.split("\"");

        String historyDisplay = "";

        for (String h : histories) {
            if (!h.equals("") && h.contains("profile " + view.getProfile().getId() + " ")) {
                historyDisplay += h + "\n";
            }
        }

        return historyDisplay;
    }
}
