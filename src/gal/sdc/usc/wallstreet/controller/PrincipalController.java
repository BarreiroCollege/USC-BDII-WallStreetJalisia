package gal.sdc.usc.wallstreet.controller;

import com.jfoenix.controls.JFXButton;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import gal.sdc.usc.wallstreet.Main;
import gal.sdc.usc.wallstreet.model.Empresa;
import gal.sdc.usc.wallstreet.model.Inversor;
import gal.sdc.usc.wallstreet.model.Participacion;
import gal.sdc.usc.wallstreet.repository.helpers.DatabaseLinker;
import gal.sdc.usc.wallstreet.util.Iconos;
import gal.sdc.usc.wallstreet.util.TipoUsuario;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import javax.swing.*;


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

    @FXML
    private TableView<Participacion> tablaParticipaciones;

    @FXML
    private TableColumn<Participacion, String> colEmpresa;

    @FXML
    private TableColumn<Participacion, Integer> colCantidad;

    @FXML
    private Menu buttonPerfil;

    @FXML
    private Menu buttonEstadisticas;


    Parent principalEmpresa;
    Scene scene;

    @FXML
    public void initialize(){
        Group root = new Group();
        scene = new Scene(root, 683, 551);
        switch (super.getTipoUsuario()) {
            case EMPRESA:
                Empresa empresa = super.getEmpresa();
                break;
            case INVERSOR:
                Inversor inversor = super.getInversor();
                break;
        }
        seleccionVentana(super.getTipoUsuario().equals(TipoUsuario.INVERSOR));
        gestionTablaParticipaciones();
        buttonPerfil.setGraphic(Iconos.icono(FontAwesomeIcon.USER, "2.5em"));
        buttonEstadisticas.setGraphic(Iconos.icono(FontAwesomeIcon.BAR_CHART, "2.5em"));
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
       ObservableList<Participacion> participaciones = FXCollections.observableArrayList(
       );
       tablaParticipaciones.setItems(participaciones);

       //Declaramos el nombre de las columnas

        colEmpresa.setCellValueFactory(new PropertyValueFactory<Participacion, String>("empresa"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<Participacion, Integer>("cantidad"));

    }

}
