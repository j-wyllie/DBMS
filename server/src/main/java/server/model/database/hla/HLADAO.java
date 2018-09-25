package server.model.database.hla;

import java.util.List;

/**
 * HLA Data Access Object interface.
 */
public interface HLADAO {

    /**
     * Select the Group X HLA's from a profile.
     *
     * @param profileId the profile ID to operate against.
     * @return a collection of HLA's.
     */
    List<HLA> getGroupX(Integer profileId);

    /**
     * Select the Group Y HLA's from a profile.
     *
     * @param profileId the profile ID to operate against.
     * @return a collection of HLA's.
     */
    List<HLA> getGroupY(Integer profileId);

    /**
     * Select the non-primary HLA's from a profile.
     *
     * @param profileId the profile ID to operate against.
     * @return a collection of HLA's.
     */
    List<HLA> getNonPrimary(Integer profileId);

    /**
     * Add a new HLA to a profile.
     *
     * @param profileId to add the HLA to.
     * @param hla to add.
     */
    void add(Integer profileId, HLA hla);

    /**
     * Remove a hlaType from a profile.
     *
     * @param profileId of HLA to remove.
     * @param hla to add.
     */
    void remove(Integer profileId, HLA hla);

    /**
     * Remove all HLAs from a profile.
     *
     * @param profileID of HLA to remove.
     */
    void removeAll(Integer profileID);
}
