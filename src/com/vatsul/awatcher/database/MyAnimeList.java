package com.vatsul.awatcher.database;

import com.vatsul.awatcher.Main;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MyAnimeList {

	public void updateMyAnimeList(List<Integer> malID, List<Integer> myStatus, List<Integer> myScore, List<Integer> watchedEpisodes) {
		Main.database.executeCmd("DELETE FROM MyAnimeList");
		String sql = "INSERT INTO MyAnimeList(malID,myStatus,myScore,watchedEpisodes)\n" +
				"VALUES(?,?,?,?);";
		try(Connection dbConn = DriverManager.getConnection(Main.database.databaseFilepath)) {
			for(int i=0; i<malID.size(); i++) {
				PreparedStatement ps = dbConn.prepareStatement(sql);
				ps.setInt(1, malID.get(i));
				ps.setInt(2, myStatus.get(i));
				ps.setInt(3, myScore.get(i));
				ps.setInt(4, watchedEpisodes.get(i));
				ps.executeUpdate();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public int getMyStatusMalID(int malID) {
		String sql = "SELECT myStatus \n"
				+ "FROM MyAnimeList WHERE malID="+malID;
		try(Connection dbConn = DriverManager.getConnection(Main.database.databaseFilepath)) {
			ResultSet rs = dbConn.createStatement().executeQuery(sql);
			while(rs.next()) {
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	public ArrayList<ArrayList> getAnimeDataForTableByMyStatus(int myStatus) {
		try(Connection dbConn = DriverManager.getConnection(Main.database.databaseFilepath)) {
			String sql = "SELECT malID, myScore, watchedEpisodes\n"
					+ "FROM MyAnimeList WHERE myStatus="+myStatus;
			ResultSet rs = dbConn.createStatement().executeQuery(sql);
			ArrayList<Integer> malIDs = new ArrayList<Integer>();
			ArrayList<Integer> myScore = new ArrayList<Integer>();
			ArrayList<Integer> watchedEpisodes = new ArrayList<Integer>();
			while(rs.next()) {
				malIDs.add(rs.getInt(1));
				myScore.add(rs.getInt(2));
				watchedEpisodes.add(rs.getInt(3));
			}
			ArrayList<ArrayList> listToReturn = new ArrayList<ArrayList>();
			listToReturn.add(malIDs);
			listToReturn.add(myScore);
			listToReturn.add(watchedEpisodes);
			return listToReturn;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public File getThumbnail(int malID) {
		String thumbnailURL = "";
		String sql = "SELECT thumbnail FROM MalAnimeData WHERE malID="+malID;
		try(Connection dbConn = DriverManager.getConnection(Main.database.databaseFilepath)) {
			ResultSet rs = dbConn.createStatement().executeQuery(sql);
			while(rs.next()) {
				thumbnailURL = rs.getString(1);
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
		String thumbnailFileName = thumbnailURL.substring(thumbnailURL.lastIndexOf("/"));
		File thumbnail = new File("cache/malthumbnails/");
		if(!new File("cache/malthumbnails/"+thumbnailFileName).exists()) {
			thumbnail.mkdirs();
			thumbnail = new File("cache/malthumbnails/"+thumbnailFileName);
			try {
				Files.copy(new URL(thumbnailURL).openStream(), Paths.get(thumbnail.toURI()), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				// Ignore, not all thumbnails have l.jpg version
			}
		}
		return new File("cache/malthumbnails/"+thumbnailFileName);
	}
}
