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
	<div>
		<form id="tiedot">
			<table border="1"> <!-- class="table"? -->
				<thead>
					<tr>
						<th colspan="5"><a href="listaaasiakkaat.jsp" id="takaisin">Takaisin listaukseen</a></th>
					</tr>
					<tr>
						<th>Etunimi</th>
						<th>Sukunimi</th>
						<th>Puhelin</th>
						<th>Sähköposti</th>
						<th></th> <!-- for buttons -->
					</tr>
				</thead>
				<tbody>
					<tr> <!-- note: name attribute required for validation -->
						<td><input type="text" name="etunimi" id="eNimi"></td>
						<td><input type="text" name="sukunimi" id="sNimi"></td>
						<td><input type="text" name="puhelin" id="puh"></td>
						<td><input type="text" name="sposti" id="email"></td>
						<td><input type="button" id="tallenna" value="Hyväksy" onclick="vieTiedot()"></td>
					</tr>
				</tbody>
			</table>
			<input type="hidden" name="asiakas_id" id="asiakas_id"> <!-- was used for whichever purpose? -->
		</form>
		<p id="ilmo"></p>
	</div>
	<script>
		function tutkiKey(event) {
			if (event.keyCode == 13) {
				vieTiedot();
			}
		}
		
		//GET /asiakkaat/haeyksi/id
		var asiakas_id = requestURLParam("asiakas_id");
		
		fetch ("asiakkaat/haeyksi/" + asiakas_id, {
			method: 'GET'
		})
		.then (function(response) {
			return response.json()
		})
		.then (function(responseJson) {
			document.getElementById("eNimi").value = responseJson.etunimi;
			document.getElementById("sNimi").value = responseJson.sukunimi;
			document.getElementById("puh").value = responseJson.puhelin;
			document.getElementById("email").value = responseJson.sposti;
			document.getElementById("asiakas_id").value = responseJson.asiakas_id;
		});
		
		//PUT /asiakkaat/
		function vieTiedot() {
			var ilmo = "";
			
			if (document.getElementById("eNimi").value.length < 2) {
				ilmo = "Etunimi on liian lyhyt.";
			} else if (document.getElementById("sNimi").value.length < 1) {
				ilmo = "Sukunimi on liian lyhyt.";
			} else if (document.getElementById("puh").value.length < 5) {
				ilmo = "Puhelinnumero on liian lyhyt.";
			} else if (document.getElementById("email").value.length < 10) {
				ilmo = "Sähköpostiosoite on liian lyhyt.";
			}
			
			//if there's anything in ilmo, there has been a problem -> print it out and stop execution
			if (ilmo != "") {
				document.getElementById("ilmo").innerHTML = ilmo;
				setTimeout(function() {
					document.getElementById("ilmo").innerHTML = ""; },
					3000);
				return;
			}
			
			//if ilmo is empty, all input values have fulfilled tests
			//clean up the input & prevent html/SQL injection
			//in real life, fancier clean-up
			document.getElementById("eNimi").value = siivoa(document.getElementById("eNimi").value);
			document.getElementById("sNimi").value = siivoa(document.getElementById("sNimi").value);
			document.getElementById("puh").value = siivoa(document.getElementById("puh").value);
			document.getElementById("email").value = siivoa(document.getElementById("email").value);
			
			var formJsonStr = formDataToJSON(document.getElementById("tiedot"));
			
			//send data to backend -> wait for response from backend
			//backend responds with result:0 (fail) or result:1 (success)
			fetch("asiakkaat", {
				method: 'PUT',
				body: formJsonStr
			})
			//when response given, change JSON response into an object
			.then(function(response) {
				return response.json()
			})
			//catch object in responseJson
			.then(function(responseJson) {
				var reply = responseJson.response;
				
				if (reply == 0) {
					document.getElementById("ilmo").innerHTML = "Asiakkaan lisääminen epäonnistui.";
				} else if (reply == 1) {
					document.getElementById("ilmo").innerHTML = "Asiakkaan lisääminen onnistui.";
				}
				
				//set the ilmo to disappear in 5 seconds
				setTimeout(function() {
					document.getElementById("ilmo").innerHTML = ""; },
					5000);
			});
			
			//clear up the form
			document.getElementById("tiedot").reset();
		}
	</script>
</body>
</html>