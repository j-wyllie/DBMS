package odms.data;

import java.util.*;

import me.xdrop.fuzzywuzzy.FuzzySearch;
import me.xdrop.fuzzywuzzy.model.ExtractedResult;
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
     * @throws IrdNumberConflictException IRD number already in use by another profile
     */
    public void addDonor(Donor donor) throws IrdNumberConflictException {
        lastID += 1;
        donor.setId(lastID);

        if (checkIRDNumberExists(donor.getIrdNumber())) {
            throw new IrdNumberConflictException("IRD number already in use", donor.getIrdNumber());
        }

        donorDb.put(lastID, donor);
    }

    /**
     * Check if an IRD number exists in the Donor Database
     *
     * @param irdNumber the number to be checked
     * @return boolean of whether or not it exists already.
     */
    private boolean checkIRDNumberExists(Integer irdNumber) {
        Set<Integer> irdNumbers = new HashSet<>();

        donorDb.forEach((id, donor) -> irdNumbers.add(donor.getIrdNumber()));

        return irdNumbers.contains(irdNumber);
    }

    /**
     * Remove donor from the database, adding their ID to the deletedID's set for
     * logging of removed donors.
     *
     * @param id unique donor ID
     */
    public boolean deleteDonor(Integer id) {
        try {
            // Should deleted users simply be disabled for safety reasons?
            deletedDonors.add(id);
            donorDb.remove(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public int undeleteDonor(Integer id, Donor donor) {
        try {
            // Should deleted users simply be disabled for safety reasons?
            lastID += 1;
            donor.setId(lastID);
            System.out.println("a");
            donorDb.put(lastID, donor);
            deletedDonors.remove(id);
            return lastID;
        } catch (Exception e) {
            e.printStackTrace();
            return lastID;
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
            if (donor.getIrdNumber().equals(searchTerm)) {
                results.add(donor);
            }
        });

        return results;
    }

    /**
     * Generate a list of donors ordered by last names.
     * Parameter to specify whether or not the list contains every donor or only donors that
     * are currently donating organs.
     *
     * @param donating specify donating organs or not
     * @return list of donors ordered by last name
     */
    public ArrayList<Donor> getDonors(boolean donating) {
        ArrayList<Donor> donors = new ArrayList<>();

        donorDb.forEach((id, donor) -> {
            if (donating) {
                if (donor.getOrgans().size() > 0) {
                    donors.add(donor);
                }
            } else {
                donors.add(donor);
            }
        });

        donors.sort(Comparator.comparing(Donor::getLastNames));

        return donors;
    }

    /**
     * Fuzzy search that finds the top 30 donors that match the provide search string.
     * @param searchString the string that the donor names will be searched against.
     */
    public ArrayList<Donor> searchDonors(String searchString) {
        boolean donating = false;
        ArrayList<String> donors = new ArrayList<>();

        if (searchString == null || searchString.equals("")) {
            return getDonors(donating);
        }

        for (Donor donor : getDonors(donating)) {
            donors.add(donor.getFullName());
        }

        //Fuzzywuzzy, fuzzy search algorithm. Returns list of donor names sorted by closest match to the searchString.
        List<ExtractedResult> result;
        result = FuzzySearch.extractSorted(searchString, donors, 30);

        //Use index values from fuzzywuzzy search to build list of donor object in same order returned from fuzzywuzzy.
        ArrayList<Donor> resultDonors = new ArrayList<>();
        for (ExtractedResult er : result) {
            resultDonors.add(getDonors(donating).get(er.getIndex()));
        }
        return resultDonors;
    }

}
