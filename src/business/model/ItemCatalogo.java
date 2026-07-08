package business.model;

public abstract class ItemCatalogo {
	private String _id;
	private String _nome;
	private double _precoBase;

	public ItemCatalogo(String nome, double precoBase) {
		this._nome = nome;
		this._precoBase = precoBase;
	}

	// Getters e Setters
	public String getNome() { return _nome; }
	public double getPrecoBase() { return _precoBase; }
	public String getId() { return _id; }

	public void setId(String id) { this._id = id; }
	public void setPrecoBase(double preco) { this._precoBase = preco; }
}