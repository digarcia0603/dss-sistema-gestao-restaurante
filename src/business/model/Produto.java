package business.model;

import java.util.List;
import java.util.ArrayList;

public class Produto extends ItemCatalogo {
	private List<String> _idsIngredientes;

	// Construtor simples
	public Produto(String nome) {
		super(nome, 0.0); // Preço base 0 por defeito ou vais buscar algures
		this._idsIngredientes = new ArrayList<>();
	}

	// Construtor completo
	public Produto(String nome, double preco, List<String> idsIngredientes) {
		super(nome, preco);
		this._idsIngredientes = idsIngredientes;
	}

	public List<String> getIngredientes() {
		return _idsIngredientes;
	}
	public void setIngredientes(List<String> ids) {
		this._idsIngredientes = ids;
	}

	public void atualizarStock() {
		data.IngredienteDAO dao = data.IngredienteDAO.getInstance();

		for (String idIng : _idsIngredientes) {
			Ingrediente ing = dao.get(idIng);
			if (ing != null) {
				ing.atualizarStock(-1);
				dao.put(idIng, ing);
				System.out.println("Stock de " + idIng + " atualizado: " + ing.getStockAtual());
			}
		}
	}
}