package odms.controller.profile;

import odms.controller.CommonController;
import odms.model.profile.Profile;

public class OrganOverride extends CommonController {
    odms.view.profile.OrganOverride view;

    public OrganOverride(odms.view.profile.OrganOverride view) {
        this.view = view;
    }

    public void confirm(Profile currentProfile) {
    }
}
