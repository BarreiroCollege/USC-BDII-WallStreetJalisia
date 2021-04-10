package gal.sdc.usc.wallstreet.repository;

import gal.sdc.usc.wallstreet.model.PagoUsuario;
import gal.sdc.usc.wallstreet.repository.helpers.DAO;
import gal.sdc.usc.wallstreet.util.Mapeador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PagoUsuarioDAO extends DAO<PagoUsuario> {
    public PagoUsuarioDAO(Connection conexion) {
        super(conexion, PagoUsuario.class);
    }

    public List<PagoUsuario> getPagosHastaAhora() {
        List<PagoUsuario> pagos = new ArrayList<>();
        try (PreparedStatement ps = conexion.prepareStatement(
                "SELECT * FROM pago_usuario WHERE pago_fecha < NOW()"
        )) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                PagoUsuario pago = Mapeador.map(rs, PagoUsuario.class);
                pagos.add(pago);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return pagos;
    }
}
