package gal.sdc.usc.wallstreet.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import gal.sdc.usc.wallstreet.Main;
import gal.sdc.usc.wallstreet.model.Empresa;
import gal.sdc.usc.wallstreet.model.Participacion;
import gal.sdc.usc.wallstreet.model.Usuario;
import gal.sdc.usc.wallstreet.repository.EmpresaDAO;
import gal.sdc.usc.wallstreet.repository.ParticipacionDAO;
import gal.sdc.usc.wallstreet.repository.helpers.DatabaseLinker;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class PartEmpresaController extends DatabaseLinker {
    public static final String VIEW = "partempresa";
    public static final Integer HEIGHT = 431;
    public static final Integer WIDTH = 439;
    public static final String TITULO = "Gestión de participaciones";

    @FXML
    private JFXButton btnCerrarVentana;
    @FXML
    private JFXButton btnAnadir;
    @FXML
    private JFXButton btnEliminar;

    @FXML
    private JFXTextField txtParticip;

    @FXML
    private Label labelParticip;

    private Usuario usuario;
    private Empresa empresa;
    private Integer participaciones;
    private Integer participacionesTotales;

    @FXML
    public void initialize() {
        // Recuperamos el usuario con sesion, así como su objeto empresa
        usuario = super.getUsuarioSesion().getUsuario();
        empresa = super.getDAO(EmpresaDAO.class).getEmpresa(usuario.getSuperUsuario().getIdentificador());

        // Funciones de los botones
        btnEliminar.setOnAction(e -> this.eliminarParticip());
        btnAnadir.setOnAction(e -> this.anadirParticip());
        btnCerrarVentana.setOnAction(e -> this.cerrarVentana());

        // Evitamos que se escriban caracteres que no sean numeros
        txtParticip.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*|\\d+\\.\\d{0,2}")) txtParticip.setText(oldValue);
            updateButtons();
        });

        updateWindow();
        updateButtons();
    }

    private void updateWindow() {
        participaciones = super.getDAO(ParticipacionDAO.class).getParticipacionesUsuarioEmpresa(usuario.getSuperUsuario().getIdentificador(), usuario.getSuperUsuario().getIdentificador());
        participacionesTotales = super.getDAO(ParticipacionDAO.class).getParticipacionesEmpresa(usuario.getSuperUsuario().getIdentificador());
        labelParticip.setText(participaciones.toString());

    }

    private void updateButtons() {
        btnAnadir.setDisable(txtParticip.getText().isEmpty());
        if (txtParticip == null || txtParticip.getText().isEmpty()) btnEliminar.setDisable(true);
        else btnEliminar.setDisable(participaciones < Integer.parseInt(txtParticip.getText()));
    }

    public void anadirParticip() {

        if (txtParticip == null || txtParticip.getText().isEmpty()) return;

        // Si la empresa no tenía ninguna participación propia, particip será 0, con lo que no se modifica la cantidad inicial
        updateWindow();

        Integer cantidad = Integer.parseInt(txtParticip.getText()) + participacionesTotales;

        Participacion part = super.getDAO(ParticipacionDAO.class).seleccionar(usuario.getSuperUsuario(), empresa);

        // Miramos si tiene o no registro de sus propias participaciones ya
        if (part != null) {
            part.setCantidad(cantidad);
            // Si ya tiene participaciones, tenemos que modificar el valor
            super.getDAO(ParticipacionDAO.class).actualizar(part);
        } else {
            part = new Participacion.Builder()
                    .withUsuario(usuario.getSuperUsuario())
                    .withEmpresa(empresa)
                    .withCantidad(cantidad)
                    .withCantidadBloqueada(0)
                    .build();

            // Si no tenía, tenemos que insertar un nuevo registro de participaciones
            super.getDAO(ParticipacionDAO.class).insertar(part);
        }

        updateWindow();
        updateButtons();
    }

    public void eliminarParticip() {

        if (txtParticip == null || txtParticip.getText().isEmpty()) return;

        // Obtenemos la cantidad actual de participaciones y bloqueadas
        updateWindow();
        Integer partBloq = super.getDAO(ParticipacionDAO.class).getParticipacionesBloqueadasUsuarioEmpresa(usuario.getSuperUsuario().getIdentificador(), usuario.getSuperUsuario().getIdentificador());

        // Recuperamos el valor de entrada
        Integer input = Integer.parseInt(txtParticip.getText());

        if (participaciones < input) {
            Main.mensaje("No hay participaciones suficientes para borrar");
            return;
        }

        // Actualizamos la cantidad
        Integer cantidad = participacionesTotales - input;

        // Y lo guardamos en la base de datos
        Participacion part = new Participacion.Builder().withUsuario(usuario.getSuperUsuario()).withEmpresa(empresa).withCantidad(cantidad).withCantidadBloqueada(partBloq).build();
        super.getDAO(ParticipacionDAO.class).actualizar(part);

        updateWindow();
        updateButtons();
    }

    public void cerrarVentana() {
        Main.ventana(
                PrincipalController.VIEW,
                PrincipalController.WIDTH,
                PrincipalController.HEIGHT,
                PrincipalController.TITULO
        );
    }

}
