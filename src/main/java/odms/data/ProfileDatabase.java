package odms.data;

import java.util.*;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import odms.profile.Organ;
import odms.profile.Profile;

public class ProfileDatabase {

    private HashMap<Integer, Profile> profileDb = new HashMap<>();
    private HashSet<Integer> deletedProfiles = new HashSet<>();

    private Integer lastID = -1;
    private String path;

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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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
                if (profile.getOrgansDonating().size() > 0) {
                    profiles.add(profile);
                }
            } else {
                profiles.add(profile);
            }
        });

        profiles.sort(Comparator.comparing(Profile::getLastNames));

        return profiles;
    }

    /**
     * Compares each string in the names array to the searchString. Returns the weightedRatio value of the string that
     * was the closest to the searchString.
     * @param searchString the string that the donor names will be searched against.
     * @param names String array of profile names.
     * @return ratio value, represents how close of a match the closest name is to the searchString
     */
    private int stringMatcher(String searchString, String[] names) {
        int searchLength = searchString.length();
        int ratio = 0;

        for (String name : names) {
            int tempRatio;

            if (name.length() < searchLength) {
                tempRatio = FuzzySearch.weightedRatio(searchString, name);
            } else {
                tempRatio = FuzzySearch.weightedRatio(searchString, name.substring(0, searchLength));
            }
            if (tempRatio > ratio) {
                ratio = tempRatio;
            }
        }
        return ratio;
    }

    //TODO add preferred name when that functionality is in dev
    /**
     * Fuzzy search that finds profiles with a name similar to the search string. Order of results goes as follows:
     * Exact matches in last names ordered alphabetically,
     * Exact matches in first names ordered alphabetically,
     * Similar matches in last names ordered alphabetically,
     * Similar matches in first names ordered alphabetically
     * @param searchString the string that the donor names will be searched against.
     * @return list of donors that match the provided search string, with a max size of 30.
     */
    public ArrayList<Profile> searchProfiles(String searchString) {
        // Constant that represents the cutoff at which profiles will not be added to search results
        final Integer matchLimit = 60;

        // Need separate Lists to order results by relevance.
        ArrayList<Profile> profilesSimilarFirst = new ArrayList<>();
        ArrayList<Profile> profilesSimilarLast = new ArrayList<>();
        ArrayList<Profile> profilesMatchesFirst = new ArrayList<>();
        ArrayList<Profile> profilesMatchesLast = new ArrayList<>();
        ArrayList<Profile> profiles = new ArrayList<>();

        if (searchString.equals("")) {
            return null;
        }

        for (Profile profile : getProfiles(false)) {
            int ratio;
            int tempRatio;
            String nameCategory;
            String[] namesFirst = profile.getGivenNames().split(" ");
            String[] namesLast = profile.getLastNames().split(" ");

            tempRatio = stringMatcher(searchString, namesFirst);
            ratio = tempRatio;
            nameCategory = "first";

            tempRatio = stringMatcher(searchString, namesLast);
            if (tempRatio >= ratio) {
                ratio = tempRatio;
                nameCategory = "last";
            }

            // Ratio of 100 is a exact match, these need to be at top of results.
            if (ratio == 100 && nameCategory.equals("last")) {
                profilesMatchesLast.add(profile);
            } else if (ratio == 100 && nameCategory.equals("first")) {
                profilesMatchesFirst.add(profile);
            // If ratio is below 60 don't include profile because it doesn't match close enough to searchString
            } else if (ratio >= matchLimit && nameCategory.equals("last")) {
                profilesSimilarLast.add(profile);
            } else if (ratio >= matchLimit && nameCategory.equals("first")) {
                profilesSimilarFirst.add(profile);
            }
        }

        // Sorts each list by alphabetical order based on profile full name
        Collections.sort(profilesMatchesLast);
        Collections.sort(profilesMatchesFirst);
        Collections.sort(profilesSimilarLast);
        Collections.sort(profilesSimilarFirst);

        // Added in this order to match relevance order of last name, first name. Complete matches are always at top.
        profiles.addAll(profilesMatchesLast);
        profiles.addAll(profilesMatchesFirst);
        profiles.addAll(profilesSimilarLast);
        profiles.addAll(profilesSimilarFirst);

        return profiles;
    }

    /**
     * Generates a list of profiles receiving organs ordered by last names.
     * Parameter to specify whether or not the list contains every receiver or only receivers that
     * are currently receiving organs.
     *
     * @param receiving specify currently receiving organs or not
     * @return Array of profiles found that match
     */
    public ArrayList<Profile> getReceivers(Boolean receiving) {
        ArrayList<Profile> profiles = new ArrayList<>();

        profileDb.forEach((id, profile) -> {

            if (profile.isReceiver()) {
                if (receiving) {
                    if (profile.getOrgansRequired().size() > 0) {
                        profiles.add(profile);
                    }
                } else {
                    profiles.add(profile);
                }
            }
        });

        profiles.sort(Comparator.comparing(Profile::getLastNames));
        return profiles;
    }


    /**
     * Generates a collection of a profile and organ for each organ that a receiver requires
     *
     *  @return Collection of Profile and Organ that match
     */
    public List<Entry<Profile, Organ>> getAllOrgansRequired() {
        List<Entry<Profile, Organ>> receivers = new ArrayList<>();

        ArrayList<Profile> allReceivers = getReceivers(true);

        for (Profile profile : allReceivers) {
            for (Organ organ : profile.getOrgansRequired()) {
                Map.Entry<Profile, Organ> pair = new SimpleEntry<>(profile, organ);
                receivers.add(pair);
            }
        }
        return receivers;
    }

}
