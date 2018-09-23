package server.model.database.hlatype;

import odms.commons.model.profile.HLAType;

import java.util.ArrayList;

public interface HLATypeDAO {
    /**
     * Get the HLA type for the profile.
     * @param profile to get the conditions for.
     */
    HLAType get(int profile);

    /**
     * Add a new condition to a profile.
     * @param profile to add the condition to.
     * @param hla to add.
     */
    void add(int profile, HLAType hla);

    /**
     * Remove a condition from a profile.
     * @param profile of hla to remove
     */
    void remove(int profile);

    /**
     * Update a condition for the profile.
     * @param hla to update.
     * @param profile of hla
     */
    void update(HLAType hla, int profile);

}
