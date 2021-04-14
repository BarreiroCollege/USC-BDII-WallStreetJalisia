package gal.sdc.usc.wallstreet.controller;

import com.jfoenix.controls.JFXButton;
import gal.sdc.usc.wallstreet.model.Empresa;
import gal.sdc.usc.wallstreet.model.Inversor;
import gal.sdc.usc.wallstreet.model.Participacion;
import gal.sdc.usc.wallstreet.repository.helpers.DatabaseLinker;
import gal.sdc.usc.wallstreet.util.TipoUsuario;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;


public class PrincipalController extends DatabaseLinker {
    public static final String VIEW = "principal";
    public static final Integer HEIGHT = 551;
    public static final Integer WIDTH = 683;

    @FXML
    private JFXButton buttonPerfilUsuario;
    @FXML
    private JFXButton buttonParticipaciones;
    @FXML
    private JFXButton buttonPagos;
    @FXML
    private JFXButton buttonComprar;
    @FXML
    private JFXButton buttonVender;
    @FXML
    private JFXButton verPerfilButton;

    private TableView<Participacion> tablaParticipaciones;

    Parent principalEmpresa;
    Scene scene;

    @FXML
    public void initialize(){
        switch (super.getTipoUsuario()) {
            case EMPRESA:
                Empresa empresa = super.getEmpresa();
                break;
            case INVERSOR:
                Inversor inversor = super.getInversor();
                break;
        }

        seleccionVentana(super.getTipoUsuario().equals(TipoUsuario.EMPRESA));
    }
    /*
    public void Initialize(){
        buttonPerfilUsuario.setOnAction(event -> {
            Parent root;
            try{
                root = FXMLLoader.load(Main.class.getResource("view/principal.fxml"));
                Stage stage = new Stage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        seleccionVentana(false);

    }
    */

    public void seleccionVentana(boolean empresa){
        if(!empresa){
            buttonPagos.setVisible(false);
            buttonParticipaciones.setVisible(false);
            buttonParticipaciones.setDisable(false);
            buttonPagos.setDisable(false);

            buttonVender.setLayoutX(buttonParticipaciones.getLayoutX());
            buttonVender.setLayoutY(buttonParticipaciones.getLayoutY());
        }
    }

    public void gestionTablaParticipaciones(){
       tablaParticipaciones = new TableView<>();
       ObservableList<Participacion> participaciones = FXCollections.observableArrayList();
       tablaParticipaciones.setItems(participaciones);

       tablaParticipaciones.setLayoutX(56.0);
       tablaParticipaciones.setLayoutY(152.0);
       tablaParticipaciones.setMaxHeight(200);
       tablaParticipaciones.setMaxWidth(200);
       tablaParticipaciones.setPrefHeight(200);
       tablaParticipaciones.setPrefWidth(200);

       //Declaramos el nombre de las columnas
        TableColumn colEmpresa = new TableColumn("Empresa");
        TableColumn colCantidad = new TableColumn("Cantidad");

        tablaParticipaciones.getColumns().addAll(colEmpresa, colCantidad);


    }



}
