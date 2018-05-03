package odms.data;

import java.util.HashMap;
import odms.user.User;

public class UserDatabase {

    private HashMap<Integer, User> userDb = new HashMap<>();
    private Integer lastID = -1;
    private String path;

    /**
     * Find clinician by ID
     *
     * @param id unique ID for requested clinician
     * @return User object
     */
    public User getClinician(Integer id){
        return userDb.get(id);
    }

    /**
     * Determine unique ID for profile and add the profile the the database
     *
     * @param user new user object
     */
    public void addClinician(User user){
        lastID += 1;
        user.setStaffId(lastID);

        userDb.put(lastID, user);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
