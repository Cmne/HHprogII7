<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="ISO-8859-1">
	<script src="scripts/main.js"></script>
	<link rel="stylesheet" type="text/css" href="css/styles.css">
	<title>Etätehtävät 7 HP</title>
</head>
<body onkeydown="tutkiKey(event)">
	<table id="listaus" border="1"> <!-- remove border -->
		<thead>
			<tr>
				<th colspan="5"><a id="uusiAsiakas" href="lisaaasiakas.jsp">Lisää uusi asiakas</a></th>
			</tr>
			<tr>
				<th colspan="3" class="right">Hakusana:</th>
				<th><input type="text" id="hakusana"></th>
				<th><input type="button" value="Hae" id="hakunappi" onclick="haeTiedot()"></th>
			</tr>
			<tr>
				<th>Etunimi</th>
				<th>Sukunimi</th>
				<th>Puhelin</th>
				<th>Sähköposti</th>
				<th></th>
			</tr>
		</thead>
		<tbody id="tbody">
		</tbody>
	</table>
	<p id="ilmo"></p>
	<script>		
		function tutkiKey(event) {
			if (event.keyCode == 13) {
				haeTiedot();
			}
		}
		
		//GET /asiakkaat/{hakusana}
		function haeTiedot() {
			document.getElementById("tbody").innerHTML = "";
			
			fetch ("asiakkaat/" + document.getElementById("hakusana").value, {
				method: 'GET'
			})
			.then (function(response) {
				return response.json()
			})
			.then (function(responseJson) {
				var asiakkaat = responseJson.asiakkaat;
				var htmlStr = "";
				
				for (var i = 0; i < asiakkaat.length; i++) {
					htmlStr += "<tr id='rivi_" + asiakkaat[i].asiakas_id + "'>";
					htmlStr += "<td>" + asiakkaat[i].etunimi + "</td>";
					htmlStr += "<td>" + asiakkaat[i].sukunimi + "</td>";
					htmlStr += "<td>" + asiakkaat[i].puhelin + "</td>";
					htmlStr += "<td>" + asiakkaat[i].sposti + "</td>";
					htmlStr += "<td><a class='button' href='muutaasiakas.jsp?asiakas_id=" + asiakkaat[i].asiakas_id + "'>Muuta</a>&nbsp"
					htmlStr += "<input type='button' value='Poista' id='delete' onclick=poista('" + asiakkaat[i].asiakas_id + "')></td>";
					htmlStr += "</tr>";
				}
				
				document.getElementById("tbody").innerHTML = htmlStr;
			})
		}
		
		//DELETE /asiakkaat/id
		function poista(asiakas_id) {
			if (confirm("Poista asiakas " + asiakas_id + "?")) {
				fetch ("asiakkaat/" + asiakas_id, {
					method: 'DELETE'
				})
				.then (function(response) {
					return response.json()
				})
				.then (function(responseJson) {
					var reply = responseJson.response;
					
					if (reply == 0) {
						document.getElementById("ilmo").innerHTML = "Asiakkaan poisto epäonnistui.";
					} else if (reply == 1) {
						document.getElementById("ilmo").innerHTML = "Asiakkaan poisto onnistui.";
						haeTiedot();
					}
					
					setTimeout(function() {
						document.getElementById("ilmo").innerHTML = ""; },
						5000);
				})
			}
		}
	</script>
</body>
</html>