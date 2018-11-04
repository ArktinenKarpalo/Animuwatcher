package com.vatsul.awatcher.database;

import com.vatsul.awatcher.Main;
import com.vatsul.awatcher.Utils;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;

public class FileTable {

	// Returns ed2k hash from database if found
	public String getEd2kHash(File file) {
		try(Connection dbConn = DriverManager.getConnection(Main.database.databaseFilepath)) {
			String sql = "SELECT ed2kHash \n"
					+ "FROM FileTable WHERE filepath= '"+ Utils.encodeAbsoluteFilepath(file)+"';";
			ResultSet rs = dbConn.createStatement().executeQuery(sql);
			while(rs.next()) {
				return rs.getString(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public ArrayList<Integer> getAidsFromFileTable() {
		try(Connection dbConn = DriverManager.getConnection(Main.database.databaseFilepath)) {
			String sql = "SELECT DISTINCT aid \n"
					+ "FROM FileTable WHERE aid IS NOT NULL";
			ResultSet rs = dbConn.createStatement().executeQuery(sql);
			ArrayList<Integer> aids = new ArrayList<Integer>();
			while(rs.next()) {
				aids.add(rs.getInt(1));
			}
			return aids;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}


	public Integer getAid(File file) {
		try(Connection dbConn = DriverManager.getConnection(Main.database.databaseFilepath)) {
			String sql = "SELECT aid \n"
					+ "FROM FileTable WHERE filepath= '"+Utils.encodeAbsoluteFilepath(file)+"';";
			ResultSet rs = dbConn.createStatement().executeQuery(sql);
			while(rs.next()) {
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public File getFileByAidEp(int aid, int epNum) {
		try(Connection dbConn = DriverManager.getConnection(Main.database.databaseFilepath)) {
			String sql = "SELECT filepath \n"
					+ "FROM FileTable WHERE aid = '"+aid+"' AND epNum = '" + epNum + "';";
			ResultSet rs = dbConn.createStatement().executeQuery(sql);
			while(rs.next()) {
				return Utils.decodeAbsoluteFilepath(rs.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	// Returns id if of column with path of given file else -1
	public int getFileTableId(File file) {
		try(Connection dbConn = DriverManager.getConnection(Main.database.databaseFilepath)) {
			String sql = "SELECT id \n"
					+ "FROM FileTable WHERE filepath = '"+Utils.encodeAbsoluteFilepath(file)+"';";
			ResultSet rs = dbConn.createStatement().executeQuery(sql);
			while(rs.next()) {
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	// Insert a new file into FileTable
	public void insertFile(File file) {
		String sql = "INSERT INTO FileTable(filepath) VALUES(?)";
		try(Connection dbConn = DriverManager.getConnection(Main.database.databaseFilepath)) {
			PreparedStatement ps = dbConn.prepareStatement(sql);
			ps.setString(1, Utils.encodeAbsoluteFilepath(file));
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void updateFid(File file, int fid) {
		String sql = "UPDATE FileTable SET fid = ?"
				+ "WHERE filepath = ?";
		try(Connection dbConn = DriverManager.getConnection(Main.database.databaseFilepath)) {
			PreparedStatement ps = dbConn.prepareStatement(sql);
			ps.setInt(1, fid);
			ps.setString(2, Utils.encodeAbsoluteFilepath(file));
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void updateAid(File file, int aid) {
		String sql = "UPDATE FileTable SET aid = ?"
				+ "WHERE filepath = ?";
		try(Connection dbConn = DriverManager.getConnection(Main.database.databaseFilepath)) {
			PreparedStatement ps = dbConn.prepareStatement(sql);
			ps.setInt(1, aid);
			ps.setString(2, Utils.encodeAbsoluteFilepath(file));
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void updateEd2k(File file, String ed2k) {
		String sql = "UPDATE FileTable SET ed2kHash = ?"
				+ "WHERE filepath = ?";
		try(Connection dbConn = DriverManager.getConnection(Main.database.databaseFilepath)) {
			PreparedStatement ps = dbConn.prepareStatement(sql);
			ps.setString(1, ed2k);
			ps.setString(2, Utils.encodeAbsoluteFilepath(file));
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void updateEpNum(File file, String epNum) {
		String sql = "UPDATE FileTable SET epNum = ?"
				+ "WHERE filepath = ?";
		try(Connection dbConn = DriverManager.getConnection(Main.database.databaseFilepath)) {
			PreparedStatement ps = dbConn.prepareStatement(sql);
			ps.setString(1, epNum);
			ps.setString(2, Utils.encodeAbsoluteFilepath(file));
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
