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
import odms.model.user.User;
import odms.model.user.UserType;

import java.io.IOException;

/**
 * Main class. GUI boots from here.
 */
public class GuiMain extends Application {

    private static final String DONOR_DATABASE = "example/example.json";
    private static final String USER_DATABASE = "example/users.json";
    private static final String APP_NAME = "ODMS";
    private static final String ADMIN = "admin";

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
