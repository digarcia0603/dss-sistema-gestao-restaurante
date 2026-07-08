package business.model;

import java.time.LocalDate;

public class Restaurante {
	private String _idRestaurante;
	private String _localizacao;
	private int _balcao; // Capacidade

	// Construtor
	public Restaurante(String id, String localizacao, int balcao) {
		this._idRestaurante = id;
		this._localizacao = localizacao;
		this._balcao = balcao;
	}

	// Getters
	public String getIdRestaurante() { return _idRestaurante; }
	public String getLocalizacao() { return _localizacao; }
	public int getBalcao() { return _balcao; }

	public double calcularFaturacao(LocalDate aInicio, LocalDate aFim) {
		return 0.0;
	}

	public double calcularTempoMedio(LocalDate aInicio, LocalDate aFim) {
		return 0.0;
	}
}