package com.vatsul.awatcher.database;

import com.vatsul.awatcher.Main;
import org.sqlite.util.StringUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AniDBAnimeTable {

	public String[] getSynonymList(int aid) {
		try(Connection dbConn = DriverManager.getConnection(Main.database.databaseFilepath)) {
			String sql = "SELECT synonymList \n"
					+ "FROM AniDBAnimeTable WHERE aid = '"+aid+"';";
			ResultSet rs = dbConn.createStatement().executeQuery(sql);
			while(rs.next()) {
				return rs.getString(1).split("}");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void updateMalID(int aid, int malID) {
		String sql = "UPDATE AniDBAnimeTable SET malID = ?"
				+ "WHERE aid = ?";
		try(Connection dbConn = DriverManager.getConnection(Main.database.databaseFilepath)) {
			PreparedStatement ps = dbConn.prepareStatement(sql);
			ps.setInt(1, malID);
			ps.setInt(2, aid);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String getAidTitle(int aid) {
		try(Connection dbConn = DriverManager.getConnection(Main.database.databaseFilepath)) {
			String sql = "SELECT mainTitle \n"
					+ "FROM AniDBAnimeTable WHERE aid= '"+aid+"';";
			ResultSet rs = dbConn.createStatement().executeQuery(sql);
			while(rs.next()) {
				return rs.getString(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	public Integer getMalID(int aid) {
		try(Connection dbConn = DriverManager.getConnection(Main.database.databaseFilepath)) {
			String sql = "SELECT malID \n"
					+ "FROM AniDBAnimeTable WHERE aid= '"+aid+"';";
			ResultSet rs = dbConn.createStatement().executeQuery(sql);
			while(rs.next()) {
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public Integer getAid(int malID) {
		try(Connection dbConn = DriverManager.getConnection(Main.database.databaseFilepath)) {
			String sql = "SELECT aid \n"
					+ "FROM AniDBAnimeTable WHERE malID= '"+malID+"';";
			ResultSet rs = dbConn.createStatement().executeQuery(sql);
			while(rs.next()) {
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	// Returns list of thumbnail strings
	public ArrayList<String> getThumbnails() {
		try(Connection dbConn = DriverManager.getConnection(Main.database.databaseFilepath)) {
			String sql = "SELECT thumbnail \n"
					+ "FROM AniDBAnimeTable WHERE thumbnail IS NOT NULL";
			ResultSet rs = dbConn.createStatement().executeQuery(sql);
			ArrayList<String> thumbnails = new ArrayList<String>();
			while(rs.next()) {
				thumbnails.add(rs.getString(1));
			}
			return thumbnails;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	// Put new anime into anime table or update values if already exists
	public void putAnimeIntoDB(int aid, String mainTitle, String description, String startDate, String endDate, String type, String thumbnail, int totalEp, List<String> synonymList) {
		String synonyms = StringUtils.join(synonymList, "}");
		String sql = "REPLACE INTO AniDBAnimeTable(aid,mainTitle,description,startDate,endDate,type,thumbnail,totalEp,synonymList) VALUES(?,?,?,?,?,?,?,?,?)";
		try(Connection dbConn = DriverManager.getConnection(Main.database.databaseFilepath)) {
			PreparedStatement ps = dbConn.prepareStatement(sql);
			ps.setInt(1, aid);
			ps.setString(2, mainTitle);
			ps.setString(3, description);
			ps.setString(4, startDate);
			ps.setString(5, endDate);
			ps.setString(6, type);
			ps.setString(7, thumbnail);
			ps.setInt(8, totalEp);
			ps.setString(9, synonyms);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
