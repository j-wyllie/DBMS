package odms.controller;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import odms.data.ProfileDataIO;
import odms.data.ProfileDatabase;
import odms.data.UserDataIO;
import odms.data.UserDatabase;
import odms.user.User;
import odms.user.UserType;

public class GuiMain extends Application {

    private static final String DONOR_DATABASE = "example/example.json";
    private static final String USER_DATABASE = "example/users.json";

    private static ProfileDatabase profileDb = ProfileDataIO.loadData(DONOR_DATABASE);
    private static UserDatabase userDb = UserDataIO.loadData(USER_DATABASE);

    /**
     * Loads in a default clinician if one does not exist. Opens the login screen
     * @param primaryStage the primary stage
     * @throws IOException file read exception for login fxml
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        try {
            userDb.getUser(0);
        } catch (NullPointerException e){
            // TODO this code always destroys the old UserDB?
            userDb = new UserDatabase();
            User user = new User(UserType.CLINICIAN, "Doc", "Christchurch");
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
