package it.polito.tdp.seriea.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.seriea.model.Season;
import it.polito.tdp.seriea.model.Team;

public class SerieADAO {
	
	public List<Season> listSeasons() {
		String sql = "SELECT season, description FROM seasons" ;
		
		List<Season> result = new ArrayList<>() ;
		
		Connection conn = DBConnect.getConnection() ;
		
		try {
			PreparedStatement st = conn.prepareStatement(sql) ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				result.add( new Season(res.getInt("season"), res.getString("description"))) ;
			}
			
			conn.close();
			return result ;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}
	
	public List<Team> listTeams() {
		String sql = "SELECT team FROM teams" ;
		
		List<Team> result = new ArrayList<>() ;
		
		Connection conn = DBConnect.getConnection() ;
		
		try {
			PreparedStatement st = conn.prepareStatement(sql) ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				result.add( new Team(res.getString("team"))) ;
			}
			
			conn.close();
			return result ;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}

	public void popola(SimpleDirectedWeightedGraph<Team, DefaultWeightedEdge> grafo, Season s) {
		final String sql = "SELECT HomeTeam AS h, AwayTeam AS t, case " + 
							"when FTR = 'H' then 1 when FTR = 'A' then -1 ELSE 0 END AS res " + 
							"FROM matches WHERE season = ? ";
		
		Map<Team, Team> mappa = new HashMap<>();
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, s.getSeason());
			ResultSet rs = st.executeQuery();
			
			while (rs.next()) {		
				
				String sqCasa = rs.getString("h");
				String sqTrasf = rs.getString("t");
				double peso = rs.getDouble("res"); // i pesi sono sempre double
				
				// creo vertici
				Team t1 = new Team(sqCasa);
				Team t2 = new Team(sqTrasf);
				int punticasa = 0;
				int puntitrasf = 0;
				
				if(!mappa.containsKey(t1)) {
					mappa.put(t1, t1);
				}
				
				if(!mappa.containsKey(t2)) {
					mappa.put(t2, t2);
				}
				
				if(peso==1) {
					punticasa = 3;
					puntitrasf=0;
				}else if(peso==-1) {
					punticasa=0;
					puntitrasf=3;
				}else if(peso==0) {
					punticasa=1;
					puntitrasf=1;
				}
			
				
				// controllo se ci sono già i vertici - se esiste
				if(!grafo.containsVertex(mappa.get(t1))) {
					grafo.addVertex(mappa.get(t1));
				}
				
				if(!grafo.containsVertex(mappa.get(t2))) {
					grafo.addVertex(mappa.get(t2));
				}
				//in caso di orientamento fai 2 controlli - meglio farli sempre
				// controllo se va da A a B
				if(!grafo.containsEdge(mappa.get(t1), mappa.get(t2))) {
					DefaultWeightedEdge edge = grafo.addEdge(mappa.get(t1), mappa.get(t2));
					grafo.setEdgeWeight(edge, peso);
					// +
					mappa.get(t1).setPunti(punticasa);
					mappa.get(t2).setPunti(puntitrasf);
				}else {
					DefaultWeightedEdge edge = grafo.getEdge(mappa.get(t1), mappa.get(t2));
					// cerca la sorgente dell arco
					grafo.getEdgeSource(edge).setPunti(punticasa);
					grafo.getEdgeTarget(edge).setPunti(puntitrasf);
				}
			}
			
			conn.close();
			
		}catch (SQLException e){
			throw new RuntimeException("Errore DB");
		}
	}


}
