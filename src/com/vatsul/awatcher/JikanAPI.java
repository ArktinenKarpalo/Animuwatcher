package com.vatsul.awatcher;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

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

	// Does not update data for MalIDs that already exist in MalAnimeData table
	public static void updateNewMALIDData() {
		List<Integer> malIds = Main.database.MALIDs.getMalIDs();
		String title="", type="", status="", thumbnail="", startDate="", synopsis="";
		int episodes = -1;
		for(int i=0; i<malIds.size(); i++) {
			if(Main.database.MalAnimeData.getTitleByMalID(malIds.get(i)) == null) {
				try {
					URL url = new URL(JikanURL + "/anime/" + malIds.get(i));
					maintainRatelimit();
					HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
					conn.setRequestMethod("GET");
					conn.connect();
					JSONObject obj = new JSONObject(new JSONTokener(conn.getInputStream()));
					if(!obj.getBoolean("request_cached")) {
						lastQuery = System.currentTimeMillis();
					}
					try {
						title = obj.getString("title");
					} catch (JSONException e) {
						title = null;
					}
					try {
						type = obj.getString("type");
					} catch (JSONException e) {
						type = null;
					}
					try {
						status = obj.getString("status");
					} catch (JSONException e) {
						status = null;
					}
					try {
						thumbnail = obj.getString("image_url");
					} catch (JSONException e) {
						thumbnail = null;
					}
					try {
						startDate = obj.getJSONObject("aired").getString("from");
					} catch (JSONException e) {
						startDate = null;
					}
					try {
						synopsis = obj.getString("synopsis");
					} catch (JSONException e) {
						synopsis = null;
					}
					try {
						episodes = obj.getInt("episodes");
					} catch (JSONException e) {
						episodes = -1;
					}
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				Main.database.MalAnimeData.updateMalAnimeData(malIds.get(i), title, type, episodes, status, thumbnail, startDate, synopsis);
			}
		}
	}

	// Update MyAnimeList table in database with values of users animelist from Jikan
	public static void updateMyAnimeList() {
		try {
			ArrayList<Integer> malID = new ArrayList<Integer>();
			ArrayList<Integer> watchedEpisodes = new ArrayList<Integer>();
			ArrayList<Integer> myScore = new ArrayList<Integer>();
			ArrayList<Integer> myStatus = new ArrayList<Integer>();
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
					String curType = cur.getString("type");
					watchedEpisodes.add(cur.getInt("watched_episodes"));
					myScore.add(cur.getInt("score"));
					myStatus.add(cur.getInt("watching_status"));
				});
				pageCnt++;
			}
			for(int id:malID)
				Main.database.MALIDs.addMalID(id);
			Main.database.MyAnimeList.updateMyAnimeList(malID, myStatus, myScore, watchedEpisodes);
		} catch (IOException e) {
			e.printStackTrace();
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
					if(results.length() == 1) {
						Main.database.MALIDs.addMalID(((JSONObject)results.get(0)).getInt("mal_id"));
						return ((JSONObject) results.get(0)).getInt("mal_id");
					}
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
