package com.vatsul.awatcher.database;

import static com.vatsul.awatcher.Main.anidbConn;
import static com.vatsul.awatcher.Main.database;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.vatsul.awatcher.Hashes;
import com.vatsul.awatcher.MalApi;
import com.vatsul.awatcher.anidbapi.Amask;
import com.vatsul.awatcher.anidbapi.AnidbConnection;
import com.vatsul.awatcher.anidbapi.AnidbHTTPApi;
import com.vatsul.awatcher.anidbapi.Fmask;

public class Indexer {

	// Checks if given file is found in AniDB by checking its hash and adds it into the database if it is
	public static void checkAnime(File anime) {
			String ed2kHash = "";
			// Insert file to database if does not already exist
			if(database.getFiletableId(anime)<1) {
				System.out.println("Inserting a new file into the database: " + anime.getName());
				database.insertFile(anime);
			}
			
			// Generate and update ed2k hash if not already found in DB
			if(database.getEd2kHash(anime)==null) {
				System.out.println("Generating Ed2k hash for the file:" + anime.getName());
				ed2kHash = Hashes.Ed2kHash(anime);
				database.updateEd2k(anime, ed2kHash);
				System.out.println("Searching the file from AniDB: " + anime.getName());
				searchFromAnidb(anime, ed2kHash, anime.length());
			} else {
				System.out.println("File ed2k hash already found, skipping: " + anime.getName());
			}
	}
	
	// Checks if given file is found in AniDB, even if ed2k hash exists
	public static void reCheckAnime(File anime) {
		String ed2kHash = "";
		// Insert file to database if does not already exist
		if(database.getFiletableId(anime)<1) {
			System.out.println("Inserting a new file into the database: " + anime.getName());
			database.insertFile(anime);
		}
		
		// Generate and update ed2k hash if not already found in DB
		if(database.getEd2kHash(anime)==null) {
			System.out.println("Generating Ed2k hash for the file:" + anime.getName());
			ed2kHash = Hashes.Ed2kHash(anime);
			database.updateEd2k(anime, ed2kHash);
			System.out.println("Searching the file from AniDB: " + anime.getName());
			searchFromAnidb(anime, ed2kHash, anime.length());
		} else if(database.getAid(anime)<=0) {
			System.out.println("Searching the file from AniDB: " + anime.getName());
			searchFromAnidb(anime, database.getEd2kHash(anime), anime.length());
		}
	}
	
	// Cache thumbnails for both AniDB and MAL entries from the respective sites
	public static void cacheThumbnails() {
		for(String s : database.getThumbnails()) {
			saveThumbnail(s);
		}
		for(Integer malID : database.MyAnimeList.getAllMalIDs()) {
			database.MyAnimeList.getThumbnail(malID);
		}
	}
	
	// Attempts to connect aids on the database to malIDs
	public static void updateMalids() {
		for(Integer aid : database.getAidsFromFiletable()) {
			if(database.getMalID(aid)==0) {
				ArrayList<String> searchStrings = new ArrayList<String>();
				searchStrings.add(database.getAidTitle(aid));
				searchStrings.addAll(Arrays.asList(database.getSynonymList(aid)));
				int malID = MalApi.getMalID(searchStrings);
				if(malID>0) {
					database.updateMalID(aid, malID);
				}
			}
		}
	}
	
	// Update all aid related data
	public static void updateAidData() {
		for(Integer i : database.getAidsFromFiletable()) {
			Indexer.lookupAnimeInfo(i);
		}
	}
	
	// Saves AniDB thumbnail to cache with given filename
	private static void saveThumbnail(String filename) {
		try {
			File thumbnail = new File("cache/thumbnails/"+filename);
			if(!thumbnail.exists()) {
				new File("cache/thumbnails/").mkdirs();
				thumbnail.createNewFile();
				URL thumbnailUrl = new URL("https://img7.anidb.net/pics/anime/"+filename);
				Files.copy(thumbnailUrl.openStream(), Paths.get(thumbnail.toURI()), StandardCopyOption.REPLACE_EXISTING);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// Fills database with data from anidb related to the anime
	public static void lookupAnimeInfo(int aid) {
		// Already exists in DB
		if(database.getAidTitle(aid)!=null)
			return;
		AnidbHTTPApi.cacheAidInfo(aid, false);
		try {
			File animeXml = new File("cache/anidb_anime/"+aid+".xml");
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(animeXml);
			String mainTitle = null;
			String type;
			String startDate;
			String endDate="???";
			String thumbnail;
			String description;
			int totalEp;
			List<String> synonymList = new ArrayList<String>();
			
			NodeList nl = doc.getElementsByTagName("title");
			for(int i=0; i<nl.getLength(); i++) {
				if(nl.item(i).getAttributes().item(0).getNodeValue().equals("main")) {
					mainTitle = nl.item(i).getTextContent();
				} else if(nl.item(i).getAttributes().item(0).getNodeValue().matches("synonym|official")) {
					if(nl.item(i).getAttributes().item(1).getTextContent().matches("en|x-jat"))
						synonymList.add(nl.item(i).getTextContent());
				}
			}
				
			nl = doc.getElementsByTagName("type");
			type = nl.item(0).getTextContent();
			
			nl = doc.getElementsByTagName("startdate");
			startDate = nl.item(0).getTextContent();
			
			nl = doc.getElementsByTagName("enddate");
			if(nl.item(0)!=null)
				endDate = nl.item(0).getTextContent();
			
			nl = doc.getElementsByTagName("picture");
			thumbnail = nl.item(0).getTextContent();
			
			nl = doc.getElementsByTagName("description");
			description = nl.item(0).getTextContent();
			
			nl = doc.getElementsByTagName("episodecount");
			totalEp = Integer.parseInt(nl.item(0).getTextContent());
			
			database.putAnimeIntoDB(aid, mainTitle, description, startDate, endDate, type, thumbnail, totalEp, synonymList);
		} catch (SAXException | IOException | ParserConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	// Looks for aid by ed2k hash from Anidb, if found, updates database with the new data
	public static void searchFromAnidb(File file, String ed2kHash, long fileLength) {
		int fid;
		int aid;
		String eid;
		aid = database.Ed2kHashes.getAidByEd2kHash(ed2kHash);
		if(aid!=0) {
			fid = database.Ed2kHashes.getFidByEd2kHash(ed2kHash);
			eid = database.Ed2kHashes.getEidByEd2kHash(ed2kHash);
		} else {
			Fmask fmask = new Fmask();
			fmask.setAid(true);
			Amask amask = new Amask();
			amask.setEpNo(true);
			
			AnidbConnection.checkAnidbConnection();
			String command = "FILE size="+fileLength+"&ed2k="+ed2kHash+"&fmask="+
					fmask.getHexStr()+"&amask="+amask.getHexStr()+"&s="+anidbConn.sessionKey;
			String resp = anidbConn.sendCommand(command);
			String response[] = resp.split("\\||\n");
			System.out.println("Resp: " + resp);
			if(!response[0].equals("220 FILE")) {
				return; // File not found on AniDB
			}
		
			fid = Integer.parseInt(response[1]);
			aid = Integer.parseInt(response[2]);
			eid = response[3];
			database.Ed2kHashes.putEd2kHash(ed2kHash, aid, fid, eid);
		}
		database.updateAid(file, aid);
		database.updateFid(file, fid);
		database.updateEpNum(file, eid);
	}
}
