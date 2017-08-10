package com.vatsul.awatcher.database;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.vatsul.awatcher.Main;

public class MyAnimeList {

	public void updateMyAnimeList(ArrayList<Integer> malID, ArrayList<String> title, 
			ArrayList<Integer> type, ArrayList<Integer> episodes, ArrayList<Integer> status, 
			ArrayList<Integer> watchedEpisodes, ArrayList<Integer> myScore, ArrayList<Integer> myStatus,
			ArrayList<String> thumbnail, ArrayList<String> startDate, ArrayList<Integer> lastUpdated) {
		Main.database.executeCmd("DELETE FROM MyAnimeList");
		String sql = "INSERT INTO MyAnimeList(malID,title,type,episodes,status,watchedEpisodes,myScore,myStatus,thumbnail,startDate,lastUpdated) VALUES(?,?,?,?,?,?,?,?,?,?,?)";
		try(Connection dbConn = DriverManager.getConnection(Main.database.databaseFilepath)) {
			for(int i=0; i<malID.size(); i++) {
				PreparedStatement ps = dbConn.prepareStatement(sql);
				ps.setInt(1, malID.get(i));
				ps.setString(2, title.get(i));
				ps.setInt(3, type.get(i));
				ps.setInt(4, episodes.get(i));
				ps.setInt(5, status.get(i));
				ps.setInt(6, watchedEpisodes.get(i));
				ps.setInt(7, myScore.get(i));
				ps.setInt(8, myStatus.get(i));
				ps.setString(9, thumbnail.get(i));
				ps.setString(10, startDate.get(i));
				ps.setInt(11, lastUpdated.get(i));
				ps.executeUpdate();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public int getWatchedEpisodesByMalID(int malID) {
		String sql = "SELECT watchedEpisodes \n"
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
	
	public void updateSynopsisByMalID(int malID, String synopsis) {
		String sql = "REPLACE INTO MyAnimeListSynopsis (malID, synopsis) VALUES(?,?)";
		try(Connection dbConn = DriverManager.getConnection(Main.database.databaseFilepath)) {
			PreparedStatement ps = dbConn.prepareStatement(sql);
			ps.setInt(1, malID);
			ps.setString(2, synopsis);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String getSynopsisByMalID(int malID) {
		String sql = "SELECT synopsis \n"
				+ "FROM MyAnimeListSynopsis WHERE malID = "+malID;
		try(Connection dbConn = DriverManager.getConnection(Main.database.databaseFilepath)) {
			ResultSet rs = dbConn.createStatement().executeQuery(sql);
			while(rs.next()) {
				return rs.getString(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String getTitleByMalID(int malID) {
		String sql = "SELECT title \n"
				+ "FROM MyAnimeList WHERE malID="+malID;
		try(Connection dbConn = DriverManager.getConnection(Main.database.databaseFilepath)) {
			ResultSet rs = dbConn.createStatement().executeQuery(sql);
			while(rs.next()) {
				return rs.getString(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public ArrayList<Integer> getAllMalIDs() {
		ArrayList<Integer> malIDs = new ArrayList<Integer>();
		String sql = "SELECT malID FROM MyAnimeList";
		try(Connection dbConn = DriverManager.getConnection(Main.database.databaseFilepath)) {
			ResultSet rs = dbConn.createStatement().executeQuery(sql);
			while(rs.next()) {
				malIDs.add(rs.getInt(1));
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
		return malIDs;
	}
	
	public ArrayList<ArrayList> getAnimeDataForTableByMyStatus(int myStatus) {
		try(Connection dbConn = DriverManager.getConnection(Main.database.databaseFilepath)) {
			String sql = "SELECT malID, title, type, episodes, watchedEpisodes, myScore, startDate, lastUpdated \n"
					+ "FROM MyAnimeList WHERE myStatus="+myStatus;
			ResultSet rs = dbConn.createStatement().executeQuery(sql);
			ArrayList<Integer> malIDs = new ArrayList<Integer>();
			ArrayList<String> titles = new ArrayList<String>();
			ArrayList<Integer> types = new ArrayList<Integer>();
			ArrayList<Integer> episodes = new ArrayList<Integer>();
			ArrayList<Integer> watchedEpisodes = new ArrayList<Integer>();
			ArrayList<Integer> myScores = new ArrayList<Integer>();
			ArrayList<String> startDates = new ArrayList<String>();
			ArrayList<Integer> lastUpdateds = new ArrayList<Integer>();
			while(rs.next()) {
				malIDs.add(rs.getInt(1));
				titles.add(rs.getString(2));
				types.add(rs.getInt(3));
				episodes.add(rs.getInt(4));
				watchedEpisodes.add(rs.getInt(5));
				myScores.add(rs.getInt(6));
				startDates.add(rs.getString(7));
				lastUpdateds.add(rs.getInt(8));
			}
			ArrayList<ArrayList> listToReturn = new ArrayList<ArrayList>();
			listToReturn.add(malIDs);
			listToReturn.add(titles);
			listToReturn.add(types);
			listToReturn.add(episodes);
			listToReturn.add(watchedEpisodes);
			listToReturn.add(myScores);
			listToReturn.add(startDates);
			listToReturn.add(lastUpdateds);
			return listToReturn;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	public File getThumbnail(int malID) {
		String thumbnailURL = "";
		String sql = "SELECT thumbnail FROM MyAnimeList WHERE malID="+malID;
		try(Connection dbConn = DriverManager.getConnection(Main.database.databaseFilepath)) {
			ResultSet rs = dbConn.createStatement().executeQuery(sql);
			while(rs.next()) {
				thumbnailURL = rs.getString(1);
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
		thumbnailURL = thumbnailURL.substring(0, thumbnailURL.lastIndexOf("."))+"l.jpg";
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
