package odms.controller;

import javafx.event.EventHandler;
import javafx.stage.WindowEvent;
import odms.data.DonorDataIO;
import odms.data.DonorDatabase;
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

    private static String DONORDATABASE = "example/example.json";
    private static String USERDATABASE = "example/users.json";

    private static DonorDatabase donorDb = DonorDataIO.loadData(DONORDATABASE);
    private static UserDatabase userDb = new UserDataIO().loadData(USERDATABASE);

    @Override
    public void start(Stage primaryStage) throws Exception{
        try {
            userDb.getClinician(0);
        } catch (NullPointerException e){
            userDb = new UserDatabase();
            User user = new User(UserType.CLINICIAN, "Doc", "Christchurch");
            userDb.addClinician(user);
            UserDataIO.saveUsers(userDb, USERDATABASE);
        }
        Parent root = FXMLLoader.load(getClass().getResource("/view/Login.fxml"));
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
                DonorDataIO.saveDonors(donorDb, DONORDATABASE);
                UserDataIO.saveUsers(userDb, USERDATABASE);
            }
        });
    }

    public static DonorDatabase getCurrentDatabase() {
        return donorDb;
    }

    public static UserDatabase getUserDatabase(){
        return userDb;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
