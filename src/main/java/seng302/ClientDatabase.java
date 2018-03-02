package seng302;

import java.util.ArrayList;
import java.util.HashMap;

public class ClientDatabase {

  // TODO explicitly declare the client object type
  private static HashMap<Integer, Object> clientDatabase = new HashMap<>();
  public static ArrayList<Client> clients = new ArrayList<>();

  public static Object getClient(Integer clientID) {

    return ClientDatabase.clientDatabase.get(clientID);

  }



}
