package seng302;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class ClientDatabase {

    private static HashMap<Integer, Client> clientDatabase = new HashMap<>();
    public ArrayList<Client> clients = new ArrayList<>();

    public static Object getClient(Integer clientID) {

        return ClientDatabase.clientDatabase.get(clientID);

    }

}
