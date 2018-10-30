package com.vatsul.awatcher;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class JikanAPI {
	
	private static long lastQuery = 0;
	private static int rateLimit = 500; // Time between requests in milliseconds
	private static String JikanURL = "https://api.jikan.moe/v3";

	private static void maintainRatelimit() {
		while(System.currentTimeMillis() - lastQuery < rateLimit) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	// Update MyAnimeList table in database with values of users animelist from Jikan
		public static void updateMyAnimeList() {
			try {
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
				int pageCnt = 1;
				while(true) {
					URL url = new URL(JikanURL + "/user/" + Main.config.getMalUsername() + "/animelist/all/" + pageCnt);
					maintainRatelimit();
					HttpsURLConnection conn = (HttpsURLConnection)url.openConnection();
					conn.setRequestMethod("GET");
					conn.connect();
					JSONTokener tokener = new JSONTokener(conn.getInputStream());
					JSONObject obj = new JSONObject(tokener);
					if(!obj.getBoolean("request_cached")) {
						lastQuery = System.currentTimeMillis();
					}
					JSONArray anime = obj.getJSONArray("anime");
					if(anime.length() == 0)
						break;
					anime.forEach(a -> {
						JSONObject cur = (JSONObject) a;
						malID.add(cur.getInt("mal_id"));
						title.add(cur.getString("title"));
						String curType = cur.getString("type");
						if(curType.equals("TV")) {
							type.add(1);
						} else if(curType.equals("OVA")) {
							type.add(2);
						} else if(curType.equals("Movie")) {
							type.add(3);
						} else if(curType.equals("ONA")) {
							type.add(5);
						} else {
							type.add(4);
						}
						episodes.add(cur.getInt("total_episodes"));
						status.add(cur.getInt("airing_status"));
						watchedEpisodes.add(cur.getInt("watched_episodes"));
						myScore.add(cur.getInt("score"));
						myStatus.add(cur.getInt("watching_status"));
						thumbnail.add(cur.getString("image_url"));
						startDate.add(cur.get("start_date").toString());
						lastUpdated.add(0); // Not available from Jikan
					});
					pageCnt++;
				}
				Main.database.MyAnimeList.updateMyAnimeList(malID, title, type, episodes, status, watchedEpisodes, myScore, myStatus, thumbnail, startDate, lastUpdated);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		// Cache synopses from MAL into database of aids that already exist in the database
		public static void cacheSynopsesToDB() {
			for(Integer malID : Main.database.MyAnimeList.getAllMalIDs()) {
				if(Main.database.MyAnimeList.getSynopsisByMalID(malID)==null) {
					try {
						URL url = new URL(JikanURL + "/anime/" + malID);
						System.out.println(url.toString());
						HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
						con.setRequestMethod("GET");
						con.setReadTimeout(5000);
						maintainRatelimit();
						JSONObject obj = new JSONObject(new JSONTokener(con.getInputStream()));
						Main.database.MyAnimeList.updateSynopsisByMalID(malID, obj.getString("synopsis"));
					} catch (JSONException e) {
						System.out.println("Error! Could not find synopsis for " + malID + "!");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		// Returns malID of anime if found on MAL List of names and/or synonyms as an input
		public static int getMalID(List<String> searchStrings) {
			for(String s : searchStrings) {
				try {
					URL url = new URL(JikanURL + "/search/anime?q=" + URLEncoder.encode(s.replaceAll("&", " ")) + "&limit=1");
					HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
					con.setRequestMethod("GET");
					con.setReadTimeout(5000);
					maintainRatelimit();
					JSONTokener tokener = new JSONTokener(con.getInputStream());
					JSONObject obj = new JSONObject(tokener);
					JSONArray results = obj.getJSONArray("results");
					if(results.length() == 1)
						return ((JSONObject)results.get(0)).getInt("mal_id");
				} catch (SocketTimeoutException e2) {
					System.out.println("MAL API Timeout - " + s);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			// No valid entry found
			return -1;
		}
	
}
