package gal.sdc.usc.wallstreet.repository;

import gal.sdc.usc.wallstreet.model.Empresa;
import gal.sdc.usc.wallstreet.model.OfertaVenta;
import gal.sdc.usc.wallstreet.repository.helpers.DAO;
import gal.sdc.usc.wallstreet.util.Mapeador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmpresaDAO extends DAO<Empresa> {
    public EmpresaDAO(Connection conexion) {
        super(conexion, Empresa.class);
    }

    public List<Empresa> getEmpresas (){
        List<Empresa> empresas = new ArrayList<>();
        try (PreparedStatement ps = conexion.prepareStatement(
                "SELECT e.* FROM empresa as e join usuario as u ON(e.usuario=u.identificador) WHERE alta is null"
        )) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Empresa empresa = Mapeador.map(rs, Empresa.class);
                empresas.add(empresa);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return empresas;
    }
}
