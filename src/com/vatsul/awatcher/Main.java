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
	public static Gui gui;
	
	public static InetSocketAddress AnidbUDPSERVER = new InetSocketAddress("api.anidb.net", 9000);
	public static InetSocketAddress AnidbHTTPSERVER = new InetSocketAddress("api.anidb.net", 9001);
	public static String version = "0.0.1";

	public static void main(String args[]) throws IOException {
		config = new Config();
		database = new Database();
		VlcConnect vlcThread = new VlcConnect();
		vlcThread.start();
		Application.launch(Gui.class, args);
		if(anidbConn!=null) {
			anidbConn.close();
		}
	}
}