package data;

import business.model.Restaurante;
import java.sql.*;
import java.util.*;

public class RestauranteDAO implements Map<String, Restaurante> {
	private static RestauranteDAO singleton = null;

	private RestauranteDAO() {
		try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
			 Statement stm = conn.createStatement()) {

			String sql = "CREATE TABLE IF NOT EXISTS restaurantes (" +
					"Id VARCHAR(50) NOT NULL PRIMARY KEY," +
					"Localizacao VARCHAR(100)," +
					"Balcao INT)";
			stm.executeUpdate(sql);

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static RestauranteDAO getInstance() {
		if (singleton == null) singleton = new RestauranteDAO();
		return singleton;
	}

	@Override
	public Restaurante get(Object key) {
		Restaurante r = null;
		try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
			 ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM restaurantes WHERE Id='" + key + "'")) {

			if (rs.next()) {
				r = new Restaurante(rs.getString("Id"), rs.getString("Localizacao"), rs.getInt("Balcao"));
			}
		} catch (SQLException e) { e.printStackTrace(); }
		return r;
	}

	@Override
	public Restaurante put(String key, Restaurante r) {
		try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
			 PreparedStatement stm = conn.prepareStatement(
					 "INSERT INTO restaurantes (Id, Localizacao, Balcao) VALUES (?,?,?) " +
							 "ON DUPLICATE KEY UPDATE Localizacao=VALUES(Localizacao), Balcao=VALUES(Balcao)")) {

			stm.setString(1, r.getIdRestaurante());
			stm.setString(2, r.getLocalizacao());
			stm.setInt(3, r.getBalcao());
			stm.executeUpdate();

		} catch (SQLException e) { e.printStackTrace(); }
		return r;
	}

	public List<Restaurante> getRestaurantes() {
		List<Restaurante> lista = new ArrayList<>();
		try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
			 ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM restaurantes")) {
			while (rs.next()) {
				lista.add(new Restaurante(rs.getString("Id"), rs.getString("Localizacao"), rs.getInt("Balcao")));
			}
		} catch (SQLException e) { e.printStackTrace(); }
		return lista;
	}

	@Override public int size() { return 0; }
	@Override public boolean isEmpty() { return false; }
	@Override public boolean containsKey(Object key) { return false; }
	@Override public boolean containsValue(Object value) { return false; }
	@Override public Restaurante remove(Object key) { return null; }
	@Override public void putAll(Map<? extends String, ? extends Restaurante> m) {}
	@Override public void clear() {}
	@Override public Set<String> keySet() { return null; }
	@Override public Collection<Restaurante> values() { return getRestaurantes(); }
	@Override public Set<Entry<String, Restaurante>> entrySet() { return null; }
}