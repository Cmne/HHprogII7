package control;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import org.json.JSONObject;
import model.dao.Dao;
import model.Asiakas;

@WebServlet("/asiakkaat/*")
public class Asiakkaat extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public Asiakkaat() {
		super();
	}

//	check for working 5 call types:
//		1) server.fi/projectname/servlet/
//		2) server.fi/projectname/servlet
//		3) GET /asiakkaat/{hakusana}
//		4) GET /asiakkaat/haeyksi/{hakusana}
//		5) result is a null
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String pathInfo = request.getPathInfo();
		Dao dao = new Dao();
		ArrayList<Asiakas> asiakkaat; //only introduce now; what array is filled with is determined by the pathInfo
		String strJSON = ""; 
		if (pathInfo == null) { //if null, GET all tietueet, call 2)
			asiakkaat = dao.listaaKaikki();
			strJSON = new JSONObject().put("asiakkaat", asiakkaat).toString(); //put an object named asiakkaat, containing asiakkaat ArrayList, into a new JSONObject
		} else if (pathInfo.indexOf("haeyksi") != -1) { //if there's this one, GET only one tietue, call 4)
			String asiakas_id_str = pathInfo.replace("/haeyksi/", "");
			int asiakas_id = Integer.parseInt(asiakas_id_str);
			Asiakas gubbe = dao.etsiAsiakas(asiakas_id);
			
			if (gubbe == null) { //call 5)
				strJSON = "{}"; //equals a null object
			} else {
				JSONObject JSON = new JSONObject();
				JSON.put("asiakas_id", gubbe.getAsiakas_id()); //handling hidden primary key
				JSON.put("etunimi", gubbe.getEtunimi());
				JSON.put("sukunimi", gubbe.getSukunimi());
				JSON.put("puhelin", gubbe.getPuhelin());
				JSON.put("sposti", gubbe.getSposti());
				strJSON = JSON.toString();
			}
		} else { //if there's a hakusana, GET all matches, call 3)
			String hakusana = pathInfo.replace("/", "");
			asiakkaat = dao.listaaKaikki(hakusana);
			strJSON = new JSONObject().put("asiakkaat", asiakkaat).toString();
		}
		//write the JSONObject strJSON into the servlet HTML rajapinta:
		response.setContentType("application/json"); //the type of what we'll write is application/json
		PrintWriter out = response.getWriter();
		out.println(strJSON);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JSONObject jsonObj = new JsonStrToObj().convert(request);
		Asiakas customer = new Asiakas();
		customer.setEtunimi(jsonObj.getString("etunimi"));
		customer.setSukunimi(jsonObj.getString("sukunimi"));
		customer.setPuhelin(jsonObj.getString("puhelin"));
		customer.setSposti(jsonObj.getString("sposti"));
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		Dao dao = new Dao();
		if (dao.lisaaAsiakas(customer)) { //returns true/false
			out.println("{\"response\":1}");
		} else {
			out.println("{\"response\":0}");
		};
	}

	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("Asiakkaat.doPut()"); //for testing purposes
		JSONObject jsonObj = new JsonStrToObj().convert(request);
		Asiakas customer = new Asiakas();
		customer.setAsiakas_id(Integer.parseInt(jsonObj.getString("asiakas_id")));
		customer.setEtunimi(jsonObj.getString("etunimi"));
		customer.setSukunimi(jsonObj.getString("sukunimi"));
		customer.setPuhelin(jsonObj.getString("puhelin"));
		customer.setSposti(jsonObj.getString("sposti"));
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		Dao dao = new Dao();
		if (dao.muutaAsiakas(customer)) { //returns true/false
			out.println("{\"response\":1}");
		} else {
			out.println("{\"response\":0}");
		};
	}

	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String pathInfo = request.getPathInfo();
		String poistettava_id = pathInfo.replace("/", "");
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		Dao dao = new Dao();
		if (dao.poistaAsiakas(poistettava_id)) { //returns true/false
			out.println("{\"response\":1}"); //success
		} else {
			out.println("{\"response\":0}"); //fail
		};
	}
}