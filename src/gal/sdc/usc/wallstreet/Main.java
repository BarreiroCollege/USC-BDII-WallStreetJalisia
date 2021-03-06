package gal.sdc.usc.wallstreet;

import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXSnackbarLayout;
import gal.sdc.usc.wallstreet.controller.AccesoController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
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

    public static void aviso(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public static void dialogo(String view, int width, int height, String titulo) {
        Main.setScene(view, width, height, titulo, true);
    }

    public static void ventana(String view, int width, int height, String titulo) {
        Main.setScene(view, width, height, titulo, false);
    }

    public static Parent getView(String view) {
        try {
            return FXMLLoader.load(Main.class.getResource("view/" + view + ".fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void setScene(String view, int width, int height, String titulo, boolean modal) {
        Stage stage = new Stage();
        Parent root = Main.getView(view);

        stage.setScene(new Scene(root, width, height));
        stage.setWidth(width);
        stage.setHeight(height);
        stage.setResizable(false);
        stage.setTitle(titulo + " | eMercado de Abastos");

        if (modal) {
            stage.initOwner(Main.primaryStage);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.show();
        } else {
            Main.primaryStage.close();
            Main.snackbar = new JFXSnackbar((AnchorPane) root);

            Main.primaryStage = stage;
            Main.primaryStage.show();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Main.primaryStage = primaryStage;
        Main.ventana(AccesoController.VIEW, AccesoController.WIDTH, AccesoController.HEIGHT, AccesoController.TITULO);
    }
}
