package gal.sdc.usc.wallstreet.controller;

import com.jfoenix.controls.JFXButton;
import gal.sdc.usc.wallstreet.Main;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.shape.Circle;

import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class PrincipalEmpresaController {
    @FXML
    private JFXButton buttonPerfilUsuario;
    @FXML
    private JFXButton buttonParticipaciones;
    @FXML
    private JFXButton verPerfilButton;
    Parent principalEmpresa;
    Scene scene;

    public void Initialize(){
        buttonPerfilUsuario.setOnAction(event -> {
            Parent root;
            try{
                root = FXMLLoader.load(Main.class.getResource("view/principal_empresa.fxml"));
                Stage stage = new Stage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        verPerfilButton.setOnAction(event -> {

        });
    }
}
