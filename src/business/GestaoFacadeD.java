package business;

import business.model.Restaurante;
import data.FuncionarioDAO;
import data.RestauranteDAO;
import java.time.LocalDate;
import java.util.List;

public class GestaoFacadeD implements IGestGestaoD {
	private RestauranteDAO _restaurantes;
	private FuncionarioDAO _funcionarios;

	public GestaoFacadeD() {
		this._restaurantes = RestauranteDAO.getInstance();
		this._funcionarios = FuncionarioDAO.getInstance();
	}

	public boolean autenticarGestor(String aUsername, String aPassword) {
		return _funcionarios.autenticarGestor(aUsername, aPassword);
	}

	public List<Restaurante> getRestaurantes() {
		return _restaurantes.getRestaurantes();
	}

	public double getFaturacao(String aIdRestaurante, LocalDate aInicio, LocalDate aFim) {
		double total = 0.0;
		// Query que soma o total dos pedidos daquele restaurante entre datas
		String sql = "SELECT SUM(Total) FROM pedidos WHERE IdRestaurante = ? AND DataHora BETWEEN ? AND ?";

		try (java.sql.Connection conn = java.sql.DriverManager.getConnection(data.DAOconfig.URL, data.DAOconfig.USERNAME, data.DAOconfig.PASSWORD);
			 java.sql.PreparedStatement stm = conn.prepareStatement(sql)) {

			// Converter LocalDate para Timestamp (Início do dia e Fim do dia)
			java.sql.Timestamp dataIni = java.sql.Timestamp.valueOf(aInicio.atStartOfDay());
			java.sql.Timestamp dataFim = java.sql.Timestamp.valueOf(aFim.atTime(23, 59, 59));

			stm.setString(1, aIdRestaurante);
			stm.setTimestamp(2, dataIni);
			stm.setTimestamp(3, dataFim);

			java.sql.ResultSet rs = stm.executeQuery();
			if (rs.next()) {
				total = rs.getDouble(1);
			}
		} catch (Exception e) { e.printStackTrace(); }

		return total;
	}

	public double getTempoMedioEspera(String aIdRestaurante, LocalDate aInicio, LocalDate aFim) {
		double mediaMinutos = 0.0;

		String sql = "SELECT AVG(TIMESTAMPDIFF(SECOND, DataHora, HoraEntrega)) " +
				"FROM pedidos " +
				"WHERE IdRestaurante = ? " +
				"AND DataHora BETWEEN ? AND ? " +
				"AND Estado = 'ENTREGUE'"; // Só conta os que já foram entregues

		try (java.sql.Connection conn = java.sql.DriverManager.getConnection(data.DAOconfig.URL, data.DAOconfig.USERNAME, data.DAOconfig.PASSWORD);
			 java.sql.PreparedStatement stm = conn.prepareStatement(sql)) {

			java.sql.Timestamp dataIni = java.sql.Timestamp.valueOf(aInicio.atStartOfDay());
			java.sql.Timestamp dataFim = java.sql.Timestamp.valueOf(aFim.atTime(23, 59, 59));

			stm.setString(1, aIdRestaurante);
			stm.setTimestamp(2, dataIni);
			stm.setTimestamp(3, dataFim);

			java.sql.ResultSet rs = stm.executeQuery();
			if (rs.next()) {
				mediaMinutos = rs.getDouble(1);
			}
		} catch (Exception e) { e.printStackTrace(); }

		return mediaMinutos;
	}
}