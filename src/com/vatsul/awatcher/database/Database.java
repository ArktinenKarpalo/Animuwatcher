package com.vatsul.awatcher.database;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.sqlite.util.StringUtils;

import com.vatsul.awatcher.Utils;

public class Database {
	
	public String databaseFilepath ="jdbc:sqlite:"+System.getProperty("user.dir")+"/animuwatcher.db";
	
	public MyAnimeList MyAnimeList;
	public Ed2kHashes Ed2kHashes;

	public Database() {
		MyAnimeList = new MyAnimeList();
		Ed2kHashes = new Ed2kHashes();
		try(Connection dbConn = DriverManager.getConnection(databaseFilepath)) {
			DatabaseMetaData meta = dbConn.getMetaData();
			meta.getDriverName();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		initDatabase();
		System.out.println("Database ready");
	}
	
	public void initDatabase() {
		// Contains information about scanned files
		String sqlFileTable = "CREATE TABLE IF NOT EXISTS `fileTable` ( \n"
				+ "`id`	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, \n"
				+ "`filepath`	TEXT UNIQUE, \n"
				+ "`ed2kHash`	VARCHAR(32), \n"
				+ "`aid`	INTEGER, \n"
				+ "`fid`	INTEGER, \n"
				+ "`epNum`	STRING \n"
				+ ");";
		
		// Contains information about AniDB aids
		String sqlAnimeTable = "CREATE TABLE IF NOT EXISTS `AniDBAnimeTable` ( \n"
				+ "`aid`	INTEGER NOT NULL PRIMARY KEY,  \n"
				+ "`mainTitle`	TEXT, \n"
				+ "`description`	TEXT, \n"
				+ "`startDate`	TEXT, \n"
				+ "`endDate`	TEXT, \n"
				+ "`type`	TEXT, \n"
				+ "`thumbnail`	TEXT, \n"
				+ "`totalEp`	INTEGER, \n"
				+ "`synonymList` TEXT, \n"
				+ "`malID`	INTEGER \n"
				+ ");";
		
		// Contains users MAL list
		String sqlMyAnimeList = "CREATE TABLE IF NOT EXISTS `MyAnimeList` ( \n"
				+ "`malID` INTEGER NOT NULL PRIMARY KEY, \n"
				+ "`title` TEXT, \n"
				+ "`type` INTEGER, \n"
				+ "`episodes` INTEGER, \n"
				+ "`status` INTEGER, \n"
				+ "`watchedEpisodes` INTEGER, \n"
				+ "`myScore` INTEGER, \n"
				+ "`myStatus` INTEGER, \n" 
				+ "`thumbnail` TEXT, \n"
				+ "`startDate` TEXT, \n"
				+ "`lastUpdated` INTEGER \n"
				+ ");";
		
		// Contains MAL synopses
		String sqlSynopsis = "CREATE TABLE IF NOT EXISTS `MyAnimeListSynopsis` ( \n"
				+ "`malID` INTEGER NOT NULL PRIMARY KEY, \n"
				+ "`synopsis` TEXT \n"
				+ ");";
		
		// Contains aid and fid of ed2k hashes
		String sqlEd2kTable = "CREATE TABLE IF NOT EXISTS `Ed2kHashes` ( \n"
				+ "`ed2kHash` TEXT, \n"
				+ "`aid` INTEGER, \n"
				+ "`fid` INTEGER, \n"
				+ "`eid` TEXT"
				+ ");";
		
			executeCmd(sqlEd2kTable);
			executeCmd(sqlMyAnimeList);
			executeCmd(sqlSynopsis);
			executeCmd(sqlFileTable);
			executeCmd(sqlAnimeTable);
	}
	
	// Returns ed2k hash from database if found
	public String getEd2kHash(File file) {
		try(Connection dbConn = DriverManager.getConnection(databaseFilepath)) {
			String sql = "SELECT ed2kHash \n"
					+ "FROM fileTable WHERE filepath= '"+Utils.encodeAbsoluteFilepath(file)+"';";
			ResultSet rs = dbConn.createStatement().executeQuery(sql);
			while(rs.next()) {
				return rs.getString(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public ArrayList<Integer> getAidsFromFiletable() {
		try(Connection dbConn = DriverManager.getConnection(databaseFilepath)) {
			String sql = "SELECT DISTINCT aid \n"
					+ "FROM fileTable WHERE aid IS NOT NULL";
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
	
	public Integer getMalID(int aid) {
		try(Connection dbConn = DriverManager.getConnection(databaseFilepath)) {
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
	
	public Integer getAid(File file) {
		try(Connection dbConn = DriverManager.getConnection(databaseFilepath)) {
			String sql = "SELECT aid \n"
					+ "FROM fileTable WHERE filepath= '"+Utils.encodeAbsoluteFilepath(file)+"';";
			ResultSet rs = dbConn.createStatement().executeQuery(sql);
			while(rs.next()) {
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Integer getAid(int malID) {
		try(Connection dbConn = DriverManager.getConnection(databaseFilepath)) {
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
	
	public String getEpNum(File file) {
		try(Connection dbConn = DriverManager.getConnection(databaseFilepath)) {
			String sql = "SELECT epNum \n"
					+ "FROM fileTable WHERE filepath= '"+Utils.encodeAbsoluteFilepath(file)+"';";
			ResultSet rs = dbConn.createStatement().executeQuery(sql);
			while(rs.next()) {
				return rs.getString(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String getAidTitle(int aid) {
		try(Connection dbConn = DriverManager.getConnection(databaseFilepath)) {
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
	
	// Returns list of thumbnail strings
	public ArrayList<String> getThumbnails() {
		try(Connection dbConn = DriverManager.getConnection(databaseFilepath)) {
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
	
	// Returns AniDB file id or -1 if does not exist in database
	public int getFid(File file) {
		try(Connection dbConn = DriverManager.getConnection(databaseFilepath)) {
			String sql = "SELECT fid \n"
					+ "FROM fileTable WHERE filepath = '"+Utils.encodeAbsoluteFilepath(file)+"';";
			ResultSet rs = dbConn.createStatement().executeQuery(sql);
			while(rs.next()) {
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	public File getFileByAidEp(int aid, int epNum) {
		try(Connection dbConn = DriverManager.getConnection(databaseFilepath)) {
			String sql = "SELECT filepath \n"
					+ "FROM fileTable WHERE aid = '"+aid+"' AND epNum = '" + epNum + "';";
			ResultSet rs = dbConn.createStatement().executeQuery(sql);
			while(rs.next()) {
				return Utils.decodeAbsoluteFilepath(rs.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void executeCmd(String sql) {
		try(Connection dbConn = DriverManager.getConnection(databaseFilepath)) {
			dbConn.createStatement().execute(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	// Returns id if of column with path of given file else -1
	public int getFiletableId(File file) {
		try(Connection dbConn = DriverManager.getConnection(databaseFilepath)) {
			String sql = "SELECT id \n"
					+ "FROM fileTable WHERE filepath = '"+Utils.encodeAbsoluteFilepath(file)+"';";
			ResultSet rs = dbConn.createStatement().executeQuery(sql);
			while(rs.next()) {
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	// Insert a new file into fileTable
	public void insertFile(File file) {
		String sql = "INSERT INTO fileTable(filepath) VALUES(?)";
		try(Connection dbConn = DriverManager.getConnection(databaseFilepath)) {
			PreparedStatement ps = dbConn.prepareStatement(sql);
			ps.setString(1, Utils.encodeAbsoluteFilepath(file));
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void updateFid(File file, int fid) {
		String sql = "UPDATE fileTable SET fid = ?"
				+ "WHERE filepath = ?";
		try(Connection dbConn = DriverManager.getConnection(databaseFilepath)) {
			PreparedStatement ps = dbConn.prepareStatement(sql);
			ps.setInt(1, fid);
			ps.setString(2, Utils.encodeAbsoluteFilepath(file));
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void updateAid(File file, int aid) {
		String sql = "UPDATE fileTable SET aid = ?"
				+ "WHERE filepath = ?";
		try(Connection dbConn = DriverManager.getConnection(databaseFilepath)) {
			PreparedStatement ps = dbConn.prepareStatement(sql);
			ps.setInt(1, aid);
			ps.setString(2, Utils.encodeAbsoluteFilepath(file));
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void updateEd2k(File file, String ed2k) {
		String sql = "UPDATE fileTable SET ed2kHash = ?"
				+ "WHERE filepath = ?";
		try(Connection dbConn = DriverManager.getConnection(databaseFilepath)) {
			PreparedStatement ps = dbConn.prepareStatement(sql);
			ps.setString(1, ed2k);
			ps.setString(2, Utils.encodeAbsoluteFilepath(file));
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void updateMalID(int aid, int malID) {
		String sql = "UPDATE AniDBAnimeTable SET malID = ?"
				+ "WHERE aid = ?";
		try(Connection dbConn = DriverManager.getConnection(databaseFilepath)) {
			PreparedStatement ps = dbConn.prepareStatement(sql);
			ps.setInt(1, malID);
			ps.setInt(2, aid);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void updateEpNum(File file, String epNum) {
		String sql = "UPDATE fileTable SET epNum = ?"
				+ "WHERE filepath = ?";
		try(Connection dbConn = DriverManager.getConnection(databaseFilepath)) {
			PreparedStatement ps = dbConn.prepareStatement(sql);
			ps.setString(1, epNum);
			ps.setString(2, Utils.encodeAbsoluteFilepath(file));
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String[] getSynonymList(int aid) {
		try(Connection dbConn = DriverManager.getConnection(databaseFilepath)) {
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
	
	public ArrayList<ArrayList> getAnimeDataForGtkTable() {
		try(Connection dbConn = DriverManager.getConnection(databaseFilepath)) {
			String sql = "SELECT aid, mainTitle, type, thumbnail, totalEp \n"
					+ "FROM AniDBAnimeTable;";
			ResultSet rs = dbConn.createStatement().executeQuery(sql);
			ArrayList<Integer> aids = new ArrayList<Integer>();
			ArrayList<String> titles = new ArrayList<String>();
			ArrayList<String> types = new ArrayList<String>();
			ArrayList<String> thumbnails = new ArrayList<String>();
			ArrayList<Integer> episodes = new ArrayList<Integer>();
			while(rs.next()) {
				aids.add(rs.getInt(1));
				titles.add(rs.getString(2));
				types.add(rs.getString(3));
				thumbnails.add(rs.getString(4));
				episodes.add(rs.getInt(5));
			}
			ArrayList<ArrayList> listToReturn = new ArrayList<ArrayList>();
			listToReturn.add(aids);
			listToReturn.add(titles);
			listToReturn.add(types);
			listToReturn.add(thumbnails);
			listToReturn.add(episodes);
			return listToReturn;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	// Returns start date
	public String getStartDateAid(int aid) {
		try(Connection dbConn = DriverManager.getConnection(databaseFilepath)) {
			String sql = "SELECT startDate \n"
					+ "FROM AniDBAnimeTable WHERE aid = " + aid;
			ResultSet rs = dbConn.createStatement().executeQuery(sql);
			while(rs.next()) {
				return rs.getString(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String getDescriptionByAid(int aid) {
		try(Connection dbConn = DriverManager.getConnection(databaseFilepath)) {
			String sql = "SELECT description \n"
					+ "FROM AniDBAnimeTable WHERE aid = " + aid;
			ResultSet rs = dbConn.createStatement().executeQuery(sql);
			while(rs.next()) {
				return rs.getString(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public int getTotalEpsByAid(int aid) {
		try(Connection dbConn = DriverManager.getConnection(databaseFilepath)) {
			String sql = "SELECT totalEp \n"
					+ "FROM AniDBAnimeTable WHERE aid= '"+aid+"';";
			ResultSet rs = dbConn.createStatement().executeQuery(sql);
			while(rs.next()) {
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	// Put new anime into anime table or update values if already exists
	public void putAnimeIntoDB(int aid, String mainTitle, String description, String startDate, String endDate, String type, String thumbnail, int totalEp, List<String> synonymList) {
		String synonyms = StringUtils.join(synonymList, "}");
		String sql = "REPLACE INTO AniDBAnimeTable(aid,mainTitle,description,startDate,endDate,type,thumbnail,totalEp,synonymList) VALUES(?,?,?,?,?,?,?,?,?)";
		try(Connection dbConn = DriverManager.getConnection(databaseFilepath)) {
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