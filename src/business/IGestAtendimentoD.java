package business;

import business.exceptions.PedidoNaoEncontradoException;
import business.model.ItemDeVenda;
import business.model.Produto;
import business.model.Menu;
import java.util.List;

public interface IGestAtendimentoD {

	public void adicionarItem(String aIdPedido, String aIdProduto) throws PedidoNaoEncontradoException;

	public void removerItem(String aIdPedido, String aIdItemCatalogo) throws PedidoNaoEncontradoException;

	public String registarPedido(String aNifCliente, String aIdRestaurante);

	public boolean finalizarPedido(String aIdPedido);

	public List<Produto> getCatalogoProdutos();

	public List<Menu> getCatalogoMenus();

	public void adicionarNota(String aIdPedido, String aIdProduto, String aTextoNota) throws PedidoNaoEncontradoException;

	public List<ItemDeVenda> getItensPedido(String aIdPedido) throws PedidoNaoEncontradoException;

	public void definirTipoEntrega(String idPedido, int tipo) throws PedidoNaoEncontradoException;
}