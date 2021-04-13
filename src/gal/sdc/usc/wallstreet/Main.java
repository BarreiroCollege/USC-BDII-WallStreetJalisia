package gal.sdc.usc.wallstreet;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("view/principal_empresa.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 683, 551));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
