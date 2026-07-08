package data;

import business.model.Funcionario;
import business.model.Gestor;
import java.sql.*;
import java.util.*;

public class FuncionarioDAO implements Map<String, Funcionario> {
	private static FuncionarioDAO singleton = null;

	private FuncionarioDAO() {
		try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
			 Statement stm = conn.createStatement()) {

			String sql = "CREATE TABLE IF NOT EXISTS funcionarios (" +
					"Id VARCHAR(50) NOT NULL PRIMARY KEY," +
					"Nome VARCHAR(100)," +
					"Funcao VARCHAR(50)," +
					"Username VARCHAR(50)," +
					"Password VARCHAR(50)," +
					"Tipo VARCHAR(20) CHECK (Tipo IN ('FUNCIONARIO', 'GESTOR')))";
			stm.executeUpdate(sql);


		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static FuncionarioDAO getInstance() {
		if (singleton == null) singleton = new FuncionarioDAO();
		return singleton;
	}

	@Override
	public Funcionario get(Object key) {
		Funcionario f = null;
		try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
			 ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM funcionarios WHERE Id='" + key + "'")) {

			if (rs.next()) {
				String id = rs.getString("Id");
				String nome = rs.getString("Nome");
				String funcao = rs.getString("Funcao");
				String tipo = rs.getString("Tipo");

				if ("GESTOR".equals(tipo)) {

					String user = rs.getString("Username");
					String pass = rs.getString("Password");

					Gestor g = new Gestor(nome, user, pass);
					g.setId(id);
					g.setFuncao(funcao);
					f = g;
				} else {
					f = new Funcionario(id, nome, funcao);
				}
			}
		} catch (SQLException e) { e.printStackTrace(); }
		return f;
	}

	@Override
	public Funcionario put(String key, Funcionario f) {
		try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
			 PreparedStatement stm = conn.prepareStatement(
					 "INSERT INTO funcionarios (Id, Nome, Funcao, Username, Password, Tipo) VALUES (?,?,?,?,?,?) " +
							 "ON DUPLICATE KEY UPDATE Nome=VALUES(Nome), Funcao=VALUES(Funcao), Username=VALUES(Username), Password=VALUES(Password)")) {

			stm.setString(1, f.getId());
			stm.setString(2, f.getNome());
			stm.setString(3, f.getFuncao());

			if (f instanceof Gestor) {
				Gestor g = (Gestor) f;
				stm.setString(4, g.getUsername());
				stm.setString(5, g.getPassword()); // Em produção, devias encriptar isto!
				stm.setString(6, "GESTOR");
			} else {
				stm.setNull(4, Types.VARCHAR);
				stm.setNull(5, Types.VARCHAR);
				stm.setString(6, "FUNCIONARIO");
			}
			stm.executeUpdate();

		} catch (SQLException e) { e.printStackTrace(); }
		return f;
	}

	public boolean autenticarGestor(String username, String password) {
		boolean valido = false;
		try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
			 PreparedStatement stm = conn.prepareStatement("SELECT * FROM funcionarios WHERE Username=? AND Password=? AND Tipo='GESTOR'")) {

			stm.setString(1, username);
			stm.setString(2, password);
			ResultSet rs = stm.executeQuery();
			if (rs.next()) valido = true;

		} catch (SQLException e) { e.printStackTrace(); }
		return valido;
	}

	@Override public int size() { return 0; }
	@Override public boolean isEmpty() { return false; }
	@Override public boolean containsKey(Object key) { return false; }
	@Override public boolean containsValue(Object value) { return false; }
	@Override public Funcionario remove(Object key) { return null; }
	@Override public void putAll(Map<? extends String, ? extends Funcionario> m) {}
	@Override public void clear() {}
	@Override public Set<String> keySet() { return null; }
	@Override public Collection<Funcionario> values() { return null; }
	@Override public Set<Entry<String, Funcionario>> entrySet() { return null; }
}