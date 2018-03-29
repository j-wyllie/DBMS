package odms.data;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import odms.profile.Profile;

public class ProfileDatabase {

    private HashMap<Integer, Profile> donorDb = new HashMap<>();
    private Integer lastID = -1;
    private HashSet<Integer> deletedDonors = new HashSet<>();

    /**
     * Find profile by ID
     *
     * @param donorID unique ID for requested profile
     * @return profile object
     */
    public Profile getDonor(Integer donorID) {
        return donorDb.get(donorID);
    }

    /**
     * Determine unique ID for profile and add the profile to the database
     *
     * @param profile new profile object
     * @throws IrdNumberConflictException IRD number already in use by another profile
     */
    public void addDonor(Profile profile) throws IrdNumberConflictException {
        lastID += 1;
        profile.setId(lastID);

        if (checkIRDNumberExists(profile.getIrdNumber())) {
            throw new IrdNumberConflictException("IRD number already in use", profile.getIrdNumber());
        }

        donorDb.put(lastID, profile);
    }

    /**
     * Check if an IRD number exists in the Profile Database
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
     * Remove profile from the database, adding their ID to the deletedID's set for
     * logging of removed donors.
     *
     * @param id unique profile ID
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

    public int undeleteDonor(Integer id, Profile profile) {
        try {
            // Should deleted users simply be disabled for safety reasons?
            lastID += 1;
            profile.setId(lastID);
            System.out.println("a");
            donorDb.put(lastID, profile);
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
    public ArrayList<Profile> searchGivenNames(String searchTerm) {
        ArrayList<Profile> results = new ArrayList<>();

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
    public ArrayList<Profile> searchLastNames(String searchTerm) {
        ArrayList<Profile> results = new ArrayList<>();

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
    public ArrayList<Profile> searchIRDNumber(Integer searchTerm) {
        ArrayList<Profile> results = new ArrayList<>();

        donorDb.forEach((id, donor) -> {
            if (donor.getIrdNumber().equals(searchTerm)) {
                results.add(donor);
            }
        });

        return results;
    }

    /**
     * Generate a list of donors ordered by last names.
     * Parameter to specify whether or not the list contains every profile or only donors that
     * are currently donating organs.
     *
     * @param donating specify donating organs or not
     * @return list of donors ordered by last name
     */
    public ArrayList<Profile> getDonors(boolean donating) {
        ArrayList<Profile> profiles = new ArrayList<>();

        donorDb.forEach((id, donor) -> {
            if (donating) {
                if (donor.getOrgans().size() > 0) {
                    profiles.add(donor);
                }
            } else {
                profiles.add(donor);
            }
        });

        profiles.sort(Comparator.comparing(Profile::getLastNames));

        return profiles;
    }

}
