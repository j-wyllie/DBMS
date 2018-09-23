package odms;

import java.util.Locale;
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

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User currentUser) {
        Session.currentUser = currentUser;
    }

    public static Profile getCurrentProfile() {
        return currentProfile;
    }

    public static void setCurrentProfile(Profile currentProfile) {
        Session.currentProfile = currentProfile;
    }

    public static int getToken() {
        return token;
    }

    public static void setToken(int token) {
        Session.token = token;
    }

    /**
     * Generates the default location of the current session.
     * @return the country of location.
     */
    public static String getDefaultLocation() {
        String country;
        if (Session.currentUser == null) {
            country = Session.currentUser.getCountry();
        } else {
            country = Session.currentProfile.getCountry();
        }
        if (country == null) {
            country = Locale.getDefault().getDisplayCountry();
        }
        return country;
    }
}
