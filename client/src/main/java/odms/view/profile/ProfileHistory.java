package odms.view.profile;

import odms.commons.model.profile.Profile;

public class ProfileHistory {
    private Profile currentProfile;

    public Profile getProfile() {
        return currentProfile;
    }

    private void setHistoryView() {
    }

    public void initialize(Profile p) {
        currentProfile = p;
        if (currentProfile != null) {
            setHistoryView();
        }
    }
}
