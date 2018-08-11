package odms.controller.profile;

import odms.commons.model.enums.OrganEnum;
import odms.commons.model.medications.Drug;
import odms.commons.model.profile.Condition;
import odms.commons.model.profile.OrganConflictException;
import odms.commons.model.profile.Profile;

import java.util.ArrayList;
import java.util.Set;

public class UndoRedoCLIService {

    public static void removeOrganDonated(OrganEnum organ, Profile p) {
        if (p.getOrgansDonated().contains(organ)) {
            p.getOrgansDonated().remove(organ);
        }

        p.getOrgansDonating().add(organ);
    }

    public static void removeOrganReceived(OrganEnum organ, Profile p) {
        if (p.getOrgansReceived().contains(organ)) {
            p.getOrgansReceived().remove(organ);
        }

        p.getOrgansRequired().add(organ);
    }

    public static void addCondition(Condition condition, Profile p) {
        p.getAllConditions().add(condition);
    }

    /**
     * Gets all the current conditions of the user
     *
     * @return the current conditions of the user
     */
    public static ArrayList<Condition> getCurrentConditions(Profile p) {
        ArrayList<Condition> currentConditions = new ArrayList<>();
        for (Condition condition : p.getAllConditions()) {
            if (!condition.getCured()) {
                currentConditions.add(condition);
            }
        }
        return currentConditions;
    }

    public static void removeCondition(Condition condition, Profile p) {
       p.getAllConditions().remove(condition);
    }

    public static void moveDrugToCurrent(Drug drug, Profile profile) {
        if (profile.getHistoryOfMedication().contains(drug)) {
            profile.getHistoryOfMedication().remove(drug);
            profile.getCurrentMedications().add(drug);
        }
    }

    public static void moveDrugToHistory(Drug drug, Profile profile) {
        if (profile.getCurrentMedications().contains(drug)) {
            profile.getCurrentMedications().remove(drug);
            profile.getHistoryOfMedication().add(drug);
        }
    }

    public static void deleteDrug(Drug drug, Profile profile) {
        if (profile.getCurrentMedications().contains(drug)) {
            profile.getCurrentMedications().remove(drug);
        } else if (profile.getHistoryOfMedication().contains(drug)) {
            profile.getHistoryOfMedication().remove(drug);
        }
    }

    public static void addDrug(Drug d, Profile profile) {
        profile.getCurrentMedications().add(d);
    }

    public static void removeOrgansDonating(Set<OrganEnum> organs, Profile p) {
        for (OrganEnum organ : organs) {
            p.getOrgansDonating().remove(organ);
        }
    }

    public static void removeOrgansDonated(Set<OrganEnum> organs, Profile p) {
        for (OrganEnum organ : organs) {
            p.getOrgansDonated().remove(organ);
        }
    }

    /**
     * Sets the attributes that are passed into the constructor
     *
     * @param attributes the attributes given in the constructor
     * @throws IllegalArgumentException when a required attribute is not included or spelt wrong
     */
    public static void setExtraAttributes(ArrayList<String> attributes, Profile profile)
            throws IllegalArgumentException {
        ProfileGeneralControllerTODOContainsOldProfileMethods.setExtraAttributes(attributes, profile);
    }



    /**
     * Add an organ to the list of donated organsDonating. If the organ exists in the donating list,
     * remove it from the donating list.
     *
     * @param organ the organ to be added
     */
    public static void addOrganDonated(OrganEnum organ, Profile p) {
        if (p.getOrgansDonating().contains(organ)) {
            p.getOrgansDonating().remove(organ);
        }
        p.getOrgansDonated().add(organ);
    }

    /**
     * Add an organ to the set of received organs. If the organ exists in the receiving set, remove
     * it.
     *
     * @param organ to be added
     */
    private static void addOrganReceived(OrganEnum organ, Profile p) {
        if (p.getOrgansRequired().contains(organ)) {
            p.getOrgansRequired().remove(organ);
        }
        p.getOrgansReceived().add(organ);
    }

    /**
     * Add a set of organs to the set of received organs.
     *
     * @param organs set to be added
     */
    public static void addOrgansReceived(Set<OrganEnum> organs, Profile p) {
        for (OrganEnum organ : organs) {
            addOrganReceived(organ,p);
        }
    }

    /**
     * Add a set of organs to the list of organs that the profile wants to donate
     *
     * @param organs the set of organs to donate
     * @throws IllegalArgumentException if a bad argument is used
     * @throws OrganConflictException   if there is a conflicting organ
     */
    public static void addOrgansDonating(Set<OrganEnum> organs, Profile p)
            throws IllegalArgumentException, OrganConflictException {
        for (OrganEnum organ : organs) {
            if (p.getOrgansDonating().contains(organ)) {
                throw new IllegalArgumentException(
                        "Organ " + organ + " already exists in donating list"
                );
            }
            addOrganDonating(organ, p);
        }
    }

    /**
     * Add an organ to the organs donate list.
     *
     * @param organ the organ the profile wishes to donate
     */
    public static void addOrganDonating(OrganEnum organ, Profile p) throws OrganConflictException {
        if (p.getOrgansReceived().contains(organ)) {
            // A donor cannot donate an organ they've received.
            throw new OrganConflictException(
                    "profile has previously received " + organ,
                    organ
            );
        }
        p.getOrgansDonating().add(organ);
    }

    /**
     * Add a set of organsDonating to the list of organsDonating that the profile has donated
     *
     * @param organs a set of organsDonating that the profile has donated
     */
    public static void addOrgansDonated(Set<OrganEnum> organs, Profile p) {
        for (OrganEnum organ : organs) {
            p.getOrgansDonated().add(organ);
        }
    }

    /**
     * Add an organ to the organs required list.
     *
     * @param organ the organ the profile requires
     */
    public static void addOrganRequired(OrganEnum organ, Profile p) {//TODO Error Check
        p.setReceiver(true);
        p.getOrgansRequired().add(organ);
    }

    /**
     * Add a set of organs that the profile requires to the required organs set.
     *
     * @param organs the set of organs to be received
     */
    public static void addOrgansRequired(Set<OrganEnum> organs, Profile p) {
        for (OrganEnum organ : organs) {
            addOrganRequired(organ, p);
        }
    }

    /**
     * Remove a set of organs from the list of organs required.
     *
     * @param organs a set of organs to be removed
     */
    public static void removeOrgansRequired(Set<OrganEnum> organs, Profile p) {
        for (OrganEnum organ : organs) {
            p.getOrgansRequired().remove(organ);
        }
    }
}
