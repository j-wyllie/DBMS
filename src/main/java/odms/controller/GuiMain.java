package odms.controller;

import odms.data.ProfileDataIO;
import odms.data.ProfileDatabase;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class GuiMain extends Application {
    private static ProfileDatabase donorDb = ProfileDataIO.loadData("example/example.json");

    @Override
    public void start(Stage primaryStage) throws Exception{

        Parent root = FXMLLoader.load(getClass().getResource("/view/Login.fxml"));
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

    }

    public static ProfileDatabase getCurrentDatabase() {
        return donorDb;
    }



    public static void main(String[] args) {
        launch(args);
    }
}
