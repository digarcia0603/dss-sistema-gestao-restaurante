package business;

import business.exceptions.PedidoNaoEncontradoException;
import business.model.*;
import data.ItemCatalogoDAO;
import data.PedidoDAO;

import java.util.List;

public class AtendimentoFacadeD implements IGestAtendimentoD {
	private PedidoDAO _pedidos;
	private ItemCatalogoDAO _itemsCatalogo;

	public AtendimentoFacadeD() {
		this._pedidos = PedidoDAO.getInstance();
		this._itemsCatalogo = ItemCatalogoDAO.getInstance();
	}

	public void adicionarItem(String aIdPedido, String aIdItem) throws PedidoNaoEncontradoException {

		Pedido p = _pedidos.get(aIdPedido);

		if (p == null) {
			throw new PedidoNaoEncontradoException("O pedido " + aIdPedido + " não existe no sistema.");
		}

		ItemCatalogo itemDoCatalogo = _itemsCatalogo.get(aIdItem);

		if (itemDoCatalogo != null) {
			ItemDeVenda item = new ItemDeVenda(itemDoCatalogo);

			// 4. Adiciona
			p.getItens().add(item);
			_pedidos.put(aIdPedido, p);

			System.out.println(">> Item adicionado!");
		} else {
			System.out.println(">> Item não encontrado no catálogo.");
		}
	}

	public void removerItem(String aIdPedido, String aIdItemCatalogo) throws PedidoNaoEncontradoException{
		Pedido p = _pedidos.get(aIdPedido);

		if (p == null) {
			throw new PedidoNaoEncontradoException("O pedido " + aIdPedido + " não existe no sistema.");
		}

		ItemCatalogo itemCat = _itemsCatalogo.get(aIdItemCatalogo);

		if (itemCat != null) {
			List<ItemDeVenda> itens = p.getItens();
			ItemDeVenda alvo = null;

			for (ItemDeVenda item : itens) {
				if (item.getElemento().getNome().equals(itemCat.getNome())) {
					alvo = item;
					break;
				}
			}

			if (alvo != null) {
				itens.remove(alvo);
				_pedidos.put(aIdPedido, p);
				System.out.println("Item removido com sucesso.");
			} else {
				System.out.println("Item não encontrado no pedido.");
			}
		}
	}

	public String registarPedido(String aNifCliente, String aIdRestaurante) {
		try {

			int proximoId = _pedidos.size() + 1;
			String idGerado = String.valueOf(proximoId);

			Pedido novoPedido = new Pedido(idGerado, aNifCliente, aIdRestaurante);
			Pedido resultado = _pedidos.put(idGerado, novoPedido);
			if (resultado == null) {
				System.out.println(">> Erro: Não foi possível criar o pedido.");
				System.out.println(">> (Verifique se o Restaurante '" + aIdRestaurante + "' existe).");
				return null;
			}

			return idGerado;
		} catch (Exception e) {
			return null;
		}
	}

	public boolean finalizarPedido(String aIdPedido) {
		Pedido p = _pedidos.get(aIdPedido);
		if (p == null) return false;

		double total = p.calcularTotal();
		String estado = "PENDENTE";


		Pagamento pag = new Pagamento("PAG-" + aIdPedido, total, estado);
		boolean pagoComSucesso = pag.validar();

		if (pagoComSucesso) {
			p.set_estado("PAGO");

			p.setPagamento(pag);

			// Grava na BD
			_pedidos.put(aIdPedido, p);

			System.out.println(">> " + pag.getRecibo());
			return true;
		} else {
			return false;
		}
	}

	public List<Produto> getCatalogoProdutos() {
		return _itemsCatalogo.getProdutos();
	}

	public List<Menu> getCatalogoMenus() {
		return _itemsCatalogo.getMenus();
	}

	public void adicionarNota(String aIdPedido, String aIdProduto, String aTextoNota) throws PedidoNaoEncontradoException{
		Pedido p = _pedidos.get(aIdPedido);

		if (p == null) {
			throw new PedidoNaoEncontradoException("O pedido " + aIdPedido + " não existe no sistema.");
		}

		ItemCatalogo itemCat = _itemsCatalogo.get(aIdProduto);

		if (itemCat != null) {
			List<ItemDeVenda> itens = p.getItens();
			boolean encontrou = false;

			for (int i = itens.size() - 1; i >= 0; i--) {
				ItemDeVenda item = itens.get(i);
				if (item.getElemento().getNome().equals(itemCat.getNome())) {
					item.setNota(aTextoNota);
					encontrou = true;
					break;
				}
			}

			if (encontrou) {
				_pedidos.put(aIdPedido, p);
				System.out.println("Nota adicionada.");
			} else {
				System.out.println("Produto não encontrado no pedido para adicionar nota.");
			}
		} else {
			System.out.println(">> Erro: O produto '" + aIdProduto + "' não existe no catálogo.");
		}
	}

	public List<ItemDeVenda> getItensPedido(String aIdPedido) throws PedidoNaoEncontradoException {
		Pedido p = _pedidos.get(aIdPedido);
		if (p == null) {
			throw new PedidoNaoEncontradoException("Pedido não encontrado.");
		}
		return p.getItens();
	}

	public void definirTipoEntrega(String idPedido, int tipo) throws PedidoNaoEncontradoException {
		Pedido p = _pedidos.get(idPedido);

		if (p == null) {
			throw new PedidoNaoEncontradoException("Pedido " + idPedido + " não existe.");
		}

		if (tipo == 1) {
			int mesaRandom = (int)(Math.random() * 16) + 1;

			// Cria a mesa com o número gerado
			business.model.Mesa mesa = new business.model.Mesa(idPedido, null, mesaRandom);
			p.setEntrega(mesa);

			System.out.println(">> Sistema atribuiu a Mesa " + mesaRandom);

		} else {
			TakeAway take = new business.model.TakeAway(idPedido, null);
			p.setEntrega(take);

			System.out.println(">> Definido como TakeAway.");
		}

		_pedidos.put(idPedido, p);
	}
}