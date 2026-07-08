package business.model;

public class Gestor extends Funcionario {
	private String _password;
	private String _username;

	// Construtor
	public Gestor(String nome, String username, String password) {
		super(null, nome, "GESTOR");
		this._username = username;
		this._password = password;
	}

	public boolean validarPassword(String aPass) {
		return this._password.equals(aPass);
	}

	public String getUsername() { return _username; }
	public String getPassword() { return _password; }
}