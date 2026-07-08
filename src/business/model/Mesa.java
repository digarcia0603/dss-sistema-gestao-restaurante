package business.model;

import java.time.LocalDateTime;

public class Mesa extends Entrega {
	private int _numeroMesa;

	public Mesa(String idEntrega, LocalDateTime hora, int numeroMesa) {
		super(idEntrega, hora);
		this._numeroMesa = numeroMesa;
	}

	public int getNumeroMesa() { return _numeroMesa; }
}