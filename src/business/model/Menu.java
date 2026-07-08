package business.model;

import java.util.List;
import java.util.ArrayList;

public class Menu extends ItemCatalogo {
	private List<Produto> _produtos;

	public Menu(String nome, double preco) {
		super(nome, preco);
		this._produtos = new ArrayList<>();
	}


	public Menu(String nome, double preco, List<Produto> produtos) {
		super(nome, preco);
		this._produtos = produtos;
	}

	public List<Produto> getProdutos() { return _produtos; }
	public void addProduto(Produto p) { _produtos.add(p); }
}