package odms.data;

import java.util.HashMap;
import odms.donor.Donor;
import odms.user.User;

public class UserDatabase {
    private HashMap<Integer, User> userDb = new HashMap<>();
    private Integer lastID = -1;

    /**
     * Find clinician by ID
     *
     * @param id unique ID for requested clinician
     * @return User object
     */
    public User getClinician(Integer id){
        return userDb.get(id);
    }

    public void addClinician(User user){
        lastID += 1;
        user.setStaffId(lastID);

        userDb.put(lastID, user);
    }

}
