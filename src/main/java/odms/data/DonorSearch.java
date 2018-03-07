package odms.data;

import odms.donor.Donor;

import java.util.ArrayList;
import java.util.HashMap;

public class DonorSearch {
    private HashMap<String, Integer> lName = new HashMap<>();
    private HashMap<String, Integer> fName = new HashMap<>();
    private HashMap<String, Integer> IRD = new HashMap<>();

    public DonorSearch(ArrayList<Donor> donorList) {
        for (Donor don : donorList) {
            fName.put(don.getGivenNames(), don.getId());
            lName.put(don.getLastNames(), don.getId());
            //IRD.put(don.getIRD(), don.getId());
        }
    }

    public Integer searchLName(String lastName) {
        Integer id = lName.get(lastName);
        return id;
    }

    public Integer searchFName(String firstName) {
        Integer id = fName.get(firstName);
        return id;
    }

    public Integer searchIRD(String IRDNo) {
        Integer id = IRD.get(IRDNo);
        return id;
    }

    public void addDonor(Donor newDonor) {
        fName.put(newDonor.getGivenNames(), newDonor.getId());
        lName.put(newDonor.getLastNames(), newDonor.getId());
        //IRD.put(newDonor.getIRD(), newDonor.getId());
    }
}
