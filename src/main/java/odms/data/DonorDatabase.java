package odms.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
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
     * Check if an IRD number exists in the Donor Database
     *
     * @param irdNumber the number to be checked
     * @return boolean of whether or not it exists already.
     */
    public boolean checkIRDNumberExists(Integer irdNumber) {
        Set<Integer> irdNumbers = new HashSet<>();

        donorDb.forEach((id, donor) -> irdNumbers.add(donor.getIRD()));

        return irdNumbers.contains(irdNumber);
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

    /**
     * Search for donors via their given names
     *
     * @param searchTerm string of the names
     * @return Array of Donors found that match
     */
    public ArrayList<Donor> searchGivenNames(String searchTerm) {
        ArrayList<Donor> results = new ArrayList<>();

        donorDb.forEach((id, donor) -> {
            if (donor.getGivenNames().toLowerCase().equals(searchTerm.toLowerCase())) {
                results.add(donor);
            }
        });

        return results;
    }

    /**
     * Search for donors via their last names
     *
     * @param searchTerm string of the names
     * @return Array of Donors found that match
     */
    public ArrayList<Donor> searchLastNames(String searchTerm) {
        ArrayList<Donor> results = new ArrayList<>();

        donorDb.forEach((id, donor) -> {
            if (donor.getLastNames().toLowerCase().equals(searchTerm.toLowerCase())) {
                results.add(donor);
            }
        });

        return results;
    }

    /**
     * Search for donors via their IRD number
     *
     * @param searchTerm integer of the IRD number
     * @return Array of Donors found that match
     */
    public ArrayList<Donor> searchIRDNumber(Integer searchTerm) {
        ArrayList<Donor> results = new ArrayList<>();

        donorDb.forEach((id, donor) -> {
            if (donor.getIRD().equals(searchTerm)) {
                results.add(donor);
            }
        });

        return results;
    }

}
