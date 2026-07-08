package business.model;

public class Funcionario {
	private String _id;
	private String _nome;
	private String _funcao;

	// Construtor
	public Funcionario(String id, String nome, String funcao) {
		this._id = id;
		this._nome = nome;
		this._funcao = funcao;
	}

	// Getters e Setters
	public String getId() { return _id; }
	public void setId(String id) { this._id = id; }

	public String getNome() { return _nome; }
	public String getFuncao() { return _funcao; }

	public void setFuncao(String f) { this._funcao = f; }
}