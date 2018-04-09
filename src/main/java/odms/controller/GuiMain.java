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


public class GuiMain extends Application {

    private static DonorDatabase donorDb = DonorDataIO.loadData("example/example.json");

    @Override
    public void start(Stage primaryStage) throws Exception{

        Parent root = FXMLLoader.load(getClass().getResource("/view/Login.fxml"));
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
                DonorDataIO.saveDonors(donorDb, "example/example.json");
            }
        });

    }

    public static DonorDatabase getCurrentDatabase() {
        return donorDb;
    }



    public static void main(String[] args) {
        launch(args);

    }
}
