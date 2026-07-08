package data;

import business.model.Ingrediente;
import java.sql.*;
import java.util.*;

public class IngredienteDAO implements Map<String, Ingrediente> {
	private static IngredienteDAO singleton = null;

	private IngredienteDAO() {
		try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
			 Statement stm = conn.createStatement()) {
			stm.executeUpdate("CREATE TABLE IF NOT EXISTS ingredientes (Id VARCHAR(50) PRIMARY KEY, Stock INT)");
		} catch (SQLException e) { e.printStackTrace(); }
	}

	public static IngredienteDAO getInstance() {
		if (singleton == null) singleton = new IngredienteDAO();
		return singleton;
	}

	@Override
	public Ingrediente put(String key, Ingrediente value) {
		try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
			 PreparedStatement stm = conn.prepareStatement(
					 "INSERT INTO ingredientes (Id, Stock) VALUES (?,?) ON DUPLICATE KEY UPDATE Stock=VALUES(Stock)")) {
			stm.setString(1, value.getId());
			stm.setInt(2, value.getStockAtual());
			stm.executeUpdate();
		} catch (SQLException e) { e.printStackTrace(); }
		return value;
	}

	@Override
	public Ingrediente get(Object key) {
		Ingrediente ing = null;
		try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
			 Statement stm = conn.createStatement();
			 ResultSet rs = stm.executeQuery("SELECT * FROM ingredientes WHERE Id='" + key + "'")) {

			if (rs.next()) {
				String id = rs.getString("Id");
				int stock = rs.getInt("Stock");
				ing = new Ingrediente(id, stock);
			}
		} catch (SQLException e) { e.printStackTrace(); }
		return ing;
	}
	@Override public int size() { return 0; }
	@Override public boolean isEmpty() { return false; }
	@Override public boolean containsKey(Object key) { return false; }
	@Override public boolean containsValue(Object value) { return false; }
	@Override public Ingrediente remove(Object key) { return null; }
	@Override public void putAll(Map<? extends String, ? extends Ingrediente> m) {}
	@Override public void clear() {}
	@Override public Set<String> keySet() { return null; }
	@Override public Collection<Ingrediente> values() { return null; }
	@Override public Set<Entry<String, Ingrediente>> entrySet() { return null; }
}