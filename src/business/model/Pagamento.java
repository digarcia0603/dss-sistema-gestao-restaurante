package business.model;

import java.time.LocalDateTime;

public class Pagamento {
	private String _idPagamento;
	private double _valor;
	private LocalDateTime _dataHora;
	private String _estado;

	public Pagamento(String id, double valor, String estado) {
		this._idPagamento = id;
		this._valor = valor;
		this._dataHora = LocalDateTime.now();
		this._estado = estado;
	}

	public String getRecibo() {
		return "Recibo #" + _idPagamento + " | Total: " + _valor + "€";
	}

	public boolean validar() {
		// Lógica de validação com sistema externo
		return true;
	}

	// Getters
	public String getIdPagamento() { return _idPagamento; }
	public double getValor() { return _valor; }
	public LocalDateTime getDataHora() { return _dataHora; }
	public String getEstado() { return _estado; }
	public void setEstado(String e) { this._estado = e; }
}