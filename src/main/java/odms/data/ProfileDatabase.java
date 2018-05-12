package odms.data;

import java.lang.reflect.Array;
import java.util.*;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import me.xdrop.fuzzywuzzy.FuzzySearch;
import me.xdrop.fuzzywuzzy.model.ExtractedResult;
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
     * Fuzzy search that finds the top 30 donors that match the provided search string.
     * @param searchString the string that the donor names will be searched against.
     * @return list of donors that match the provided search string, with a max size of 30.
     */
    public ArrayList<Profile> searchProfiles(String searchString, int ageSearchInt, String regionSearchString, List selectedGenders, List selectedTypes, List selectedOrgans) {
        ArrayList<String> profiles = new ArrayList<>();
        ArrayList<Profile> resultProfiles = getProfiles(false);

        //parsing out organs as strings for use
        List<String> selectedOrgansStrings = new ArrayList<>();
        if (selectedOrgans != null) {
            for (int i = 0; i< selectedOrgans.size(); i++) {
                //todo need some consistency in how we are naming organs that have two words in them.
                if (selectedOrgans.get(i).toString().toLowerCase().equals("connective tissue")) {
                    selectedOrgansStrings.add("connective-tissue");
                }
                if (selectedOrgans.get(i).toString().toLowerCase().equals("bone marrow")) {
                    selectedOrgansStrings.add("bone-marrow");
                }
                selectedOrgansStrings.add(selectedOrgans.get(i).toString().toLowerCase());
            }
        }

        //need some data for testing, test data not fully populated i think--------
        resultProfiles.forEach(profile -> profile.setGender("male"));
        //resultProfiles.forEach(profile -> profile.setReceiver(false));

        resultProfiles.get(0).setGender("female");
        resultProfiles.get(3).setGender("female");
        resultProfiles.get(7).setGender("female");
        resultProfiles.get(10).setGender("female");
        resultProfiles.get(11).setGender("female");

        resultProfiles.get(0).setReceiver(true);
        resultProfiles.get(0).setDonor(false);
        // ------------------------------------------------------------------------

        if (searchString.equals("") && searchString.equals("") && ageSearchInt == -999 && selectedGenders.isEmpty() && selectedTypes.isEmpty() && selectedOrgans.isEmpty()) {
            return getProfiles(false);
        }

        //definitely need a better way than just a magic number lol
        if (ageSearchInt != -999) {
            resultProfiles.removeIf(profile -> profile.getAge() != ageSearchInt);

        }

        //todo i think region might need some work, not initialized in test data? same as some genders
        //resultProfiles.forEach(profile -> System.out.println(profile.getRegion()));          // <<<<<< they are all null

        if (!regionSearchString.equals("")) {
            resultProfiles.removeIf(profile -> !profile.getRegion().equals(regionSearchString));
        }


        if (!selectedGenders.isEmpty()) {
            System.out.println("genders");
            resultProfiles.removeIf(profile -> !selectedGenders.contains(profile.getGender()));
        }


        if (!selectedOrgans.isEmpty()) {
            System.out.println("organs");
            resultProfiles.removeIf(profile -> {

                HashSet<Organ> organsDonatingHash = profile.getOrgansDonating();
                List<String> organsDonating = new ArrayList<String>();

                for (Organ temp : organsDonatingHash) {
                    organsDonating.add(temp.getName());
                }

                if (organsDonating == null || organsDonating.size() == 0) {
                    return true;
                }
                organsDonating.retainAll(selectedOrgansStrings);         //intersection
                return organsDonating.size() == 0;
            });

        }

        if (!selectedTypes.isEmpty()) {
            resultProfiles.removeIf(profile -> {
                ArrayList<String> profileTypes = new ArrayList();
                System.out.println("types");

                if (profile.isReceiver()) {profileTypes.add("receiver");}
                try {
                    if (profile.getDonor()) {profileTypes.add("donor");}
                } catch (NullPointerException e) {
                    //profile.getDonor() throws null pointer, is this not initialized for all?
                    return true;
                }

                if (profileTypes.size() == 0) {
                    return true;
                }

                //edge case, if they only ask for donors, and a profile is a donor and receiver, it would display.
                //since they can specify if they want BOTH receivers and donors
                if (profileTypes.size() == 2 && selectedTypes.size() == 1) {
                    return true;
                }


                profileTypes.retainAll(selectedTypes);
                return (profileTypes.size() == 0);
            });
        }



        if (searchString.equals("")) {
            return resultProfiles;
        }

        for (Profile profile : resultProfiles) {
            profiles.add(profile.getFullName());
        }

        //Fuzzywuzzy, fuzzy search algorithm. Returns list of donor names sorted by closest match to the searchString.
        List<ExtractedResult> result;
        result = FuzzySearch.extractSorted(searchString, profiles, 50);

        //Use index values from fuzzywuzzy search to build list of donor object in same order returned from fuzzywuzzy.
        resultProfiles = new ArrayList<>();
        for (ExtractedResult er : result) {
            resultProfiles.add(getProfiles(false).get(er.getIndex()));
        }
        return resultProfiles;
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
