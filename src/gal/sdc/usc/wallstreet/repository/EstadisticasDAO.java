package gal.sdc.usc.wallstreet.repository;

import gal.sdc.usc.wallstreet.model.Estadistica;
import gal.sdc.usc.wallstreet.model.Inversor;
import gal.sdc.usc.wallstreet.repository.helpers.DAO;
import gal.sdc.usc.wallstreet.util.Mapeador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EstadisticasDAO extends DAO<Estadistica> {

    public EstadisticasDAO(Connection conexion) {
        super(conexion, Estadistica.class);
    }

    /***
     * Devuelve los datos de la materialized view estadistica
     *
     * @return Lista de empresas con sus correspondientes atributos; null en caso de error
     */
    public List<Estadistica> getDatos(){
        List<Estadistica> estadisticas = new ArrayList<>();

        try (PreparedStatement ps = conexion.prepareStatement(
                "SELECT * FROM estadistica"
        )){
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                estadisticas.add(new Estadistica.Builder().withEmpresa(rs.getString(1)).withBeneficioMedio(
                        rs.getFloat(2)).withParticipacionesMedias(rs.getFloat(3)).withNumPagosMes(rs.getFloat(4))
                        .withPrecioMedioMes(rs.getFloat(5)).build());
                //estadisticas.add(Mapeador.map(rs, Estadistica.class));
            }
        } catch (SQLException e){
            e.printStackTrace();
            return null;
        }

        return estadisticas;
    }
}


