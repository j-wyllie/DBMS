package odms;

import java.util.AbstractMap.SimpleEntry;
import java.util.Locale;
import java.util.Map.Entry;
import odms.commons.model.enums.CountriesEnum;
import odms.commons.model.enums.UserType;
import odms.commons.model.profile.Profile;
import odms.commons.model.user.User;

public class Session {

    private static User currentUser;
    private static Profile currentProfile;
    private static int token;

    /**
     * Constructor - cannot instantiate a static class.
     */
    public Session () {
        throw new UnsupportedOperationException();
    }

    public static Entry<Object, UserType> getCurrentUser() {
        if (currentUser != null) {
            User user = (User) currentUser;
            if (user.getUserType() == UserType.ADMIN) {
                return new SimpleEntry<>(user, UserType.ADMIN);
            } else {
                return new SimpleEntry<>(user, UserType.CLINICIAN);
            }
        } else if (currentProfile != null) {
            return new SimpleEntry<>(currentProfile, UserType.PROFILE);
        }
        return null;
    }

    /**
     * Sets the user currently running the session based on the user type.
     * @param currentUser using the session.
     * @param userType of the user.
     */
    public static void setCurrentUser(Object currentUser, UserType userType) {
        if (userType == UserType.ADMIN || userType == UserType.CLINICIAN) {
            Session.currentUser = (User) currentUser;
        } else {
            Session.currentProfile = (Profile) currentUser;
        }
    }

    public static int getCurrentId() {
        if (currentUser != null) {
            User user = (User) currentUser;
            return user.getId();
        } else if (currentProfile != null) {
            Profile profile = (Profile) currentProfile;
            return profile.getId();
        }
        return -1;
    }

    public static int getToken() {
        return token;
    }

    public static void setToken(int token) {
        Session.token = token;
    }

    /**
     * Generates the default location of the current session.
     * @return the settings of location.
     */
    public static CountriesEnum getDefaultLocation() {
        if (currentUser != null) {
            User user = (User) currentUser;
            return user.getCountry() != null ? user.getCountry()
                    : CountriesEnum.getEnumByString(Locale.getDefault().getDisplayCountry());
        } else {
            Profile profile = (Profile) currentProfile;
            return profile.getCountry() != null ? profile.getCountry()
                    : CountriesEnum.getEnumByString(Locale.getDefault().getDisplayCountry());
        }
    }

    public static void clear() {
        currentUser = null;
        currentProfile = null;
    }
}
