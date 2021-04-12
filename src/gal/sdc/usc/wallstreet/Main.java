package gal.sdc.usc.wallstreet;

import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXSnackbarLayout;
import gal.sdc.usc.wallstreet.controller.AccesoController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class Main extends Application {
    private static Parent root;
    private static Stage primaryStage;

    private static JFXSnackbar snackbar;

    public static void mensaje(String mensaje) {
        mensaje(mensaje, null);
    }

    public static void mensaje(String mensaje, Integer duracion) {
        mensaje(new JFXSnackbarLayout(mensaje), duracion);
    }

    private static void mensaje(JFXSnackbarLayout layout, Integer duracion) {
        JFXSnackbarLayout finalLayout = new JFXSnackbarLayout(layout.getToast(), "Cerrar", e -> snackbar.close());
        if (duracion != null) {
            snackbar.enqueue(new JFXSnackbar.SnackbarEvent(finalLayout, Duration.seconds(duracion)));
        } else {
            snackbar.enqueue(new JFXSnackbar.SnackbarEvent(finalLayout));
        }
    }


    @Override
    public void start(Stage primaryStage) {
        Main.primaryStage = primaryStage;
        primaryStage.setTitle("Wall Street Jalisia");
        Main.setScene(AccesoController.VIEW, AccesoController.WIDTH, AccesoController.HEIGHT);
        Main.primaryStage.setResizable(false);
    }

    public static void setScene(String view, int width, int height) {
        try {
            Main.root = FXMLLoader.load(Main.class.getResource("view/" + view + ".fxml"));
            snackbar = new JFXSnackbar((AnchorPane) Main.root);
            Main.primaryStage.setScene(new Scene(Main.root, width, height));
            Main.primaryStage.setWidth(width);
            Main.primaryStage.setHeight(height);
            Main.primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
