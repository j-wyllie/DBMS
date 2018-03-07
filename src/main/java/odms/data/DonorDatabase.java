package odms.data;

import java.util.HashMap;
import java.util.HashSet;
import odms.donor.Donor;

public class DonorDatabase {
    private HashMap<Integer, Donor> donorDb = new HashMap<>();
    private Integer lastID = -1;
    private HashSet<Integer> deletedDonors = new HashSet<>();

    /**
     * Find donor by ID
     *
     * @param donorID unique ID for requested donor
     * @return donor object
     */
    public Donor getDonor(Integer donorID) {
        return donorDb.get(donorID);
    }

    /**
     * Determine unique ID for donor and add the donor to the database
     *
     * @param donor new donor object
     */
    public void addDonor(Donor donor) {
        try {
            lastID += 1;
            donor.setId(lastID);
            donorDb.put(lastID, donor);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Remove donor from the database, adding their ID to the deletedID's set for
     * logging of removed donors.
     *
     * @param id unique donor ID
     */
    public void deleteDonor(Integer id) {
        try {
            // Should deleted users simply be disabled for safety reasons?
            deletedDonors.add(id);
            donorDb.remove(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Integer getDonorPopulation() {
        return donorDb.size();
    }

}
