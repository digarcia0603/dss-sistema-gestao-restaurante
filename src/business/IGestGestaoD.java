package business;

import business.model.Restaurante;
import java.time.LocalDate;
import java.util.List;

public interface IGestGestaoD {

	public boolean autenticarGestor(String aUsername, String aPassword);

	public List<Restaurante> getRestaurantes();

	public double getFaturacao(String aIdRestaurante, LocalDate aInicio, LocalDate aFim);

	public double getTempoMedioEspera(String aIdRestaurante, LocalDate aInicio, LocalDate aFim);
}