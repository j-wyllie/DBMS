package odms.controller;

import java.sql.SQLException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import odms.commons.model.enums.OrganEnum;
import odms.commons.model.profile.Profile;
import odms.controller.database.DAOFactory;
import odms.controller.user.UserNotFoundException;
import odms.commons.model.user.User;
import odms.commons.model.enums.UserType;

import java.io.IOException;
import java.util.*;

/**
 * Main class. GUI boots from here.
 */
public class GuiMain extends Application {

    private static final String DONOR_DATABASE = "example/example.json";
    private static final String USER_DATABASE = "example/users.json";
    private static final String APP_NAME = "ODMS";
    private static final String ADMIN = "admin";

    private odms.controller.user.AvailableOrgans controller = new odms.controller.user.AvailableOrgans();

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

        //thread that runs in the background to check if organs have expired since last launch
        Thread checkOrgan = new Thread() {
            public void run() {
                try {
                    List<Map.Entry<Profile, OrganEnum>> availableOrgans = controller
                            .getAllOrgansAvailable();
                    for (Map.Entry<Profile, OrganEnum> m : availableOrgans) {
                        controller.checkOrganExpired(m.getValue(), m.getKey(), m);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        };
        checkOrgan.setDaemon(true);
        checkOrgan.start();

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
            clinician.setPassword("password");
            clinician.setDefault(true);
            DAOFactory.getUserDao().add(clinician);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * Launches the GUI of the program.
     * @param args command arguments
     */
    public static void main(String[] args) {
        launch(args);
    }


}
