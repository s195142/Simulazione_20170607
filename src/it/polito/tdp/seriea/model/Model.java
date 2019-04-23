package it.polito.tdp.seriea.model;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.seriea.db.SerieADAO;

public class Model {
	
	private SimpleDirectedWeightedGraph<Team, DefaultWeightedEdge> grafo;

	public static List<Team> getAllTeams() {
		SerieADAO dao = new SerieADAO();
		return dao.listTeams();
	}

	public static List<Season> getAllSeasons() {
		SerieADAO dao = new SerieADAO();
		return dao.listSeasons();
	}

	public String doClassifica(Season s) {
		// riesco a creare tutto il grafo da DB? O_O T_T si :D ma devo convertire i dati
		SerieADAO dao = new SerieADAO();
		String res = "";
		//il grafo rimane sempre uguale o cambia in base al parametro che gli passo? -> in qst caso cambia
		// -> lo dichiaro fuori
		// -> lo inizializzo qua xk cambia
		grafo = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		dao.popola(grafo, s);
		
		List<Team> classifica = new LinkedList<Team>(grafo.vertexSet());
		Collections.sort(classifica);
		
		for(Team t : classifica) {
			res += t.getTeam() + " "+ t.getPunti()+"\n";
		}
		
		return res;
	}

}
