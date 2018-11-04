package com.vatsul.awatcher.database;

import com.vatsul.awatcher.Main;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MALIDs {

	// Adds malID to table which contains IDs that we want information about
	public void addMalID(int malID) {
		String sql = "INSERT OR IGNORE INTO MALIDs(malID)\n" +
				"VALUES(?)";
		try (Connection dbConn = DriverManager.getConnection(Main.database.databaseFilepath)) {
			PreparedStatement ps = dbConn.prepareStatement(sql);
			ps.setInt(1, malID);
			ps.executeUpdate();
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}

	// Returns list of all malIDs that we want information about
	public List<Integer> getMalIDs() {
		String sql = "SELECT malID \n"
				+ "FROM MALIDs";
		try(Connection dbConn = DriverManager.getConnection(Main.database.databaseFilepath)) {
			ResultSet rs = dbConn.createStatement().executeQuery(sql);
			ArrayList<Integer> list = new ArrayList<Integer>();
			while(rs.next()) {
				list.add(rs.getInt(1));
			}
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
