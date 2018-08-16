//package odms.controller.profile;
//
//
//public class ProfileHistory {
//
//    private odms.view.profile.ProfileHistory view;
//
//    public ProfileHistory(odms.view.profile.ProfileHistory v) {
//        view = v;
//    }
//      TODO: redo history
//    public String getHistory() {
//        String history = ProfileDataIO.getHistory();
//        history = history.replace(",", " ").replace("]", "")
//                .replace("[", "").replace("\\u003d", "=");
//        String[] histories = history.split("\"");
//
//        String historyDisplay = "";
//
//        for (String h : histories) {
//            if (!h.equals("") && h.contains("profile " + view.getProfile().getId() + " ")) {
//                historyDisplay += h + "\n";
//            }
//        }
//
//        return historyDisplay;
//    }
//}
//
//
