package gal.sdc.usc.wallstreet.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import gal.sdc.usc.wallstreet.Main;
import gal.sdc.usc.wallstreet.model.PropuestaCompra;
import gal.sdc.usc.wallstreet.model.Sociedad;
import gal.sdc.usc.wallstreet.model.Usuario;
import gal.sdc.usc.wallstreet.model.UsuarioSesion;
import gal.sdc.usc.wallstreet.repository.PropuestaCompraDAO;
import gal.sdc.usc.wallstreet.repository.helpers.DatabaseLinker;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

public class SociedadController extends DatabaseLinker implements Initializable {
    public static final String VIEW = "sociedad";
    public static final Integer HEIGHT = 600;
    public static final Integer WIDTH = 800;
    public static final String TITULO = "Sociedad";

    private final BooleanProperty editando = new SimpleBooleanProperty(false);

    @FXML
    private JFXTabPane tabVentana;

    @FXML
    private JFXTextField txtIdentificador;
    @FXML
    private JFXTextField txtSaldoComunal;
    @FXML
    private JFXTextField txtTolerancia;
    @FXML
    private JFXComboBox<Label> cmbToleranciaUnidad;

    @FXML
    private TableView<PropuestaCompra> tblPropuestas;

    @FXML
    private JFXButton btnVolver;
    @FXML
    private JFXButton btnEditar;

    public SociedadController() {
    }

    private void asignarValores() {
        UsuarioSesion us = super.getUsuarioSesion();
        Usuario u = us.getUsuario();
        Sociedad s = u.getSociedad();

        txtIdentificador.setText(s.getIdentificador().getIdentificador());
        txtTolerancia.setText(s.getTolerancia().toString());
        txtSaldoComunal.setText(s.getSaldoComunal().toString());
    }

    private void onBtnEditar(ActionEvent e) {
        if (!editando.get()) {
            btnEditar.setText("Guardar");
            btnVolver.setText("Cancelar");
            editando.setValue(true);
            tabVentana.setDisable(true);
        } else {
            // TODO: Guardar
            tabVentana.setDisable(false);
        }
    }

    private void onBtnVolver(ActionEvent e) {
        if (!editando.get()) {
            Main.ventana(PrincipalController.VIEW, PrincipalController.WIDTH, PrincipalController.HEIGHT, PrincipalController.TITULO);
        } else {
            this.asignarValores();
            editando.setValue(false);
            tabVentana.setDisable(false);
            btnEditar.setText("Editar");
            btnVolver.setText("Volver");
        }
    }

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        if (super.getUsuarioSesion().getUsuario().getSociedad() == null) {
            this.onBtnVolver(null);
            return;
        }

        Usuario u = super.getUsuarioSesion().getUsuario();
        Sociedad s = u.getSociedad();

        if (!u.getLider()) {
            btnEditar.setDisable(true);
        }

        tabVentana.getSelectionModel().selectedItemProperty().addListener(listener -> {
            switch (tabVentana.getSelectionModel().getSelectedIndex()) {
                case 0:
                    btnEditar.setVisible(true);
                    break;
                case 1:
                    btnEditar.setVisible(false);
                    break;
            }
        });

        TableColumn<PropuestaCompra, String> colFecha = new TableColumn<>("Fecha");
        colFecha.setPrefWidth(150);
        colFecha.setCellValueFactory((TableColumn.CellDataFeatures<PropuestaCompra, String> param)
                -> new SimpleStringProperty(param.getValue().getFechaInicio().toString()));

        TableColumn<PropuestaCompra, Number> colCantidad = new TableColumn<>("Cantidad");
        colCantidad.setPrefWidth(150);
        colCantidad.setCellValueFactory((TableColumn.CellDataFeatures<PropuestaCompra, Number> param)
                -> new SimpleIntegerProperty(param.getValue().getCantidad()));

        TableColumn<PropuestaCompra, Number> colPrecioMax = new TableColumn<>("Precio Max");
        colPrecioMax.setPrefWidth(150);
        colPrecioMax.setCellValueFactory((TableColumn.CellDataFeatures<PropuestaCompra, Number> param)
                -> new SimpleFloatProperty(param.getValue().getPrecioMax()));

        TableColumn<PropuestaCompra, String> colEmpresa = new TableColumn<>("Empresa");
        colEmpresa.setPrefWidth(150);
        colEmpresa.setCellValueFactory((TableColumn.CellDataFeatures<PropuestaCompra, String> param)
                -> new SimpleStringProperty(param.getValue().getEmpresa().getNombre()));

        ObservableList<PropuestaCompra> pcs = FXCollections.observableList(
                super.getDAO(PropuestaCompraDAO.class).getPropuestasPorSociedad(s)
        );

        Label lblMinuto = new Label("Minutos");
        lblMinuto.setId("minuto");
        cmbToleranciaUnidad.getItems().addAll(lblMinuto);
        Label lblHora = new Label("Horas");
        lblHora.setId("hora");
        cmbToleranciaUnidad.getItems().addAll(lblHora);
        Label lblDia = new Label("Días");
        lblDia.setId("dia");
        cmbToleranciaUnidad.getItems().addAll(lblDia);

        tblPropuestas.getColumns().addAll(Arrays.asList(colFecha, colCantidad, colPrecioMax, colEmpresa));
        tblPropuestas.setItems(pcs);

        btnEditar.setOnAction(this::onBtnEditar);
        btnVolver.setOnAction(this::onBtnVolver);

        this.asignarValores();
    }
}
