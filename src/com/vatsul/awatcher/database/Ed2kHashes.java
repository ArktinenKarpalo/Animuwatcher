package com.vatsul.awatcher.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.vatsul.awatcher.Main;

public class Ed2kHashes {

	public static void putEd2kHash(String ed2kHash, int aid, int fid, String eid) {
		String sql = "REPLACE INTO Ed2kHashes(ed2kHash,aid,fid,eid) VALUES(?,?,?,?)";
		try(Connection dbConn = DriverManager.getConnection(Main.database.databaseFilepath)) {
			PreparedStatement ps = dbConn.prepareStatement(sql);
			ps.setString(1, ed2kHash);
			ps.setInt(2, aid);
			ps.setInt(3, fid);
			ps.setString(4, eid);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static int getAidByEd2kHash(String ed2kHash) {
		String sql = "SELECT aid \n"
				+ "FROM Ed2kHashes WHERE ed2kHash = "+ed2kHash;
		try(Connection dbConn = DriverManager.getConnection(Main.database.databaseFilepath)) {
			ResultSet rs = dbConn.createStatement().executeQuery(sql);
			while(rs.next()) {
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			//e.printStackTrace();
		}
		return 0;
	}
	
	public static String getEidByEd2kHash(String ed2kHash) {
		String sql = "SELECT eid \n"
				+ "FROM Ed2kHashes WHERE ed2kHash = "+ed2kHash;
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
	
	public static int getFidByEd2kHash(String ed2kHash) {
		String sql = "SELECT fid \n"
				+ "FROM Ed2kHashes WHERE ed2kHash = "+ed2kHash;
		try(Connection dbConn = DriverManager.getConnection(Main.database.databaseFilepath)) {
			ResultSet rs = dbConn.createStatement().executeQuery(sql);
			while(rs.next()) {
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
}
