package business;

import business.exceptions.PedidoNaoEncontradoException;
import business.model.Ingrediente;
import business.model.Pedido;
import data.IngredienteDAO;
import data.PedidoDAO;
import java.util.List;

public class ProducaoFacadeD implements IGestProducaoD {
	private PedidoDAO _pedidos;
	private IngredienteDAO _ingredientes;

	public ProducaoFacadeD() {
		this._pedidos = PedidoDAO.getInstance();
		this._ingredientes = IngredienteDAO.getInstance();
	}

	public List<Pedido> listarPedidosPendentes() {
		return _pedidos.getPedidosPendentes();
	}

	public void iniciarPreparacao(String aIdPedido) throws PedidoNaoEncontradoException {
		Pedido p = _pedidos.get(aIdPedido);

		if (p == null) {
			throw new PedidoNaoEncontradoException("Pedido " + aIdPedido + " não encontrado para preparação.");
		}

        if (p.get_estado().equalsIgnoreCase("REGISTADO") || p.get_estado().equalsIgnoreCase("PAGO")) {
            p.set_estado("EM PREPARACAO");
            _pedidos.put(aIdPedido, p);
            System.out.println("Pedido " + aIdPedido + " entrou em preparação.");
        } else {
            System.out.println("Erro: O pedido já está em " + p.get_estado());
        }
    }

	public void registarRuturaStock(String aIdIngrediente) {
		Ingrediente ing = _ingredientes.get(aIdIngrediente);
		if (ing != null) {
			ing.setStockAtual(0);
			_ingredientes.put(aIdIngrediente, ing);
			System.out.println("Stock do ingrediente " + aIdIngrediente + " marcado como esgotado (0).");
		} else {
			System.out.println("Ingrediente não encontrado.");
		}
	}

	public void registarEntrega(String aIdPedido) throws PedidoNaoEncontradoException {
		Pedido p = _pedidos.get(aIdPedido);

		if (p == null) {
			throw new PedidoNaoEncontradoException("Pedido " + aIdPedido + " não encontrado para preparação.");
		}

		for (business.model.ItemDeVenda item : p.getItens()) {
			item.atualizarStock();
		}

		// Registar a entrega
		p.set_estado("ENTREGUE");
		p.setHoraEntrega(java.time.LocalDateTime.now());

		_pedidos.put(aIdPedido, p);
		System.out.println("Pedido " + aIdPedido + " marcado como ENTREGUE e stock atualizado.");
	}
}