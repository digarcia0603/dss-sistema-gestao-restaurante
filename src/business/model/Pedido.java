package business.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

public class Pedido {
	private String _idPedido;
	private String _estado;
	private String _nifCliente;
	private String _idRestaurante;
	private LocalDateTime _dataHora;
	private double _total;
	private List<ItemDeVenda> _itemsPedido;
	private Pagamento _pagamento;
	private LocalDateTime _horaEntrega;
	private Entrega _entrega;

	// Construtor vazio
	public Pedido(String idPedido, String nif, String idRestaurante) {
		this._idPedido = idPedido;
		this._nifCliente = nif;
		this._idRestaurante = idRestaurante;
		this._estado = "REGISTADO";
		this._dataHora = LocalDateTime.now();
		this._total = 0.0;
		this._itemsPedido = new ArrayList<>();
	}

	// Construtor COMPLETO
	public Pedido(String id, String nif, String idRestaurante, LocalDateTime data, String estado, List<ItemDeVenda> itens) {
		this._idPedido = id;
		this._nifCliente = nif;
		this._idRestaurante = idRestaurante;
		this._dataHora = data;
		this._estado = estado;
		this._itemsPedido = itens;

		this.calcularTotal();
	}


	// Métodos do Diagrama
	public double calcularTotal() {
		double t = 0;
		for (ItemDeVenda i : _itemsPedido) {
			t += i.getPrecoRegistado();
		}
		this._total = t;
		return t;
	}

	// Getters
	public String getIdPedido() { return _idPedido; }
	public String getNifCliente() { return _nifCliente; }
	public String get_idRestaurante() { return _idRestaurante; }
	public String get_estado() { return _estado; }
	public LocalDateTime getDataHora() { return _dataHora; }
	public double getTotal() { return _total; }
	public List<ItemDeVenda> getItens() { return _itemsPedido; }
	public Pagamento getPagamento() {
		return _pagamento;
	}
	public LocalDateTime getHoraEntrega() { return _horaEntrega; }
	public Entrega getEntrega() { return _entrega; }

	public void setEntrega(Entrega entrega) {this._entrega = entrega;}
	public void setPagamento(Pagamento p) {
		this._pagamento = p;
	}
	public void setHoraEntrega(LocalDateTime h) { this._horaEntrega = h; }
	public void set_estado(String e) { this._estado = e; }


}