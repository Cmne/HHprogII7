package model.dao;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import model.Asiakas;

public class Dao {
	private Connection con=null;
	private ResultSet rs = null;
	private PreparedStatement stmtPrep=null; 
	private String sql;
	private String db ="Myynti.sqlite"; //name the correct db here

	private Connection yhdista(){ //why does the function need to return connection though?
    	Connection con = null; //wonder why this needs to be re-established
    	String path = System.getProperty("catalina.base");
    	path = path.substring(0, path.indexOf(".metadata")).replace("\\", "/"); //in Eclipse IDE
    	String url = "jdbc:sqlite:" + path + db; //specify the full path to db with the jar
    	try { //try to form a connection
    		Class.forName("org.sqlite.JDBC"); //??
	        con = DriverManager.getConnection(url); //??
	     }catch (Exception e){ //if connection failed
	    	 System.out.println("Dao: Yhteyden avaus epäonnistui."); //for testing purposes
	        e.printStackTrace(); //??
	     }
	     return con;
	}

	public ArrayList<Asiakas> listaaKaikki() { //READ method for getting everything
		ArrayList<Asiakas> asiakkaat = new ArrayList<Asiakas>();
		sql = "SELECT * FROM asiakkaat";
		try {
			con = yhdista();
			if (con != null) {
				stmtPrep = con.prepareStatement(sql);
				rs = stmtPrep.executeQuery();
				if (rs != null) {
					while(rs.next()) {
						Asiakas dude = new Asiakas();
						dude.setAsiakas_id(rs.getInt(1));
						dude.setEtunimi(rs.getString(2));
						dude.setSukunimi(rs.getString(3));
						dude.setPuhelin(rs.getString(4));
						dude.setSposti(rs.getString(5));
						asiakkaat.add(dude);
					}
				} else {
					System.out.println("Kysely epäonnistui"); //for testing
				}
			}
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return asiakkaat;
	}
	
	public ArrayList<Asiakas> listaaKaikki(String hakusana) { //READ method for getting only the searched stuff
		ArrayList<Asiakas> asiakkaat = new ArrayList<Asiakas>();
		sql = "SELECT * FROM asiakkaat WHERE etunimi LIKE ? or sukunimi LIKE ? or puhelin LIKE ? or sposti LIKE ?";
		try {
			con = yhdista();
			if (con != null) {
				stmtPrep = con.prepareStatement(sql);
				stmtPrep.setString(1, "%" + hakusana + "%");
				stmtPrep.setString(2, "%" + hakusana + "%");
				stmtPrep.setString(3, "%" + hakusana + "%");
				stmtPrep.setString(4, "%" + hakusana + "%");
				rs = stmtPrep.executeQuery();
				if (rs != null) {
					while(rs.next()) {
						Asiakas dude = new Asiakas();
						dude.setAsiakas_id(rs.getInt(1));
						dude.setEtunimi(rs.getString(2));
						dude.setSukunimi(rs.getString(3));
						dude.setPuhelin(rs.getString(4));
						dude.setSposti(rs.getString(5));
						asiakkaat.add(dude);
					}
				} else {
					System.out.println("Kysely epäonnistui"); //for testing
				}
			}
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return asiakkaat;
	}
	
	public boolean lisaaAsiakas(Asiakas customer) {
		boolean result = true;
		sql = "INSERT INTO asiakkaat(etunimi, sukunimi, puhelin, sposti) VALUES(?, ?, ?, ?)";
		try {
			con = yhdista();
			stmtPrep=con.prepareStatement(sql);
			stmtPrep.setString(1, customer.getEtunimi());
			stmtPrep.setString(2, customer.getSukunimi());
			stmtPrep.setString(3, customer.getPuhelin());
			stmtPrep.setString(4, customer.getSposti());
			stmtPrep.executeUpdate();
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
			result=false;
		}
		return result;
	}
	
	public boolean poistaAsiakas(String id) {
		boolean result = true;
		sql = "DELETE FROM asiakkaat WHERE asiakas_id = ?";
		try {
			con = yhdista();
			stmtPrep=con.prepareStatement(sql);
			stmtPrep.setString(1, id);
			stmtPrep.executeUpdate();
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
			result=false;
		}
		return result;
	}
	
	public Asiakas etsiAsiakas(int asiakas_id) {
		Asiakas dude = null;
		sql = "SELECT * FROM asiakkaat WHERE asiakas_id = ?";
		try {
			con = yhdista();
			if (con != null) { //if connection was successful
				stmtPrep=con.prepareStatement(sql);
				stmtPrep.setInt(1, asiakas_id);
				rs = stmtPrep.executeQuery();
				if (rs.isBeforeFirst()) { //if there's any result
					rs.next();
					dude = new Asiakas();
					dude.setAsiakas_id(rs.getInt(1)); //handling asiakas_id
					dude.setEtunimi(rs.getString(2));
					dude.setSukunimi(rs.getString(3));
					dude.setPuhelin(rs.getString(4));
					dude.setSposti(rs.getString(5));
				}
			}
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dude;
	}
	
	public boolean muutaAsiakas(Asiakas gubbe) {
		boolean result = true;
		sql = "UPDATE asiakkaat SET etunimi = ?, sukunimi = ?, puhelin = ?, sposti = ? WHERE asiakas_id = ?";
		try {
			con = yhdista();
			stmtPrep = con.prepareStatement(sql);
			
			stmtPrep.setString(1, gubbe.getEtunimi());
			stmtPrep.setString(2, gubbe.getSukunimi());
			stmtPrep.setString(3, gubbe.getPuhelin());
			stmtPrep.setString(4, gubbe.getSposti());
			stmtPrep.setInt(5, gubbe.getAsiakas_id());
			
			stmtPrep.executeUpdate();
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		}
		return result;
	}
}