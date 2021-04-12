package gal.sdc.usc.wallstreet;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    private static Parent root;
    private static Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        Main.primaryStage = primaryStage;
        primaryStage.setTitle("Wall Street Jalisia");
        Main.setScene("acceso", 400, 500);
        Main.primaryStage.show();
    }

    public static void setScene(String view, int width, int height) {
        try {
            Main.root = FXMLLoader.load(Main.class.getResource("view/" + view + ".fxml"));
            Main.primaryStage.setScene(new Scene(Main.root, width, height));
            Main.primaryStage.setMinWidth(width);
            Main.primaryStage.setMinHeight(height);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
