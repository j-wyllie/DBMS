package odms.model.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import me.xdrop.fuzzywuzzy.model.ExtractedResult;
import odms.controller.data.IrdNumberConflictException;
import odms.model.enums.OrganEnum;
import odms.model.profile.Profile;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;

/**
 * Handles the local profile database.
 * Contains method to manipulate profiles.
 */
public class ProfileDatabase {

    private Map<Integer, Profile> profileDb = new HashMap<>();
    private Set<Integer> deletedProfiles = new HashSet<>();

    private Integer lastID = -1;
    private String path;

    /**
     * Find profile by ID.
     *
     * @param profileID unique ID for requested profile.
     * @return profile object.
     */
    public Profile getProfile(Integer profileID) {
        return profileDb.get(profileID);
    }

    /**
     * Determine unique ID for profile and add the profile to the database.
     *
     * @param profile new profile object.
     * @throws IrdNumberConflictException IRD number already in use by another profile.
     */
    public void addProfile(Profile profile) throws IrdNumberConflictException {
        lastID += 1;
        profile.setId(lastID);

        if (checkIRDNumberExists(profile.getIrdNumber())) {
            throw new IrdNumberConflictException("IRD number already in use",
                    profile.getIrdNumber());
        }

        profileDb.put(lastID, profile);
    }

    /**
     * Check if an IRD number exists in the profile Database.
     *
     * @param irdNumber the number to be checked.
     * @return boolean of whether or not it exists already.
     */
    private boolean checkIRDNumberExists(Integer irdNumber) {
        Set<Integer> irdNumbers = new HashSet<>();

        profileDb.forEach((id, profile) -> irdNumbers.add(profile.getIrdNumber()));

        return irdNumbers.contains(irdNumber);
    }

