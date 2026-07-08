package business.model;

public class Ingrediente {
	private String _idIngrediente;
	private int _stockAtual;

	public Ingrediente(String id, int stock) {
		this._idIngrediente = id;
		this._stockAtual = stock;
	}

	public void atualizarStock(int qtd) {
		this._stockAtual += qtd; // Soma ou subtrai
	}

	// Getters e Setters
	public String getId() { return _idIngrediente; }
	public int getStockAtual() { return _stockAtual; }
	public void setStockAtual(int s) { this._stockAtual = s; }
}