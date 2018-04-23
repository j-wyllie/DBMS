package odms.data;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import odms.profile.Profile;

public class ProfileDatabase {

    private HashMap<Integer, Profile> profileDb = new HashMap<>();
    private Integer lastID = -1;
    private HashSet<Integer> deletedProfiles = new HashSet<>();

    /**
     * Find profile by ID
     *
     * @param profileID unique ID for requested profile
     * @return profile object
     */
    public Profile getProfile(Integer profileID) {
        return profileDb.get(profileID);
    }

    /**
     * Determine unique ID for profile and add the profile to the database
     *
     * @param profile new profile object
     * @throws IrdNumberConflictException IRD number already in use by another profile
     */
    public void addProfile(Profile profile) throws IrdNumberConflictException {
        lastID += 1;
        profile.setId(lastID);

        if (checkIRDNumberExists(profile.getIrdNumber())) {
            throw new IrdNumberConflictException("IRD number already in use", profile.getIrdNumber());
        }

        profileDb.put(lastID, profile);
    }

    /**
     * Check if an IRD number exists in the Profile Database
     *
     * @param irdNumber the number to be checked
     * @return boolean of whether or not it exists already.
     */
    private boolean checkIRDNumberExists(Integer irdNumber) {
        Set<Integer> irdNumbers = new HashSet<>();

        profileDb.forEach((id, profile) -> irdNumbers.add(profile.getIrdNumber()));

        return irdNumbers.contains(irdNumber);
    }

    /**
     * Remove profile from the database, adding their ID to the deletedID's set for
     * logging of removed profiles.
     *
     * @param id unique profile ID
     */
    public boolean deleteProfile(Integer id) {
        try {
            // Should deleted users simply be disabled for safety reasons?
            deletedProfiles.add(id);
            profileDb.remove(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Restore a previously deleted profile
     *
     * @param id ODMS ID of deleted profile
     * @param profile the profile to be restored
     * @return current ProfileDatabase lastId
     */
    public int restoreProfile(Integer id, Profile profile) {
        try {
            // Should deleted users simply be disabled for safety reasons?
            lastID += 1;
            profile.setId(lastID);
            profileDb.put(lastID, profile);
            deletedProfiles.remove(id);
            return lastID;
        } catch (Exception e) {
            e.printStackTrace();
            return lastID;
        }
    }


    public Integer getProfilePopulation() {
        return profileDb.size();
    }

    /**
     * Search for profiles via their given names
     *
     * @param searchTerm string of the names
     * @return Array of profiles found that match
     */
    public ArrayList<Profile> searchGivenNames(String searchTerm) {
        ArrayList<Profile> results = new ArrayList<>();

        profileDb.forEach((id, profile) -> {
            if (profile.getGivenNames().toLowerCase().equals(searchTerm.toLowerCase())) {
                results.add(profile);
            }
        });

        return results;
    }

    /**
     * Search for profiles via their last names
     *
     * @param searchTerm string of the names
     * @return Array of profiles found that match
     */
    public ArrayList<Profile> searchLastNames(String searchTerm) {
        ArrayList<Profile> results = new ArrayList<>();

        profileDb.forEach((id, profile) -> {
            if (profile.getLastNames().toLowerCase().equals(searchTerm.toLowerCase())) {
                results.add(profile);
            }
        });

        return results;
    }

    /**
     * Search for profiles via their IRD number
     *
     * @param searchTerm integer of the IRD number
     * @return Array of profiles found that match
     */
    public ArrayList<Profile> searchIRDNumber(Integer searchTerm) {
        ArrayList<Profile> results = new ArrayList<>();

        profileDb.forEach((id, profile) -> {
            if (profile.getIrdNumber().equals(searchTerm)) {
                results.add(profile);
            }
        });

        return results;
    }

    /**
     * Generate a list of profiles ordered by last names.
     * Parameter to specify whether or not the list contains every profile or only profiles that
     * are currently donating organs.
     *
     * @param donating specify donating organs or not
     * @return Array of profiles found that match
     */
    public ArrayList<Profile> getProfiles(boolean donating) {
        ArrayList<Profile> profiles = new ArrayList<>();

        profileDb.forEach((id, profile) -> {
            if (donating) {
                if (profile.getOrgans().size() > 0) {
                    profiles.add(profile);
                }
            } else {
                profiles.add(profile);
            }
        });

        profiles.sort(Comparator.comparing(Profile::getLastNames));

        return profiles;
    }

}
