package odms.controller;

import odms.data.DonorDataIO;
import odms.data.DonorDatabase;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {

    private static DonorDatabase donorDb = DonorDataIO.loadData("example/example.json");

    @Override
    public void start(Stage primaryStage) throws Exception{

//        FXMLLoader loader = new FXMLLoader();
//        loader.setLocation(Main.class.getResource("/Login.fxml"));
        //Parent root = loader.load();
        Parent root = FXMLLoader.load(getClass().getResource("/Login.fxml"));
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static DonorDatabase getCurrentDatabase() {
        return donorDb;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
