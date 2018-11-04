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

public class MalAnimeData {

	public String getSynopsisByMalID(int malID) {
		String sql = "SELECT synopsis \n"
				+ "FROM MalAnimeData WHERE malID = "+malID;
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
		String sql = "SELECT malID FROM MalAnimeData";
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

	public String getTitleByMalID(int malID) {
		String sql = "SELECT title \n"
				+ "FROM MalAnimeData WHERE malID="+malID;
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

	public ArrayList<Object> getAnimeDataForTableByMalID(int malID) {
		try(Connection dbConn = DriverManager.getConnection(Main.database.databaseFilepath)) {
			String sql = "SELECT title, type, episodes, startDate\n"
					+ "FROM MalAnimeData WHERE malID="+malID;
			ResultSet rs = dbConn.createStatement().executeQuery(sql);
			if(!rs.next())
				return null;
			ArrayList<Object>  listToReturn = new ArrayList<>();
			listToReturn.add(rs.getString(1));
			listToReturn.add(rs.getString(2));
			listToReturn.add(rs.getInt(3));
			listToReturn.add(rs.getString(4));
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

	public void updateMalAnimeData(int malID, String title, String type, Integer episodes, String status, String thumbnail, String startDate, String synopsis) {
		String sql = "REPLACE INTO MalAnimeData(malID,title,type,episodes,status,thumbnail,startDate,synopsis)\n" +
				"VALUES(?,?,?,?,?,?,?,?)";
		try(Connection dbConn = DriverManager.getConnection(Main.database.databaseFilepath)) {
			PreparedStatement ps = dbConn.prepareStatement(sql);
			ps.setInt(1, malID);
			ps.setString(2, title);
			ps.setString(3, type);
			ps.setInt(4, episodes);
			ps.setString(5, status);
			ps.setString(6, thumbnail);
			ps.setString(7, startDate);
			ps.setString(8, synopsis);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
