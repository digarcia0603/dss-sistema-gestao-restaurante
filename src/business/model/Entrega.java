package business.model;

import java.time.LocalDateTime;

public abstract class Entrega {
	private String _idEntrega;
	private LocalDateTime _horaEntregue;


	public Entrega(String idEntrega, LocalDateTime hora) {
		this._idEntrega = idEntrega;
		this._horaEntregue = hora;
	}

	public String getIdEntrega() { return _idEntrega; }
	public LocalDateTime getHoraEntregue() { return _horaEntregue; }
	public void setHoraEntregue(LocalDateTime h) { this._horaEntregue = h; }
}