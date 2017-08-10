package com.vatsul.awatcher;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class VlcConnect {
	/*
	 * Enable VLC HTTP interface from Preferences -> All -> Interface -> Main interfaces -> Web
	 * Set password from Preferences -> All -> Interface -> Main Interfaces -> Lua -> Lua HTTP -> Password
	 */
	
	// Returns position of playback on scale 0 to 1
	public static Double getPlaybackPosition(int port, String password) {
		try {
			URL url = new URL("http://localhost:"+port+"/requests/status.xml");
			String credentials = java.util.Base64.getEncoder().encodeToString((""+":"+password).getBytes());
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.setDoOutput(true);
			con.setRequestProperty("Authorization",  "Basic " + credentials);
			InputStream is = con.getInputStream();
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(is);
			NodeList position = doc.getElementsByTagName("position");
			return Double.parseDouble(position.item(0).getTextContent());
		} catch (IOException | SAXException | ParserConfigurationException | DOMException e) {
			if(e.getClass() == ConnectException.class) {
				return (double) -1; // VLC probably not running
			}
			e.printStackTrace();
		}
		// No valid entry found
		return (double) -1;
	}
	
	// Returns file of current track being played on VLC, webinterface port and password as an input
	public static File getCurrentTrack(int port, String password) {
		try {
			URL url = new URL("http://localhost:"+port+"/requests/playlist.xml");
			String credentials = java.util.Base64.getEncoder().encodeToString((""+":"+password).getBytes());
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.setDoOutput(true);
			con.setRequestProperty("Authorization",  "Basic " + credentials);
			InputStream is = con.getInputStream();
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(is);
			NodeList uris = doc.getElementsByTagName("leaf");
			for(int i=0; i<uris.getLength(); i++) { // oh no
				for(int j=0; j<uris.item(i).getAttributes().getLength(); j++) {
					if(uris.item(i).getAttributes().item(j).getTextContent().equals("current")) {
						for(int k=0; k<uris.item(i).getAttributes().getLength(); k++) {
							if(uris.item(i).getAttributes().item(k).getNodeName().equals("uri")) {
								String absoluteFilepath = uris.item(i).getAttributes().item(k).getTextContent();
								return new File(new URI(absoluteFilepath).getPath());	
							}
						}
					}
				}
			}
		} catch (IOException | SAXException | ParserConfigurationException | DOMException | URISyntaxException e) {
			if(e.getClass() == ConnectException.class) {
				return null; // VLC probably not running
			}
			e.printStackTrace();
		}
		// No valid entry found
		return null;
	}	
}
