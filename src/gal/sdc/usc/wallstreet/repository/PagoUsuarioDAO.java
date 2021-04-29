package gal.sdc.usc.wallstreet.repository;

import gal.sdc.usc.wallstreet.model.Empresa;
import gal.sdc.usc.wallstreet.model.PagoUsuario;
import gal.sdc.usc.wallstreet.repository.helpers.DAO;
import gal.sdc.usc.wallstreet.util.Mapeador;

import java.sql.*;
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

    public boolean insertarListaPagos(List<PagoUsuario> pagoUsuarios){
        for(PagoUsuario p: pagoUsuarios){
            try (PreparedStatement ps = conexion.prepareStatement(
                    "INSERT INTO pago_usuario (usuario, pago_fecha, pago_empresa, num_participaciones)" +
                            " VALUES (?, ?, ?, ?)"
            )){
                ps.setString(1, p.getUsuario().getIdentificador());
                ps.setTimestamp(2, new Timestamp(((Date) p.getPago().getFecha()).getTime()));
                ps.setString(3,  p.getPago().getEmpresa().getNombre());
                ps.setInt(4, p.getNumParticipaciones());
                ps.executeUpdate();
            } catch (SQLException e) {
                System.err.println(e.getMessage());
                return false;
            }
        }
        return true;
    }



}
