package odms.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import odms.Donor.Donor;

public class DonorDatabase {

    private static HashMap<Integer, Donor> clientDatabase = new HashMap<>();
    public static ArrayList<Donor> donors = new ArrayList<>();

    private static Integer lastID;

    public static void loadClientDatabase() {
        // Populate HashMap with donors
        for (Donor donor : donors
        ) {
            clientDatabase.put(donor.getId(), donor);
        }

        // Clear donors buffer from memory
        donors.clear();

        // Set lastID for adding future donors
        List<Integer> idArray = new ArrayList<>(clientDatabase.keySet());
        Collections.sort(idArray);
        Object temptlastID = idArray.remove(-1);
    }

    public static Donor getClient(Integer clientID) {
        return DonorDatabase.clientDatabase.get(clientID);
    }

    public static void addDonor(Donor donor) {
        try {
            Integer newID = lastID + 1;
            donor.setId(newID);
            clientDatabase.put(newID, donor);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
