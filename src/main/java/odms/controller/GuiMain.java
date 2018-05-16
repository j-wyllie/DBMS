package odms.controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import odms.App;
import odms.data.ProfileDatabase;
import odms.data.UserDataIO;
import odms.data.UserDatabase;
import odms.user.User;
import odms.user.UserType;

import java.io.IOException;

public class GuiMain extends Application {

    private static final String DONOR_DATABASE = "example/example.json";
    private static final String USER_DATABASE = "example/users.json";

    private static ProfileDatabase profileDb = App.getProfileDb();
    private static UserDatabase userDb = App.getUserDb();

    /**
     * Loads in a default clinician if one does not exist. Opens the login screen
     * @param primaryStage the primary stage
     * @throws IOException file read exception for login fxml
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        if (!userDb.isUser(0)) {
            User user = new User(UserType.CLINICIAN, "Doc", "Christchurch");
            userDb.addUser(user);
            UserDataIO.saveUsers(userDb, USER_DATABASE);
        }

        if (!userDb.isUser("admin")) {
            User user = new User(UserType.ADMIN, "admin");
            user.setUsername("admin");
            user.setPassword("admin");
            userDb.addUser(user);
            UserDataIO.saveUsers(userDb, USER_DATABASE);
        }

        Parent root = FXMLLoader.load(getClass().getResource("/view/Login.fxml"));
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.setTitle("ODMS"); // TODO Remove magic string
        primaryStage.show();

//        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
//            public void handle(WindowEvent we) {
//                ProfileDataIO.saveData(donorDb, DONOR_DATABASE);
//                UserDataIO.saveUsers(userDb, USER_DATABASE);
//            }
//        });
    }

    public static ProfileDatabase getCurrentDatabase() {
        return profileDb;
    }

    public static UserDatabase getUserDatabase(){
        return userDb;
    }

    public void setCurrentDatabase(ProfileDatabase profileDb) { GuiMain.profileDb = profileDb; }

    public static void main(String[] args) {
        launch(args);
    }
}
