package com.vatsul.awatcher;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.vatsul.awatcher.anidbapi.AnidbConnection;
import com.vatsul.awatcher.database.Database;
import com.vatsul.awatcher.gui.Gui;

import javafx.application.Application;

public class Main {
	
	public static AnidbConnection anidbConn;
	public static Config config;
	public static Database database;
	
	public static InetSocketAddress AnidbUDPSERVER = new InetSocketAddress("api.anidb.net", 9000);
	public static InetSocketAddress AnidbHTTPSERVER = new InetSocketAddress("api.anidb.net", 9001);
	public static String version = "0.0.1";

	public static void main(String args[]) throws IOException {
		config = new Config();
		database = new Database();
		/*MalApi.updateMyAnimeList();
		MalApi.cacheSynopsesToDB();
		Indexer.cacheThumbnails();*/
		Application.launch(Gui.class, args);
		if(anidbConn!=null) {
			anidbConn.close();
		}
		
		/*System.out.println("Monitoring vlc...");
		while(true) {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Double playbackPos = VlcConnect.getPlaybackPosition(config.getVlcPort(), config.getVlcPassword());
			if(playbackPos>=0.90) {
				File currentTrack = VlcConnect.getCurrentTrack(config.getVlcPort(), config.getVlcPassword());
				if(updatedEps.contains(currentTrack))
					continue;
				int aid = database.getAid(currentTrack);
				int malID = database.getMalID(aid);
				int watchedEpMal = MalApi.getWatchedEpisodes(malID, config.getMalUsername());
				String currentEpNum = database.getEpNum(currentTrack);
				if(currentEpNum.matches("^\\d+$")) { // Non-episodes could be labeled as T36
					if((Integer.parseInt(currentEpNum)-1)==watchedEpMal) {
						MalApi.updateAnimeList(malID,Integer.parseInt(currentEpNum));
					}
					updatedEps.add(currentTrack);
				}
			}
		}*/
		
	}
}