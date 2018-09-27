package odms.controller.database.hla;

import odms.commons.model.profile.HLAType;

/**
 * HLA Data Access Object interface.
 */
public interface HLADAO {

    /**
     * Gets the HLAType of the given profile
     *
     * @param profileID
     * @return
     */
    HLAType get(Integer profileID);

    /**
     * Adds the HLAType of the given profile
     *
     * @param profileID
     * @param hlaType
     */
    void add(Integer profileID, HLAType hlaType);

    /**
     * Edits the HLAType of the given profile
     *
     * @param profileID
     * @param hlaType
     */
    void edit(Integer profileID, HLAType hlaType);

    /**
     * Deletes the HLAType of the given profile
     *
     * @param profileID
     */
    void delete(Integer profileID);
}
