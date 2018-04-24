package odms.controller;

import odms.data.ProfileDataIO;
import odms.data.ProfileDatabase;

import javafx.event.EventHandler;
import javafx.stage.WindowEvent;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import odms.data.UserDataIO;
import odms.data.UserDatabase;
import odms.user.User;
import odms.user.UserType;


public class GuiMain extends Application {

    private static final String DONOR_DATABASE = "example/example.json";
    private static final String USER_DATABASE = "example/users.json";

    private static ProfileDatabase profileDb = ProfileDataIO.loadData(DONOR_DATABASE);
    private static UserDatabase userDb = new UserDataIO().loadData(USER_DATABASE);

    @Override
    public void start(Stage primaryStage) throws Exception{
        try {
            userDb.getClinician(0);
        } catch (NullPointerException e){
            userDb = new UserDatabase();
            User user = new User(UserType.CLINICIAN, "Doc", "Christchurch");
            userDb.addClinician(user);
            UserDataIO.saveUsers(userDb, USER_DATABASE);
        }
        Parent root = FXMLLoader.load(getClass().getResource("/view/Login.fxml"));
        primaryStage.setScene(new Scene(root));
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

    public static void main(String[] args) {
        launch(args);
    }
}
