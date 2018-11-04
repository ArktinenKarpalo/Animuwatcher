package com.vatsul.awatcher.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
	
	public String databaseFilepath ="jdbc:sqlite:"+System.getProperty("user.dir")+"/animuwatcher.db";
	
	public Ed2kHashes Ed2kHashes;
	public AniDBAnimeTable AniDBAnimeTable;
	public FileTable FileTable;
	public MalAnimeData MalAnimeData;
	public MALIDs MALIDs;
	public MyAnimeList MyAnimeList;

	public Database() {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		Ed2kHashes = new Ed2kHashes();
		AniDBAnimeTable = new AniDBAnimeTable();
		FileTable = new FileTable();
		MalAnimeData = new MalAnimeData();
		MALIDs = new MALIDs();
		MyAnimeList = new MyAnimeList();
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
		String sqlFileTable = "CREATE TABLE IF NOT EXISTS `FileTable` ( \n"
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
		
		// Contains aid and fid of ed2k hashes
		String sqlEd2kTable = "CREATE TABLE IF NOT EXISTS `Ed2kHashes` ( \n"
				+ "`ed2kHash` TEXT, \n"
				+ "`aid` INTEGER, \n"
				+ "`fid` INTEGER, \n"
				+ "`eid` TEXT"
				+ ");";
		// Contains information about MAL animes
		String sqlMalAnimeData = "CREATE TABLE IF NOT EXISTS `MalAnimeData` ( \n"
				+ "`malID` INTEGER NOT NULL PRIMARY KEY, \n"
				+ "`title` TEXT, \n"
				+ "`type` TEXT, \n"
				+ "`episodes` INTEGER, \n"
				+ "`status` TEXT, \n"
				+ "`thumbnail` TEXT, \n"
				+ "`startDate` TEXT, \n"
				+ "`synopsis` TEXT\n"
				+ ");";

		// Contains all malIDs that we want information of
		String sqlMalIDs = "CREATE TABLE IF NOT EXISTS `MALIDs` (\n" +
				"`malID` INTEGER NOT NULL PRIMARY KEY\n" +
				");";

		// Contains data about users MyAnimeList
		String sqlMyAnimeList = "CREATE TABLE IF NOT EXISTS `MyAnimeList` (\n" +
				"`malID` INTEGER NOT NULL PRIMARY KEY,\n" +
				"`myScore` INTEGER,\n" +
				"`myStatus` INTEGER,\n" +
				"`watchedEpisodes` INTEGER\n" +
				");";

		executeCmd(sqlMalIDs);
		executeCmd(sqlMyAnimeList);
		executeCmd(sqlMalAnimeData);
		executeCmd(sqlEd2kTable);
		executeCmd(sqlFileTable);
		executeCmd(sqlAnimeTable);
	}

	public void executeCmd(String sql) {
		try(Connection dbConn = DriverManager.getConnection(databaseFilepath)) {
			dbConn.createStatement().execute(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}