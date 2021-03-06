package odms.controller.profile;

import lombok.extern.slf4j.Slf4j;
import odms.commons.model.enums.OrganEnum;
import odms.commons.model.profile.ExpiredOrgan;
import odms.commons.model.profile.Profile;
import odms.controller.CommonController;
import odms.controller.database.DAOFactory;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Controller class for the organ display view.
 */
@Slf4j
public class OrganDisplay extends CommonController {

    /**
     * Gets profile data from the database. This is to confirm is the donating organs are expired or
     * not.
     *
     * @param p current profile that is being viewed
     * @return the updated profile object
     */
    public Profile getUpdatedProfileDetails(Profile p) {
        Profile profile = null;
        try {
            profile = DAOFactory.getProfileDao().get(p.getId());
            profile.addOrgansRequired(DAOFactory.getOrganDao().getRequired(p));
            odms.controller.user.AvailableOrgans controller = new odms.controller.user.AvailableOrgans();
            List<Map.Entry<Profile, OrganEnum>> availableOrgans =
                    controller.getAllOrgansAvailable();
            for (Map.Entry<Profile, OrganEnum> m : availableOrgans) {
                controller.checkOrganExpired(m.getValue(), m.getKey());
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }

        return profile;
    }

    /**
     * Gets donating organs with manually expired organs removed.
     * @param profile profile to get organs info from
     * @return set of donating organs
     */
    public Set<OrganEnum> getDonatingOrgans(Profile profile) {
        Set<OrganEnum> expiredOrgans = new HashSet<>();
        try {
            List<ExpiredOrgan> expired = DAOFactory.getOrganDao().getExpired(profile);
            for (ExpiredOrgan organ : expired) {
                expiredOrgans.add(organ.getOrgan());
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }

        Set<OrganEnum> donatingOrgans = new HashSet(profile.getOrgansDonating());
        donatingOrgans.removeAll(expiredOrgans);

        return donatingOrgans;
    }
}
