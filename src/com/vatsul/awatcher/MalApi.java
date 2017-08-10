package com.vatsul.awatcher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class MalApi {
	
	private static long lastQuery = 0;	// Time when the last query was sent
	private static int ratelimit = 250; // Minimum time in milliseconds between requests
	
	// Status - 1/watching, 2/completed, 3/onhold, 4/dropped, 6/plantowatch 
	public static void updateAnimeListStatusScore(int malID, int myStatus, int myScore) {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.newDocument();
			
			Element entry = doc.createElement("entry");
			doc.appendChild(entry);
			
			Element status = doc.createElement("status");
			status.appendChild(doc.createTextNode(""+myStatus));
			entry.appendChild(status);
			
			Element score = doc.createElement("score");
			score.appendChild(doc.createTextNode(""+myScore));
			entry.appendChild(score);
			
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			StringWriter writer = new StringWriter();
			transformer.transform(new DOMSource(doc), new StreamResult(writer));
			String output = URLEncoder.encode(writer.getBuffer().toString(), "UTF-8");
			
			URL url = new URL("https://myanimelist.net/api/animelist/update/"+malID+".xml?data="+output);
			String credentials = java.util.Base64.getEncoder().encodeToString((Main.config.getMalUsername()+":"+Main.config.getMalPassword()).getBytes());
			checkLastQuery();
			HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.setDoOutput(true);
			con.setRequestProperty("Authorization",  "Basic " + credentials);
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			con.setReadTimeout(5000);
			InputStream is = con.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			System.out.println("Attempting to update MalID: " + malID + " Status: " + myStatus + " Score: " + myScore);
			System.out.println(br.readLine());
		} catch (SocketTimeoutException e2) {
			System.out.println("MAL API Timeout");
		} catch (IOException | DOMException | ParserConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// No valid entry found
		return;
	}
	
	// Adds anime with given malID and status into users MAL list
	public static void addAnimeToList(int malID, int status) {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.newDocument();
			
			Element entry = doc.createElement("entry");
			doc.appendChild(entry);
			
			Element statusElement = doc.createElement("status");
			statusElement.appendChild(doc.createTextNode(""+status));
			entry.appendChild(statusElement);
			
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			StringWriter writer = new StringWriter();
			transformer.transform(new DOMSource(doc), new StreamResult(writer));
			String output = URLEncoder.encode(writer.getBuffer().toString(), "UTF-8");
			
			URL url = new URL("https://myanimelist.net/api/animelist/add/"+malID+".xml?data="+output);
			String credentials = java.util.Base64.getEncoder().encodeToString((Main.config.getMalUsername()+":"+Main.config.getMalPassword()).getBytes());
			checkLastQuery();
			HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.setDoOutput(true);
			con.setRequestProperty("Authorization",  "Basic " + credentials);
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			con.setReadTimeout(5000);
			InputStream is = con.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			System.out.println("Attempting to update MalID: " + malID + " Status: " + status);
			System.out.println(br.readLine());
		} catch (SocketTimeoutException e2) {
			System.out.println("MAL API Timeout");
		} catch (IOException | DOMException | ParserConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// No valid entry found
		return;
	}
	
	// Updates amount of watched episodes in users MAL list
	public static void updateAnimeListWatchedEpisodes(int malid, int watchedEpisodes) {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.newDocument();
			
			Element entry = doc.createElement("entry");
			doc.appendChild(entry);
			
			Element episode = doc.createElement("episode");
			episode.appendChild(doc.createTextNode(""+watchedEpisodes));
			entry.appendChild(episode);
			
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			StringWriter writer = new StringWriter();
			transformer.transform(new DOMSource(doc), new StreamResult(writer));
			String output = URLEncoder.encode(writer.getBuffer().toString(), "UTF-8");
			
			URL url = new URL("https://myanimelist.net/api/animelist/update/"+malid+".xml?data="+output);
			String credentials = java.util.Base64.getEncoder().encodeToString((Main.config.getMalUsername()+":"+Main.config.getMalPassword()).getBytes());
			checkLastQuery();
			HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.setDoOutput(true);
			con.setRequestProperty("Authorization",  "Basic " + credentials);
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			con.setReadTimeout(5000);
			InputStream is = con.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			System.out.println("Attempting to update MalID: " + malid + " Watched episodes: " + watchedEpisodes);
			System.out.println(br.readLine());
		} catch (SocketTimeoutException e2) {
			System.out.println("MAL API Timeout");
		} catch (IOException | DOMException | ParserConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// No valid entry found
		return;
	}
	
	// Update MyAnimeList table in database with values of users animelist from MAL
	public static void updateMyAnimeList() {
		try {
			URL url = new URL("https://myanimelist.net/malappinfo.php?u="+Main.config.getMalUsername()+"&status=all&type=anime");
			checkLastQuery();
			HttpsURLConnection conn = (HttpsURLConnection)url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept-Encoding", "gzip");
			conn.connect();
			GZIPInputStream gis = new GZIPInputStream(conn.getInputStream());
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(gis);
			NodeList animes = doc.getElementsByTagName("anime");
			ArrayList<Integer> malID = new ArrayList<Integer>();
			ArrayList<String> title = new ArrayList<String>();
			ArrayList<Integer> type = new ArrayList<Integer>();
			ArrayList<Integer> episodes = new ArrayList<Integer>();
			ArrayList<Integer> status = new ArrayList<Integer>();
			ArrayList<Integer> watchedEpisodes = new ArrayList<Integer>();
			ArrayList<Integer> myScore = new ArrayList<Integer>();
			ArrayList<Integer> myStatus = new ArrayList<Integer>();
			ArrayList<String> thumbnail = new ArrayList<String>();
			ArrayList<String> startDate = new ArrayList<String>();
			ArrayList<Integer> lastUpdated = new ArrayList<Integer>();
			for(int i=0; i<animes.getLength(); i++) {
				NodeList childNodes = animes.item(i).getChildNodes();
				for(int j=0; j<childNodes.getLength();j++) {
					Node currentNode = childNodes.item(j);
					String nodeName = currentNode.getNodeName();
					if(nodeName.equals("series_animedb_id")) {
						malID.add(Integer.parseInt(currentNode.getTextContent()));
					} else if(nodeName.equals("series_title")) {
						title.add(currentNode.getTextContent());
					} else if(nodeName.equals("series_type")) {
						type.add(Integer.parseInt(currentNode.getTextContent()));
					} else if(nodeName.equals("series_episodes")) {
						episodes.add(Integer.parseInt(currentNode.getTextContent()));
					} else if(nodeName.equals("series_status")) {
						status.add(Integer.parseInt(currentNode.getTextContent()));
					} else if(nodeName.equals("my_watched_episodes")) {
						watchedEpisodes.add(Integer.parseInt(currentNode.getTextContent()));
					} else if(nodeName.equals("my_score")) {
						myScore.add(Integer.parseInt(currentNode.getTextContent()));
					} else if(nodeName.equals("my_status")) {
						myStatus.add(Integer.parseInt(currentNode.getTextContent()));
					} else if(nodeName.equals("series_image")) {
						thumbnail.add(currentNode.getTextContent());
					} else if(nodeName.equals("series_start")) {
						startDate.add(currentNode.getTextContent());
					} else if(nodeName.equals("my_last_updated")) {
						lastUpdated.add(Integer.parseInt(currentNode.getTextContent()));
					}
				}
			}
			Main.database.MyAnimeList.updateMyAnimeList(malID, title, type, episodes, status, watchedEpisodes, myScore, myStatus, thumbnail, startDate, lastUpdated);
		} catch (IOException | SAXException | ParserConfigurationException | DOMException e) {
			e.printStackTrace();
		}
	}
	
	public static int getWatchedEpisodes(int malID, String user) {
		try {
			URL url = new URL("https://myanimelist.net/malappinfo.php?u="+user+"&status=all&type=anime");
			checkLastQuery();
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.setDoOutput(true);
			InputStream is = con.getInputStream();
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(is);
			NodeList malids = doc.getElementsByTagName("series_animedb_id");
			for(int i=0; i<malids.getLength(); i++) {
				if(Integer.parseInt(malids.item(i).getTextContent()) == malID) {
					NodeList cn = malids.item(i).getParentNode().getChildNodes();
					for(int j=0; j<cn.getLength(); j++) {
						if(cn.item(j).getNodeName().equals("my_watched_episodes")) {
							return Integer.parseInt(cn.item(j).getTextContent());
						}
					}
				}
			}
		} catch (IOException | SAXException | ParserConfigurationException | DOMException e) {
			e.printStackTrace();
		}
		// No valid entry found
		return  0;
	}
	
	// Cache synopses from MAL into database of aids that already exist in the database
	public static void cacheSynopsesToDB() {
		for(Integer malID : Main.database.MyAnimeList.getAllMalIDs()) {
			if(Main.database.MyAnimeList.getSynopsisByMalID(malID)==null) {
				try {
					String title = Main.database.MyAnimeList.getTitleByMalID(malID);
					URL url = new URL("https://myanimelist.net/api/anime/search.xml?q="+title.replaceAll(" ", "%20"));
					String credentials = java.util.Base64.getEncoder().encodeToString((Main.config.getMalUsername()+":"+Main.config.getMalPassword()).getBytes());
					HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
					con.setRequestMethod("GET");
					con.setDoOutput(true);
					con.setRequestProperty("Authorization",  "Basic " + credentials);
					con.setReadTimeout(5000);
					checkLastQuery();
					InputStream is = con.getInputStream();
					if(is.available()==0)
						continue;
					DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
					DocumentBuilder db = dbf.newDocumentBuilder();
					Document doc = db.parse(is);
					NodeList synopses = doc.getElementsByTagName("synopsis");
					Main.database.MyAnimeList.updateSynopsisByMalID(malID, synopses.item(0).getTextContent());
				} catch ( ParserConfigurationException | IOException | SAXException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	// Returns malID of anime if found on MAL List of names and/or synonyms as an input
	public static int getMalID(List<String> searchStrings) {
		for(String s : searchStrings) {
			try {
				URL url = new URL("https://myanimelist.net/api/anime/search.xml?q="+URLEncoder.encode(s
						.replaceAll("\\`", "'").replaceAll(";", " "), "UTF-8"));
				String credentials = java.util.Base64.getEncoder().encodeToString((Main.config.getMalUsername()+":"+Main.config.getMalPassword()).getBytes());
				HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
				con.setRequestMethod("GET");
				con.setDoOutput(true);
				con.setRequestProperty("Authorization",  "Basic " + credentials);
				con.setReadTimeout(5000);
				checkLastQuery();
				InputStream is = con.getInputStream();
				if(is.available()==0)
					continue;
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();
				Document doc = db.parse(is);
				NodeList ids = doc.getElementsByTagName("id");
				return Integer.parseInt(ids.item(0).getTextContent());
			} catch (SocketTimeoutException e2) {
				System.out.println("MAL API Timeout - " + s);
			} catch (IOException | SAXException | ParserConfigurationException | DOMException e) {
				e.printStackTrace();
			}
		}
		// No valid entry found
		return -1;
	}
	
	// Sleeps for remaining time to achieve rate limit
	private static void checkLastQuery() {
		if(System.currentTimeMillis()-lastQuery<ratelimit) {
			try {
				Thread.sleep(ratelimit-(System.currentTimeMillis()-lastQuery));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		lastQuery = System.currentTimeMillis();
	}
}
