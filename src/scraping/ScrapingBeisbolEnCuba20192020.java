package scraping;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Attr;

public class ScrapingBeisbolEnCuba20192020 {

	public static final String xmlFilePath = "xmlfile.xml";

	public static void main(String[] args) throws ParserConfigurationException, TransformerConfigurationException {
		// TODO Auto-generated method stub

		String url = "http://www.beisbolencuba.com/series/serie-nacional-beisbol-2019-2020/todos-contra/mayabeque-contra-camaguey-2.html";

//		extraerDataGame(url,111);
		
		String urlBoxscoreGame = "http://www.beisbolencuba.com/series/serie-nacional-beisbol-2019-2020/todos-contra";

		listGame(urlBoxscoreGame);

	}
	
	private static void listGame(String urlTournament) {
		String url = urlTournament;
		String urlGame = "";
		
		if (getStatusConnectionCode(url) == 200) {
//			if (getStatusFile(file) == 1) {

				Document documento = getHtmlDocument(url);
//				Document documento = getHtmlFileToDocument(file);
				//toma el elemnto time
				Elements elementos = documento
						.select("table.fpgt");
				int contador = 0;
				for (Element element : elementos) {
					contador++;
					Elements tbody = element.select("tr > th > a");
					if (tbody.get(0).html().equals("Final")) {
						System.out.println(""+contador+":"+tbody.get(0).html()+":"+tbody.get(0).attr("href"));
						urlGame = "http://www.beisbolencuba.com" + tbody.get(0).attr("href");
						extraerDataGame(urlGame,contador);
					}else {
						System.out.println(""+contador+":"+tbody.get(0).html());
					}
					
				}
				
		}
		
	}
	private static void extraerDataGame(String urlGame, int numero) {
		String url = urlGame;

		String file = "ejemplo.html";

		File input = new File("data/"  + file);
		Document doc = null;
		try {
			doc = Jsoup.parse(input, "UTF-8", "http://example.com/");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// elemento raiz
//		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement("game");
		doc.appendChild(rootElement);

		if (getStatusConnectionCode(url) == 200) {
//		if (getStatusFile(file) == 1) {

			Document documento = getHtmlDocument(url);
//			Document documento = getHtmlFileToDocument(file);

			//buscando en class game-stat
//			Elements gameSstat = documento
//					.select("div.game-stat");
//			System.out.println(gameSstat.size());
			
			//toma el elemnto time
			Elements elementosTime = documento
					.select("time");
			rootElement.attr("fecha",elementosTime.get(0).attr("datetime"));
			System.out.println(elementosTime.get(0).attr("datetime"));
			rootElement.attr("fase","clasificatoria");
			
			//se toma los elementos table con class stats y tbody
			Elements elementosTbody = documento
					.select("table.stats > tbody");
			System.out.println(elementosTbody.size());
			if(elementosTbody.size() == 6){
				
			
			//marcador
			Element marcadorCompleto = elementosTbody.get(0);
			Elements marcadorCompletoTr = marcadorCompleto.select("tr");
			
			//marcador visitante
			Element marcadorCompletoTrVisi = marcadorCompletoTr.get(2);
			Elements marcadorCompletoTrVisiTd = marcadorCompletoTrVisi.select("td");
			Element teamVisi = extractTeamMarcador(marcadorCompletoTrVisiTd, doc);
			rootElement.appendChild(teamVisi);
			
			//marcador homeclub
			Element marcadorCompletoTrHome = marcadorCompletoTr.get(3);
			Elements marcadorCompletoTrHomeTd = marcadorCompletoTrHome.select("td");
			Element teamHome = extractTeamMarcador(marcadorCompletoTrHomeTd, doc);
			rootElement.appendChild(teamHome);
			
			//System.out.println(marcadorCompletoTrVisi.html());
			//se toma los elementos table con class stats y tbody
			Elements elementosTbodyBat = documento
					.select("table.stats.batters > tbody");
			System.out.println(elementosTbodyBat.size());
			
			//batter visi
			Element batVisi = elementosTbodyBat.get(0);
			Elements batVisiList = batVisi.select("tr");
			Element batVisiExtract = extractBat(batVisiList,doc);
			teamVisi.appendChild(batVisiExtract);
			
			//batter home
			Element batHome = elementosTbodyBat.get(1);
			Elements batHomeList = batHome.select("tr");
			Element batHomeExtract = extractBat(batHomeList,doc);
			teamHome.appendChild(batHomeExtract);
			
			//pitch visi
			Element pitchVisi = elementosTbodyBat.get(2);
			Elements pitchVisiList = pitchVisi.select("tr");
			Element pitchVisiExtract = extractPitch(pitchVisiList,doc);
			teamVisi.appendChild(pitchVisiExtract);
			
			//pitch home
			Element pitchHome = elementosTbodyBat.get(3);
			Elements pitchHomeList = pitchHome.select("tr");
			Element pitchHomeExtract = extractPitch(pitchHomeList,doc);
			teamHome.appendChild(pitchHomeExtract);
			
			//pronostico
			Element pronostico = elementosTbody.get(5);
			Elements pronosticoTr = pronostico.select("tr");
			Element pronosticoExtract = extractPronostico(pronosticoTr,doc);
			rootElement.appendChild(pronosticoExtract);
			
			//anotaciones
			
			}
			
		}

		// nombre del fichero
		Date fecha = new Date();
		DateFormat hourdateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss");
		System.out.println("Hora y fecha: " + hourdateFormat.format(fecha));
		String nombreFichero = hourdateFormat.format(fecha);

		// escribimos el contenido en un archivo .xml
				String ruta = "dataXML\\";
				
				
				BufferedWriter  writer = null;
		        try
		        {
		            writer = new BufferedWriter( new FileWriter(ruta  + nombreFichero + "-" + numero + ".xml"));
		            System.out.println(rootElement.outerHtml());
		            writer.write(rootElement.outerHtml());

		        }
		        catch ( IOException e)
		        {
		        	System.out.println("error");
		        }
		        finally
		        {try {
		        	if (writer != null){
		        		writer.close();
		        	}
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}}
				
				System.out.println("File saved!");
	}

	private static Element extractPitch(Elements pitchVisiList, Document doc) {
		Element bat = doc.createElement("pitchers");
		
		for (int i = 2; i < pitchVisiList.size(); i++) {
			Element nodo = doc.createElement("player");
			Elements nodoPitcherAttr = pitchVisiList.get(i).select("td");
			
			nodo.attr("name", nodoPitcherAttr.get(0).text());
			nodo.attr("inn", nodoPitcherAttr.get(1).text());
			nodo.attr("vb", nodoPitcherAttr.get(2).text());
			nodo.attr("h", nodoPitcherAttr.get(3).text());
			nodo.attr("c", nodoPitcherAttr.get(4).text());
			nodo.attr("cl", nodoPitcherAttr.get(5).text());
			nodo.attr("so", nodoPitcherAttr.get(6).text());
			nodo.attr("bb", nodoPitcherAttr.get(7).text());
			nodo.attr("sb", nodoPitcherAttr.get(8).text());
			
			bat.appendChild(nodo);
		}
//		team.appendChild(runxining);
		return bat;
	}

	
	private static Element extractPronostico(Elements pitchVisiList, Document doc) {
		Element bat = doc.createElement("pronostico");
		
			Element nodo1 = doc.createElement("voto");
			Element nodo2 = doc.createElement("voto");
			
			Elements nodoEncabezado = pitchVisiList.get(0).select("th");
			Elements nodoData = pitchVisiList.get(1).select("td > b");
			
			nodo1.attr("name", nodoEncabezado.get(0).text());
			nodo2.attr("name", nodoEncabezado.get(1).text());
			
			nodo1.attr("valor", nodoData.get(0).text());
			nodo2.attr("valor", nodoData.get(1).text());
			
			
			bat.appendChild(nodo1);
			bat.appendChild(nodo2);

			return bat;
	}
	
	private static Element extractBat(Elements batVisiList, Document doc) {
		Element bat = doc.createElement("batters");
		
		for (int i = 2; i < batVisiList.size(); i++) {
			Element nodoBatters = doc.createElement("player");
			Elements nodoBattersAttr = batVisiList.get(i).select("td");
			
			nodoBatters.attr("name", nodoBattersAttr.get(0).text());
			nodoBatters.attr("vb", nodoBattersAttr.get(1).text());
			nodoBatters.attr("r", nodoBattersAttr.get(2).text());
			nodoBatters.attr("h", nodoBattersAttr.get(3).text());
			nodoBatters.attr("double", nodoBattersAttr.get(4).text());
			nodoBatters.attr("triple", nodoBattersAttr.get(5).text());
			nodoBatters.attr("hr", nodoBattersAttr.get(6).text());
			nodoBatters.attr("rbi", nodoBattersAttr.get(7).text());
			nodoBatters.attr("error", nodoBattersAttr.get(8).text());
			
			bat.appendChild(nodoBatters);
		}
//		team.appendChild(runxining);
		return bat;
	}

	/**
	 * Con esta método compruebo el Status code de la respuesta que recibo al hacer
	 * la petición EJM: 200 OK 300 Multiple Choices 301 Moved Permanently 305 Use
	 * Proxy 400 Bad Request 403 Forbidden 404 Not Found 500 Internal Server Error
	 * 502 Bad Gateway 503 Service Unavailable
	 * 
	 * @param url
	 * @return Status Code
	 */
	public static int getStatusConnectionCode(String url) {

		Response response = null;

		try {
			response = Jsoup.connect(url).userAgent("Mozilla/5.0").timeout(100000).ignoreHttpErrors(true).execute();
		} catch (IOException ex) {
			System.out.println("Excepción al obtener el Status Code: " + ex.getMessage());
		}
		return response.statusCode();
	}

	/**
	 * Con este método devuelvo un objeto de la clase Document con el contenido del
	 * HTML de la web que me permitirá parsearlo con los métodos de la librelia
	 * JSoup
	 * 
	 * @param url
	 * @return Documento con el HTML
	 */
	public static Document getHtmlDocument(String url) {

		Document doc = null;
		try {
			doc = Jsoup.connect(url).userAgent("Mozilla/5.0").timeout(100000).get();
		} catch (IOException ex) {
			System.out.println("Excepción al obtener el HTML de la página" + ex.getMessage());
		}
		return doc;
	}

	public static int getStatusFile(String file) {
		return 1;
	}

	public static Document getHtmlFileToDocument(String file) {

		File input = new File("data/" + file);
		Document doc = null;
		try {
			doc = Jsoup.parse(input, "UTF-8", "http://example.com/");
		} catch (IOException ex) {
			System.out.println("Excepción al obtener el HTML de la página" + ex.getMessage());
		}
		return doc;
	}
	
	private static Element extractTeamMarcador(Elements elementos, Document doc) {
		Element team = doc.createElement("team");
		Element runxining = doc.createElement("runxining");
		for (int i = 0; i < elementos.size(); i++) {
			if(i == 0) {
				// atributo nombre
				team.attr("name", elementos.get(i).text());
				
			}else if(i == elementos.size()-1) {
				//atributo errores
				team.attr("e", elementos.get(i).text());
				
			}else if(i == elementos.size()-2) {
				//atribiuto hit
				team.attr("h", elementos.get(i).text());
				
			}else if(i == elementos.size()-3) {
				//atributo carreras
				team.attr("c", elementos.get(i).text());
				
			}else if (i>1 && i<elementos.size() - 4) {
				//las carreras anotadas por entradas
				runxining.attr(""+(i-1), elementos.get(i).text());
			}
		}
		team.appendChild(runxining);
		return team;
	}
	
	private static org.w3c.dom.Element extractOffensiveHtmlToXml(Elements elementos,org.w3c.dom.Document doc) {
		
		org.w3c.dom.Element players = doc.createElement("players");
		
		for (Element elem : elementos) {

			// para no tomar la primera entrada que tiene el encabezado
			if (!(elem.equals(elementos.first()))) {
//				System.out.println("ok");

				org.w3c.dom.Element player = doc.createElement("player");
				players.appendChild(player);
				Integer contador = 0;
				Elements playerData = elem.select("td");
				for (Element playerElement : playerData) {
					contador++;
					
					//analisis de el id en sn
					Element playerDataId = elem.select("a").get(0);
					if (playerDataId != null) {
						String playerDataIdA = playerDataId.attr("href");
						// atributo del player
						Attr attr = doc.createAttribute("id");
						attr.setValue(extractIdLink(playerDataIdA));
						player.setAttributeNode(attr);
					}
					
					String attrName = "";
					switch(contador) {
					case 1: attrName = "name";
					break;
					case 2: attrName = "vb";
					break;
					case 3: attrName = "c";
					break;
					case 4: attrName = "h";
					break;
					case 5: attrName = "b2";
					break;
					case 6: attrName = "b3";
					break;
					case 7: attrName = "hr";
					break;
					case 8: attrName = "ci";
					break;
					case 9: attrName = "o";
					break;
					case 10: attrName = "a";
					break;
					case 11: attrName = "e";
					break;
					}
					String cadena = playerElement.text();

					// atributo del player
					Attr attr = doc.createAttribute(attrName);
					attr.setValue(cadena);
					player.setAttributeNode(attr);
				}
			}

		}
		return players;
	}

private static String extractIdLink(String cadena) {
	String[] cadenaSplit = cadena.split("id=");
	return cadenaSplit[1];
}	
	
private static org.w3c.dom.Element extractPitchHtmlToXml(Elements elementos,org.w3c.dom.Document doc) {
		
		org.w3c.dom.Element players = doc.createElement("players");
		
		for (Element elem : elementos) {

			// para no tomar la primera entrada que tiene el encabezado
			if (!(elem.equals(elementos.first()))) {
//				System.out.println("ok");

				org.w3c.dom.Element player = doc.createElement("player");
				players.appendChild(player);
				Integer contador = 0;
				Elements playerData = elem.select("td");
				for (Element playerElement : playerData) {
					contador++;
					String attrName = "";
					switch(contador) {
					case 1: attrName = "name";
					break;
					case 2: attrName = "vb";
					break;
					case 3: attrName = "h";
					break;
					case 4: attrName = "c";
					break;
					case 5: attrName = "cl";
					break;
					case 6: attrName = "so";
					break;
					case 7: attrName = "bb";
					break;
					case 8: attrName = "bi";
					break;
					case 9: attrName = "wp";
					break;
					case 10: attrName = "db";
					break;
					case 11: attrName = "bk";
					break;
					case 12: attrName = "inn";
					break;
					}
					String cadena = playerElement.text();

					// atributo del player
					Attr attr = doc.createAttribute(attrName);
					attr.setValue(cadena);
					player.setAttributeNode(attr);
				}
			}

		}
		return players;
	}
}