    /**
     * Remove profile from the database, adding their ID to the deletedID's set for logging of
     * removed profiles.
     *
     * @param id unique profile ID.
     * @return Boolean based on whether the profile was successfully removed.
     */
    public boolean deleteProfile(Integer id) {
        try {
            deletedProfiles.add(id);
            profileDb.remove(id);
            return true;
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
    }

    /**
     * Restore a previously deleted profile.
     *
     * @param id      ODMS ID of deleted profile.
     * @param profile the profile to be restored.
     * @return current ProfileDatabase lastId.
     */
    public int restoreProfile(Integer id, Profile profile) {
        lastID += 1;
        profile.setId(lastID);
        profileDb.put(lastID, profile);
        deletedProfiles.remove(id);
        return lastID;
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
     * Search for profiles via their given names.
     *
     * @param searchTerm string of the names.
     * @return Array of profiles found that match.
     */
    public List<Profile> searchGivenNames(String searchTerm) {
        List<Profile> results = new ArrayList<>();

        profileDb.forEach((id, profile) -> {
            if (profile.getGivenNames().equalsIgnoreCase(searchTerm)) {
                results.add(profile);
            }
        });

        return results;
    }

    /**
     * Search for profiles via their last names.
     *
     * @param searchTerm string of the names.
     * @return Array of profiles found that match.
     */
    public List<Profile> searchLastNames(String searchTerm) {
        List<Profile> results = new ArrayList<>();

        profileDb.forEach((id, profile) -> {
            if (profile.getLastNames().equalsIgnoreCase(searchTerm)) {
                results.add(profile);
            }
        });

        return results;
    }

    /**
     * Search for profiles via their IRD number.
     *
     * @param searchTerm integer of the IRD number.
     * @return Array of profiles found that match.
     */
    public List<Profile> searchIRDNumber(Integer searchTerm) {
        List<Profile> results = new ArrayList<>();

        profileDb.forEach((id, profile) -> {
            if (profile.getIrdNumber().equals(searchTerm)) {
                results.add(profile);
            }
        });

        return results;
    }

    /**
     * Generate a list of profiles ordered by last names. Parameter to specify whether or not the
     * list contains every profile or only profiles that are currently donating organs.
     *
     * @param donating specify donating organs or not
     * @return Array of profiles found that match
     */
    public List<Profile> getProfiles(boolean donating) {
        List<Profile> profiles = new ArrayList<>();

        profileDb.forEach((id, profile) -> {
            if (donating) {
                if (!profile.getOrgansDonating().isEmpty()) {
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
     * Searches a given list of profiles with a given search string using fuzzy search
     *
     * @param profilesGiven list of profiles to search through
     * @param searchString  string to match profiles against
     * @param type          type of attribute to filter against
     * @return the filtered list of profiles
     */
    private List<Profile> fuzzySearch(List<Profile> profilesGiven, String searchString,
            String type) {
        ArrayList<Profile> resultProfiles = new ArrayList<>();
        ArrayList<String> profiles = new ArrayList<>();
        ArrayList<Profile> temp = new ArrayList<>();

        switch (type) {
            case "name":
                return searchProfilesName(profilesGiven, searchString);
            case "region":
                for (Profile profile : profilesGiven) {
                    if (profile.getRegion() == null || profile.getRegion().equals("")) {
                        temp.add(profile);
                    }
                }
                for (Profile profile : temp) {
                    profilesGiven.remove(profile);
                }
                for (Profile profile : profilesGiven) {
                    if (profile.getRegion() == null || profile.getRegion().equals("")) {
                        continue;
                    } else {
                        profiles.add(profile.getRegion());
                    }
                }
                break;
            default:
                return profilesGiven;
        }

        //Fuzzywuzzy, fuzzy search algorithm. Returns list of donor names sorted by closest match to the searchString.
        List<ExtractedResult> result;
        result = FuzzySearch.extractSorted(searchString, profiles, 60);

        //Use index values from fuzzywuzzy search to build list of donor object in same order returned from fuzzywuzzy.
        resultProfiles.clear();
        for (ExtractedResult er : result) {
            resultProfiles.add(profilesGiven.get(er.getIndex()));
        }

        return resultProfiles;
    }

    /**
     * Compares each string in the names array to the searchString. Returns the weightedRatio value
     * of the string that was the closest to the searchString.
     *
     * @param searchString the string that the donor names will be searched against.
     * @param names        String array of profile names.
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
                tempRatio = FuzzySearch
                        .weightedRatio(searchString, name.substring(0, searchLength));
            }
            if (tempRatio > ratio) {
                ratio = tempRatio;
            }
        }
        return ratio;
    }

    //TODO add preferred name when that functionality is in dev

    /**
     * Fuzzy search that finds profiles with a name similar to the search string. Order of results
     * goes as follows: Exact matches in last names ordered alphabetically, Exact matches in first
     * names ordered alphabetically, Similar matches in last names ordered alphabetically, Similar
     * matches in first names ordered alphabetically.
     *
     * @param profilesList The list of profiles being searched.
     * @param searchString the string that the donor names will be searched against.
     * @return list of donors that match the provided search string, with a max size of 30.
     */
    private List<Profile> searchProfilesName(List<Profile> profilesList,
            String searchString) {
        // Constant that represents the cutoff at which profiles will not be added to search results
        final Integer matchLimit = 60;

        // Need separate Lists to order results by relevance.
        List<Profile> profilesSimilarFirst = new ArrayList<>();
        List<Profile> profilesSimilarLast = new ArrayList<>();
        List<Profile> profilesMatchesFirst = new ArrayList<>();
        List<Profile> profilesMatchesLast = new ArrayList<>();
        List<Profile> profilesMatchesPreferred = new ArrayList<>();
        List<Profile> profilesSimilarPreferred = new ArrayList<>();
        List<Profile> profiles = new ArrayList<>();

        if (searchString.equals("")) {
            return null;
        }

        for (Profile profile : profilesList) {
            int ratio;
            int tempRatio;
            String nameCategory;
            String[] namesFirst = profile.getGivenNames().split(" ");
            String[] namesPreferred = new String[0];
            if (profile.getPreferredName() != null) {
                namesPreferred = profile.getPreferredName().split(" ");
            }
            String[] namesLast = profile.getLastNames().split(" ");

            tempRatio = stringMatcher(searchString, namesFirst);
            ratio = tempRatio;
            nameCategory = "first";

            tempRatio = stringMatcher(searchString, namesPreferred);
            if (tempRatio >= ratio) {
                ratio = tempRatio;
                nameCategory = "preferred";
            }

            tempRatio = stringMatcher(searchString, namesLast);
            if (tempRatio >= ratio) {
                ratio = tempRatio;
                nameCategory = "last";
            }

            // Ratio of 100 is a exact match, these need to be at top of results.
            if (ratio == 100 && nameCategory.equals("last")) {
                profilesMatchesLast.add(profile);
            } else if (ratio == 100 && nameCategory.equals("preferred")) {
                profilesMatchesPreferred.add(profile);
            } else if (ratio == 100 && nameCategory.equals("first")) {
                profilesMatchesFirst.add(profile);
                // If ratio is below 60 don't include profile because it doesn't match close enough to searchString
            } else if (ratio >= matchLimit && nameCategory.equals("last")) {
                profilesSimilarLast.add(profile);
            } else if (ratio >= matchLimit && nameCategory.equals("preferred")) {
                profilesSimilarPreferred.add(profile);
            } else if (ratio >= matchLimit && nameCategory.equals("first")) {
                profilesSimilarFirst.add(profile);
            }
        }

        // Sorts each list by alphabetical order based on profile full name
        Collections.sort(profilesMatchesLast);
        Collections.sort(profilesMatchesPreferred);
        Collections.sort(profilesMatchesFirst);
        Collections.sort(profilesSimilarLast);
        Collections.sort(profilesSimilarPreferred);
        Collections.sort(profilesSimilarFirst);

        // Added in this order to match relevance order of last name, first name. Complete matches are always at top.
        profiles.addAll(profilesMatchesLast);
        profiles.addAll(profilesMatchesPreferred);
        profiles.addAll(profilesMatchesFirst);
        profiles.addAll(profilesSimilarLast);
        profiles.addAll(profilesSimilarPreferred);
        profiles.addAll(profilesSimilarFirst);

        return profiles;
    }

    /**
     * Fuzzy search that finds the top 30 donors that match the provided search string.
     *
     * @param searchString The string being searched.
     * @param ageSearchInt The age being searched.
     * @param ageRangeSearchInt The age range being search.
     * @param regionSearchString The region being searched.
     * @param selectedGender The selected gender of the search.
     * @param selectedType The type of profile being searched.
     * @param selectedOrgans The organs being searched for.
     * @return A list of profiles that matched the search criteria.
     */
    public List<Profile> searchProfiles(String searchString, int ageSearchInt,
            int ageRangeSearchInt, String regionSearchString, String selectedGender,
            String selectedType, Set<OrganEnum> selectedOrgans) {
        List<Profile> resultProfiles;

        switch (selectedType) {
            case "any":
                resultProfiles = getProfiles(false);
                break;
            case "donor":
                resultProfiles = getProfiles(false);
                break;
            default:
                resultProfiles = getReceivers(true);
                break;
        }


        //parsing out organs as strings for later use

        if (searchString.equals("") && regionSearchString.equals("") && ageSearchInt == -999
                && selectedGender.equals("") && selectedType == null && selectedOrgans.isEmpty()) {
            return resultProfiles;
        }

        if (!searchString.equals("")) {
            resultProfiles = fuzzySearch(resultProfiles, searchString, "name");
        }

        if (!regionSearchString.equals("")) {
            List<Profile> resultProfilesBefore = resultProfiles;
            resultProfiles = fuzzySearch(resultProfilesBefore, regionSearchString, "region");
        }

        //definitely need a better way than just a magic number lol
        if (ageSearchInt != -999) {
            if (ageRangeSearchInt != -999) {
                //use a range
                if (ageRangeSearchInt > ageSearchInt) {
                    resultProfiles.removeIf(
                            profile -> ((profile.getAge() > ageRangeSearchInt) || (
                                    profile.getAge() < ageSearchInt)));
                } else {
                    resultProfiles.removeIf(
                            profile -> ((profile.getAge() < ageRangeSearchInt) || (
                                    profile.getAge() > ageSearchInt)));
                }

            } else {
                //just the age specified
                resultProfiles.removeIf(profile -> profile.getAge() != ageSearchInt);
            }
        }

        if (!selectedGender.equals("")) {
            if (!selectedGender.equals("any")) {
                resultProfiles.removeIf(profile -> {
                    if (profile.getGender() == null) {
                        return true;
                    }
                    return !selectedGender.equalsIgnoreCase(profile.getGender());
                });
            }
        }

        if (!selectedOrgans.isEmpty()) {
            resultProfiles.removeIf(profile -> {

                Set<OrganEnum> organsDonatingHash = new HashSet<>(profile.getOrgansDonating());
                List<String> organsDonating = new ArrayList<String>();

                for (OrganEnum temp : organsDonatingHash) {
                    organsDonating.add(temp.getName());
                }

                if (organsDonating.isEmpty()) {
                    return true;
                }
                organsDonatingHash.retainAll(selectedOrgans);         //intersection
                return organsDonating.size() == 0;
            });

        }

        if (!(selectedType == null)) {
            resultProfiles.removeIf(profile -> {
                if (selectedType.equals("donor")) {
                    return !profile.isDonatingCertainOrgans(selectedOrgans);
                } else if (selectedType.equals("receiver")) {
                    return !profile.isReceivingCertainOrgans(selectedOrgans);
                } else {
                    return !(profile.isReceivingCertainOrgans(selectedOrgans) || profile
                            .isDonatingCertainOrgans(selectedOrgans));
                }
            });
        }

        return resultProfiles;
    }


    private List<String> getOrgansAsStrings(List selectedOrgans) {
        List<String> selectedOrgansStrings = new ArrayList<>();
        if (selectedOrgans != null) {
            for (Object selectedOrgan : selectedOrgans) {
                //todo need some consistency in how we are naming organs that have two words in them.
                if (selectedOrgan.toString().equalsIgnoreCase("connective tissue")) {
                    selectedOrgansStrings.add("connective_tissue");
                }
                if (selectedOrgan.toString().equalsIgnoreCase("bone marrow")) {
                    selectedOrgansStrings.add("bone_marrow");
                }
                selectedOrgansStrings.add(selectedOrgan.toString().toLowerCase());
            }
        }
        return selectedOrgansStrings;
    }

    /**
     * Generates a list of profiles receiving organs ordered by last names. Parameter to specify
     * whether or not the list contains every receiver or only receivers that are currently
     * receiving organs.
     *
     * @param receiving specify currently receiving organs or not
     * @return Array of profiles found that match
     */
    private List<Profile> getReceivers(Boolean receiving) {
        ArrayList<Profile> profiles = new ArrayList<>();

        profileDb.forEach((id, profile) -> {

            if (profile.getReceiver()) {
                if (receiving) {
                    //todo make getting organs permanent
                    if (!profile.getOrgansRequired().isEmpty()) {
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
     * Generates a collection of a profile and organ for each organ that a receiver requires.
     *
     * @return Collection of profile and Organ that match.
     */
    public List<Entry<Profile, OrganEnum>> getAllOrgansRequired() {
        List<Entry<Profile, OrganEnum>> receivers = new ArrayList<>();

        List<Profile> allReceivers = getReceivers(true);

        for (Profile profile : allReceivers) {
            for (OrganEnum organ : profile.getOrgansRequired()) {
                Map.Entry<Profile, OrganEnum> pair = new SimpleEntry<>(profile, organ);
                receivers.add(pair);
            }
        }
        return receivers;
    }

}
