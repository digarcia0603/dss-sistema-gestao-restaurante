package business;

import business.exceptions.PedidoNaoEncontradoException;
import business.model.Pedido;
import java.util.List;

public interface IGestProducaoD {

	public List<Pedido> listarPedidosPendentes();

	public void iniciarPreparacao(String aIdPedido) throws PedidoNaoEncontradoException;

	public void registarRuturaStock(String aIdIngrediente);

	public void registarEntrega(String aIdPedido) throws PedidoNaoEncontradoException;
}