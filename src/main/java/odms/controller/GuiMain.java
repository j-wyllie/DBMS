package odms.controller;

import java.sql.SQLException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import odms.App;
import odms.controller.data.UserDataIO;
import odms.controller.database.DAOFactory;
import odms.controller.user.UserNotFoundException;
import odms.model.data.ProfileDatabase;
import odms.model.data.UserDatabase;
import odms.model.enums.OrganEnum;
import odms.model.profile.Profile;
import odms.model.user.User;
import odms.model.enums.UserType;
import odms.view.user.AvailableOrgans;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Main class. GUI boots from here.
 */
public class GuiMain extends Application {

    private static final String DONOR_DATABASE = "example/example.json";
    private static final String USER_DATABASE = "example/users.json";
    private static final String APP_NAME = "ODMS";
    private static final String ADMIN = "admin";

    private odms.controller.user.AvailableOrgans controller = new odms.controller.user.AvailableOrgans();
    private static ProfileDatabase profileDb = App.getProfileDb();
    private static UserDatabase userDb = App.getUserDb();


    /**
     * Loads in a default clinician if one does not exist. Opens the login screen
     *
     * @param primaryStage the primary stage
     * @throws IOException file read exception for login fxml
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        try {
            DAOFactory.getUserDao().get(ADMIN);
        } catch (UserNotFoundException e) {
            createDefaultAdmin();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            DAOFactory.getUserDao().get("0");
        } catch (UserNotFoundException e) {
            createDefaultClinician();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        if (!userDb.isUser(0)) {
            User user = new User(UserType.CLINICIAN, "Doc", "Christchurch", "Clinician", "");
            user.setStaffID(0);
            userDb.addUser(user);
            user.setDefault(true);
            UserDataIO.saveUsers(userDb, USER_DATABASE);
        }

        if (!userDb.isUser(ADMIN)) {
            User user = new User(UserType.ADMIN, ADMIN);
            user.setUsername(ADMIN);
            user.setPassword(ADMIN);
            user.setDefault(true);
            userDb.addUser(user);
            UserDataIO.saveUsers(userDb, USER_DATABASE);
        }
        //timer that runs in the background to check if organs have expired
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                try {
                    List<Map.Entry<Profile, OrganEnum>> availableOrgans = controller
                            .getAllOrgansAvailable();
                    for(Map.Entry<Profile, OrganEnum> m : availableOrgans) {
                        controller.checkOrganExpired(m.getValue(), m.getKey(), m);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        },0,1);

        Parent root = FXMLLoader.load(getClass().getResource("/view/Login.fxml"));
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.setTitle(APP_NAME);
        primaryStage.show();
    }

    /**
     * Creates a default admin profile in the database.
     */
    private static void createDefaultAdmin() {
        try {
            User admin = new User(UserType.ADMIN, ADMIN);
            admin.setUsername(ADMIN);
            admin.setPassword(ADMIN);
            admin.setDefault(true);
            DAOFactory.getUserDao().add(admin);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a default clinician profile in the database.
     */
    private static void createDefaultClinician() {
        try {
            User clinician = new User(UserType.CLINICIAN, "Doc");
            clinician.setUsername("0");
            clinician.setPassword("");
            clinician.setDefault(true);
            DAOFactory.getUserDao().add(clinician);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ProfileDatabase getCurrentDatabase() {
        return profileDb;
    }

    public static void setCurrentDatabase(ProfileDatabase profileDb) {
        GuiMain.profileDb = profileDb;
    }

    public static UserDatabase getUserDatabase() {
        return userDb;
    }

    /**
     * Launches the GUI of the program.
     * @param args command arguments
     */
    public static void main(String[] args) {
        launch(args);
    }


}
