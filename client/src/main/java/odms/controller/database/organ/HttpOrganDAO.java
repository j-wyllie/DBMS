package odms.controller.database.organ;

import java.util.Set;
import odms.commons.model.enums.OrganEnum;
import odms.commons.model.profile.OrganConflictException;
import odms.commons.model.profile.Profile;

public class HttpOrganDAO implements OrganDAO {

    @Override
    public Set<OrganEnum> getDonations(Profile profile) {
        return null;
    }

    @Override
    public Set<OrganEnum> getDonating(Profile profile) {
        return null;
    }

    @Override
    public Set<OrganEnum> getRequired(Profile profile) {
        return null;
    }

    @Override
    public Set<OrganEnum> getReceived(Profile profile) {
        return null;
    }

    @Override
    public void addDonation(Profile profile, OrganEnum organ) {

    }

    @Override
    public void addDonating(Profile profile, OrganEnum organ) throws OrganConflictException {

    }

    @Override
    public void addRequired(Profile profile, OrganEnum organ) {

    }

    @Override
    public void addReceived(Profile profile, OrganEnum organ) {

    }

    @Override
    public void removeDonation(Profile profile, OrganEnum organ) {

    }

    @Override
    public void removeDonating(Profile profile, OrganEnum organ) {

    }

    @Override
    public void removeRequired(Profile profile, OrganEnum organ) {

    }

    @Override
    public void removeReceived(Profile profile, OrganEnum organ) {

    }
}
