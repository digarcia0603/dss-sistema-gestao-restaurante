package business.model;

import java.time.LocalDateTime;

public class TakeAway extends Entrega {

	public TakeAway(String idEntrega, LocalDateTime hora) {
		super(idEntrega, hora);
	}

}