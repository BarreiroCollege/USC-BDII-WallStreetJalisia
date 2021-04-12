package gal.sdc.usc.wallstreet.controller;

import com.jfoenix.controls.JFXButton;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.shape.Circle;

import javafx.scene.input.MouseEvent;

import java.io.IOException;

public class PrincipalEmpresaController {
    @FXML
    private Circle buttonPerfilUsuario;
    @FXML
    private JFXButton buttonParticipaciones;
    Parent principalEmpresa;
    Scene scene;
    public PrincipalEmpresaController() throws IOException {
        principalEmpresa = FXMLLoader.load(getClass().getResource("view/principal_empresa.fxml"));
        scene = new Scene(principalEmpresa);
    }

    EventHandler<MouseEvent> clickPerfil = new EventHandler<MouseEvent>(){
        @Override
        public void handle(MouseEvent e) {

        }
    };
}
