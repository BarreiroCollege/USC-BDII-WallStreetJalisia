package gal.sdc.usc.wallstreet;

import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXSnackbarLayout;
import gal.sdc.usc.wallstreet.controller.CarteraController;
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
        Main.setScene(CarteraController.VIEW, CarteraController.WIDTH, CarteraController.HEIGHT);
    }

    public static void setScene(String view, int width, int height) {
        try {
            Main.primaryStage.hide();

            Stage stage = new Stage();
            Parent root = FXMLLoader.load(Main.class.getResource("view/" + view + ".fxml"));
            snackbar = new JFXSnackbar((AnchorPane) root);

            stage.setScene(new Scene(root, width, height));
            stage.setWidth(width);
            stage.setHeight(height);
            stage.setResizable(false);
            stage.setTitle("Wall Street Jalisia");

            Main.primaryStage = stage;
            Main.primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
