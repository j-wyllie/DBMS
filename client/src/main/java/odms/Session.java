package odms;

import java.util.AbstractMap.SimpleEntry;
import java.util.Locale;
import java.util.Map.Entry;
import odms.commons.model.enums.UserType;
import odms.commons.model.profile.Profile;
import odms.commons.model.user.User;

public class Session {

    private static Object currentUser;
    private static Object currentProfile;
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
            }
            else {
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
            Session.currentUser = currentUser;
        } else {
            Session.currentProfile = currentUser;
        }
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
    public static String getDefaultLocation() {
        String country;
        if (currentUser != null) {
            User user = (User) currentUser;
            country = user.getCountry();
        } else {
            Profile profile = (Profile) currentProfile;
            country = profile.getCountry();
        }
        if (country == null) {
            country = Locale.getDefault().getDisplayCountry();
        }
        return country;
    }
}
