package business.model;

public class ItemDeVenda {
	private String _nota;
	private double _precoRegistado;
	private ItemCatalogo _elemento;

	// Construtor vazio
	public ItemDeVenda(ItemCatalogo elemento) {
		this._elemento = elemento;
		this._nota = "";
		this._precoRegistado = elemento.getPrecoBase();
	}

	// Construtor completo
	public ItemDeVenda(ItemCatalogo elemento, String nota, double preco) {
		this._elemento = elemento;
		this._nota = nota;
		this._precoRegistado = preco;
	}

	public void atualizarStock() {
		if (_elemento instanceof Produto) {
			((Produto) _elemento).atualizarStock();
		} else if (_elemento instanceof Menu) {
			Menu m = (Menu) _elemento;
			for(Produto p : m.getProdutos()) {
				p.atualizarStock();
			}
		}
	}

	// Getters
	public ItemCatalogo getElemento() {
		return _elemento;
	}
	public String getNota() { return _nota; }
	public double getPrecoRegistado() { return _precoRegistado; }

	public void setNota(String nota) { this._nota = nota; }

	@Override
	public String toString() {
		return _elemento.getNome() + " | " + _precoRegistado + "€" + (_nota.isEmpty() ? "" : " [" + _nota + "]");
	}
}